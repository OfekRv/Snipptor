package snipptor.snipptor.snipptor.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Rule.
 */
@Table("rule")
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("raw")
    private String raw;

    @Transient
    private Engine engine;

    @Transient
    private Vulnerability vulnerability;

    @Transient
    @JsonIgnoreProperties(value = { "rules", "snippets" }, allowSetters = true)
    private Set<SnippetMatchedRules> snippetMatchedRules = new HashSet<>();

    @Column("engine_id")
    private Long engineId;

    @Column("vulnerability_id")
    private Long vulnerabilityId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Rule name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaw() {
        return this.raw;
    }

    public Rule raw(String raw) {
        this.setRaw(raw);
        return this;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        this.engineId = engine != null ? engine.getId() : null;
    }

    public Rule engine(Engine engine) {
        this.setEngine(engine);
        return this;
    }

    public Vulnerability getVulnerability() {
        return this.vulnerability;
    }

    public void setVulnerability(Vulnerability vulnerability) {
        this.vulnerability = vulnerability;
        this.vulnerabilityId = vulnerability != null ? vulnerability.getId() : null;
    }

    public Rule vulnerability(Vulnerability vulnerability) {
        this.setVulnerability(vulnerability);
        return this;
    }

    public Set<SnippetMatchedRules> getSnippetMatchedRules() {
        return this.snippetMatchedRules;
    }

    public void setSnippetMatchedRules(Set<SnippetMatchedRules> snippetMatchedRules) {
        this.snippetMatchedRules = snippetMatchedRules;
    }

    public Rule snippetMatchedRules(Set<SnippetMatchedRules> snippetMatchedRules) {
        this.setSnippetMatchedRules(snippetMatchedRules);
        return this;
    }

    public Rule addSnippetMatchedRules(SnippetMatchedRules snippetMatchedRules) {
        this.snippetMatchedRules.add(snippetMatchedRules);
        snippetMatchedRules.getRules().add(this);
        return this;
    }

    public Rule removeSnippetMatchedRules(SnippetMatchedRules snippetMatchedRules) {
        this.snippetMatchedRules.remove(snippetMatchedRules);
        snippetMatchedRules.getRules().remove(this);
        return this;
    }

    public Long getEngineId() {
        return this.engineId;
    }

    public void setEngineId(Long engine) {
        this.engineId = engine;
    }

    public Long getVulnerabilityId() {
        return this.vulnerabilityId;
    }

    public void setVulnerabilityId(Long vulnerability) {
        this.vulnerabilityId = vulnerability;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rule)) {
            return false;
        }
        return id != null && id.equals(((Rule) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rule{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", raw='" + getRaw() + "'" +
            "}";
    }
}
