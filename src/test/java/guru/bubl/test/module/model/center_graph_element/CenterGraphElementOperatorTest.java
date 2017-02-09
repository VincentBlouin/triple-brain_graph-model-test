/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.module.model.center_graph_element.CenterGraphElement;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CenterGraphElementOperatorTest extends ModelTestResources{


    @Test
    public void can_increment_number_of_visits(){
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingGraphElement(
                vertexA
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        CenterGraphElement centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        assertThat(
                centerGraphElement.getNumberOfVisits(),
                is(2)
        );
        centerGraphElementOperator.incrementNumberOfVisits();
        centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        assertThat(
                centerGraphElement.getNumberOfVisits(),
                is(3)
        );
    }
}
