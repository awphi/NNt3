package ph.adamw.nnt3.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import lombok.NoArgsConstructor;
import ph.adamw.nnt3.gui.grid.GridState;

@NoArgsConstructor
public class GUIController {
	@FXML
	private GridPane grid;

	private boolean isRunning = false;
	private GridState drawingState;

	@FXML
	private void initialize() {
		RowConstraints n = new RowConstraints();

		n.setPrefHeight(30);
		n.setMinHeight(10);
		n.setVgrow(Priority.ALWAYS);

		grid.getRowConstraints().add(n);
	}
}
