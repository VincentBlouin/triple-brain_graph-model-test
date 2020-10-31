/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestResources;
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
        Set<VertexOperator> vertices = wholeGraph.getAllVertices();
        for (Vertex vertex : vertices) {
            if (visitedVertices.contains(vertex)) {
                fail();
            }
            visitedVertices.add(vertex);
        }
    }


    @Test
    public void can_get_all_vertices_of_single_user() {
        Vertex anotherUserVertex = userGraphFactory.withUser(anotherUser).createVertex();
        Vertex newUserVertex = userGraphFactory.withUser(user).createVertex();
        Set<VertexOperator> allUserVertices = wholeGraph.getAllVerticesOfUser(user);
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
                is(6)
        );
    }

    @Test
    public void can_get_edges() {
        assertThat(
                wholeGraph.getAllEdges().size(),
                is(4)
        );
    }

    @Test
    public void can_get_all_edges_of_single_user() {
        VertexOperator anotherUserVertex = vertexFactory.withUri(
                userGraphFactory.withUser(anotherUser).createVertex().uri()
        );
        Relation anotherUserRelation = anotherUserVertex.addVertexAndRelation();
        VertexOperator newUserVertex = vertexFactory.withUri(
                userGraphFactory.withUser(user).createVertex().uri()
        );
        Relation newUserRelation = newUserVertex.addVertexAndRelation();
        Set<RelationOperator> allUserEdges = wholeGraph.getAllEdgesOfUser(user);
        assertTrue(
                allUserEdges.contains(
                        newUserRelation
                )
        );
        assertFalse(
                allUserEdges.contains(
                        anotherUserRelation
                )
        );
    }

    @Test
    public void can_get_all_graph_elements() {
        assertThat(
                wholeGraph.getAllGraphElements().size(),
                is(12)
        );
    }

    @Test
    public void can_get_all_identifications() {
        assertThat(
                wholeGraph.getAllTags().size(),
                is(1)
        );
        vertexA.addTag(
                modelTestScenarios.human()
        );
        vertexA.addTag(
                modelTestScenarios.person()
        );
        vertexA.addTag(
                modelTestScenarios.timBernersLee()
        );
        assertThat(
                wholeGraph.getAllTags().size(),
                is(4)
        );
    }

    @Test
    public void can_get_all_tags_of_single_user() {
        VertexOperator anotherUserVertex = vertexFactory.withUri(
                userGraphFactory.withUser(anotherUser).createVertex().uri()
        );
        TagPojo anotherUserTag = anotherUserVertex.addTag(
                modelTestScenarios.book()
        ).values().iterator().next();
        VertexOperator newUserVertex = vertexFactory.withUri(
                userGraphFactory.withUser(user).createVertex().uri()
        );
        TagPojo newUserTag = newUserVertex.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        Set<TagOperator> allUserTags = wholeGraph.getAllTagsOfUser(user);
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
