package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.Suggestion;

import java.util.HashSet;
import java.util.Set;

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
    public void on_vertex_delete_its_additional_type_label_is_removed_from_the_model() {
        FriendlyResource personType = testScenarios.personType();
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
    public void on_vertex_remove_suggestions_properties_are_also_delete_from_the_model() {
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        Suggestion startDateSuggestion = testScenarios.startDateSuggestion();
        suggestions.add(
                startDateSuggestion
        );
        vertexA.suggestions(
                suggestions
        );
        assertTrue(
                graphContainsLabel(
                        startDateSuggestion.label()
                )
        );
        assertTrue(
                userGraph.haveElementWithId(
                        startDateSuggestion.typeUri().toString()
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
                        startDateSuggestion.typeUri().toString()
                )
        );
        assertFalse(
                userGraph.haveElementWithId(
                        startDateSuggestion.domainUri().toString()
                )
        );
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
        FriendlyResource computerScientistType = testScenarios.computerScientistType();
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
        FriendlyResource remainingType = vertexA.getAdditionalTypes().iterator().next();
        assertThat(
                remainingType.label(),
                is(computerScientistType.label())
        );
    }

    @Test
    public void can_set_suggestions_of_vertex() throws Exception {
        assertTrue(vertexA.suggestions().isEmpty());
        Set<Suggestion> suggestions = new HashSet<Suggestion>();
        suggestions.add(
                testScenarios.startDateSuggestion()
        );
        vertexA.suggestions(suggestions);
        assertFalse(vertexA.suggestions().isEmpty());
        Suggestion getSuggestion = vertexA.suggestions().iterator().next();
        assertThat(getSuggestion.label(), is("Start date"));
    }
}
