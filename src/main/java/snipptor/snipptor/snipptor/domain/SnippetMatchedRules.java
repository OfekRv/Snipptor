package snipptor.snipptor.snipptor.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SnippetMatchedRules.
 */
@Entity
@Table(name = "snippet_matched_rules")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SnippetMatchedRules implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToMany(mappedBy = "snippetMatchedRules")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "engine", "vulnerability", "snippetMatchedRules" }, allowSetters = true)
    private Set<Rule> rules = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SnippetMatchedRules id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Rule> getRules() {
        return this.rules;
    }

    public void setRules(Set<Rule> rules) {
        if (this.rules != null) {
            this.rules.forEach(i -> i.removeSnippetMatchedRules(this));
        }
        if (rules != null) {
            rules.forEach(i -> i.addSnippetMatchedRules(this));
        }
        this.rules = rules;
    }

    public SnippetMatchedRules rules(Set<Rule> rules) {
        this.setRules(rules);
        return this;
    }

    public SnippetMatchedRules addRule(Rule rule) {
        this.rules.add(rule);
        rule.getSnippetMatchedRules().add(this);
        return this;
    }

    public SnippetMatchedRules removeRule(Rule rule) {
        this.rules.remove(rule);
        rule.getSnippetMatchedRules().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SnippetMatchedRules)) {
            return false;
        }
        return id != null && id.equals(((SnippetMatchedRules) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SnippetMatchedRules{" +
            "id=" + getId() +
            "}";
    }
}
