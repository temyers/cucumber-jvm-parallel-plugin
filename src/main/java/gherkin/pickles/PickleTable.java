package gherkin.pickles;

import java.util.List;

public class PickleTable implements Argument {

    private final List<PickleRow> rows;

    public PickleTable(List<PickleRow> rows) {
        this.rows = java.util.Collections.unmodifiableList(rows);
    }

    public List<PickleRow> getRows() {
        return rows;
    }

    @Override
    public PickleLocation getLocation() {
        return rows.get(0).getCells().get(0).getLocation();
    }
}
