package snipptor.snipptor.snipptor.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import snipptor.snipptor.snipptor.domain.Snippet;

import java.util.Optional;

/**
 * Spring Data SQL repository for the Snippet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    public Optional<Snippet> findByHash(String hash);
}
