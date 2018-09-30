package jp.silverbullet;

import jp.silverbullet.register.RegisterMapModel;

public class StaticInstances {
	public static final String DESIGNER_TMP = "sv_tmp";
	
	private static RegisterMapModel registerMapModel = null;
	
	public static BuilderModel getBuilderModel() {
		return BuilderModelImpl.getInstance();
	}

	public static RegisterMapModel getRegisterMapModel() {
		if (registerMapModel == null) {
			registerMapModel = new RegisterMapModel(getBuilderModel());
			BuilderModelImpl.getInstance().setDeviceDriver(registerMapModel);
		}
		return registerMapModel;
	}

}
