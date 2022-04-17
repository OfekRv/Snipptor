package snipptor.snipptor.snipptor.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import snipptor.snipptor.snipptor.domain.Snippet;

/**
 * Spring Data SQL repository for the Snippet entity.
 */
@Repository
public interface SnippetRepository extends SnippetRepositoryWithBagRelationships, JpaRepository<Snippet, Long> {
    default Optional<Snippet> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Snippet> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Snippet> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
