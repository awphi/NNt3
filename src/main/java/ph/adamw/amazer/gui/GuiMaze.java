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

import javafx.event.EventTarget;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import ph.adamw.amazer.maze.Cell;
import ph.adamw.amazer.maze.Maze;
import ph.adamw.amazer.agent.MazerAgent;
import ph.adamw.amazer.agent.entity.DrawingMazerEntity;
import ph.adamw.amazer.maze.CellState;

public class GuiMaze extends GridPane {
    @Setter
    @Getter
    private boolean isEditable = true;

    private CellState dragOverrideState;
    private CellState nextStartFinish = CellState.START;

    private DrawingMazerEntity entity;
    private MazerAgent agent;

    private static final Insets INSETS_20 = new Insets(20, 20, 0, 20);

    public GuiMaze(int col, int row) {
        super();
        setGridLinesVisible(true);
        setSize(col, row);
        setPadding(INSETS_20);

        setOnMouseReleased(event -> {
            dragOverrideState = null;
        });

        setOnMouseDragged(event -> {
            final Node hoveredNode = event.getPickResult().getIntersectedNode();
            final EventTarget target = event.getTarget();

            if(!(target instanceof GuiCell && hoveredNode instanceof GuiCell && isEditable)) {
                return;
            }

            final GuiCell hoveredPane = ((GuiCell) hoveredNode);
            final GuiCell targetPane = ((GuiCell) target);

            if(dragOverrideState == null) {
                switch (targetPane.getCell().getState()) {
                    case EMPTY: dragOverrideState = CellState.WALL; break;
                    case WALL: dragOverrideState = CellState.EMPTY; break;
                }
            }

            if(hoveredPane.getCell().getState() == dragOverrideState && targetPane != hoveredPane) {
                hoveredPane.switchState();
            }
        });

        setOnMousePressed(event -> {
            if(event.getTarget() instanceof GuiCell && isEditable) {
                final GuiCell targetPane = (GuiCell) event.getTarget();

                switch(event.getButton()) {
                    case PRIMARY: targetPane.switchState(); break;
                    case SECONDARY: {
                        if(containsState(CellState.GOAL)) {
                            clearCellWithState(CellState.START);
                            clearCellWithState(CellState.GOAL);
                        } else if (targetPane.getCell().getState() == CellState.EMPTY) {
                            targetPane.setState(nextRightClickState());
                        }
                    } break;
                }
            }
        });
    }

    public void setSize(int cols, int rows) {
        getRowConstraints().clear();
        getColumnConstraints().clear();
        getChildren().removeIf(node -> node instanceof GuiCell);

        addCols(cols);
        addRows(rows);
    }

    public int getRows() {
        return getRowConstraints().size();
    }

    public int getCols() {
        return getColumnConstraints().size();
    }

    private GuiCell getCellAt(int col, int row) {
        for(Node i : getManagedChildren()) {
            if(!(i instanceof GuiCell)) {
                continue;
            }

            final Cell c = ((GuiCell) i).getCell();

            if(c.getRow() == row && c.getCol() == col) {
                return (GuiCell) i;
            }
        }

        // Relatively safe since
        return new GuiCell(new Cell(-1, -1, null));
    }

    public void drawStateAt(int col, int row, CellState state) {
        getCellAt(col, row).drawState(state);
    }

    public void clearCellWithState(CellState state) {
    	final GuiCell d = getFirstState(state);
    	if(d != null) {
			d.setState(CellState.EMPTY);
		}
    }

    public boolean isValid() {
        return containsState(CellState.GOAL) && containsState(CellState.START);
    }

    public Maze asDataGrid() {
        final Cell[][] cells = new Cell[getCols()][getRows()];

        for(Node i : getManagedChildren()) {
            if(i instanceof GuiCell) {
                final Cell dc = ((GuiCell) i).getCell();
                cells[dc.getCol()][dc.getRow()] = dc;
            }
        }

        if(isValid()) {
            //noinspection ConstantConditions
            return new Maze(getCols(), getRows(), cells, getFirstState(CellState.START).getCell(), getFirstState(CellState.GOAL).getCell());
        }

        return null;
    }

    public void loadDataGrid(Maze grid) {
        setSize(grid.getWidth(), grid.getHeight());

        final Cell[][] cache = grid.getCells();

        for(int i = 0; i < getCols(); i ++) {
            for(int j = 0; j < getRows(); j ++) {
                getCellAt(i, j).setState(cache[i][j].getState());
            }
        }
    }

    private void addRows(int rows) {
        for(int i = 0; i < rows; i ++) {
            RowConstraints n = new RowConstraints();

            n.setPrefHeight(30);
            n.setMinHeight(0);
            n.setVgrow(Priority.ALWAYS);

            getRowConstraints().add(n);

            final GuiCell[] panes = new GuiCell[getCols()];
            for(int j = 0; j < panes.length; j ++) {
                panes[j] = new GuiCell(new Cell(j, getRows() - 1, CellState.EMPTY));
                setHalignment(panes[j], HPos.CENTER);
            }

            addRow(getRows() - 1, panes);
        }
    }

    private void addCols(int cols) {
        for(int i = 0; i < cols; i ++) {
            ColumnConstraints n = new ColumnConstraints();

            n.setPrefWidth(30);
            n.setMinWidth(0);
            n.setHgrow(Priority.ALWAYS);

            getColumnConstraints().add(n);
        }
    }

    private boolean containsState(CellState state) {
        return getFirstState(state) != null;
    }

    private GuiCell getFirstState(CellState state) {
        for(Node i : getManagedChildren()) {
            if(((GuiCell) i).getCell().getState() == state) {
                return ((GuiCell) i);
            }
        }

        return null;
    }

    private CellState nextRightClickState() {
        final CellState ret = nextStartFinish;

        switch(ret) {
            case START: nextStartFinish = CellState.GOAL; break;
            case GOAL: nextStartFinish = CellState.START; break;
        }

        return ret;
    }

    public void drawAgentPath(MazerAgent newAgent) {
        if(entity == null) {
            entity = new DrawingMazerEntity(asDataGrid(), this, 50);
        }

        this.agent = newAgent;
        agent.setEntity(entity);
        agent.start(true);
    }

    public boolean isReadyToDrawPath() {
        return agent == null || agent.isDone();
    }
}
