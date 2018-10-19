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

package ph.adamw.amazer.gui.grid;

import javafx.event.EventTarget;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;
import ph.adamw.amazer.gui.grid.data.DataCell;
import ph.adamw.amazer.gui.grid.data.DataGrid;
import ph.adamw.amazer.mazer.Mazer;
import ph.adamw.amazer.mazer.entity.DrawingMazerEntity;

public class LiveGrid extends GridPane {
    @Setter
    @Getter
    private boolean isEditable = true;

    private GridState dragOverrideState;
    private GridState nextStartFinish = GridState.START;

    private static final Insets INSETS_20 = new Insets(20, 20, 0, 20);

    public LiveGrid(int col, int row) {
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

            if(!(target instanceof CellPane && hoveredNode instanceof CellPane && isEditable)) {
                return;
            }

            final CellPane hoveredPane = ((CellPane) hoveredNode);
            final CellPane targetPane = ((CellPane) target);

            if(dragOverrideState == null) {
                switch (targetPane.getCell().getState()) {
                    case EMPTY: dragOverrideState = GridState.WALL; break;
                    case WALL: dragOverrideState = GridState.EMPTY; break;
                }
            }

            if(hoveredPane.getCell().getState() == dragOverrideState && targetPane != hoveredPane) {
                hoveredPane.switchAndDrawState();
            }
        });

        setOnMousePressed(event -> {
            if(event.getTarget() instanceof CellPane && isEditable) {
                final CellPane targetPane = (CellPane) event.getTarget();

                switch(event.getButton()) {
                    case PRIMARY: targetPane.switchAndDrawState(); break;
                    case SECONDARY: {
                        if(containsState(GridState.GOAL)) {
                            clearCellWithState(GridState.START);
                            clearCellWithState(GridState.GOAL);
                        } else if (targetPane.getCell().getState() == GridState.EMPTY) {
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
        getChildren().removeIf(node -> node instanceof CellPane);

        addCols(cols);
        addRows(rows);
    }

    public int getRows() {
        return getRowConstraints().size();
    }

    public int getCols() {
        return getColumnConstraints().size();
    }

    private CellPane getCellAt(int col, int row) {
        for(Node i : getManagedChildren()) {
            if(!(i instanceof CellPane)) {
                continue;
            }

            final DataCell c = ((CellPane) i).getCell();

            if(c.getRow() == row && c.getCol() == col) {
                return (CellPane) i;
            }
        }

        // Relatively safe since
        return null;
    }

    public void drawStateAt(int col, int row, GridState state) {
        getCellAt(col, row).drawState(state);
    }

    public void clearCellWithState(GridState state) {
    	final CellPane d = getFirstState(state);
    	if(d != null) {
			d.setState(GridState.EMPTY);
		}
    }

    public boolean isValid() {
        return containsState(GridState.GOAL) && containsState(GridState.START);
    }

    public DataGrid asDataGrid() {
        final DataCell[][] dataCells = new DataCell[getCols()][getRows()];

        int colCount = 0;
        int rowCount = 0;

        for(Node i : getManagedChildren()) {
            dataCells[colCount][rowCount] = ((CellPane) i).getCell();
            colCount ++;

            if(colCount == getCols()) {
                rowCount ++;
                colCount = 0;
            }
        }

        // Inspection disabled as isValid() method guarantees we have a START and GOAL
        if(isValid()) {
            //noinspection ConstantConditions
            return new DataGrid(getCols(), getRows(), dataCells, getFirstState(GridState.START).getCell(), getFirstState(GridState.GOAL).getCell());
        } else {
            return null;
        }
    }

    public void loadDataGrid(DataGrid grid) {
        setSize(grid.getWidth(), grid.getHeight());

        final DataCell[][] cache = grid.getCells();

        for(int i = 0; i < getCols(); i ++) {
            for(int j = 0; j < getRows(); j ++) {
                //Fix broken cellAt
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

            final CellPane[] panes = new CellPane[getCols()];
            for(int j = 0; j < panes.length; j ++) {
                panes[j] = new CellPane(new DataCell(j, getRows() - 1, GridState.EMPTY));
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

    private boolean containsState(GridState state) {
        return getFirstState(state) != null;
    }

    private CellPane getFirstState(GridState state) {
        for(Node i : getManagedChildren()) {
            if(((CellPane) i).getCell().getState() == state) {
                return ((CellPane) i);
            }
        }

        return null;
    }

    private GridState nextRightClickState() {
        final GridState ret = nextStartFinish;

        switch(ret) {
            case START: nextStartFinish = GridState.GOAL; break;
            case GOAL: nextStartFinish = GridState.START; break;
        }

        return ret;
    }

    public Mazer playMazer(Mazer mazer, int interval) {
        // If we're loading form the same session i.e. we have a DataGrid compiled already don't bother remaking it
        mazer.setEntity(new DrawingMazerEntity(mazer.getEntity() == null ? asDataGrid() : mazer.getEntity().getDataGrid(), this, interval));
        mazer.start(true);

        return mazer;
    }
}
