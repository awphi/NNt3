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

package ph.adamw.amazer.maze;

import lombok.Getter;
import ph.adamw.amazer.agent.entity.EntityDirection;
import ph.adamw.amazer.maze.graph.Graph;
import ph.adamw.amazer.maze.graph.GraphNode;
import ph.adamw.amazer.maze.graph.GraphUtils;

import java.io.Serializable;

/**
 * Serializable data class to store information required to load, save and runOneGeneration game grids. To display a Maze
 * in a GUI there also must be a GuiMaze to operate on.
 */
@Getter
public class Maze implements Serializable {
	private final int width;
	private final int height;

	private final Cell[][] cells;

	private final Cell start;
	private final Cell goal;

	private final transient Graph graph;

	public Maze(int width, int height, Cell[][] cells, Cell start, Cell goal) {
		this.width = width;
		this.height = height;
		this.cells = cells;
		this.start = start;
		this.goal = goal;
		this.graph = GraphUtils.buildGraph(this);
	}

	public int getOptimalDistanceToGoal(int col, int row) {
		final GraphNode node = graph.getNode(new Cell(col, row, CellState.EMPTY).toString());
		return node == null ? Integer.MAX_VALUE : node.getShortestDistanceToSource();
	}

	public Cell getCellInDirection(Cell cell, EntityDirection dir) {
		switch(dir) {
			case UP: {
				if(cell.getRow() == 0) {
					break;
				}

				return cells[cell.getCol()][cell.getRow() - 1];
			}

			case DOWN: {
				if(cell.getRow() == height - 1) {
					break;
				}

				return cells[cell.getCol()][cell.getRow() + 1];
			}

			case LEFT: {
				if(cell.getCol() == 0) {
					break;
				}

				return cells[cell.getCol() - 1][cell.getRow()];
			}

			case RIGHT: {
				if(cell.getCol() == width - 1) {
					break;
				}

				return cells[cell.getCol() + 1][cell.getRow()];
			}
		}

		return null;
	}

	public int getDistanceToNextObstacle(int col, int row, EntityDirection dir) {
		// Finds out if the movement is left/right or up/down
		final boolean xBased = dir.getX() != 0;

		int x = 0;
		int y = 0;
		boolean xBoundsReached = false;
		boolean yBoundsReached = false;

		while(!(xBoundsReached || yBoundsReached || cells[col + x][row + y].getState() == CellState.WALL)) {
			if(xBased) {
				x += dir.getX();
			} else {
				y += dir.getY();
			}

			xBoundsReached = col + x < 0 || col + x > getWidth() - 1;
			yBoundsReached = row + y < 0 || row + y > getHeight() - 1;
		}

		int result = 0;

		switch (dir) {
			case UP: result = y + 1; break;
			case DOWN: result = y - 1; break;
			case LEFT: result = x + 1; break;
			case RIGHT: result = x - 1; break;
		}

		return Math.abs(result);
	}
}
