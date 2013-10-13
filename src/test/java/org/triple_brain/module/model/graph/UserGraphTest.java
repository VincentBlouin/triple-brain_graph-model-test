package org.triple_brain.module.model.graph;

import org.junit.Ignore;
import org.junit.Test;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.graph.exceptions.InvalidDepthOfSubVerticesException;
import org.triple_brain.module.model.graph.exceptions.NonExistingResourceException;
import org.triple_brain.module.model.suggestion.Suggestion;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class UserGraphTest extends AdaptableGraphComponentTest {
    public static final int DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;
    @Test
    public void can_get_graph_with_default_center_vertex() {
        SubGraph graph = userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(graph.numberOfEdges(), is(2));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                vertexB.uri());
        assertThat(graph, is(not(nullValue())));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexB.uri());
        assertThat(graph.numberOfEdges(), is(2));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(centerVertex.label(), is("vertex B"));
    }

    @Test
    public void can_get_circular_graph_with_default_center_vertex() {
        vertexC.addRelationToVertex(vertexA);
        SubGraph graph = userGraph.graphWithDefaultVertexAndDepth(DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES);
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices()));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexA.uri());
        assertThat(centerVertex.label(), is("vertex A"));
    }

    @Test
    public void can_get_a_limited_graph_with_default_center_vertex() throws Exception {
        SubGraph subGraph = userGraph.graphWithDefaultVertexAndDepth(2);
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDefaultVertexAndDepth(1);
        assertThat(subGraph.numberOfEdges(), is(1));
        assertThat(subGraph.numberOfVertices(), is(2));
        assertFalse(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(vertexA));
    }

    @Test

    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                vertexC.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(2));
        assertThat(subGraph.numberOfEdges(), is(1));
        assertFalse(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test

    public void can_get_sub_graph_of_destination_vertex_of_center_vertex() {
        Edge newEdge = vertexC.addVertexAndRelation();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2, vertexB.uri()
        );
        assertThat(subGraph.numberOfEdges(), is(3));
        assertThat(subGraph.numberOfVertices(), is(4));

        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex() {
        SubGraph subGraph;
        Edge newEdge = vertexA.addVertexAndRelation();
        subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2, vertexB.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(4));
        assertThat(subGraph.numberOfEdges(), is(3));

        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex_having_also_a_circular_relation() {
        vertexC.addRelationToVertex(vertexA);
        Edge edgeGoingOutOfC = vertexC.addVertexAndRelation();

        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2,
                vertexA.uri()
        );
        assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = userGraph.graphWithDefaultVertexAndDepth(0);
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDepthAndCenterVertexId(
                0, vertexB.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexB));
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_default_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDefaultVertexAndDepth(-1);
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexA.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_custom_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDepthAndCenterVertexId(-1, vertexB.uri());
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexB.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_non_existing_center_vertex() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        try {
            userGraph.graphWithDepthAndCenterVertexId(1, Uris.get("/invalid_uri"));
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: /invalid_uri not found"));
        }
        assertThat(numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    @Ignore("to implement later, not a priority")
    public void can_get_rdf_xml_representation_of_graph() {
        assertThat(userGraph.toRdfXml(), is(not(nullValue())));
    }

    @Test
    public void vertex_additional_type_label_is_in_sub_graph(){
        assertTrue(
                vertexA.getAdditionalTypes().isEmpty()
        );
        vertexA.addType(
                modelTestScenarios.personType()
        );
        SubGraph subGraph = wholeGraphAroundDefaultCenterVertex();
        vertexA = subGraph.vertexWithIdentifier(vertexA.uri());
        FriendlyResource additionalType = vertexA.getAdditionalTypes().iterator().next();
        assertThat(additionalType.label(), is("Person"));
    }

    @Test
    public void vertex_suggestions_have_their_properties_sub_graph(){
        vertexA.addSuggestions(
                modelTestScenarios.startDateSuggestion()
        );
        SubGraph subGraph = wholeGraphAroundDefaultCenterVertex();
        vertexA = subGraph.vertexWithIdentifier(vertexA.uri());
        Suggestion suggestion = vertexA.suggestions().iterator().next();
        assertThat(suggestion.label(), is("Start date"));
    }

    @Test
    public void distance_from_center_vertex_is_set_for_each_vertex_in_sub_graph(){
       assertThat(
               vertexInWholeGraph(vertexA).minDistanceFromCenterVertex(),
               is(0)
       );
        assertThat(
                vertexInWholeGraph(vertexB).minDistanceFromCenterVertex(),
                is(1)
        );
        assertThat(
                vertexInWholeGraph(vertexC).minDistanceFromCenterVertex(),
                is(2)
        );
    }

    @Test
    public void the_minimum_distance_from_center_vertex_is_returned(){
        assertThat(
                vertexInWholeGraph(vertexC).minDistanceFromCenterVertex(),
                is(2)
        );
        vertexC().addRelationToVertex(vertexA());
        assertThat(
                vertexInWholeGraph(vertexC).minDistanceFromCenterVertex(),
                is(1)
        );
    }

    @Test
    public void can_create_new_vertex_out_of_nothing(){
        Vertex vertex = userGraph.createVertex();
        SubGraph subGraph = wholeGraph();
        assertTrue(subGraph.containsVertex(vertex));
    }
}
