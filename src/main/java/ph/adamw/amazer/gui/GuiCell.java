/*
 * MIT License
 *
 * Copyright (c) 2019 awphi
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

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import lombok.Getter;
import static ph.adamw.amazer.maze.CellState.EMPTY;
import static ph.adamw.amazer.maze.CellState.WALL;
import ph.adamw.amazer.maze.Cell;
import ph.adamw.amazer.maze.CellState;

public class GuiCell extends BorderPane {
	@Getter
	private final Cell cell;

	private static final Insets INSETS_2 = new Insets(2, 2, 2, 2);

	GuiCell(Cell cell) {
		this.cell = cell;

		try {
			drawState(cell.getState());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void drawState(CellState state) {
		setBackground(new Background(new BackgroundFill(Paint.valueOf(state.getColor().toString()), CornerRadii.EMPTY, INSETS_2)));
		setCenter(state.getText());
	}

	void setState(CellState state) {
		cell.setState(state);
		drawState(state);
	}

	void switchState() {
		switch (cell.getState()) {
			case WALL: setState(EMPTY); break;
			case EMPTY: setState(WALL); break;
		}
	}
}
