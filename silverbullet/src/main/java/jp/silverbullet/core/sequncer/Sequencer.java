package jp.silverbullet.core.sequncer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.CommitListener;
import jp.silverbullet.core.dependency2.DependencyEngine;
import jp.silverbullet.core.dependency2.DependencyListener;
import jp.silverbullet.core.dependency2.DepenendencyRequest;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.register2.RegisterAccessor;

public abstract class Sequencer {
	protected abstract RuntimePropertyStore getPropertiesStore();
	protected abstract DependencyEngine getDependency();
//	protected abstract EasyAccessInterface getEasyAccessInterface();
	protected abstract RegisterAccessor getRegisterAccessor();

	private Set<SequencerListener> listeners = new HashSet<SequencerListener>();
	private List<UserSequencer> userSequencers = new ArrayList<>();
	private List<String> debugDepLog;
	private Object sync = new Object();
	
	private SvHandlerModel model = new SvHandlerModel() {
		@Override
		public RegisterAccessor getRegisterAccessor() {
			return Sequencer.this.getRegisterAccessor();
		}

		@Override
		public EasyAccessInterface getEasyAccessInterface() {
			return accessFromSystem;
		}

		@Override
		public SystemAccessor getSystem() {
			return getSystemAccessor();
		}

	};

	private EasyAccessInterface accessFromSystem = new EasyAccessInterface() {
		@Override
		public void requestChange(final String id, final String value) throws RequestRejectedException {
			requestChange(id, 0, value);
		}

		@Override
		public void requestChange(String id, int index, String value) throws RequestRejectedException {
			try {
			//	fireChangeFromSystem(id, value);
			//	getDependency().requestChange(new Id(id, index), value, false);
				Sequencer.this.requestChange(id, index, value, false, null, Actor.System);
				debugDepLog.addAll(getDependency().getDebugLog());
				
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}
		
		}
		@Override
		public String getCurrentValue(String id) {
			return getPropertiesStore().get(id).getCurrentValue();
		}

		@Override
		public void requestChange(String id, Object blobData, String name) throws RequestRejectedException {
			storeBlob(id, blobData);
			requestChange(id, name);
		}

		@Override
		public String getSelectedListTitle(String id) {
			return getPropertiesStore().get(id).getSelectedListTitle();
		}
		
	};
				
	public Sequencer() {
		createDependencyThread(MyThread.DEFAULT);
	}
	
	public void requestChange(String id, String value, CommitListener commitListener) throws RequestRejectedException {
		requestChange(id, 0, value, false, commitListener, Actor.User);
	}
	
	public void requestChange(String id, String value) throws RequestRejectedException {
		requestChange(id, 0, value, false);
	}

	protected abstract SystemAccessor getSystemAccessor();
	public void requestChange(String id, String value, boolean forceChange) throws RequestRejectedException {
		requestChange(id, 0, value, forceChange);
	}
	
	private void storeBlob(String id, Object blobData) {
		getBlobStore().put(id, blobData);
	}
	
	protected abstract BlobStore getBlobStore();
	
	public void requestChange(String id, Integer index, String value, boolean forceChange) throws RequestRejectedException {
		requestChange(id, index, value, forceChange, new CommitListener() {
			@Override
			public Reply confirm(Set<IdValue> message) {
				return Reply.Accept;
			}	
		}, Actor.User);
	}
	
	public enum Actor {
		User,
		System
	};

	private BlockingQueue<DepenendencyRequest> dependencyQueue = new LinkedBlockingQueue<>();
	RequestRejectedException exception = null;
	
//	private Map<String, MyThread> threads = new HashMap<>();
	
