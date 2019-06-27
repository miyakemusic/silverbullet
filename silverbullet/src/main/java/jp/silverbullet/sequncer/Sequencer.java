package jp.silverbullet.sequncer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.BlobStore;
import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.dependency2.CommitListener;
import jp.silverbullet.dependency2.DependencyEngine;
import jp.silverbullet.dependency2.DependencyListener;
import jp.silverbullet.dependency2.Id;
import jp.silverbullet.dependency2.IdValue;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.property2.SvFileException;
import jp.silverbullet.register2.RegisterAccessor;

public abstract class Sequencer {
	protected abstract RuntimePropertyStore getPropertiesStore();
	protected abstract DependencyEngine getDependency();
	protected abstract EasyAccessInterface getEasyAccessInterface();
	protected abstract RegisterAccessor getRegisterAccessor();

	private Set<SequencerListener> listeners = new HashSet<SequencerListener>();
	private List<UserSequencer> userSequencers = new ArrayList<>();
	private List<String> debugDepLog;
	
	private SvHandlerModel model = new SvHandlerModel() {
		@Override
		public RegisterAccessor getRegisterAccessor() {
			return Sequencer.this.getRegisterAccessor();
		}

		@Override
		public EasyAccessInterface getEasyAccessInterface() {
			return easyAccessInterface;
		}

		@Override
		public SystemAccessor getSystem() {
			return getSystemAccessor();
		}

	};

	private EasyAccessInterface easyAccessInterface = new EasyAccessInterface() {
		@Override
		public void requestChange(final String id, final String value) throws RequestRejectedException {
			requestChange(id, 0, value);
		}

		@Override
		public void requestChange(String id, int index, String value) throws RequestRejectedException {
			try {
				fireChangeFromSystem(id, value);
				getDependency().requestChange(new Id(id, index), value, false);
				debugDepLog.addAll(getDependency().getDebugLog());
				
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}			
		}
		@Override
		public RuntimeProperty getProperty(String id) {
			return getPropertiesStore().get(id);
		}

		@Override
		public void requestChange(String id, Object blobData, String name) throws RequestRejectedException {
			storeBlob(id, blobData);
			requestChange(id, name);
		}
		
	};
			
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
		});
	}
	
	RequestRejectedException exception = null;
	public void requestChange(String id, Integer index, String value, boolean forceChange, CommitListener commitListener)
			throws RequestRejectedException {
//		
//		if (!isMainThread(Thread.currentThread().getId())) {
//			System.out.println("Not main thread");
//		}
		fireRequestChangeByUser(id, value);
		
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
		
		List<String> changedIds = engine.getChangedIds();
			
		for (UserSequencer us : userSequencers) {
			if (matches(us.targetIds(), changedIds)) {
				if (us.isAsync()) {
					new Thread() {
						@Override
						public void run() {
							try {
								us.handle(model, getDependency().getChagedItems());
							} catch (RequestRejectedException e) {
								exception = e;
								e.printStackTrace();
							}
						}
					}.start();
				}
				else {
					try {
						us.handle(model, getDependency().getChagedItems());
					} catch (RequestRejectedException e) {
						exception = e;
						e.printStackTrace();
					}				
				}

			}
		}	

	}

	private boolean matches(List<String> targetIds, List<String> changedIds) {
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

}
