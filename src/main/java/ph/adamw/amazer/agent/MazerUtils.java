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

package ph.adamw.amazer.agent;

import ph.adamw.amazer.maze.Cell;

class MazerUtils {
	static double distanceBetween(int col, int row, Cell other) {
		final double colDiff = Math.pow((double) col - (double) other.getCol(), 2);
		final double rowDiff = Math.pow((double) row - (double) other.getRow(), 2);

		return Math.sqrt(colDiff + rowDiff);
	}

	static double bearing(int col, int row, Cell other) {
		final double deltaY = (row - other.getRow());
		final double deltaX = (other.getCol() - col);
		return Math.atan2(deltaX, deltaY);
	}
}
