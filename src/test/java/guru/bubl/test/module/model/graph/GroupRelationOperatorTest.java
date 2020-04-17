package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GroupRelationOperatorTest extends ModelTestResources {

    @Test
    public void can_change_source_of_group_relation() {
        SubGraphPojo aroundA = userGraph.aroundForkUriInShareLevels(
                vertexA.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertFalse(
                aroundA.containsGraphElement(
                        groupRelation
                )
        );
        SubGraphPojo aroundC = userGraph.aroundForkUriInShareLevels(
                vertexC.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertTrue(
                aroundC.containsGraphElement(
                        groupRelation
                )
        );
        groupRelation.changeSource(
                vertexA.uri(),
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        assertThat(
                groupRelation.sourceUri(),
                is(vertexA.uri())
        );
        aroundA = userGraph.aroundForkUriInShareLevels(
                vertexA.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertTrue(
                aroundA.containsGraphElement(
                        groupRelation
                )
        );
        aroundC = userGraph.aroundForkUriInShareLevels(
                vertexC.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertFalse(
                aroundC.containsGraphElement(
                        groupRelation
                )
        );
    }
}
