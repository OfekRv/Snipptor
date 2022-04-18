package snipptor.snipptor.snipptor.contracts;

import java.util.Collection;

public class SnippetRulesScanResult {
    private Collection<String> matches;

    public SnippetRulesScanResult(Collection<String> matched) {
        this.matches = matched;
    }

    public SnippetRulesScanResult() {
    }

    public Collection<String> getMatches() {
        return matches;
    }

    public void setMatches(Collection<String> matches) {
        this.matches = matches;
    }
}
