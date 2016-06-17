package gherkin.pickles;

import java.util.List;

public class PickleStep {
    private final String text;
    private final List<Argument> arguments;
    private final List<PickleLocation> locations;

    public PickleStep(String text, List<Argument> arguments, List<PickleLocation> locations) {
        this.text = text;
        this.arguments = java.util.Collections.unmodifiableList(arguments);
        this.locations = java.util.Collections.unmodifiableList(locations);
    }

    public String getText() {
        return text;
    }

    public List<PickleLocation> getLocations() {
        return locations;
    }

    public List<Argument> getArgument() {
        return arguments;
    }
}
