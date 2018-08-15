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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;
import ph.adamw.amazer.Amazer;

import java.io.IOException;

@NoArgsConstructor
public class ErrorGuiController {
	@FXML
	private Label errorTitle;

	@FXML
	private Label errorText;

	private static String nextErrorMsg;
	private static String nextErrorTitle;

	private static Stage currentError;

	static void openError(String title, String description) {
		if(currentError != null) {
			currentError.close();
		}

		nextErrorMsg = description;
		nextErrorTitle = title;
		final Parent root;

		try {
			root = FXMLLoader.load(ErrorGuiController.class.getResource("error.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		currentError = new Stage();
		currentError.setTitle("a_mazer - Error Information");
		currentError.initModality(Modality.WINDOW_MODAL);
		currentError.initOwner(Amazer.getScene().getWindow());
		currentError.setScene(new Scene(root));
		currentError.setResizable(false);
		currentError.show();
	}

	@FXML
	private void initialize() {
		errorTitle.setText(nextErrorTitle);
		errorText.setText(nextErrorMsg);
	}

	@FXML
	private void continueButtonPressed(ActionEvent actionEvent) {
		currentError.close();
	}
}
