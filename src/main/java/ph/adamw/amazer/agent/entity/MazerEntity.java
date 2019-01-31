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

package ph.adamw.amazer.agent.entity;
import lombok.Getter;
import ph.adamw.amazer.gui.grid.data.DataCell;
import ph.adamw.amazer.gui.grid.data.DataGrid;
import ph.adamw.amazer.agent.MazerAgent;

/*
 * Class to allow the MazerAgent and DataGrid to interface with an entity between them. Also used
 * to add ease of switching between a non-drawing and drawing entity.
 */
public class MazerEntity {
	// Stored as a just a location since it's an entity - not a state of a cell
	@Getter
	protected int currentCol;

	@Getter
	protected int currentRow;

	@Getter
	protected final DataGrid dataGrid;

	@Getter
	private int stationaryCount = 0;

	public MazerEntity(DataGrid dataGrid) {
		this.dataGrid = dataGrid;

		resetPosition();
	}

	public void move(Double[] values) {
		if (values.length != MazerAgent.OUTPUTS) {
			throw new RuntimeException("Unexpected number of agent outputs given to entity! Expected " + MazerAgent.OUTPUTS + " but got: " + values.length + "!");
		}

		int maxIndex = 0;

		// Finds the 'most wanted' movement i.e. the direction with the highest weight
		for (int i = 0; i < values.length; i++) {
			maxIndex = Math.max(values[maxIndex], values[i]) == values[maxIndex] ? maxIndex : i;
		}

		final EntityDirection direction = EntityDirection.get(maxIndex);

		// Collision detection
		if (dataGrid.getDistanceToNextObstacle(currentCol, currentRow, direction) != 0) {
			currentCol += direction.getX();
			currentRow += direction.getY();

			stationaryCount = 0;
		} else {
			stationaryCount++;
		}
	}

	public void reset() {
		resetPosition();
	}

	public int getInterval() {
		return 0;
	}

	private void resetPosition() {
		final DataCell c = dataGrid.getStart();
		currentCol = c.getCol();
		currentRow = c.getRow();
	}
}
