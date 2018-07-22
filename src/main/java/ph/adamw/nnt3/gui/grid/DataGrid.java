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

package ph.adamw.nnt3.gui.grid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.nnt3.brain.EntityDirection;

/**
 * Data class to store information required to load, save and runOneGeneration game grids. To display a DataGrid
 * in a GUI there also must be a LiveGrid to operate on.
 */
@Getter
@AllArgsConstructor
public class DataGrid {
	private final int width;
	private final int height;

	private final Cell[][] cells;

	private final Cell start;
	private final Cell goal;

	public int getDistanceToNextObstacle(Cell from, EntityDirection dir) {
		// Finds out if the movement is left/right or up/down
		final boolean xBased = dir.getX() != 0;

		final int startX = from.getCol();
		final int startY = from.getRow();

		boolean hit = false;
		int x = 0;
		int y = 0;


		while(!hit) {
			hit = cells[startX + x][startY + y].getState() != GridState.EMPTY;

			if(xBased) {
				x += dir.getX();
			} else {
				y += dir.getY();
			}
		}

		if(xBased) {
			return x - 1;
		} else {
			return y - 1;
		}
	}
}
