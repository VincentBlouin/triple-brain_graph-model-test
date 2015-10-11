/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CenterGraphElementsOperatorTest extends ModelTestResources {

    @Test
    public void does_not_return_center_elements_of_another_user() {
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).get().size(),
                is(
                        1
                )
        );
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        anotherUser
                ).get().size(),
                is(
                        1
                )
        );
        centerGraphElementOperatorFactory.usingGraphElement(
                vertexC
        ).incrementNumberOfVisits();
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).get().size(),
                is(
                        2
                )
        );
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        anotherUser
                ).get().size(),
                is(
                        1
                )
        );
    }
}
