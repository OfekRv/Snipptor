package snipptor.snipptor.snipptor.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import snipptor.snipptor.snipptor.domain.Rule;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class RuleRepositoryWithBagRelationshipsImpl implements RuleRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Rule> fetchBagRelationships(Optional<Rule> rule) {
        return rule.map(this::fetchSnippetMatchedRules);
    }

    @Override
    public Page<Rule> fetchBagRelationships(Page<Rule> rules) {
        return new PageImpl<>(fetchBagRelationships(rules.getContent()), rules.getPageable(), rules.getTotalElements());
    }

    @Override
    public List<Rule> fetchBagRelationships(List<Rule> rules) {
        return Optional.of(rules).map(this::fetchSnippetMatchedRules).get();
    }

    Rule fetchSnippetMatchedRules(Rule result) {
        return entityManager
            .createQuery("select rule from Rule rule left join fetch rule.snippetMatchedRules where rule is :rule", Rule.class)
            .setParameter("rule", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Rule> fetchSnippetMatchedRules(List<Rule> rules) {
        return entityManager
            .createQuery("select distinct rule from Rule rule left join fetch rule.snippetMatchedRules where rule in :rules", Rule.class)
            .setParameter("rules", rules)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
