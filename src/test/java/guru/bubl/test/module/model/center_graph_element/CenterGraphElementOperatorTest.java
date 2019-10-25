/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.module.model.center_graph_element.CenterGraphElement;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CenterGraphElementOperatorTest extends ModelTestResources {


    @Test
    public void can_increment_number_of_visits() {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        CenterGraphElement centerGraphElement = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        ).iterator().next();
        Integer nbCenterGraphElements = centerGraphElement.getNumberOfVisits();
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElement = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        ).iterator().next();
        assertThat(
                centerGraphElement.getNumberOfVisits(),
                is(nbCenterGraphElements + 1)
        );
    }

    @Test
    public void can_get_center_elements_of_type_meta() {
        IdentifierPojo meta = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        List<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.usingDefaultLimits(
                
        ).getPublicAndPrivateForOwner(user);
        Integer nbCenterGraphElements = centerGraphElements.size();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                meta
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElements = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user);
        assertThat(
                centerGraphElements.size(),
                is(nbCenterGraphElements + 1)
        );
    }

//    @Test
//    public void cannot_get_metas_of_another_user() {
//        IdentifierPojo meta = vertexA.addMeta(
//                modelTestScenarios.person()
//        ).values().iterator().next();
//        List<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.forUser(
//                user
//        ).getPublicOnlyOfType();
//        assertThat(
//                centerGraphElements.size(),
//                is(0)
//        );
//        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
//                meta
//        );
//        centerGraphElementOperator.updateLastCenterDate();
//        centerGraphElements = centerGraphElementsOperatorFactory.forUser(
//                user
//        ).getPublicOnlyOfType();
//        assertThat(
//                centerGraphElements.size(),
//                is(0)
//        );
//    }

    @Test
    public void can_get_center_elements_of_type_relation() {
        Edge edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        List<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(
                user
        );
        Integer nbCenterGraphElements = centerGraphElements.size();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                edgeBetweenBAndC
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElements = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user);
        assertThat(
                centerGraphElements.size(),
                is(nbCenterGraphElements + 1)
        );
    }

    @Test
    public void returns_colors() {
        vertexA.setColors("patatie");
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        CenterGraphElement centerGraphElement = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user).iterator().next();
        assertThat(
                centerGraphElement.getGraphElement().getColors(),
                is("patatie")
        );
    }

    @Test
    public void can_remove_centers() {
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        ).updateLastCenterDate();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user);
        assertThat(
                centers.size(),
                is(2)
        );
        Iterator<CenterGraphElementPojo> it = centers.iterator();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                it.next().getGraphElement()
        ).remove();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                it.next().getGraphElement()
        ).remove();
        assertTrue(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user).isEmpty()
        );
    }

    @Test
    public void removing_center_does_not_remove_all_centers() {
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        ).updateLastCenterDate();
        assertThat(
                centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user).size(),
                Is.is(2)
        );
        CenterGraphElementPojo centerGraphElementPojo = new CenterGraphElementPojo(
                vertexB.uri()
        );
        centerGraphElementOperatorFactory.usingFriendlyResource(
                centerGraphElementPojo.getGraphElement()
        ).remove();
        List<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.usingDefaultLimits().getPublicAndPrivateForOwner(user);
        assertThat(
                centers.size(),
                Is.is(1)
        );
        assertThat(
                centers.iterator().next().getGraphElement().uri(),
                Is.is(vertexA.uri())
        );
    }
}
