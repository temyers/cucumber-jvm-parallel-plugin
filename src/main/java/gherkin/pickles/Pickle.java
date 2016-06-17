package gherkin.pickles;

import java.util.List;

public class Pickle {
    private final String name;
    private final List<PickleStep> steps;
    private final List<PickleTag> tags;
    private final List<PickleLocation> locations;

    public Pickle(String name,
                  List<PickleStep> steps,
                  List<PickleTag> tags, List<PickleLocation> locations) {
        this.name = name;
        this.steps = java.util.Collections.unmodifiableList(steps);
        this.tags = tags;
        this.locations = java.util.Collections.unmodifiableList(locations);
    }

    public String getName() {
        return name;
    }

    public List<PickleStep> getSteps() {
        return steps;
    }

    public List<PickleLocation> getLocations() {
        return locations;
    }
}
