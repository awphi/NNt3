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

package ph.adamw.nnt3.mazer;
import lombok.Getter;
import ph.adamw.nnt3.gui.grid.data.DataCell;
import ph.adamw.nnt3.gui.grid.data.DataGrid;

/**
 * Class to allow the Mazer and DataGrid to interface with an entity between them. Also used
 * to add ease of switching between a non-drawing and drawing entity.
 */
public class MazerEntity {
	// Stored as a just a location since it's an entity - not a state of a cell
	protected int currentCol;
	protected int currentRow;

	protected final DataGrid dataGrid;

	@Getter
	private int stationaryCount = 0;

	public MazerEntity(DataGrid dataGrid) {
		this.dataGrid = dataGrid;

		resetPosition();
	}

	public void move(Double[] values) {
		if(values.length != Mazer.STATIC_SETTINGS.getOutputs()) {
			throw new RuntimeException("Unexpected number of mazer outputs given to entity! Expected " + Mazer.STATIC_SETTINGS.getOutputs() +" but got: " + values.length + "!");
		}

		int maxIndex = 0;

		// Finds the most wanted movement
		for(int i = 0; i < values.length; i ++) {
			maxIndex = Math.max(values[maxIndex], values[i]) == values[maxIndex] ? maxIndex : i;
		}

		final EntityDirection direction = EntityDirection.get(maxIndex);

		// Checks for collision
		if(dataGrid.getDistanceToNextObstacle(currentCol, currentRow, direction) != 0) {
			currentCol += direction.getX();
			currentRow += direction.getY();
			stationaryCount = 0;
		} else {
			stationaryCount ++;
		}
	}

	public void reset() {
		resetPosition();
	}

	// reset() cannot be called in the constructor due to DrawingMazerEntity's call to it
	private void resetPosition() {
		final DataCell c = dataGrid.getStart();
		currentCol = c.getCol();
		currentRow = c.getRow();
	}
}
