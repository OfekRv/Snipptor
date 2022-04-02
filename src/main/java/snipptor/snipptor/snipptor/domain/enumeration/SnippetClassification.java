package snipptor.snipptor.snipptor.domain.enumeration;

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
}
