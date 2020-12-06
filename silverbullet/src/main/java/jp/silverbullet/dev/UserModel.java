package jp.silverbullet.dev;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.sequncer.Sequencer;

public class UserModel {

	private Map<String, BuilderModelImpl> builderModels = new HashMap<>();
	private Map<String, BuilderModelImpl> runtimeModels = new HashMap<>();
	private Automator automator = null;
	
	private AutomatorInterface automaterInterface = new AutomatorInterface() {

		@Override
		public void write(String device, String id, String value) {
			Sequencer sequencer = runtimeModels.get(device).getSequencer();
			try {
				sequencer.requestChange(id, value);
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String read(String addr, String query) {
			//return runtimeModels
			return null;
		}				
	};
	
	public UserModel() {
		automator = new Automator(automaterInterface);
	}
	
	public BuilderModelImpl getBuilderModel(String app) {
		return builderModels.get(app);
	}

	public Map<String, BuilderModelImpl> getBuilderModels() {
		return builderModels;
	}

	public Map<String, BuilderModelImpl> getRuntimeModels() {
		return runtimeModels;
	}

	public void reloadRuntime() {
		String folder = SbFiles.TMP_FOLDER;
		for (BuilderModelImpl model : runtimeModels.values()) {
			model.load(folder);
		}
	}

	public BuilderModelImpl addRuntimeModel(String app, String device, String userid) {
		try {
			BuilderModelImpl runtimeModel = SbFiles.loadAfile(userid, app, device);
			runtimeModel.setApplicationName(app);
			runtimeModels.put(device, runtimeModel);
			
			runtimeModel.getSequencer().addSequencerListener(automator.createListener(device));
			System.out.println("Runtime Model was generated. :" + 
					device + " @" + Thread.currentThread().getName() + ":" + runtimeModels.hashCode());
			return runtimeModel;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addDevModel(BuilderModelImpl model, String app) {
		this.builderModels.put(app, model);
	}	
	

	public Automator getAutomator() {
		return this.automator;
	}

	public void removeDevice(String device) {
		runtimeModels.remove(device);
		this.automator.removeDevice(device);
	}

	public BuilderModelImpl getModel(String app, String device) {
		if (!runtimeModels.containsKey(device)) {
			return this.getBuilderModel(app);
		}
		else {
			BuilderModelImpl model = runtimeModels.get(device);
			return model;
		}
	}
}