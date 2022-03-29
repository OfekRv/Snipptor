package snipptor.snipptor.snipptor.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import snipptor.snipptor.snipptor.web.rest.TestUtil;

class EngineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Engine.class);
        Engine engine1 = new Engine();
        engine1.setId(1L);
        Engine engine2 = new Engine();
        engine2.setId(engine1.getId());
        assertThat(engine1).isEqualTo(engine2);
        engine2.setId(2L);
        assertThat(engine1).isNotEqualTo(engine2);
        engine1.setId(null);
        assertThat(engine1).isNotEqualTo(engine2);
    }
}
