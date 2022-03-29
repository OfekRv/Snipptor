package snipptor.snipptor.snipptor.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import snipptor.snipptor.snipptor.domain.Authority;

/**
 * Spring Data R2DBC repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends R2dbcRepository<Authority, String> {}
