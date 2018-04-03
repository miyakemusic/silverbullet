package jp.silverbullet.dependency.speceditor3.ui;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import jp.silverbullet.dependency.speceditor3.DNode;
import jp.silverbullet.dependency.speceditor3.GlobalMap;

public class GlobalMapUi extends AnchorPane {

	public GlobalMapUi(GlobalMap globalMap) {
		for (int level = 0; level < globalMap.getLevelCount(); level++) {
			List<DNode> nodes = globalMap.getNodes(level);
			for (int i = 0; i < nodes.size(); i++) {
				DNode node = nodes.get(i);
				Button button = new Button(node.getId());
				this.getChildren().add(button);
				button.setLayoutX(level * 200);
				button.setLayoutY(i * 30);
			}
		}
	}
}
