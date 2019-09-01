/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphOperator;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class WholeGraphTest extends ModelTestResources {

    @Test
    public void there_are_no_duplicates_in_vertices() {
        assertFalse(wholeGraph.getAllVertices().isEmpty());
        Set<Vertex> visitedVertices = new HashSet<Vertex>();
        Set<VertexInSubGraphOperator> vertices = wholeGraph.getAllVertices();
        for (VertexInSubGraph vertex : vertices) {
            if (visitedVertices.contains(vertex)) {
                fail();
            }
            visitedVertices.add(vertex);
        }
    }


    @Test
    public void can_get_all_vertices_of_single_user() {
        Vertex anotherUserVertex = neo4jUserGraphFactory.withUser(anotherUser).createVertex();
        Vertex newUserVertex = neo4jUserGraphFactory.withUser(user).createVertex();
        Set<VertexInSubGraphOperator> allUserVertices = wholeGraph.getAllVerticesOfUser(user);
        assertTrue(
                allUserVertices.contains(
                        newUserVertex
                )
        );
        assertFalse(
                allUserVertices.contains(
                        anotherUserVertex
                )
        );
    }

    @Test
    public void can_get_all_vertices() {
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(4)
        );
    }

    @Test
    public void schemas_are_not_included_in_vertices() {
        createSchema();
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(4)
        );
    }

    @Test
    public void can_get_edges() {
        assertThat(
                wholeGraph.getAllEdges().size(),
                is(2)
        );
    }

    @Test
    public void can_get_all_edges_of_single_user() {
        VertexOperator anotherUserVertex = vertexFactory.withUri(
                neo4jUserGraphFactory.withUser(anotherUser).createVertex().uri()
        );
        Edge anotherUserEdge = anotherUserVertex.addVertexAndRelation();
        VertexOperator newUserVertex = vertexFactory.withUri(
                neo4jUserGraphFactory.withUser(user).createVertex().uri()
        );
        Edge newUserEdge = newUserVertex.addVertexAndRelation();
        Set<EdgeOperator> allUserEdges = wholeGraph.getAllEdgesOfUser(user);
        assertTrue(
                allUserEdges.contains(
                        newUserEdge
                )
        );
        assertFalse(
                allUserEdges.contains(
                        anotherUserEdge
                )
        );
    }

    @Test
    @Ignore("schema feature is suspended")
    public void can_get_schemas() {
        createSchema();
        createSchema();
        createSchema();
        createSchema();
        assertThat(
                wholeGraph.getAllSchemas().size(),
                is(4)
        );
    }

    @Test
    public void can_get_all_graph_elements() {
        assertThat(
                wholeGraph.getAllGraphElements().size(),
                is(6)
        );
    }

    @Test
    public void can_get_all_identifications() {
        assertTrue(
                wholeGraph.getAllTags().isEmpty()
        );
        vertexA.addMeta(
                modelTestScenarios.human()
        );
        vertexA.addMeta(
                modelTestScenarios.person()
        );
        vertexA.addMeta(
                modelTestScenarios.timBernersLee()
        );
        assertThat(
                wholeGraph.getAllTags().size(),
                is(3)
        );
    }

    @Test
    public void can_get_all_tags_of_single_user() {
        VertexOperator anotherUserVertex = vertexFactory.withUri(
                neo4jUserGraphFactory.withUser(anotherUser).createVertex().uri()
        );
        IdentifierPojo anotherUserTag = anotherUserVertex.addMeta(
                modelTestScenarios.book()
        ).values().iterator().next();
        VertexOperator newUserVertex = vertexFactory.withUri(
                neo4jUserGraphFactory.withUser(user).createVertex().uri()
        );
        IdentifierPojo newUserTag = newUserVertex.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        Set<IdentificationOperator> allUserTags = wholeGraph.getAllTagsOfUser(user);
        assertTrue(
                allUserTags.contains(
                        newUserTag
                )
        );
        assertFalse(
                allUserTags.contains(
                        anotherUserTag
                )
        );
    }

}
