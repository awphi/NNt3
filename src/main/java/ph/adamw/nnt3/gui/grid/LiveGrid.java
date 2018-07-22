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

import javafx.event.EventTarget;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class LiveGrid extends GridPane {
    @Setter
    private boolean isEditable = true;

    private GridState dragOverrideState;
    private GridState nextStartFinish = GridState.START;

    private static final Insets INSETS_20 = new Insets(20, 20, 20, 20);

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

            if(!(target instanceof Cell && hoveredNode instanceof Cell && isEditable)) {
                return;
            }

            final Cell hoveredCell = ((Cell) hoveredNode);
            final Cell targetCell = ((Cell) target);

            if(dragOverrideState == null) {
                switch (targetCell.getState()) {
                    case EMPTY: dragOverrideState = GridState.WALL; break;
                    case WALL: dragOverrideState = GridState.EMPTY; break;
                }
            }

            if(hoveredCell.getState() == dragOverrideState && targetCell != hoveredCell) {
                hoveredCell.switchState();
            }
        });

        setOnMousePressed(event -> {
            if(event.getTarget() instanceof Cell && isEditable) {
                final Cell cell = (Cell) event.getTarget();

                switch(event.getButton()) {
                    case PRIMARY: cell.switchState(); break;
                    case SECONDARY: {
                        if(containsState(GridState.GOAL)) {
                            removeStartFinish();
                        } else if (cell.getState() == GridState.EMPTY) {
                            cell.setState(getNextStartFinish());
                        }
                    } break;
                }
            }
        });
    }

    public void setSize(int cols, int rows) {
        getRowConstraints().clear();
        getColumnConstraints().clear();

        final Set<Node> toRemove = new HashSet<>();

        for(Node i : getChildren()) {
            if(i instanceof Cell) {
                toRemove.add(i);
            }
        }

        getChildren().removeAll(toRemove);

        addCols(cols);
        addRows(rows);
    }

    public int getRows() {
        return getRowConstraints().size();
    }

    public int getCols() {
        return getColumnConstraints().size();
    }

    private void addRows(int rows) {
        for(int i = 0; i < rows; i ++) {
            RowConstraints n = new RowConstraints();

            n.setPrefHeight(30);
            n.setMinHeight(0);
            n.setVgrow(Priority.ALWAYS);

            getRowConstraints().add(n);

            final Cell[] panes = new Cell[getCols()];
            for(int j = 0; j < panes.length; j ++) {
                panes[j] = new Cell(getRows() - 1, j, GridState.EMPTY);
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

    private Cell getFirstState(GridState state) {
        for(Node i : getChildren()) {
            if(i instanceof Cell && ((Cell) i).getState() == state) {
                return (Cell) i;
            }
        }

        return null;
    }

    private void removeStartFinish() {
        int returnEarly = 0;

        for(Node i : getChildren()) {
            if(i instanceof Cell) {
                final Cell cell = (Cell) i;
                if(cell.getState() == GridState.START || cell.getState() == GridState.GOAL) {
                    cell.setState(GridState.EMPTY);
                    returnEarly ++;
                    if(returnEarly == 2) {
                        return;
                    }
                }
            }
        }
    }

    private GridState getNextStartFinish() {
        final GridState ret = nextStartFinish;

        switch(ret) {
            case START: nextStartFinish = GridState.GOAL; break;
            case GOAL: nextStartFinish = GridState.START; break;
        }

        return ret;
    }

    public DataGrid getDataGrid() {
        if(!containsState(GridState.GOAL) || !containsState(GridState.START)) {
            throw new RuntimeException("To produce a DataGrid, a LiveGrid must have a start and finish!");
        }

        final Cell[][] cells = new Cell[getCols()][getRows()];

        int colCount = 0;
        int rowCount = 0;

        for(Node i : getChildren()) {
            if(i instanceof Cell) {
                cells[colCount][rowCount] = (Cell) i;
                colCount ++;
            }

            if(colCount == getCols()) {
                rowCount ++;
                colCount = 0;
            }
        }

        return new DataGrid(getCols(), getRows(), cells, getFirstState(GridState.START), getFirstState(GridState.GOAL));
    }
}
