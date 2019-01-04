package obsolute;

import java.util.List;

import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register.RegisterMapModel;
import jp.silverbullet.register.RegisterSpecHolder;
import jp.silverbullet.register.RegisterShortCutHolder;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.spec.SpecElement;
import jp.silverbullet.test.TestRecorder;
import jp.silverbullet.web.ui.UiLayout;
import jp.silverbullet.Sequencer;
//import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.handlers.EasyAccessInterface;
import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;

public interface BuilderModel {

	RuntimeProperty getProperty(String id);

	List<String> getAllTypes();

	List<RuntimeProperty> getAllProperties();

	PropertyHolder getPropertyHolder();

	RuntimePropertyStore getPropertyStore();

	List<RuntimeProperty> getAllProperties(PropertyType2 type);

	List<String> getSelectedIds();

	List<String> getIds(PropertyType2 type);

	void save(String folder);

//	DependencyInterface getDependency();

	void load(String folder);

	HandlerPropertyHolder getHandlerPropertyHolder();

	SvTexHolder getTexHolder();

	RegisterSpecHolder getRegisterProperty();

	SpecElement getHardwareContorlProcedure();

	SpecElement getUserStory();
	
	String getUserApplicationPath();

	Sequencer getSequencer();

	void setDeviceDriver(SvDevice deviceDriver);

	void loadDefault();

	RegisterAccess getRegisterAccess();

	void setUserPath(String userPath);

	void importFile(String filename);

//	DependencySpecHolder getDependencySpecHolder();

	RegisterShortCutHolder getRegisterShortCut();
	
	EasyAccessModel getEasyAccess();
	
	UiLayout getUiLayout();

	void changeId(String id, String value);

	void saveParameters(String filename);

	void loadParameters(String filename);

	List<String> getUiFiles();

	List<String> createUiFile(String filename);

	UiLayout switchUiFile(String filename);

	void removeUiFile(String filename);

	TestRecorder getTestRecorder();

	RegisterMapModel getRegisterMapModel();
	void switchDependency(String type);

	jp.silverbullet.dependency2.DependencySpecHolder getDependencySpecHolder2();

	EasyAccessInterface getEasyAccessInterface();
}
