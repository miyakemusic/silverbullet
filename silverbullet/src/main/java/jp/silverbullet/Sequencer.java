package jp.silverbullet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.silverbullet.dependency.ChangedItemValue;
import jp.silverbullet.dependency.DependencyEngine;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.dependency.DependencyListener;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.handlers.AbstractSvHandler;
import jp.silverbullet.handlers.CommonSvHandler;
import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.HandlerProperty;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvHandlerModel;

public abstract class Sequencer implements DependencyInterface {
	abstract protected SvPropertyStore getPropertiesStore();
	abstract protected HandlerPropertyHolder getHandlerPropertyHolder();
	abstract protected DependencyEngine getDependency();
	abstract protected String getUserApplicationPath();
	abstract protected EasyAccessModel getEasyAccessModel();
	abstract protected RegisterAccess getRegisterAccess();
	
	private List<AbstractSvHandler> handlers = new ArrayList<>();
	private Set<SequencerListener> listeners = new HashSet<SequencerListener>();
	
	LinkedHashMap<String, List<ChangedItemValue>> history = new LinkedHashMap<>();
	private List<String> debugDepLog;
	
	public Sequencer() {
		Thread.currentThread().getId();
	}
	
	@Override
	public void requestChange(String id, String value)
			throws RequestRejectedException {
		fireRequestChange(id, value);
		
		// resolves dependencies
		
		DependencyEngine engine = getDependency();
		engine.requestChange(id, value);
		debugDepLog = engine.getDebugLog();
		
		List<String> changedIds = engine.getChangedIds();

		Set<HandlerProperty> toRunHandlers = new LinkedHashSet<>();
		for (HandlerProperty handler : getHandlerPropertyHolder().getHandlers()) {
			for (String changed : changedIds) {
				if (handler.getIds().contains(changed)) {
					toRunHandlers.add(handler);
					break;
				}
			}
		}
		
		SvHandlerModel model = new SvHandlerModel() {
			@Override
			public SvProperty getProperty(String id) {
				return getPropertiesStore().getProperty(id);
			}

			@Override
			public void requestChange(final String id, final String value) throws RequestRejectedException {
				try {
					fireChangeFromSystem(id, value);
					getDependency().requestChange(id, value);
					debugDepLog.addAll(getDependency().getDebugLog());
					
				} catch (RequestRejectedException e) {
					e.printStackTrace();
				}					
			}

			@Override
			public String getUserApplicationPath() {
				return Sequencer.this.getUserApplicationPath();
			}

			@Override
			public EasyAccessModel getEasyAccessModel() {
				return Sequencer.this.getEasyAccessModel();
			}

			@Override
			public RegisterAccess getRegisterAccess() {
				return Sequencer.this.getRegisterAccess();
			}

		};
		
		for (HandlerProperty handler : toRunHandlers) {
			new CommonSvHandler(model, handler).execute(/*getRelatedChanges(handler.getIds(), getDependency().getChagedItems()),*/
					getDependency().getChagedItems());
		}
		
	}

	public List<String> getDebugDepLog() {
		return debugDepLog;
	}
	protected void fireChangeFromSystem(String id, String value) {
		for (SequencerListener listener : this.listeners) {
			listener.onChangedBySystem(id, value);
		}
	}
	private void fireRequestChange(String id, String value) {
		for (SequencerListener listener : this.listeners) {
			listener.onChangedByUser(id, value);
		}
	}
	private List<ChangedItemValue> getRelatedChanges(List<String> ids, Map<String, List<ChangedItemValue>> map) {
		List<ChangedItemValue> ret = new ArrayList<ChangedItemValue>();
		for (String id : ids) {
			if (map.get(id) != null) {
				ret.addAll(map.get(id));
			}
		}
		return ret;
	}

	@Override
	public void addDependencyListener(DependencyListener dependencyListener) {
		getDependency().addDependencyListener(dependencyListener);
	}

	public void add(AbstractSvHandler handler) {
		this.handlers.add(handler);
	}
	
	public void addSequencerListener(SequencerListener sequencerListener) {
		this.listeners.add(sequencerListener);
	}
	
	public void removeSequencerListener(SequencerListener sequencerListener) {
		this.listeners.remove(sequencerListener);
	}
}
