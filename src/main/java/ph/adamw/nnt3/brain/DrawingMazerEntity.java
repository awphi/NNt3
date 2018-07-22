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

package ph.adamw.nnt3.brain;

import ph.adamw.nnt3.gui.grid.DataGrid;
import ph.adamw.nnt3.gui.grid.LiveGrid;

/**
 * Overrides MazerEntity's moving method to also draw to a live game grid after moving, used when a single network
 * is ran in preview mode.
 */
public class DrawingMazerEntity extends MazerEntity {
	private final LiveGrid liveGrid;

	public DrawingMazerEntity(DataGrid dataGrid, LiveGrid liveGrid) {
		super(dataGrid);
		this.liveGrid = liveGrid;
		//TODO draw character here @ datagrid pos
	}

	@Override
	public void move(int[] ints) {
		//TODO remove old drawn character here @ datagrid pos
		super.move(ints);
		//TODO draw character here @ datagrid pos
	}
}
