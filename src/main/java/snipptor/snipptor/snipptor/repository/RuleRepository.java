package snipptor.snipptor.snipptor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import snipptor.snipptor.snipptor.domain.Rule;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Rule entity.
 */
@Repository
public interface RuleRepository extends RuleRepositoryWithBagRelationships, JpaRepository<Rule, Long> {
    public Optional<Rule> findByName(String name);

    default Optional<Rule> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Rule> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Rule> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
