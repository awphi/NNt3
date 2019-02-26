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

import javafx.application.Platform;
import lombok.Setter;
import ph.adamw.amazer.maze.CellState;
import ph.adamw.amazer.maze.Maze;
import ph.adamw.amazer.gui.GuiMaze;

/*
 * Overrides MazerEntity's moving method to also draw to a live game maze after moving, used when a single network
 * is ran in preview mode.
 */
public class DrawingMazerEntity extends MazerEntity {
	private final GuiMaze guiMaze;

	@Setter
	private int interval;

	public DrawingMazerEntity(Maze maze, GuiMaze guiMaze, int interval) {
		super(maze);
		this.guiMaze = guiMaze;
		this.interval = interval;

		drawState(CellState.ENTITY);
	}

	private void drawState(CellState state) {
		// Cache the values for the runLater
		final int x = currentCol;
		final int y = currentRow;

		Platform.runLater(() -> guiMaze.drawStateAt(x, y, state));
	}

	private CellState getStateBehindCurrent() {
		return maze.getCells()[currentCol][currentRow].getState();
	}

	@Override
	public void reset() {
		drawState(getStateBehindCurrent());
		super.reset();
	}

	@Override
	public EntityDirection move(double[] vals) {
		drawState(getStateBehindCurrent());
		final EntityDirection b = super.move(vals);
		drawState(CellState.ENTITY);
		return b;
	}

	@Override
	public int getInterval() {
		return interval;
	}
}
