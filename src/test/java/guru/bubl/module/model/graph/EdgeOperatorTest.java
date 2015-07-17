/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.module.model.graph;

import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.utils.ModelTestResources;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import org.junit.Test;

import java.net.URI;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class EdgeOperatorTest extends ModelTestResources {

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
        assertThat(
                wholeGraphAroundDefaultCenterVertex().numberOfEdgesAndVertices(),
                is(numberOfEdgesAndVertices + 1)
        );
    }

    @Test
    public void can_update_label() {
        EdgeOperator edge = vertexA.addVertexAndRelation();
        edge.label("likes");
        assertThat(edge.label(), is("likes"));
    }

    @Test
    public void there_is_a_creation_date() {
        Edge edge = vertexA.addVertexAndRelation();
        assertThat(
                edge.creationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void there_is_a_last_modification_date() {
        Edge edge = vertexA.addVertexAndRelation();
        assertThat(
                edge.lastModificationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void a_vertex_can_have_multiple_relations_with_same_vertex() {
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
        EdgeOperator edge = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        URI edgeId = edge.uri();
        assertTrue(userGraph.haveElementWithId(edgeId));
        edge.remove();
        assertFalse(userGraph.haveElementWithId(edgeId));
        assertFalse(vertexA.hasDestinationVertex(vertexB));

        Integer updatedNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 1));
    }

    @Test
    public void deleting_a_relation_decrements_number_of_connected_edges_to_vertices() {
        assertThat(vertexA.getNumberOfConnectedEdges(), is(1));
        assertThat(vertexB.getNumberOfConnectedEdges(), is(2));
        vertexA.getEdgeThatLinksToDestinationVertex(vertexB).remove();
        assertThat(vertexA.getNumberOfConnectedEdges(), is(0));
        assertThat(vertexB.getNumberOfConnectedEdges(), is(1));
    }

    @Test
    public void can_add_same_as() {
        EdgeOperator newEdge = vertexA.addVertexAndRelation();
        assertTrue(newEdge.getSameAs().isEmpty());
        newEdge.addSameAs(
                modelTestScenarios.creatorPredicate()
        );
        assertFalse(newEdge.getSameAs().isEmpty());
    }

    @Test
    public void can_check_equality() {
        Edge anEdge = vertexA.addVertexAndRelation();
        assertTrue(anEdge.equals(anEdge));
        Edge anotherEdge = vertexA.addVertexAndRelation();
        assertFalse(anEdge.equals(anotherEdge));
    }

    @Test
    public void can_compare_to_friendly_resource() {
        Edge anEdge = vertexA.addVertexAndRelation();
        assertTrue(anEdge.equals(anEdge));
    }

    @Test
    public void can_inverse() {
        EdgeOperator betweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(betweenAAndB.sourceVertex(), is((Vertex) vertexA));
        assertThat(betweenAAndB.destinationVertex(), is((Vertex) vertexB));
        betweenAAndB.inverse();
        assertThat(
                betweenAAndB.sourceVertex().uri(),
                is(vertexB.uri())
        );
        assertThat(betweenAAndB.destinationVertex(), is((Vertex) vertexA));
    }
}
