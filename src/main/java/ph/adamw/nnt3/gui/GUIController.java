package ph.adamw.nnt3.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.NoArgsConstructor;
import ph.adamw.nnt3.gui.grid.GameGrid;
import ph.adamw.nnt3.gui.grid.GridState;

@NoArgsConstructor
public class GUIController {
    @FXML
    private BorderPane borderPane;

	private GameGrid grid = new GameGrid();

	private boolean isRunning = false;
	private GridState drawingState;

	@FXML
	private void initialize() {
	    grid.addCol();
	    grid.addCol();
	    grid.addRow();
	    grid.addRow();
	    borderPane.setCenter(grid);
	}
}
