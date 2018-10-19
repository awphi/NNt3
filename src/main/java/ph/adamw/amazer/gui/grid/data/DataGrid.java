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

package ph.adamw.amazer.gui.grid.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.amazer.gui.grid.GridState;
import ph.adamw.amazer.mazer.entity.EntityDirection;

import java.io.Serializable;

/**
 * Serializable data class to store information required to load, save and runOneGeneration game grids. To display a DataGrid
 * in a GUI there also must be a LiveGrid to operate on.
 */
@Getter
@AllArgsConstructor
public class DataGrid implements Serializable {
	private final int width;
	private final int height;

	private final DataCell[][] cells;

	private final DataCell start;
	private final DataCell goal;

	public int getDistanceToNextObstacle(int col, int row, EntityDirection dir) {
		// Finds out if the movement is left/right or up/down
		final boolean xBased = dir.getX() != 0;

		int x = 0;
		int y = 0;
		boolean xBoundsReached = false;
		boolean yBoundsReached = false;

		while(!(xBoundsReached || yBoundsReached || cells[col + x][row + y].getState() == GridState.WALL)) {
			if(xBased) {
				x += dir.getX();
			} else {
				y += dir.getY();
			}

			xBoundsReached = col + x < 0 || col + x > getWidth() - 1;
			yBoundsReached = row + y < 0 || row + y > getHeight() - 1;
		}

		switch (dir) {
			case UP: return y + 1;
			case DOWN: return y - 1;
			case LEFT: return x + 1;
			case RIGHT: return x - 1;
		}

		return 0;
	}
}
