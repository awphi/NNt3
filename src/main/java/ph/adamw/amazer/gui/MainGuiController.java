/*
 * MIT License
 *
 * Copyright (c) 2018 awphi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ph.adamw.amazer.gui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import ph.adamw.amazer.Amazer;
import ph.adamw.amazer.FileUtils;
import ph.adamw.amazer.gui.grid.data.DataGrid;
import ph.adamw.amazer.agent.MazerAgent;
import ph.adamw.amazer.gui.grid.LiveGrid;
import ph.adamw.amazer.agent.MazerEvolution;

import java.io.File;
import java.util.List;

@NoArgsConstructor
public class MainGuiController {
	@FXML
	private Slider skipGenerationsSlider;

	@FXML
	private VBox gridSizeControlsBox;

	@FXML
	private Button skipGenerationsButton;

	@FXML
	private ListView<MazerListEntry> mazerListView;

	// -- Number Fields --;
    @FXML
	private TextField gridRowsField;
    @FXML
	private TextField gridColsField;
	// --

	@FXML
	private VBox evolutionControlsBox;

	@FXML
	private Button nextGenButton;

	@FXML
	private BorderPane borderPane;

	@FXML
	private MenuBar menuBar;

	private final LiveGrid grid = new LiveGrid(6, 6);

	@FXML
	private Slider gridRowsSlider;

	@FXML
	private Slider gridColsSlider;

	@FXML
	private void initialize() {
	    borderPane.setCenter(grid);

		final String os = System.getProperty("os.name");
		if (os != null && os.startsWith("Mac")) {
			menuBar.useSystemMenuBarProperty().set(true);
		}

		skipGenerationsSlider.valueProperty().addListener(e ->
				skipGenerationsButton.textProperty().setValue("Skip " + (int) skipGenerationsSlider.getValue() + " Generations"));
		
		GuiUtils.bindIntSliderValueToTextField(gridRowsSlider, gridRowsField);
		GuiUtils.bindIntSliderValueToTextField(gridColsSlider, gridColsField);

		gridRowsSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateSize());
		gridColsSlider.valueProperty().addListener((observable, oldValue, newValue) -> updateSize());
	}

	@SuppressWarnings("ConstantConditions")
	private void runGenerations(int amount) {
		// Have we loaded one yet? I.e. via import
		if(Amazer.getEvolution() == null) {
			final DataGrid dg = grid.asDataGrid();
			if (!grid.isValid()) {
				GuiUtils.alert(Alert.AlertType.ERROR,"The current grid form is invalid.", "The grid must contain a start node, a goal node and be possible to solve.");
				return;
			}

			// Opens a menu to create the evolution
			Amazer.openSplash(grid.asDataGrid());
			return;
		}

		gridSizeControlsBox.setDisable(true);
		evolutionControlsBox.setDisable(true);

		Task<Boolean> task = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				Amazer.getEvolution().run(amount, true);
				return true;
			}
		};

		task.setOnSucceeded(e -> {
			evolutionControlsBox.setDisable(false);

			skipGenerationsButton.setDisable(false);
			skipGenerationsSlider.setDisable(false);

			nextGenButton.setText("Run Generation " + Amazer.getEvolution().getGenerationCount());

			occupyGenerationList(Amazer.getEvolution().getGeneration().getSortedCopyOfMembers());
		});

		new Thread(task).start();
	}

	public void loadGrid(DataGrid gr) {
		grid.loadDataGrid(gr);
	}

	public void occupyGenerationList(List<MazerAgent> nn) {
		//TODO (FUN) store the evolutionary path of each winner so we can see a sort of family tree, this could store just names or the Mazers themselves
		// this would exponentially increase memory usage though
        mazerListView.getItems().clear();

		for(MazerAgent i : nn) {
			mazerListView.getItems().add(mazerListView.getItems().size(), new MazerListEntry(i));
		}
	}

	private void updateSize() {
		if(!grid.isEditable()) {
			GuiUtils.alert(Alert.AlertType.WARNING,"The grid is currently locked", "The grid is currently edit-locked. Please reset the program to unlock it.");
			return;
		}

		grid.setSize((int) gridColsSlider.getValue(), (int) gridRowsSlider.getValue());
	}

	@FXML
	private void onMazerListClicked(MouseEvent mouseEvent) {
		final MazerListEntry e = mazerListView.getSelectionModel().getSelectedItem();

		if(e == null) {
			return;
		}

		if(grid.isReadyToDrawPath()) {
			grid.drawAgentPath(e.getAgent());
		}
	}

	public void onNextGenPressed(ActionEvent actionEvent) {
		runGenerations(1);
	}

	@FXML
	private void onSkipGensPressed(ActionEvent actionEvent) {
		runGenerations((int) skipGenerationsSlider.getValue());
	}

	@FXML
	private void onExportMazePressed(ActionEvent actionEvent) {
		//Show save file dialog
		final File file = FileUtils.getMazeChooser().showSaveDialog(Amazer.getStage());

		if(file == null) {
			return;
		}

		if(!FileUtils.writeObjectToFile(file, grid.asDataGrid())) {
			GuiUtils.alert(Alert.AlertType.ERROR,"Failed to export maze.", "Please ensure the maze contains a start and goal node and that a_mazer has appropriate permissions to save files.");
		}
	}

	@FXML
	private void onImportMazePressed(ActionEvent actionEvent) {
		final File file = FileUtils.getMazeChooser().showOpenDialog(Amazer.getStage());

		if(file == null) {
			return;
		}

		final DataGrid dg = FileUtils.readObjectFromFile(file);

		if(dg != null) {
			grid.loadDataGrid(dg);
		} else {
			GuiUtils.alert(Alert.AlertType.ERROR,"Failed to import maze.", "The maze file may have been corrupted or a_mazer does not have the appropriate read permissions to access the given file.");
		}
	}

	@FXML
	private void onAboutMenuPressed(ActionEvent actionEvent) {
		//TODO provide some about info a little window here
	}

	@FXML
	private void onLoadEvolutionPressed(ActionEvent actionEvent) {
		final File file = FileUtils.getEvolutionChooser().showOpenDialog(Amazer.getStage());

		if(file == null) {
			return;
		}

		final MazerEvolution evo = FileUtils.readObjectFromFile(file);

		if(evo != null) {
			Amazer.loadEvolution(evo);
		} else {
			GuiUtils.alert(Alert.AlertType.ERROR,"Failed to import evolution.", "The file may have been corrupted or a_mazer does not have the appropriate write permissions to access the given file.");
		}
	}

	@FXML
	private void onExportEvolutionPressed(ActionEvent actionEvent) {
		if(Amazer.getEvolution() == null) {
			GuiUtils.alert(Alert.AlertType.ERROR,"Cannot export evolution.", "Please create an evolution before attempting to export.");
			return;
		}

		final File file = FileUtils.getEvolutionChooser().showSaveDialog(Amazer.getStage());

		if(file == null) {
			return;
		}

		if(!FileUtils.writeObjectToFile(file, Amazer.getEvolution())) {
			GuiUtils.alert(Alert.AlertType.ERROR,"Failed to export evolution.", "Please ensure that a_mazer has appropriate permissions to save files.");
		}
	}

	public void setGridEditable(boolean b) {
		grid.setEditable(b);
	}
}
