package org.triple_brain.module.model.graph;

import org.junit.Assert;
import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexOperator;

import java.net.URI;

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
        VertexOperator vertexD = vertexA.addVertexAndRelation().destinationVertex();
        VertexOperator vertexE = vertexD.addVertexAndRelation().destinationVertex();

        Integer numberOfEdgesAndVertices = wholeGraphAroundDefaultCenterVertex().numberOfEdgesAndVertices();
        EdgeOperator newEdge = vertexE.addRelationToVertex(vertexA);

        assertThat(newEdge.sourceVertex(), is((Vertex) vertexE));
        assertThat(newEdge.destinationVertex(), is((Vertex) vertexA));
        assertTrue(userGraph.haveElementWithId(newEdge.uri()));
        assertThat(newEdge.label(), is(""));
        assertThat(numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices + 1));
    }

    @Test
    public void a_vertex_can_have_multiple_relations_with_same_vertex(){
        assertTrue(
                vertexB.hasDestinationVertex(vertexC)
        );
        assertThat(vertexC.connectedEdges().size(), is(1));
        vertexB.addRelationToVertex(vertexC);
        /*  don't test with getNumberOfConnectedEdges
            because we want to test the actual value and not the cached one
         */
        assertThat(vertexC.connectedEdges().size(), is(2));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        EdgeOperator edge = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        URI edgeId = edge.uri();
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
    public void can_add_same_as(){
        EdgeOperator newEdge = vertexA.addVertexAndRelation();
        Assert.assertTrue(newEdge.getSameAs().isEmpty());
        newEdge.addSameAs(modelTestScenarios.creatorPredicate());
        assertFalse(newEdge.getSameAs().isEmpty());
    }

    @Test
    public void can_check_equality(){
        Edge anEdge = vertexA.addVertexAndRelation();
        assertTrue(anEdge.equals(anEdge));
        Edge anotherEdge = vertexA.addVertexAndRelation();
        assertFalse(anEdge.equals(anotherEdge));
    }

    @Test
    public void can_compare_to_friendly_resource(){
        Edge anEdge = vertexA.addVertexAndRelation();
        FriendlyResource anEdgeAsFriendlyResource = (FriendlyResource) anEdge;
        assertTrue(anEdge.equals(anEdgeAsFriendlyResource));
    }

    @Test
    public void can_inverse(){
        EdgeOperator betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        assertThat(betweenAAndB.sourceVertex(), is((Vertex) vertexA));
        assertThat(betweenAAndB.destinationVertex(), is((Vertex) vertexB));
        betweenAAndB.inverse();
        assertThat(betweenAAndB.sourceVertex(), is((Vertex) vertexB));
        assertThat(betweenAAndB.destinationVertex(), is((Vertex) vertexA));
    }

    @Test
    public void deleting_a_relation_decrements_number_of_connected_edges_to_vertices(){
        assertThat(vertexA.getNumberOfConnectedEdges(), is(1));
        assertThat(vertexB.getNumberOfConnectedEdges(), is(2));
        vertexA.edgeThatLinksToDestinationVertex(vertexB).remove();
        assertThat(vertexA.getNumberOfConnectedEdges(), is(0));
        assertThat(vertexB.getNumberOfConnectedEdges(), is(1));
    }
}
