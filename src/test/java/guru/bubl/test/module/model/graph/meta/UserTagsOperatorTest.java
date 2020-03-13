/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.meta;

import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UserTagsOperatorTest extends ModelTestResources {

    @Test
    public void can_get_user_metas(){
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        TagPojo toDo = edgeBetweenAAndB.addMeta(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        edgeBetweenBAndC.addMeta(
                toDo
        );
        vertexC.addMeta(modelTestScenarios.computerScientistType());
        assertThat(
                userTagsOperatorFactory.forUser(user).get().size(),
                is(2)
        );
    }
    @Test
    public void does_not_return_metas_of_another_user(){
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        TagPojo toDo = edgeBetweenAAndB.addMeta(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        edgeBetweenBAndC.addMeta(
                toDo
        );
        vertexC.addMeta(modelTestScenarios.computerScientistType());
        assertThat(
                userTagsOperatorFactory.forUser(anotherUser).get().size(),
                is(0)
        );
    }
    @Test
    public void user_meta_has_label(){
        vertexA.addMeta(modelTestScenarios.human());
        assertThat(
                userTagsOperatorFactory.forUser(user).get().iterator().next().label(),
                is("Human")
        );
    }

    @Test
    public void user_meta_has_number_of_references(){
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        TagPojo toDo = edgeBetweenAAndB.addMeta(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        edgeBetweenBAndC.addMeta(
                toDo
        );
        toDo = userTagsOperatorFactory.forUser(user).get().iterator().next();
        assertThat(
                toDo.getNbReferences(),
                is(2)
        );
    }
}