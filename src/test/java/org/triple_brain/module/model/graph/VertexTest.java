package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.ExternalFriendlyResource;
import org.triple_brain.module.model.suggestion.PersistedSuggestion;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.graph.scenarios.TestScenarios;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;


/*
* Copyright Mozilla Public License 1.1
*/
public class VertexTest extends AdaptableGraphComponentTest {
    @Test
    public void can_update_label() {
        Edge newEdge = vertexA.addVertexAndRelation();
        Vertex vertex = newEdge.destinationVertex();
        vertex.label("Ju-Ji-Tsu");
        assertThat(vertex.label(), is("Ju-Ji-Tsu"));
    }

    @Test
    public void can_add_vertex_and_relation() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        Edge edge = vertexA.addVertexAndRelation();

        assertThat(edge, is(not(nullValue())));
        assertTrue(edge.hasLabel());

        Integer newNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(newNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices + 2));
        assertTrue(vertexA.hasEdge(edge));

        assertThat(edge.sourceVertex().id(), is(vertexA.id()));

        Vertex destinationVertex = edge.destinationVertex();
        assertThat(destinationVertex, is(not(nullValue())));
        assertTrue(destinationVertex.hasLabel());
    }

    @Test
    public void can_remove_a_vertex() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();

        String vertexBId = vertexB.id();

        assertTrue(userGraph.haveElementWithId(vertexBId));
        vertexB.remove();
        assertFalse(userGraph.haveElementWithId(vertexBId));

        Integer updatedNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 3));
    }

    @Test
    public void can_add_an_additional_type_to_vertex() throws Exception {
        assertTrue(
                vertexA.getAdditionalTypes().isEmpty()
        );
        vertexA.addType(
                testScenarios.personType()
        );
        assertFalse(
                vertexA.getAdditionalTypes().isEmpty()
        );
    }

    @Test
    public void on_vertex_delete_its_additional_type_are_removed_from_the_model() {
        ExternalFriendlyResource personType = testScenarios.personType();
        vertexA.addType(
                personType
        );
        assertTrue(
                graphContainsLabel(
                        personType.label()
                )
        );
        vertexA.remove();
        assertFalse(
                graphContainsLabel(
                        personType.label()
                )
        );
    }

    @Test
    public void can_add_multiple_additional_types_to_a_vertex() throws Exception {
        assertTrue(
                vertexA.getAdditionalTypes().isEmpty()
        );
        vertexA.addType(
                testScenarios.personType()
        );
        vertexA.addType(
                testScenarios.computerScientistType()
        );
        assertThat(
                vertexA.getAdditionalTypes().size(),
                is(2)
        );
    }

    @Test
    public void can_remove_an_additional_type_to_vertex() throws Exception {
        vertexA.addType(
                testScenarios.personType()
        );
        ExternalFriendlyResource computerScientistType = testScenarios.computerScientistType();
        vertexA.addType(
                computerScientistType
        );
        assertThat(
                vertexA.getAdditionalTypes().size(),
                is(2)
        );
        vertexA.removeFriendlyResource(testScenarios.personType());
        assertThat(
                vertexA.getAdditionalTypes().size(),
                is(1)
        );
        ExternalFriendlyResource remainingType = vertexA.getAdditionalTypes().iterator().next();
        assertThat(
                remainingType.label(),
                is(computerScientistType.label())
        );
    }

    @Test
    public void when_removing_an_external_resource_the_suggestions_that_depend_on_it_are_removed(){
        vertexA.addType(
                TestScenarios.person()
        );
        vertexA.addType(
                TestScenarios.event()
        );
        vertexA.addSuggestions(
                TestScenarios.nameSuggestion(),
                TestScenarios.startDateSuggestion()
        );
        assertTrue(
                TestScenarios.nameSuggestion().origins().iterator().next()
                        .isTheIdentificationWithUri(
                                TestScenarios.person().uri()
                        )
        );
        assertThat(vertexA.suggestions().size(), is(2));
        vertexA.removeFriendlyResource(
                TestScenarios.person()
        );
        assertThat(vertexA.suggestions().size(), is(1));
        PersistedSuggestion remainingSuggestion = vertexA.suggestions().iterator().next();
        assertSame(
                remainingSuggestion.get().sameAsUri().toString(),
                TestScenarios.startDateSuggestion().sameAsUri().toString()
        );
    }

    @Test
    public void can_add_suggestions_to_a_vertex() throws Exception {
        assertTrue(vertexA.suggestions().isEmpty());
        vertexA.addSuggestions(
                testScenarios.startDateSuggestion()
        );
        assertFalse(vertexA.suggestions().isEmpty());
        PersistedSuggestion getSuggestion = vertexA.suggestions().iterator().next();
        assertThat(getSuggestion.get().label(), is("Start date"));
    }

    @Test
    public void on_vertex_remove_suggestions_properties_are_also_delete_from_the_model() {
        Suggestion startDateSuggestion = testScenarios.startDateSuggestion();
        vertexA.addSuggestions(
                startDateSuggestion
        );
        assertTrue(
                graphContainsLabel(
                        startDateSuggestion.label()
                )
        );
        assertTrue(
                userGraph.haveElementWithId(
                        startDateSuggestion.sameAsUri().toString()
                )
        );
        assertTrue(
                userGraph.haveElementWithId(
                        startDateSuggestion.domainUri().toString()
                )
        );
        vertexA.remove();
        assertFalse(
                graphContainsLabel(
                        startDateSuggestion.label()
                )
        );
        assertFalse(
                userGraph.haveElementWithId(
                        startDateSuggestion.sameAsUri().toString()
                )
        );
        assertFalse(
                userGraph.haveElementWithId(
                        startDateSuggestion.domainUri().toString()
                )
        );
    }

    @Test
    public void can_add_same_as(){
        Edge newEdge = vertexA.addVertexAndRelation();
        Vertex newVertex = newEdge.destinationVertex();
        newVertex.label("Tim Berners Lee");
        assertTrue(newVertex.getSameAs().isEmpty());
        newVertex.addSameAs(testScenarios.timBernersLee());
        assertFalse(newVertex.getSameAs().isEmpty());
    }

    @Test
    public void deleting_a_vertex_does_not_delete_its_identifications_in_the_graph(){
        ExternalFriendlyResource timBernersLee = testScenarios.timBernersLee();
        assertFalse(
                userGraph.haveElementWithId(
                        timBernersLee.uri().toString()
                )
        );
        vertexA.addSameAs(
                timBernersLee
        );
        assertTrue(
                userGraph.haveElementWithId(
                        timBernersLee.uri().toString()
                )
        );
        vertexA.remove();
        assertTrue(
                userGraph.haveElementWithId(
                        timBernersLee.uri().toString()
                )
        );
    }

    @Test
    public void can_assign_the_same_identification_to_2_vertices(){
        ExternalFriendlyResource timBernersLee = testScenarios.timBernersLee();
        vertexA.addSameAs(
                timBernersLee
        );
        vertexB.addSameAs(
                timBernersLee
        );
        assertTrue(vertexA.getSameAs().iterator().next().equals(timBernersLee));
        assertTrue(vertexB.getSameAs().iterator().next().equals(timBernersLee));
    }

    @Test
    public void can_get_same_as(){
        Edge newEdge = vertexA.addVertexAndRelation();
        Vertex newVertex = newEdge.destinationVertex();
        newVertex.label("Tim Berners Lee");
        assertTrue(newVertex.getSameAs().isEmpty());
        newVertex.addSameAs(testScenarios.timBernersLee());
        ExternalFriendlyResource sameAs = newVertex.getSameAs().iterator().next();
        assertThat(sameAs.label(), is(testScenarios.timBernersLee().label()));
    }

    @Test
    public void can_test_if_vertex_has_destination_vertex(){
        assertFalse(vertexA.hasDestinationVertex(vertexC));
        vertexA.addRelationToVertex(vertexC);
        assertTrue(vertexA.hasDestinationVertex(vertexC));
    }

    @Test
    public void source_vertex_is_not_a_destination_vertex(){
        vertexA.addRelationToVertex(vertexC);
        assertTrue(vertexA.hasDestinationVertex(vertexC));
        assertFalse(vertexC.hasDestinationVertex(vertexA));
    }

}
