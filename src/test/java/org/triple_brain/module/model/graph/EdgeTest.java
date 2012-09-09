package org.triple_brain.module.model.graph;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class EdgeTest extends AdaptableGraphComponentTest {
    @Test
    public void can_add_relation() {
        Vertex vertexD = vertexA.addVertexAndRelation().destinationVertex();
        Vertex vertexE = vertexD.addVertexAndRelation().destinationVertex();

        Integer numberOfEdgesAndVertices = wholeGraph().numberOfEdgesAndVertices();
        Edge newEdge = vertexE.addRelationToVertex(vertexA);

        assertThat(newEdge.sourceVertex(), is(vertexE));
        assertThat(newEdge.destinationVertex(), is(vertexA));
        assertTrue(userGraph.haveElementWithId(newEdge.id()));
        assertThat(newEdge.label(), is(""));
        assertThat(numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices + 1));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        Edge edge = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        assertTrue(userGraph.haveElementWithId(edge.id()));
        edge.remove();
        assertFalse(userGraph.haveElementWithId(edge.id()));
        assertFalse(vertexA.hasDestinationVertex(vertexB));

        Integer updatedNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 1));
    }

    @Test
    public void can_update_label() {
        Edge edge = vertexA.addVertexAndRelation();
        edge.label("likes");
        assertThat(edge.label(), is("likes"));
    }
}
