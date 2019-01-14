package jp.silverbullet.sequncer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
//	abstract protected HandlerPropertyHolder getHandlerPropertyHolder();
	abstract protected DependencyEngine getDependency();
//	abstract protected String getUserApplicationPath();
	abstract protected EasyAccessInterface getEasyAccessInterface();
	abstract protected RegisterAccessor getRegisterAccessor();
	
//	private List<AbstractSvHandler> handlers = new ArrayList<>();
	private Set<SequencerListener> listeners = new HashSet<SequencerListener>();
	private List<UserSequencer> userSequencers = new ArrayList<>();
	
//	private LinkedHashMap<String, List<ChangedItemValue>> history = new LinkedHashMap<>();
	private List<String> debugDepLog;

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
		
	public Sequencer() {
		Thread.currentThread().getId();
	}
	
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
	
	public void requestChange(String id, Integer index, String value, CommitListener commitListener)
			throws RequestRejectedException {
		fireRequestChangeByUser(id, value);
		
		// resolves dependencies
		
		DependencyEngine engine = getDependency();
		engine.setCommitListener(commitListener);
		engine.requestChange(new Id(id, index), value);
		debugDepLog = engine.getDebugLog();
		
		List<String> changedIds = engine.getChangedIds();

//		Set<HandlerProperty> toRunHandlers = new LinkedHashSet<>();
//		for (HandlerProperty handler : getHandlerPropertyHolder().getHandlers()) {
//			for (String changed : changedIds) {
//				if (changed.contains(RuntimeProperty.INDEXSIGN)) {
//					changed = changed.split(RuntimeProperty.INDEXSIGN)[0];
//				}
//				if (handler.getIds().contains(changed)) {
//					toRunHandlers.add(handler);
//					break;
//				}
//			}
//		}
		
		SvHandlerModel model = new SvHandlerModel() {
			@Override
			public RegisterAccessor getRegisterAccessor() {
				return Sequencer.this.getRegisterAccessor();
			}

			@Override
			public EasyAccessInterface getEasyAccessInterface() {
				return easyAccessInterface;
			}

		};
		
		for (UserSequencer us : this.userSequencers) {
			if (matches(us.targetIds(), changedIds)) {
				us.handle(model, getDependency().getChagedItems());
			}
		}
//		for (HandlerProperty handler : toRunHandlers) {
//			new CommonSvHandler(model, handler).execute(getDependency().getChagedItems());
//		}
		
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
