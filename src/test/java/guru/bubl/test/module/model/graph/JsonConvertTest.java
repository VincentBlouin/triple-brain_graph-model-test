/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.vertex.*;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.SubGraphJson;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class JsonConvertTest extends ModelTestResources {

    @Test
    public void can_convert_vertex_to_and_from() {
        JSONObject vertexAJson = VertexInSubGraphJson.toJson(
                userGraph.aroundVertexUriInShareLevels(vertexA.uri(), ShareLevel.allShareLevelsInt).vertexWithIdentifier(vertexA.uri())
        );
        VertexInSubGraphPojo vertexA = VertexInSubGraphJson.fromJson(
                vertexAJson
        );
        assertThat(
                vertexA.label(), is("vertex A")
        );
    }

    @Test
    public void converting_edge_to_json_throws_no_error() {
        Edge edge = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        EdgeJson.toJson(
                userGraph.aroundVertexUriInShareLevels(vertexA.uri(), ShareLevel.allShareLevelsInt).edgeWithIdentifier(edge.uri())
        );
    }

    @Test
    public void can_convert_subgraph_to_and_from() {
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                10,
                ShareLevel.allShareLevelsInt
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
    public void subgraph_vertices_are_a_json_object_with_uri_as_key() throws Exception {
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                10,
                ShareLevel.allShareLevelsInt
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

}

