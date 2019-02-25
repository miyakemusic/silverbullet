package jp.silverbullet.sequncer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import jp.silverbullet.dependency2.CommitListener;
import jp.silverbullet.dependency2.DependencyEngine;
import jp.silverbullet.dependency2.DependencyListener;
import jp.silverbullet.dependency2.Id;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register2.RegisterAccessor;

public abstract class Sequencer {
	abstract protected RuntimePropertyStore getPropertiesStore();
	abstract protected DependencyEngine getDependency();
	abstract protected EasyAccessInterface getEasyAccessInterface();
	abstract protected RegisterAccessor getRegisterAccessor();
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
				getDependency().requestChange(id, index, value);
				debugDepLog.addAll(getDependency().getDebugLog());
				
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}			
		}
		@Override
		public RuntimeProperty getProperty(String id) {
			return getPropertiesStore().get(id);
		}
		
	};
			
	public void requestChange(String id, String value) throws RequestRejectedException {
		requestChange(id, 0, value);
	}
	
	public void requestChange(String id, Integer index, String value) throws RequestRejectedException {
		requestChange(id, index, value, new CommitListener() {
			@Override
			public Reply confirm(String message) {
				return Reply.Accept;
			}	
		});
	}
	
	RequestRejectedException exception = null;
	public void requestChange(String id, Integer index, String value, CommitListener commitListener)
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
			engine.requestChange(new Id(id, index), value);
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

	public void addDependencyListener(DependencyListener dependencyListener) {
		getDependency().addDependencyListener(dependencyListener);
	}

//	public void add(AbstractSvHandler handler) {
//		this.handlers.add(handler);
//	}
	
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
