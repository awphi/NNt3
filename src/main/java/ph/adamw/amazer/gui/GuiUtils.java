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

import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.Arrays;
import java.util.function.UnaryOperator;

class GuiUtils {
	static final UnaryOperator<TextFormatter.Change> NUMBER_FIELD_OPERATOR = change -> {
		String text = change.getText();

		if (text.matches("[0-9]*")) {
			return change;
		}

		return null;
	};

	private static Integer getNumberFieldValue(TextField field) {
		if(field.getTextFormatter().getFilter() == NUMBER_FIELD_OPERATOR && !field.getText().isEmpty()) {
			return Integer.parseInt(field.getText());
		}

		return null;
	}

	static boolean anyObjectNull(Object... objs) {
		return Arrays.asList(objs).contains(null);
	}

	static Integer getBoundedIntFromField(TextField field, int upper, int lower) {
		Integer i = getNumberFieldValue(field);

		if(i != null) {
			return Math.min(Math.max(i, lower), upper);
		}

		return null;
	}

	static void bindIntSliderValueToTextField(Slider s, TextField f) {
		f.setText((String.valueOf((int) s.getValue())));

		s.valueProperty().addListener((observable, oldValue, newValue)
				-> f.setText((String.valueOf((int) s.getValue()))));
	}
}
