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

import javafx.application.Platform;
import lombok.Getter;
import ph.adamw.nnt3.gui.grid.GridState;
import ph.adamw.nnt3.gui.grid.data.DataGrid;
import ph.adamw.nnt3.gui.grid.LiveGrid;

/**
 * Overrides MazerEntity's moving method to also draw to a live game grid after moving, used when a single network
 * is ran in preview mode.
 */
public class DrawingMazerEntity extends MazerEntity {
	private final LiveGrid liveGrid;

	@Getter
	private final int interval;

	public DrawingMazerEntity(DataGrid dataGrid, LiveGrid liveGrid, int interval) {
		super(dataGrid);
		this.liveGrid = liveGrid;
		this.interval = interval;

		drawState(GridState.CHARACTER);
	}

	@Override
	public void move(Double[] vals) {
		drawState(getStateBehindCurrent());
		super.move(vals);
		drawState(GridState.CHARACTER);
	}

	private void drawState(GridState state) {
		final int x = currentCol;
		final int y = currentRow;

		Platform.runLater(() -> liveGrid.drawStateAt(x, y, state));
	}

	@Override
	public void reset() {
		// Not wrapped in a runLater as it's only to be executed from the main thread anyway
		liveGrid.drawStateAt(currentCol, currentRow, getStateBehindCurrent());
	}

	private GridState getStateBehindCurrent() {
		return dataGrid.getCells()[currentCol][currentRow].getState();
	}
}
