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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import lombok.NoArgsConstructor;
import ph.adamw.amazer.FileUtils;
import ph.adamw.amazer.mazer.Mazer;
import ph.adamw.amazer.mazer.MazerEvolution;
import ph.adamw.amazer.nnt3.neural.NeuralNetSettings;
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
	// -- Number Fields --
	@FXML
	private TextField hiddenLayersSizeField;
	@FXML
	private TextField hiddenLayersAmountField;
	@FXML
	private TextField generationSizeField;
    @FXML
	private TextField gridRowsField;
    @FXML
	private TextField gridColsField;
	// --

	@FXML
	private VBox evolutionControlsBox;

	@FXML
	private HBox genZeroConfigBox;

	@FXML
	private Button nextGenButton;

	@FXML
	private BorderPane borderPane;

	private final LiveGrid grid = new LiveGrid(6, 6);

	private static final DirectoryChooser directorChooser = new DirectoryChooser();

	private MazerEvolution mainEvo;
	private Mazer playingMazer;

	@FXML
	private void initialize() {
	    borderPane.setCenter(grid);

		gridColsField.setText(grid.getCols() + "");
		gridRowsField.setText(grid.getRows() + "");

	    final TextField[] numberFields = {hiddenLayersSizeField, hiddenLayersAmountField, generationSizeField, gridRowsField, gridColsField};

		for (TextField numberField : numberFields) {
			numberField.setTextFormatter(new TextFormatter<>(GuiUtils.NUMBER_FIELD_OPERATOR));
		}

		skipGenerationsSlider.valueProperty().addListener(e ->
				skipGenerationsButton.textProperty().setValue("Skip " + (int) skipGenerationsSlider.getValue() + " Generations"));
	}

	private void runGenerations(int amount) {
		if(mainEvo != null) {
			evolutionControlsBox.setDisable(true);

			Task<Boolean> task = new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					mainEvo.run(amount, true);
					return true;
				}
			};

			task.setOnSucceeded(e -> {
				evolutionControlsBox.setDisable(false);
				nextGenButton.setText("Run Generation " + mainEvo.getGenerationCount());

				//TODO (FUN) store the evolutionary path of each winner so we can see a sort of family tree, this could store just names or the Mazers themselves
				mazerListView.getItems().clear();

				for(Mazer i : mainEvo.getGeneration().getSortedCopyOfMembers()) {
					mazerListView.getItems().add(mazerListView.getItems().size(), new MazerListEntry(i));
				}
			});

			new Thread(task).start();
		} else {
			if (!grid.isValid()) {
				ErrorController.openError("Grid Invalid", "The grid is currently invalid. It must contain a start and a goal to be valid.");
				return;
			}

			final Integer hiddenAmount = GuiUtils.getBoundedIntFromField(hiddenLayersAmountField, 10, 1);
			final Integer hiddenSize = GuiUtils.getBoundedIntFromField(hiddenLayersSizeField, 10, 1);
			final Integer genSize = GuiUtils.getBoundedIntFromField(generationSizeField, 1000, 1);
			//TODO add mutation rate to these options

			if (GuiUtils.anyObjectNull(hiddenAmount, hiddenSize, genSize)) {
				ErrorController.openError("Missing Parameters", "All parameters must be filled in order to run a generation.");
				return;
			}

			grid.setEditable(false);
			evolutionControlsBox.getChildren().remove(genZeroConfigBox);
			gridSizeControlsBox.setDisable(true);

			final NeuralNetSettings s = Mazer.STATIC_SETTINGS;
			final NeuralNetSettings settings =
					new NeuralNetSettings(s.getInputs(), hiddenAmount, hiddenSize, s.getOutputs(), s.getMutationRate(), s.getActivationFunction());

			mainEvo = new MazerEvolution(grid.asDataGrid(), settings, genSize);

			skipGenerationsButton.setDisable(false);
			skipGenerationsSlider.setDisable(false);
			runGenerations(1);
		}
	}

	public void onUpdateGridPressed(ActionEvent actionEvent) {
		if(!grid.isEditable()) {
			ErrorController.openError("Grid Locked", "The grid is currently edit-locked. Please reset the program to unlock it.");
			return;
		}

		//TODO remove ugly and weird dependencies on bounding integers in text fields and switch to sliders/dropdowns or something
		final Integer c = GuiUtils.getBoundedIntFromField(gridColsField, 48, 6);
		final Integer r = GuiUtils.getBoundedIntFromField(gridRowsField, 48, 6);

		if(GuiUtils.anyObjectNull(c, r)) {
			ErrorController.openError("Missing Parameters", "All parameters must be filled in order to change the grid size.");
			return;
		}

		grid.setSize(c, r);

		gridColsField.setText(c.toString());
		gridRowsField.setText(r.toString());
	}

	public void onMazerListClicked(MouseEvent mouseEvent) {
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

	public void onNextGenPressed(ActionEvent actionEvent) {
		runGenerations(1);
	}

	public void onSkipGensPressed(ActionEvent actionEvent) {
		runGenerations((int) skipGenerationsSlider.getValue());
	}

	public void onExportMazePressed(ActionEvent actionEvent) {
		//TODO use file saver window to decide where to save to
		FileUtils.writeDataGrid(new File("D:/"), "super", grid.asDataGrid());
	}

	public void onImportMazePressed(ActionEvent actionEvent) {
		//TODO use a file picker here to decide where to read from
		grid.loadDataGrid(FileUtils.readDataGrid(new File("D:/super.maz")));
	}
}
