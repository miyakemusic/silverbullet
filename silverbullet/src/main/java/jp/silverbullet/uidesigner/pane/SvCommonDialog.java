package jp.silverbullet.uidesigner.pane;

import javafx.scene.layout.Pane;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.SvProperty;

public class SvCommonDialog {

	public static String showTenkey(SvProperty prop, Pane parent) throws Exception {
		MyDialogFx dlg = new MyDialogFx(prop.getTitle(), parent);
		dlg.setWidth(300);
		dlg.setHeight(300);
		TenKeyFx pane = new TenKeyFx(prop);
		dlg.showModal(pane);
		if (!dlg.isOkClicked()) {
			throw new Exception();
		}
		return pane.getValue();
	}
}
