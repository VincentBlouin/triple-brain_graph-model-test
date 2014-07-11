package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jFriendlyResource;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class GraphElementOperatorTest extends AdaptableGraphComponentTest {
    @Test
    public void cannot_identify_to_self() {
        String errorMessage = "identification cannot be the same";
        GraphElementOperator vertexAGraphElement = vertexA;
        try {
            vertexAGraphElement.addGenericIdentification(vertexA);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
        try {
            vertexAGraphElement.addSameAs(vertexA);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
        try {
            vertexAGraphElement.addType(vertexA);
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
    }

    @Test
    public void cannot_have_same_identification_twice() {
        GraphElementOperator vertexAGraphElement = vertexA;
        Integer numberOfGenericIdentifications = vertexAGraphElement.getGenericIdentifications().size();
        vertexAGraphElement.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getGenericIdentifications().size(),
                is(
                        numberOfGenericIdentifications + 1
                )
        );
        Integer numberOfSameAs = vertexAGraphElement.getSameAs().size();
        vertexAGraphElement.addSameAs(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addSameAs(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getSameAs().size(),
                is(
                        numberOfSameAs + 1
                )
        );
        Integer numberOfTypes = vertexAGraphElement.getAdditionalTypes().size();
        vertexAGraphElement.addType(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addType(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getAdditionalTypes().size(),
                is(
                        numberOfTypes + 1
                )
        );
    }

    @Test
    public void a_graph_element_is_not_identified_to_itself_if_used_as_an_identification_for_another_element() {
        assertTrue(vertexB.getIdentifications().isEmpty());
        GraphElementOperator vertexAGraphElement = vertexA;
        IdentificationPojo vertexBPojo = new IdentificationPojo(
                vertexB.uri(),
                new FriendlyResourcePojo(
                        vertexB.uri(),
                        vertexB.label()
                )
        );
        vertexAGraphElement.addSameAs(vertexBPojo);
        assertTrue(vertexB.getIdentifications().isEmpty());
    }

    @Test
    public void adding_identification_returns_identification_created_fields(){
        IdentificationPojo identification = vertexA.addSameAs(
                modelTestScenarios.timBernersLee()
        );
        assertNotNull(
                identification.creationDate()
        );
        assertNotNull(
                identification.lastModificationDate()
        );
    }

    @Test
    public void users_identification_have_their_own_uri_for_same_identification(){
        IdentificationPojo identificationOfAnotherUser = vertexOfAnotherUser.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertTrue(
                identificationOfAnotherUser.getExternalResourceUri().equals(
                        identification.getExternalResourceUri()
                )
        );
        assertFalse(
                identificationOfAnotherUser.uri().equals(
                        identification.uri()
                )
        );
    }

    @Test
    public void uri_of_identification_does_not_change_if_added_twice(){
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        IdentificationPojo identification2 = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertTrue(identification.uri().equals(identification2.uri()));
    }

}
