package jp.silverbullet;

import java.util.List;

import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.register.RegisterProperty;
import jp.silverbullet.register.RegisterShortCutHolder;
import jp.silverbullet.register.SvRegister;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.spec.SpecElement;
import jp.silverbullet.web.ui.UiLayout;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.dependency.DependencySpecHolder;
import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;

public interface BuilderModel {

	SvProperty getProperty(String id);

	List<String> getAllTypes();

	List<SvProperty> getAllProperties();

	PropertyHolder getPropertyHolder();

	SvPropertyStore getPropertyStore();

	List<SvProperty> getAllProperties(String type);

	List<String> getSelectedIds();

	List<String> getIds(String type);

	void save(String folder);

	DependencyInterface getDependency();

	void load(String folder);

	HandlerPropertyHolder getHandlerPropertyHolder();

	SvTexHolder getTexHolder();

	RegisterProperty getRegisterProperty();

	SpecElement getHardwareContorlProcedure();

	SpecElement getUserStory();
	
	String getUserApplicationPath();

	Sequencer getSequencer();

	void setDeviceDriver(SvDevice deviceDriver);

	void loadDefault();

	RegisterAccess getRegisterAccess();

	void setUserPath(String userPath);

	void importFile(String filename);

	DependencySpecHolder getDependencySpecHolder();

	RegisterShortCutHolder getRegisterShortCut();
	
	EasyAccessModel getEasyAccess();
	
	UiLayout getUiLayout();
}
