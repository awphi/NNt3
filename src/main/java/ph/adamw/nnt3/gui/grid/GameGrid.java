package ph.adamw.nnt3.gui.grid;

import javafx.scene.layout.*;

public class GameGrid extends GridPane {
    public GameGrid() {
        super();
        setGridLinesVisible(true);
    }

    public void addRow() {
        RowConstraints n = new RowConstraints();

        n.setPrefHeight(30);
        n.setMinHeight(10);
        n.setVgrow(Priority.ALWAYS);

        getRowConstraints().add(n);
    }

    public void addCol() {
        ColumnConstraints n = new ColumnConstraints();

        n.setPrefWidth(30);
        n.setMinWidth(10);
        n.setHgrow(Priority.ALWAYS);

        getColumnConstraints().add(n);
    }

    public void setGridSquareState(int col, int row, GridState state) {
        Pane p = new Pane();
        p.setMinSize(0,0);

        switch(state) {
            case WALL: p.setStyle("-fx-background-color: black");
            case START: p.setStyle("-fx-background-color: lime");
            case FINISH: p.setStyle("-fx-background-color: green");
            case CHARACTER: p.setStyle("-fx-background-color: red");
        }

        add(p, col, row);
    }
}
