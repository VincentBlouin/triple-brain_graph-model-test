/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
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
        VertexOperator vertexD = vertexFactory.withUri(
                vertexA.addVertexAndRelation().destinationVertex().uri()
        );
        VertexOperator vertexE = vertexFactory.withUri(
                vertexD.addVertexAndRelation().destinationVertex().uri()
        );
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
        EdgeOperator edge = edgeFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
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
    public void removing_an_edge_decrements_number_of_references_to_its_identification() {
        testThatRemovingGraphElementRemovesTheNumberOfReferencesToItsIdentification(
                vertexA.getEdgeThatLinksToDestinationVertex(vertexB)
        );
    }

    @Test
    public void can_add_meta() {
        EdgeOperator newEdge = edgeFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        assertTrue(newEdge.getIdentifications().isEmpty());
        newEdge.addMeta(
                modelTestScenarios.creatorPredicate()
        );
        assertFalse(newEdge.getIdentifications().isEmpty());
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

    @Test
    public void an_edge_is_private_at_creation_if_both_end_vertices_are_private() {
        Edge edge = vertexA.addVertexAndRelation();
        assertFalse(
                edge.isPublic()
        );
    }

    @Test
    public void an_edge_is_private_at_creation_if_one_of_the_end_vertices_is_private() {
        vertexA.makePublic();
        Edge edge = vertexA.addVertexAndRelation();
        assertFalse(
                edge.isPublic()
        );
    }

    @Test
    public void an_edge_is_public_at_creation_if_both_end_vertices_are_public() {
        vertexA.makePublic();
        vertexC.makePublic();
        Edge edge = vertexA.addRelationToVertex(vertexC);
        assertTrue(
                edge.isPublic()
        );
    }

    @Test
    public void can_change_source_vertex() {
        EdgeOperator edge = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(
                edge.sourceVertex(),
                is(vertexA)
        );
        edge.changeSourceVertex(vertexB);
        assertThat(
                edge.sourceVertex(),
                is(vertexB)
        );
    }

    @Test
    public void changing_source_vertex_increments_number_of_connected_vertices_for_new_source_vertex() {
        EdgeOperator edge = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        assertThat(
                vertexA.getNumberOfConnectedEdges(),
                is(1)
        );
        edge.changeSourceVertex(vertexA);
        assertThat(
                vertexA.getNumberOfConnectedEdges(),
                is(2)
        );
    }

    @Test
    public void changing_source_vertex_decrements_number_of_connected_vertices_for_previous_source_vertex() {
        EdgeOperator edge = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        assertThat(
                vertexB.getNumberOfConnectedEdges(),
                is(2)
        );
        edge.changeSourceVertex(vertexA);
        assertThat(
                vertexB.getNumberOfConnectedEdges(),
                is(1)
        );
    }

    @Test
    public void can_change_destination_vertex() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(
                edgeBetweenAAndB.destinationVertex(),
                is(vertexB)
        );
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                edgeBetweenAAndB.destinationVertex(),
                is(vertexC)
        );
    }

    @Test
    public void kept_vertex_nb_public_neighbors_is_unchanged_when_previous_and_new_end_are_private() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void kept_vertex_nb_public_neighbors_is_unchanged_when_previous_and_new_end_are_public() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(1)
        );
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(1)
        );
    }


    @Test
    public void kept_vertex_nb_public_neighbors_increments_when_previous_end_is_private_and_new_is_public() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
        vertexC.makePublic();
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(1)
        );
    }

    @Test
    public void changing_destination_keeps_vertex_nb_public_neighbors_decrements_when_previous_end_is_public_and_new_is_private() {
        vertexB.makePublic();
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(1)
        );
        vertexC.makePrivate();
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void previous_vertex_nb_public_neighbors_decrements_when_kept_vertex_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(1)
        );
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void new_vertex_nb_public_neighbors_increments_when_kept_vertex_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexC.getNbPublicNeighbors(),
                is(0)
        );
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        edgeBetweenAAndB.changeDestinationVertex(vertexC);
        assertThat(
                vertexC.getNbPublicNeighbors(),
                is(1)
        );
    }

    @Test
    public void remove_decrements_nb_public_neighbors_to_destination_if_source_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(1)
        );
        vertexA.getEdgeThatLinksToDestinationVertex(vertexB).remove();
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void decrements_nb_public_neighbors_to_source_if_destination_is_public() {
        vertexB.makePublic();
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(1)
        );
        vertexA.getEdgeThatLinksToDestinationVertex(vertexB).remove();
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void does_not_decrement_nb_public_neighbors_if_both_are_private() {
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(0)
        );
        vertexA.getEdgeThatLinksToDestinationVertex(vertexB).remove();
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void remove_decrements_nb_public_neighbors_if_both_are_public() {
        vertexA.makePublic();
        vertexB.makePublic();
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(1)
        );
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(1)
        );
        vertexA.getEdgeThatLinksToDestinationVertex(vertexB).remove();
        assertThat(
                vertexA.getNbPublicNeighbors(),
                is(0)
        );
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void can_use_as_tag_even_if_deleted() {
        EdgeOperator edgeAB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        IdentifierPojo edgeABTag = TestScenarios.identificationFromFriendlyResource(
                edgeAB
        );
        edgeAB.remove();
        EdgeOperator edgeBC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        assertThat(
                edgeBC.getIdentifications().size(),
                is(0)
        );
        edgeBC.addMeta(edgeABTag);
        assertThat(
                edgeBC.getIdentifications().size(),
                is(1)
        );
    }
}
