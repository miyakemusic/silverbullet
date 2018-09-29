package jp.silverbullet;

import jp.silverbullet.register.RegisterMapModel;

public class StaticInstances {
	public static final String DESIGNER_TMP = "sv_tmp";
	
	public static BuilderModel getBuilderModel() {
		return BuilderModelImpl.getInstance();
	}

	public static RegisterMapModel getRegisterMapModel() {
		return new RegisterMapModel(getBuilderModel());
	}

}
