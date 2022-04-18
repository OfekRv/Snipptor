package snipptor.snipptor.snipptor.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import snipptor.snipptor.snipptor.domain.Snippet;

public interface SnippetRepositoryWithBagRelationships {
    Optional<Snippet> fetchBagRelationships(Optional<Snippet> snippet);

    List<Snippet> fetchBagRelationships(List<Snippet> snippets);

    Page<Snippet> fetchBagRelationships(Page<Snippet> snippets);
}
