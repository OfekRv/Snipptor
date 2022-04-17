package snipptor.snipptor.snipptor.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import snipptor.snipptor.snipptor.domain.SnippetMatchedRules;

/**
 * Spring Data SQL repository for the SnippetMatchedRules entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SnippetMatchedRulesRepository extends JpaRepository<SnippetMatchedRules, Long> {}
