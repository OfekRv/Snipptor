package snipptor.snipptor.snipptor.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import snipptor.snipptor.snipptor.domain.Snippet;

/**
 * Spring Data SQL repository for the Snippet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {}
