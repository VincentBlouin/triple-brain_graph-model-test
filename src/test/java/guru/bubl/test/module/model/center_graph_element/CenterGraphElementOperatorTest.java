/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.module.model.center_graph_element.CenterGraphElement;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CenterGraphElementOperatorTest extends ModelTestResources {


    @Test
    public void can_increment_number_of_visits() {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        CenterGraphElement centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        Integer nbCenterGraphElements = centerGraphElement.getNumberOfVisits();
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
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
        Set<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        Integer nbCenterGraphElements = centerGraphElements.size();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                meta
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centerGraphElements.size(),
                is(nbCenterGraphElements + 1)
        );
    }

    @Test
    public void cannot_get_metas_of_another_user() {
        IdentifierPojo meta = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        Set<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicOnlyOfType();
        assertThat(
                centerGraphElements.size(),
                is(0)
        );
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                meta
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicOnlyOfType();
        assertThat(
                centerGraphElements.size(),
                is(0)
        );
    }

    @Test
    public void can_get_center_elements_of_type_relation() {
        Edge edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        Set<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        Integer nbCenterGraphElements = centerGraphElements.size();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                edgeBetweenBAndC
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centerGraphElements.size(),
                is(nbCenterGraphElements + 1)
        );
    }

    @Test
    public void returns_colors(){
        vertexA.setColors("patatie");
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        CenterGraphElement centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        assertThat(
                centerGraphElement.getGraphElement().getColors(),
                is("patatie")
        );
    }
}
