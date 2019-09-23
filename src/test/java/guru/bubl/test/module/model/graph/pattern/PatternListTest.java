package guru.bubl.test.module.model.graph.pattern;

import guru.bubl.module.model.graph.pattern.PatternList;
import guru.bubl.module.model.graph.pattern.PatternPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PatternListTest extends ModelTestResources {

    @Inject
    PatternList patternList;

    @Test
    public void can_get() {
        Set<PatternPojo> patterns = patternList.get();
        assertThat(
                patterns.size(),
                is(0)
        );
        vertexA.makePattern();
        vertexC.makePattern();
        patterns = patternList.get();
        assertThat(
                patterns.size(),
                is(2)
        );
    }

}
