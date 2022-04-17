package snipptor.snipptor.snipptor.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import snipptor.snipptor.snipptor.domain.enumeration.SnippetClassification;

/**
 * A Snippet.
 */
@Entity
@Table(name = "snippet")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Snippet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "hash")
    private String hash;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "url")
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "classification")
    private SnippetClassification classification;

    @Column(name = "scan_count")
    private Long scanCount;

    @ManyToMany
    @JoinTable(
        name = "rel_snippet__snippet_matched_rules",
        joinColumns = @JoinColumn(name = "snippet_id"),
        inverseJoinColumns = @JoinColumn(name = "snippet_matched_rules_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "rules", "snippets" }, allowSetters = true)
    private Set<SnippetMatchedRules> snippetMatchedRules = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Snippet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return this.hash;
    }

    public Snippet hash(String hash) {
        this.setHash(hash);
        return this;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getContent() {
        return this.content;
    }

    public Snippet content(String content) {
        this.setContent(content);
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return this.url;
    }

    public Snippet url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SnippetClassification getClassification() {
        return this.classification;
    }

    public Snippet classification(SnippetClassification classification) {
        this.setClassification(classification);
        return this;
    }

    public void setClassification(SnippetClassification classification) {
        this.classification = classification;
    }

    public Long getScanCount() {
        return this.scanCount;
    }

    public Snippet scanCount(Long scanCount) {
        this.setScanCount(scanCount);
        return this;
    }

    public void setScanCount(Long scanCount) {
        this.scanCount = scanCount;
    }

    public Set<SnippetMatchedRules> getSnippetMatchedRules() {
        return this.snippetMatchedRules;
    }

    public void setSnippetMatchedRules(Set<SnippetMatchedRules> snippetMatchedRules) {
        this.snippetMatchedRules = snippetMatchedRules;
    }

    public Snippet snippetMatchedRules(Set<SnippetMatchedRules> snippetMatchedRules) {
        this.setSnippetMatchedRules(snippetMatchedRules);
        return this;
    }

    public Snippet addSnippetMatchedRules(SnippetMatchedRules snippetMatchedRules) {
        this.snippetMatchedRules.add(snippetMatchedRules);
        snippetMatchedRules.getSnippets().add(this);
        return this;
    }

    public Snippet removeSnippetMatchedRules(SnippetMatchedRules snippetMatchedRules) {
        this.snippetMatchedRules.remove(snippetMatchedRules);
        snippetMatchedRules.getSnippets().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Snippet)) {
            return false;
        }
        return id != null && id.equals(((Snippet) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Snippet{" +
            "id=" + getId() +
            ", hash='" + getHash() + "'" +
            ", content='" + getContent() + "'" +
            ", url='" + getUrl() + "'" +
            ", classification='" + getClassification() + "'" +
            ", scanCount=" + getScanCount() +
            "}";
    }
}
