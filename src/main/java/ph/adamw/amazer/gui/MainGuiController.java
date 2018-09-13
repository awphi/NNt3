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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.NoArgsConstructor;
import ph.adamw.amazer.Amazer;
import ph.adamw.amazer.FileUtils;
import ph.adamw.amazer.gui.grid.data.DataGrid;
import ph.adamw.amazer.mazer.Mazer;
import ph.adamw.amazer.gui.grid.LiveGrid;

import java.io.File;

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

	private final LiveGrid grid = new LiveGrid(6, 6);

	private Mazer playingMazer;

	@FXML
	private void initialize() {
	    borderPane.setCenter(grid);

		gridColsField.setText(String.valueOf(grid.getCols()));
		gridRowsField.setText(String.valueOf(grid.getRows()));

		gridColsField.setTextFormatter(new TextFormatter<>(GuiUtils.NUMBER_FIELD_OPERATOR));
		gridRowsField.setTextFormatter(new TextFormatter<>(GuiUtils.NUMBER_FIELD_OPERATOR));

		skipGenerationsSlider.valueProperty().addListener(e ->
				skipGenerationsButton.textProperty().setValue("Skip " + (int) skipGenerationsSlider.getValue() + " Generations"));
	}

	@SuppressWarnings("ConstantConditions")
	private void runGenerations(int amount) {
		if(Amazer.getEvolution() == null) {
			if (!grid.isValid()) {
				ErrorGuiController.openError("The current grid form is invalid.", "The grid must contain a start and goal node in order to be solved.");
				return;
			}

			// Opens a menu to create the evolution
			Amazer.openSplash(grid.asDataGrid());
			return;
		}

		grid.setEditable(false);
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

			//TODO (FUN) store the evolutionary path of each winner so we can see a sort of family tree, this could store just names or the Mazers themselves
			mazerListView.getItems().clear();

			for(Mazer i : Amazer.getEvolution().getGeneration().getSortedCopyOfMembers()) {
				mazerListView.getItems().add(mazerListView.getItems().size(), new MazerListEntry(i));
			}
		});

		new Thread(task).start();
	}

	@FXML
	private void onUpdateGridPressed(ActionEvent actionEvent) {
		if(!grid.isEditable()) {
			ErrorGuiController.openError("The grid is currently locked", "The grid is currently edit-locked. Please reset the program to unlock it.");
			return;
		}

		//TODO remove ugly and weird dependencies on bounding integers in text fields and switch to sliders/dropdowns or something
		final Integer c = GuiUtils.getBoundedIntFromField(gridColsField, 48, 6);
		final Integer r = GuiUtils.getBoundedIntFromField(gridRowsField, 48, 6);

		if(GuiUtils.anyObjectNull(c, r)) {
			ErrorGuiController.openError("There are empty parameters", "A height and width must be given in order to change the grid size.");
			return;
		}

		grid.setSize(c, r);

		gridColsField.setText(c.toString());
		gridRowsField.setText(r.toString());
	}

	@FXML
	private void onMazerListClicked(MouseEvent mouseEvent) {
		final MazerListEntry e = mazerListView.getSelectionModel().getSelectedItem();

		if(e == null) {
			return;
		}

		if(playingMazer != null) {
			playingMazer.kill();
		}

		playingMazer = e.getMazer();
		playingMazer.playOnGrid(grid, 100);
	}

	@FXML
	private void onNextGenPressed(ActionEvent actionEvent) {
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
			ErrorGuiController.openError("Failed to export maze.", "Please ensure the maze contains a start and goal node and that a_mazer has appropriate permissions to save files.");
		}
	}

	@FXML
	private void onImportMazePressed(ActionEvent actionEvent) {
		final File file = FileUtils.getMazeChooser().showOpenDialog(Amazer.getStage());

		if(file == null || !file.exists()) {
			return;
		}

		final DataGrid dg = FileUtils.readObjectFromFile(file);

		if(dg != null) {
			grid.loadDataGrid(dg);
		} else {
			ErrorGuiController.openError("Failed to import maze.", "The maze file may have been corrupted or a_mazer does not have the appropriate write permissions to access the given file.");
		}
	}

	@FXML
	private void onAboutMenuPressed(ActionEvent actionEvent) {
		//TODO provide some about info a little window here
	}

	@FXML
	private void onLoadEvolutionPressed(ActionEvent actionEvent) {

	}

	@FXML
	private void onExportEvolutionPressed(ActionEvent actionEvent) {

	}
}