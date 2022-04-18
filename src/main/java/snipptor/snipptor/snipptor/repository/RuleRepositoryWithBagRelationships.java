package snipptor.snipptor.snipptor.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import snipptor.snipptor.snipptor.domain.Rule;

public interface RuleRepositoryWithBagRelationships {
    Optional<Rule> fetchBagRelationships(Optional<Rule> rule);

    List<Rule> fetchBagRelationships(List<Rule> rules);

    Page<Rule> fetchBagRelationships(Page<Rule> rules);
}
