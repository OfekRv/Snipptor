package snipptor.snipptor.snipptor.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import snipptor.snipptor.snipptor.domain.Snippet;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class SnippetRepositoryWithBagRelationshipsImpl implements SnippetRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Snippet> fetchBagRelationships(Optional<Snippet> snippet) {
        return snippet.map(this::fetchSnippetMatchedRules);
    }

    @Override
    public Page<Snippet> fetchBagRelationships(Page<Snippet> snippets) {
        return new PageImpl<>(fetchBagRelationships(snippets.getContent()), snippets.getPageable(), snippets.getTotalElements());
    }

    @Override
    public List<Snippet> fetchBagRelationships(List<Snippet> snippets) {
        return Optional.of(snippets).map(this::fetchSnippetMatchedRules).get();
    }

    Snippet fetchSnippetMatchedRules(Snippet result) {
        return entityManager
            .createQuery(
                "select snippet from Snippet snippet left join fetch snippet.snippetMatchedRules where snippet is :snippet",
                Snippet.class
            )
            .setParameter("snippet", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Snippet> fetchSnippetMatchedRules(List<Snippet> snippets) {
        return entityManager
            .createQuery(
                "select distinct snippet from Snippet snippet left join fetch snippet.snippetMatchedRules where snippet in :snippets",
                Snippet.class
            )
            .setParameter("snippets", snippets)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
