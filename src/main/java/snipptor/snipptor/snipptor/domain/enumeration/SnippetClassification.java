package snipptor.snipptor.snipptor.domain.enumeration;

import snipptor.snipptor.snipptor.domain.Rule;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;

import java.util.Collection;

/**
 * The SnippetClassification enumeration.
 */
public enum SnippetClassification {
    UNKNOWN("Unknown"),
    SAFE("Safe"),
    MALICIOUS("Malicious"),
    VULNERABLE("Vulnerable");

    private final String value;

    SnippetClassification(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SnippetClassification classifyByRules(Collection<Rule> rules) {
        return rules.isEmpty() ? UNKNOWN : VULNERABLE;
    }
}
