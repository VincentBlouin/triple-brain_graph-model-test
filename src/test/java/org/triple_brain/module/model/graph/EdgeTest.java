package org.triple_brain.module.model.graph;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
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
        String edgeId = edge.id();
        assertTrue(userGraph.haveElementWithId(edgeId));
        edge.remove();
        assertFalse(userGraph.haveElementWithId(edgeId));
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

    @Test
    public void there_is_a_creation_date(){
        Edge edge = vertexA.addVertexAndRelation();
        assertThat(
                edge.creationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void there_is_a_last_modification_date(){
        Edge edge = vertexA.addVertexAndRelation();
        assertThat(
                edge.lastModificationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void updating_label_updates_last_modification_date(){
        Edge edge = vertexA.addVertexAndRelation();
        DateTime lastModificationDate = edge.lastModificationDate();
        edge.label("patate");
        Assert.assertTrue(edge.lastModificationDate().isAfter(
                lastModificationDate
        ));
    }
}
