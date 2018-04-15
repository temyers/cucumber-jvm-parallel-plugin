package com.github.timm.cucumber.runtime;

import static java.util.Arrays.asList;

import gherkin.events.PickleEvent;
import gherkin.pickles.PickleTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.cucumber.tagexpressions.Expression;
import io.cucumber.tagexpressions.TagExpressionParser;

/**
 * This file is copied from Cucumber-runtime library 
 *
 */
public class TagPredicate {
    private final List<Expression> expressions = new ArrayList<Expression>();
    private final List<TagExpressionOld> oldStyleExpressions = new ArrayList<TagExpressionOld>();

    public TagPredicate(List<String> tagExpressions) {
        if (tagExpressions == null) {
            return;
        }
        TagExpressionParser parser = new TagExpressionParser();
        for (String tagExpression : tagExpressions) {
            if (TagExpressionOld.isOldTagExpression(tagExpression)) {
                oldStyleExpressions.add(new TagExpressionOld(asList(tagExpression)));
            } else {
                expressions.add(parser.parse(tagExpression));
            }
        }
    }

    public boolean apply(PickleEvent pickleEvent) {
        return apply(pickleEvent.pickle.getTags());
    }

    /**
     * 
     * @param pickleTags
     * @return
     */
    public boolean apply(Collection<PickleTag> pickleTags) {
        for (TagExpressionOld oldStyleExpression : oldStyleExpressions) {
            if (!oldStyleExpression.evaluate(pickleTags)) {
                return false;
            }
        }
        List<String> tags = new ArrayList<String>();
        for (PickleTag pickleTag : pickleTags) {
            tags.add(pickleTag.getName());
        }
        for (Expression expression : expressions) {
            if (!expression.evaluate(tags)) {
                return false;
            }
        }
        return true;
    }

}
