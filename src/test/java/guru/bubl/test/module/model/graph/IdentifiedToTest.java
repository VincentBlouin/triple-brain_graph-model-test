/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.Identification;
import guru.bubl.test.module.utils.ModelTestResources;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IdentifiedToTest extends ModelTestResources {

    User someUser;

    @Before
    public void beforeRelatedIdentificationTest(){
        someUser = User.withEmailAndUsername("a", "b");
    }

    @Test
    public void can_get_related_identification() {
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).isEmpty()
        );
        VertexOperator aVertexRepresentingATshirt = testScenarios.createAVertex(
                someUser
        );
        aVertexRepresentingATshirt.addGenericIdentification(
                modelTestScenarios.tShirt()
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).contains(
                        aVertexRepresentingATshirt
                )
        );
    }

    @Test
    public void can_have_multiple_related_resources_for_one_identification() {
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).isEmpty()
        );
        VertexOperator aVertexRepresentingATshirt = testScenarios.createAVertex(
                someUser
        );
        aVertexRepresentingATshirt.addGenericIdentification(
                modelTestScenarios.tShirt()
        );
        VertexOperator anotherVertexRepresentingATshirt = testScenarios.createAVertex(
                someUser
        );
        anotherVertexRepresentingATshirt.addGenericIdentification(
                modelTestScenarios.tShirt()
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).contains(
                        aVertexRepresentingATshirt
                )
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).contains(
                        anotherVertexRepresentingATshirt
                )
        );
    }

    @Test
    public void can_remove_related_identification() {
        VertexOperator anotherVertexRepresentingATshirt = testScenarios.createAVertex(
                someUser
        );
        Identification tshirtIdentification = modelTestScenarios.tShirt();
        tshirtIdentification = anotherVertexRepresentingATshirt.addGenericIdentification(
                tshirtIdentification
        );
        assertFalse(
                identifiedTo.getForIdentificationAndUser(
                        tshirtIdentification,
                        someUser
                ).isEmpty()
        );
        anotherVertexRepresentingATshirt.removeIdentification(
                tshirtIdentification
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        tshirtIdentification,
                        someUser
                ).isEmpty()
        );
    }

    @Test
    public void cannot_get_related_identifications_of_another_user() {
        VertexOperator user1Resource = testScenarios.createAVertex(
                someUser
        );
        user1Resource.addSameAs(
                modelTestScenarios.tShirt()
        );
        assertFalse(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).isEmpty()
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        User.withEmailAndUsername("c", "d")
                ).isEmpty()
        );
    }

    @Test
    public void adding_related_identification_doesnt_overwrite_other_user_related_identifications_for_same_identification() {
        VertexOperator user1Resource = testScenarios.createAVertex(
                someUser
        );
        user1Resource.addType(
                modelTestScenarios.tShirt()
        );
        User otherUser = User.withEmailAndUsername("c", "d");
        VertexOperator otherUserResource = testScenarios.createAVertex(
                        otherUser
                );

        otherUserResource.addType(
                modelTestScenarios.tShirt()
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).contains(user1Resource)
        );
        assertFalse(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).contains(otherUserResource)
        );
    }

    @Test
    public void removing_related_identification_doesnt_erase_other_user_related_identifications_for_same_identification() {
        User someUser = User.withEmailAndUsername("a", "b");
        VertexOperator user1Resource = testScenarios.createAVertex(
                someUser
        );
        user1Resource.addGenericIdentification(
                modelTestScenarios.tShirt()
        );
        User otherUser = User.withEmailAndUsername("c", "d");
        VertexOperator otherUserResource = testScenarios.createAVertex(
                otherUser
        );
        Identification otherUserIdentification = otherUserResource.addGenericIdentification(
                modelTestScenarios.tShirt()
        );
        otherUserResource.removeIdentification(
                otherUserIdentification
        );
        assertTrue(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ).contains(user1Resource)
        );
    }
}
