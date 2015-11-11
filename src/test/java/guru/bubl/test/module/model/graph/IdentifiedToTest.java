/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.Identification;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.VertexSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class IdentifiedToTest extends ModelTestResources {

    User someUser;

    @Before
    public void beforeRelatedIdentificationTest() {
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
                searchResultsHaveResource(
                        identifiedTo.getForIdentificationAndUser(
                                modelTestScenarios.tShirt(),
                                someUser
                        ),
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
                searchResultsHaveResource(
                        identifiedTo.getForIdentificationAndUser(
                                modelTestScenarios.tShirt(),
                                someUser
                        ),
                        aVertexRepresentingATshirt
                )
        );
        assertTrue(
                searchResultsHaveResource(
                        identifiedTo.getForIdentificationAndUser(
                                modelTestScenarios.tShirt(),
                                someUser
                        ),
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
        ).values().iterator().next();
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
                searchResultsHaveResource(
                        identifiedTo.getForIdentificationAndUser(
                                modelTestScenarios.tShirt(),
                                someUser
                        ),
                        user1Resource
                )
        );
        assertFalse(
                searchResultsHaveResource(
                        identifiedTo.getForIdentificationAndUser(
                                modelTestScenarios.tShirt(),
                                someUser
                        ),
                        otherUserResource
                )
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
        ).values().iterator().next();
        otherUserResource.removeIdentification(
                otherUserIdentification
        );
        searchResultsHaveResource(
                identifiedTo.getForIdentificationAndUser(
                        modelTestScenarios.tShirt(),
                        someUser
                ),
                user1Resource
        );
    }

    @Test
    public void includes_the_surrounding_edges_of_a_vertex_related_to_the_identification(){
        VertexOperator vertex = testScenarios.createAVertex(
                someUser
        );
        vertex.addGenericIdentification(
                new ModelTestScenarios().event()
        );
        VertexSearchResult searchResult = (VertexSearchResult) identifiedTo.getForIdentificationAndUser(
                new ModelTestScenarios().event(),
                someUser
        ).iterator().next();
        assertTrue(
                searchResult.getProperties().isEmpty()
        );
        EdgeOperator newEdge = vertex.addVertexAndRelation();
        searchResult = (VertexSearchResult) identifiedTo.getForIdentificationAndUser(
                new ModelTestScenarios().event(),
                someUser
        ).iterator().next();
        assertFalse(
                searchResult.getProperties().isEmpty()
        );
        assertTrue(
                searchResult.getProperties().containsValue(
                        newEdge
                )
        );
    }


    public Boolean searchResultsHaveResource(Set<GraphElementSearchResult> searchResults, FriendlyResource resource) {
        for (GraphElementSearchResult searchResult : searchResults) {
            if (searchResult.getGraphElement().equals(resource)) {
                return true;
            }
        }
        return false;
    }
}
