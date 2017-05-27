/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.module.model.graph.GraphElementType;
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
                ).getPublicAndPrivate().size(),
                is(
                        1
                )
        );
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        anotherUser
                ).getPublicAndPrivate().size(),
                is(
                        1
                )
        );
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexC
        ).updateLastCenterDate();
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicAndPrivate().size(),
                is(
                        2
                )
        );
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        anotherUser
                ).getPublicAndPrivate().size(),
                is(
                        1
                )
        );
    }

    @Test
    public void can_get_only_public_bubbles(){
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicOnlyOfType().size(),
                is(
                        0
                )
        );
        vertexA.makePublic();
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicOnlyOfType().size(),
                is(
                        1
                )
        );
    }

    @Test
    public void does_not_return_public_bubble_if_not_a_center_bubble(){
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicOnlyOfType().size(),
                is(
                        0
                )
        );
        vertexB.makePublic();
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicOnlyOfType().size(),
                is(
                        0
                )
        );
    }
}
