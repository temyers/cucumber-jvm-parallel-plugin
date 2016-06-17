package gherkin;

import gherkin.deps.com.google.gson.Gson;

import gherkin.ast.Location;



import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class GherkinDialectProvider implements IGherkinDialectProvider {
    private static Map<String, Map<String, List<String>>> DIALECTS;
    private final String defaultDialectName;

    static {
        Gson gson = new Gson();
        try {
            Reader dialects = new InputStreamReader(
                GherkinDialectProvider.class.getResourceAsStream(
                    "gherkin-languages.json"), "UTF-8");
            DIALECTS = gson.fromJson(dialects, Map.class);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public GherkinDialectProvider(String defaultDialectName) {
        this.defaultDialectName = defaultDialectName;
    }

    public GherkinDialectProvider() {
        this("en");
    }

    public GherkinDialect getDefaultDialect() {
        return getDialect(defaultDialectName, null);
    }

    @Override
    public GherkinDialect getDialect(String language, Location location) {
        Map<String, List<String>> map = DIALECTS.get(language);
        if (map == null) {
            throw new ParserException.NoSuchLanguageException(language, location);
        }

        return new GherkinDialect(language, map);
    }
}
