/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

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

    @Test
    public void includes_context(){
        graphIndexer.indexVertex(
                vertexA
        );
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        assertThat(
                centerGraphElement.getContext().values().iterator().next(),
                is("vertex B")
        );
    }

    @Test
    public void includes_public_context_only_if_fetching_plublic_only_centers(){
        vertexA.makePublic();
        vertexB.makePublic();
        graphIndexer.indexVertex(
                vertexA
        );
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicOnlyOfType().iterator().next();
        assertFalse(
                centerGraphElement.getContext().isEmpty()
        );
        vertexB.makePrivate();
        graphIndexer.indexVertex(
                vertexA
        );
        centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicOnlyOfType().iterator().next();
        assertTrue(
                centerGraphElement.getContext().isEmpty()
        );
    }

    @Test
    public void can_remove_centers(){
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        Set<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertFalse(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicAndPrivate().isEmpty()
        );
        centerGraphElementsOperatorFactory.forUser(
                user
        ).removeCenterGraphElements(centers);
        assertTrue(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicAndPrivate().isEmpty()
        );
    }

    @Test
    public void removing_center_does_not_remove_all_centers(){
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        ).updateLastCenterDate();
        assertThat(
                centerGraphElementsOperatorFactory.forUser(
                        user
                ).getPublicAndPrivate().size(),
                is(2)
        );
        CenterGraphElementPojo centerGraphElementPojo = new CenterGraphElementPojo(
                vertexB.uri()
        );
        Set<CenterGraphElementPojo> centers = new HashSet<>();
        centers.add(
                centerGraphElementPojo
        );
        centerGraphElementsOperatorFactory.forUser(
                user
        ).removeCenterGraphElements(centers);
        centers = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centers.size(),
                is(1)
        );
        assertThat(
                centers.iterator().next().getGraphElement().uri(),
                is(vertexA.uri())
        );
    }

    @Test
    public void cannot_remove_centers_of_another_user(){
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        Set<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centers.size(),
                is(1)
        );
        centerGraphElementsOperatorFactory.forUser(
                anotherUser
        ).removeCenterGraphElements(centers);
        centers = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centers.size(),
                is(1)
        );
    }

    @Test
    public void can_remove_multiple_centers_at_once(){
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).updateLastCenterDate();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        ).updateLastCenterDate();
        Set<CenterGraphElementPojo> centers = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centers.size(),
                is(2)
        );
        centerGraphElementsOperatorFactory.forUser(
                user
        ).removeCenterGraphElements(centers);
        centers = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centers.size(),
                is(0)
        );
    }

    @Test
    public void returns_number_of_references_of_center_metas(){
        IdentifierPojo meta = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                meta
        ).updateLastCenterDate();
        Set<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        CenterGraphElementPojo centerMeta = null;
        for(CenterGraphElementPojo centerGraphElement: centerGraphElements){
            if(UserUris.isUriOfAnIdentifier(centerGraphElement.getGraphElement().uri())){
                centerMeta = centerGraphElement;
            }
        }
        assertThat(
                centerMeta.getNbReferences(),
                is(1)
        );
    }
}
