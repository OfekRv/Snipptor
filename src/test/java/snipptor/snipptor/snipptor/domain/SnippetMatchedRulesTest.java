package snipptor.snipptor.snipptor.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import snipptor.snipptor.snipptor.web.rest.TestUtil;

class SnippetMatchedRulesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SnippetMatchedRules.class);
        SnippetMatchedRules snippetMatchedRules1 = new SnippetMatchedRules();
        snippetMatchedRules1.setId(1L);
        SnippetMatchedRules snippetMatchedRules2 = new SnippetMatchedRules();
        snippetMatchedRules2.setId(snippetMatchedRules1.getId());
        assertThat(snippetMatchedRules1).isEqualTo(snippetMatchedRules2);
        snippetMatchedRules2.setId(2L);
        assertThat(snippetMatchedRules1).isNotEqualTo(snippetMatchedRules2);
        snippetMatchedRules1.setId(null);
        assertThat(snippetMatchedRules1).isNotEqualTo(snippetMatchedRules2);
    }
}
