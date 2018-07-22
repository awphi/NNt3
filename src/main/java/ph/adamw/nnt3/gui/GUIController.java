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

package ph.adamw.nnt3.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import lombok.NoArgsConstructor;
import ph.adamw.nnt3.brain.Mazer;
import ph.adamw.nnt3.brain.MazerEvolution;
import ph.adamw.nnt3.evolution.neural.NeuralNetSettings;
import ph.adamw.nnt3.gui.grid.LiveGrid;

import java.util.function.UnaryOperator;

@NoArgsConstructor
public class GUIController {
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
	private BorderPane borderPane;

	private final LiveGrid grid = new LiveGrid(32, 32);

	private static MazerEvolution mainEvo;

	private static final UnaryOperator<TextFormatter.Change> NUMBER_FIELD_OPERATOR = change -> {
		String text = change.getText();

		if (text.matches("[0-9]*")) {
			return change;
		}

		return null;
	};

	@FXML
	private void initialize() {
	    borderPane.setCenter(grid);

		gridColsField.setText(grid.getCols() + "");
		gridRowsField.setText(grid.getRows() + "");

	    final TextField[] numberFields = {hiddenLayersSizeField, hiddenLayersAmountField, generationSizeField, gridRowsField, gridColsField};

		for (TextField numberField : numberFields) {
			numberField.setTextFormatter(new TextFormatter<>(NUMBER_FIELD_OPERATOR));
		}
	}

	public void onUpdateGridPress(ActionEvent actionEvent) {
		final String c = gridColsField.getText();
		final String r = gridRowsField.getText();

		if(c.isEmpty() || r.isEmpty()) {
			return;
		}

		int cols = Integer.parseInt(c);
		int rows = Integer.parseInt(r);

		// Sets bounds of cols and rows at 6 (inclusive) and 48 (inclusive)
		grid.setSize(Math.min(Math.max(cols, 6), 48), Math.min(Math.max(rows, 6), 48));

		gridColsField.setText(Math.min(Math.max(cols, 6), 48) + "");
		gridRowsField.setText(Math.min(Math.max(rows, 6), 48) + "");
	}

	@SuppressWarnings("SynchronizeOnNonFinalField")
	public void onStartPress(ActionEvent actionEvent) {
		grid.setEditable(false);

		// Read in the properties from the GUI to apply to the evo
		//TODO add validation (empty fields etc) to these inputs + move this into a nice method
		final int amount = Math.min(Math.max(Integer.parseInt(hiddenLayersAmountField.getText()), 1), 5);
		final int size = Math.min(Math.max(Integer.parseInt(hiddenLayersSizeField.getText()), 1), 5);
		final int genSize = Math.min(Math.max(Integer.parseInt(generationSizeField.getText()), 1), 24);

		final NeuralNetSettings s = Mazer.getStaticSettings();
		final NeuralNetSettings settings =
				new NeuralNetSettings(s.getInputs(), amount, size, s.getOutputs(), s.getMutationRate(), s.getActivationFunction());

		mainEvo = new MazerEvolution(grid.getDataGrid(), settings);
		mainEvo.setGenSize(genSize);

		mainEvo.start(1);

		synchronized (mainEvo) {
			if(!mainEvo.isDone()) {
				try {
					mainEvo.wait();
				} catch (InterruptedException ignored) {}
			}
		}

		System.out.println(mainEvo.getHighestPerformer().getFitness());
	}
}
