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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import ph.adamw.amazer.Amazer;
import ph.adamw.amazer.maze.Maze;
import ph.adamw.amazer.agent.MazerAgent;
import ph.adamw.amazer.agent.MazerEvolution;
import ph.adamw.amazer.nnt3.neural.NeuralNetSettings;

public class SplashGuiController {
	@FXML
	private TextField hiddenLayersSizeTextField;
	@FXML
	private Slider hiddenLayersSizeSlider;

	@FXML
	private TextField hiddenLayersAmountTextField;
	@FXML
	private Slider hiddenLayersAmountSlider;

	@FXML
	private TextField mutationRateTextField;
	@FXML
	private Slider mutationRateSlider;

	@FXML
	private TextField generationSizeTextField;
	@FXML
	private Slider generationSizeSlider;

	@Setter
	private Maze grid;

	@FXML
	private void initialize() {
		//TODO Find a less ugly way of doing this (i.e. band them together into 1 componentpair)
		GuiUtils.bindIntSliderValueToTextField(hiddenLayersSizeSlider, hiddenLayersSizeTextField);
		GuiUtils.bindIntSliderValueToTextField(hiddenLayersAmountSlider, hiddenLayersAmountTextField);
		GuiUtils.bindIntSliderValueToTextField(mutationRateSlider, mutationRateTextField);
		GuiUtils.bindIntSliderValueToTextField(generationSizeSlider, generationSizeTextField);
	}

	@FXML
	private void confirmSettingsPressed(ActionEvent actionEvent) {
		final NeuralNetSettings settings =
				new NeuralNetSettings(
						MazerAgent.INPUTS,
						(int) hiddenLayersAmountSlider.getValue(),
						(int) hiddenLayersSizeSlider.getValue(),
						MazerAgent.OUTPUTS,
						(int) mutationRateSlider.getValue(),
						MazerAgent.ACTIVATION_FUNCTION
				);

		Amazer.loadEvolution(new MazerEvolution(grid, settings, (int) generationSizeSlider.getValue()));

		final Node source = (Node) actionEvent.getSource();
		((Stage) source.getScene().getWindow()).close();
	}
}