	private void createDependencyThread(String user) {
//		BlockingQueue<DepenendencyRequest> dependencyQueue = new LinkedBlockingQueue<>();
		Thread thread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						DepenendencyRequest req = dependencyQueue.take();
						String id = req.getId().getId();
						int index = req.getId().getIndex();
						handleRequestChange(id, index, req.getValue(), req.isForceChange(), 
								req.getCommitListener(), req.getActor());
						synchronized(sync) {
							sync.notify();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (RequestRejectedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
		
//		threads.put(user, new MyThread(dependencyQueue, thread));
	}
	
	public void requestChange(String id, Integer index, String value, boolean forceChange, 
			CommitListener commitListener, Actor actor)
			throws RequestRejectedException {
	
		dependencyQueue.add(new DepenendencyRequest(id, index, value, forceChange, commitListener, actor));
//		this.threads.get(MyThread.DEFAULT).requestDependency(id, index, value, forceChange, commitListener, actor);
	}
	
	private void handleRequestChange(String id, Integer index, String value, boolean forceChange, 
			CommitListener commitListener, Actor actor)
			throws RequestRejectedException {
		
//		System.out.println("handleRequestChange " + id + " -> " + value + " ; " + dependencyQueue.size());
		if (actor.equals(Actor.User)) {
			fireRequestChangeByUser(id, value);
		}
		else if (actor.equals(Actor.System)) {
			fireChangeFromSystem(id, value);
		}
		// resolves dependencies
		
		DependencyEngine engine = getDependency();
		engine.setCommitListener(commitListener);
		try {
			engine.requestChange(new Id(id, index), value, forceChange);
		} catch (RequestRejectedException e1) {
			exception = e1;
			e1.printStackTrace();
		}
		debugDepLog = engine.getDebugLog();
		
		List<String> changedIds = engine.getChangedIdsWithMaskingBlockPropagation();
			
//		Map<String, List<ChangedItemValue>> changes = deepCopy(getDependency().getChagedItems());
		
		Set<UserSequencer> matchedSequencer = new LinkedHashSet<>();
		for (UserSequencer us : userSequencers) {
			if (matches(us.targetIds(), changedIds)) {
				matchedSequencer.add(us);	
			}
		}	
		for (UserSequencer us : matchedSequencer) {
			try {
				us.handle(model, getDependency().getChagedItemsWithMaskingBlockPropagation());
			} catch (RequestRejectedException e) {
				exception = e;
				e.printStackTrace();
			}
		}
	}

	private Map<String, List<ChangedItemValue>> deepCopy(Map<String, List<ChangedItemValue>> original) {
		Map<String, List<ChangedItemValue>> ret = new LinkedHashMap<>();
		for (String key : original.keySet()) {
			ret.put(key, new ArrayList<>(original.get(key)));
		}
		return ret;
	}
	
	private boolean matches(List<String> targetIds, List<String> changedIds) {
		if (targetIds == null) {
			return true;
		}
		for (String id : changedIds) {
			if (targetIds.contains(id.split(RuntimeProperty.INDEXSIGN)[0])) {
				return true;
			}
		}
		return false;
	}
	public List<String> getDebugDepLog() {
		return debugDepLog;
	}
	protected void fireChangeFromSystem(String id, String value) {
		for (SequencerListener listener : this.listeners) {
			listener.onChangedBySystem(id, value);
		}
	}
	private void fireRequestChangeByUser(String id, String value) {
		for (SequencerListener listener : this.listeners) {
			listener.onChangedByUser(id, value);
		}
	}

//	public void addDependencyListener(DependencyListener dependencyListener) {
//		getDependency().addDependencyListener(dependencyListener);
//	}
//	
	public void addSequencerListener(SequencerListener sequencerListener) {
		this.listeners.add(sequencerListener);
	}
	
	public void removeSequencerListener(SequencerListener sequencerListener) {
		this.listeners.remove(sequencerListener);
	}
	public void removeDependencyListener(DependencyListener listener) {
		this.getDependency().removeDependencyListener(listener);
	}
	public void addUserSequencer(UserSequencer testSequencer) {
		this.userSequencers.add(testSequencer);
	}
	public void syncDependency() {
		synchronized(sync) {
			try {
				sync.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public EasyAccessInterface getAccessFromSystem() {
		return accessFromSystem;
	}

}
