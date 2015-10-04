/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CenterGraphElementsOperatorTest extends ModelTestResources {

    @Test
    public void does_not_return_center_elements_of_another_user() {
        centerGraphElementOperatorFactory.usingGraphElement(
                vertexC
        ).incrementNumberOfVisits();
        assertFalse(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).get().isEmpty()
        );
        assertTrue(
                centerGraphElementsOperatorFactory.forUser(
                        anotherUser
                ).get().isEmpty()
        );
    }
}
