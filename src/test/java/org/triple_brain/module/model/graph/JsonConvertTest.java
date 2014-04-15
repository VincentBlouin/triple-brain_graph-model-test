package org.triple_brain.module.model.graph;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphPojo;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.EdgeJson;
import org.triple_brain.module.model.json.graph.SubGraphJson;
import org.triple_brain.module.model.json.graph.VertexInSubGraphJson;
import org.triple_brain.module.model.suggestion.SuggestionPojo;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*
* Copyright Mozilla Public License 1.1
*/
public class JsonConvertTest extends AdaptableGraphComponentTest {

    @Inject
    protected VertexFactory vertexFactory;

    @Test
    public void can_convert_vertex_to_and_from() {
        JSONObject vertexAJson = VertexInSubGraphJson.toJson(
                vertexInWholeConnectedGraph(vertexA)
        );
        VertexInSubGraphPojo vertexA = VertexInSubGraphJson.fromJson(
                vertexAJson
        );
        assertThat(
                vertexA.label(), is("vertex A")
        );
    }

    @Test
    public void can_convert_vertex_included_vertices_and_edges() {
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        newVertex.addRelationToVertex(vertexA);
        JSONObject newVertexJson = VertexInSubGraphJson.toJson(
                vertexInWholeConnectedGraph(
                        newVertex
                )
        );
        VertexInSubGraphPojo newVertexPojo = VertexInSubGraphJson.fromJson(
                newVertexJson
        );
        assertThat(
                newVertexPojo.getIncludedVertices().size(),
                is(2)
        );
        assertThat(
                newVertexPojo.getIncludedEdges().size(),
                is(1)
        );
    }

    @Test
    public void can_convert_when_having_suggestions() {
        Set<SuggestionPojo> suggestions = new HashSet<>(
                Arrays.asList(
                        modelTestScenarios.nameSuggestionFromPersonIdentification(),
                        modelTestScenarios.startDateSuggestionFromEventIdentification()
                )
        );
        vertexA.addSuggestions(
                suggestions
        );
        JSONObject vertexAJson = VertexInSubGraphJson.toJson(
                vertexInWholeConnectedGraph(vertexA)
        );
        VertexInSubGraphPojo vertexAPojo = VertexInSubGraphJson.fromJson(
                vertexAJson
        );
        assertThat(
                vertexAPojo.suggestions().size(),
                is(2)
        );
    }

    @Test
    public void converting_edge_to_json_throws_no_error() {
        EdgeJson.toJson(
                edgeInWholeGraph(
                        vertexA.edgeThatLinksToDestinationVertex(vertexB)
                )
        );
    }

    @Test
    public void can_convert_subgraph_to_and_from() {
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                10
        );
        JSONObject subGraphJson = SubGraphJson.toJson(
                subGraph
        );
        subGraph = SubGraphJson.fromJson(
                subGraphJson
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    @Test
    public void subgraph_vertices_are_a_json_object_with_uri_as_key()throws Exception{
        SubGraphPojo subGraph = userGraph.graphWithDefaultVertexAndDepth(
                10
        );
        JSONObject subGraphJson = SubGraphJson.toJson(
                subGraph
        );
        assertTrue(subGraphJson.has("vertices"));
        JSONObject vertices = subGraphJson.getJSONObject(
                "vertices"
        );
        JSONObject vertexAJson = vertices.getJSONObject(
                vertexA.uri().toString()
        );
        Vertex vertexAFromJson = VertexInSubGraphJson.fromJson(
                vertexAJson
        );
        assertThat(
                vertexAFromJson.label(),
                is("vertex A")
        );
    }

    @Test
    public void included_vertices_are_a_json_object_mapped_with_uri() throws Exception{
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        newVertex.addRelationToVertex(vertexA);
        JSONObject newVertexJson = VertexInSubGraphJson.toJson(
                vertexInWholeConnectedGraph(newVertex)
        );
        JSONObject includedVertices = newVertexJson.getJSONObject(
                "vertex"
        ).getJSONObject("includedVertices");
        Vertex vertexBFromJson = VertexInSubGraphJson.fromJson(
                includedVertices.getJSONObject(
                vertexB.uri().toString()
        ));
        assertTrue(
                vertexBFromJson.equals(vertexB)
        );
    }

    private Set<Vertex> vertexBAndC() {
        Set<Vertex> vertexBAndC = new HashSet<>();
        vertexBAndC.add(vertexB);
        vertexBAndC.add(vertexC);
        return vertexBAndC;
    }

    private Set<Edge> edgeBetweenBAndCInSet() {
        Set<Edge> edges = new HashSet<>();
        edges.add(
                vertexB.edgeThatLinksToDestinationVertex(
                        vertexC
                )
        );
        return edges;
    }
}

