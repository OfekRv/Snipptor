package snipptor.snipptor.snipptor.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import snipptor.snipptor.snipptor.domain.Engine;

/**
 * Spring Data SQL repository for the Engine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EngineRepository extends JpaRepository<Engine, Long> {}
