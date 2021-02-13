package jp.silverbullet.dev;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.sequncer.Sequencer;
import jp.silverbullet.testspec.NetworkTestConfigurationHolder;

public class UserModel {

	private Map<String, BuilderModelImpl> builderModels = new HashMap<>(); // key = app
	private Map<String, BuilderModelImpl> runtimeModels = new HashMap<>(); // key = app
	private Automator automator = null;
	private NetworkTestConfigurationHolder networkConfiguration = new NetworkTestConfigurationHolder();
	private SbFiles sbFiles = new SbFiles();
	
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
		public String read(String device, String query) {
			return runtimeModels.get(device).getRuntimePropertyStore().get(query).getCurrentValue();
		}

		@Override
		public String message(String device, String message, String controls, String messageId) {
			try {
				ControlObject controlsObj = new ObjectMapper().readValue(controls, ControlObject.class);
				runtimeModels.get(device).message(message, controlsObj, messageId);
				//WebSocketBroadcaster.getInstance().sendMessageAsync(userid, "MESSAGE@" + device, message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return "";
		}

		@Override
		public void debug(String text) {
			System.out.println(text);
		}

		@Override
		public void closeMessage(String device, String messageId) {
			runtimeModels.get(device).closeMessage(messageId);
		}				
	};
//	private String userid;
	
	public UserModel(String userid) {
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
			BuilderModelImpl runtimeModel = sbFiles.loadAfile(userid, app, device);
			runtimeModel.setApplicationName(app, sbFiles.getStorePath(userid));
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

	public NetworkTestConfigurationHolder getNetworkTestConfig() {
		return this.networkConfiguration;
	}
}