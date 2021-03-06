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

import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

class GuiUtils {
	static void bindIntSliderValueToTextField(Slider s, TextField f) {
		f.setEditable(false);
		f.setText((String.valueOf((int) s.getValue())));

		s.valueProperty().addListener((observable, oldValue, newValue)
				-> f.setText((String.valueOf((int) s.getValue()))));
	}

	private static String getNameOfAlert(Alert.AlertType type) {
		switch (type) {
			case INFORMATION: return "Information";
			case ERROR: return "Error";
			case WARNING: return "Warning";
			case CONFIRMATION: return "Confirmation";
		}

		return "";
	}

	static void alert(Alert.AlertType type, String header, String context) {
		final Alert alert = new Alert(type);
		final String name = getNameOfAlert(type);
		alert.setTitle("a_mazer" + (name.length() > 0 ? " - " + name : ""));
		alert.setHeaderText(header);
		alert.setContentText(context);

		alert.showAndWait();
	}
}
