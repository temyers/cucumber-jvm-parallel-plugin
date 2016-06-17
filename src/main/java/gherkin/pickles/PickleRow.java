package gherkin.pickles;

import java.util.List;

public class PickleRow {
    private final List<PickleCell> cells;

    public PickleRow(List<PickleCell> cells) {
        this.cells = java.util.Collections.unmodifiableList(cells);
    }

    public List<PickleCell> getCells() {
        return cells;
    }

}
