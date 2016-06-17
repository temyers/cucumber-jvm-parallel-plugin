package gherkin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstNode {
    private final Map<gherkin.Parser.RuleType, List<Object>> subItems
        = new HashMap<gherkin.Parser.RuleType, List<Object>>();
    public final gherkin.Parser.RuleType ruleType;

    public AstNode(gherkin.Parser.RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public void add(gherkin.Parser.RuleType ruleType, Object obj) {
        List<Object> items = subItems.get(ruleType);
        if (items == null) {
            items = new ArrayList<Object>();
            subItems.put(ruleType, items);
        }
        items.add(obj);
    }

    public <T> T getSingle(gherkin.Parser.RuleType ruleType, T defaultResult) {
        List<Object> items = getItems(ruleType);
        return (T) (items.isEmpty() ? defaultResult : items.get(0));
    }

    public <T> List<T> getItems(gherkin.Parser.RuleType ruleType) {
        List<T> items = (List<T>) subItems.get(ruleType);
        if (items == null) {
            return Collections.emptyList();
        }
        return items;
    }

    public Token getToken(gherkin.Parser.TokenType tokenType) {
        gherkin.Parser.RuleType ruleType = gherkin.Parser.RuleType.cast(tokenType);
        return getSingle(ruleType, new Token(null, null));
    }

    public List<Token> getTokens(gherkin.Parser.TokenType tokenType) {
        return getItems(gherkin.Parser.RuleType.cast(tokenType));
    }
}
