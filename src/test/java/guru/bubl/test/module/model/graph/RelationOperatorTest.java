/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.fork.NbNeighbors;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.group_relation.GroupRelationPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class RelationOperatorTest extends ModelTestResources {

    @Test
    public void can_add_relation() {
        VertexOperator vertexD = vertexFactory.withUri(
                vertexA.addVertexAndRelation().destinationUri()
        );
        VertexOperator vertexE = vertexFactory.withUri(
                vertexD.addVertexAndRelation().destinationUri()
        );
        Integer numberOfEdgesAndVertices = wholeGraphAroundDefaultCenterVertex().numberOfEdgesAndVertices();
        RelationOperator newEdge = vertexE.addRelationToFork(vertexA);

        assertThat(newEdge.sourceUri(), is(vertexE.uri()));
        assertThat(newEdge.destinationUri(), is(vertexA.uri()));
        assertTrue(userGraph.haveElementWithId(newEdge.uri()));
        assertThat(newEdge.label(), is(""));
        assertThat(
                wholeGraphAroundDefaultCenterVertex().numberOfEdgesAndVertices(),
                is(numberOfEdgesAndVertices + 1)
        );
    }

    @Test
    public void can_update_label() {
        RelationOperator edge = relationFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        edge.label("likes");
        assertThat(edge.label(), is("likes"));
    }

    @Test
    public void there_is_a_creation_date() {
        Relation relation = vertexA.addVertexAndRelation();
        assertThat(
                relation.creationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void there_is_a_last_modification_date() {
        Relation relation = vertexA.addVertexAndRelation();
        assertThat(
                relation.lastModificationDate(),
                is(not(nullValue()))
        );
    }

    @Test
    public void a_vertex_can_have_multiple_relations_with_same_vertex() {
        assertTrue(
                vertexB.hasDestinationVertex(vertexC)
        );
        assertThat(vertexC.connectedEdges().size(), is(2));
        vertexB.addRelationToFork(vertexC);
        /*  don't test with getNumberOfConnectedEdges
            because we want to test the actual value and not the cached one
         */
        assertThat(vertexC.connectedEdges().size(), is(3));
    }

    @Test
    public void can_remove_an_edge() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        RelationOperator edge = vertexA.getEdgeToDestinationVertex(vertexB);
        URI edgeId = edge.uri();
        assertTrue(userGraph.haveElementWithId(edgeId));
        edge.remove();
        assertFalse(userGraph.haveElementWithId(edgeId));
        assertFalse(vertexA.hasDestinationVertex(vertexB));

        Integer updatedNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(updatedNumberOfEdgesAndVertices, is(numberOfEdgesAndVertices - 1));
    }


    @Test
    public void can_add_tag() {
        RelationOperator newEdge = relationFactory.withUri(
                vertexA.addVertexAndRelation().uri()
        );
        assertTrue(newEdge.getTags().isEmpty());
        newEdge.addTag(
                modelTestScenarios.creatorPredicate()
        );
        assertFalse(newEdge.getTags().isEmpty());
    }

    @Test
    public void can_check_equality() {
        Relation anRelation = vertexA.addVertexAndRelation();
        assertTrue(anRelation.equals(anRelation));
        Relation anotherRelation = vertexA.addVertexAndRelation();
        assertFalse(anRelation.equals(anotherRelation));
    }

    @Test
    public void can_compare_to_friendly_resource() {
        Relation anRelation = vertexA.addVertexAndRelation();
        assertTrue(anRelation.equals(anRelation));
    }

    @Test
    public void can_inverse() {
        RelationOperator betweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(betweenAAndB.sourceUri(), is(vertexA.uri()));
        assertThat(betweenAAndB.destinationUri(), is(vertexB.uri()));
        betweenAAndB.inverse();
        assertThat(
                betweenAAndB.sourceUri(),
                is(vertexB.uri())
        );
        assertThat(betweenAAndB.destinationUri(), is(vertexA.uri()));
    }

    @Test
    public void an_edge_is_private_at_creation_if_both_end_vertices_are_private() {
        Relation relation = vertexA.addVertexAndRelation();
        assertFalse(
                relationFactory.withUri(relation.uri()).isPublic()
        );
    }

    @Test
    public void an_edge_is_private_at_creation_if_one_of_the_end_vertices_is_private() {
        vertexA.makePublic();
        Relation relation = vertexA.addVertexAndRelation();
        assertFalse(
                relationFactory.withUri(relation.uri()).isPublic()
        );
    }

    @Test
    public void an_edge_is_public_at_creation_if_both_end_vertices_are_public() {
        vertexA.makePublic();
        vertexC.makePublic();
        RelationOperator edge = vertexA.addRelationToFork(vertexC);
        assertTrue(
                edge.isPublic()
        );
    }

    @Test
    public void can_change_source_vertex() {
        RelationOperator edge = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                edge.sourceUri(),
                is(vertexA.uri())
        );
        edge.changeSource(vertexB.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                edge.sourceUri(),
                is(vertexB.uri())
        );
    }

    @Test
    public void changing_source_vertex_increments_number_of_connected_vertices_for_new_source_vertex() {
        RelationOperator edge = vertexB.getEdgeToDestinationVertex(vertexC);
        assertThat(
                vertexA.getNbNeighbors().getTotal(),
                is(1)
        );
        edge.changeSource(vertexA.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                vertexA.getNbNeighbors().getTotal(),
                is(2)
        );
    }

    @Test
    public void change_source_can_be_a_group_relation() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                edgeAB.sourceUri(),
                is(vertexA.uri())
        );
        edgeAB.changeSource(groupRelation.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                edgeAB.sourceUri(),
                is(groupRelation.uri())
        );
    }

    @Test
    public void changing_source_vertex_decrements_number_of_connected_vertices_for_previous_source_vertex() {
        RelationOperator edge = vertexB.getEdgeToDestinationVertex(vertexC);
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
        edge.changeSource(vertexA.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(1)
        );
    }


    @Test
    public void can_change_destination_vertex() {
        RelationOperator edgeBetweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                edgeBetweenAAndB.destinationUri(),
                is(vertexB.uri())
        );
        edgeBetweenAAndB.changeDestination(vertexC.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                edgeBetweenAAndB.destinationUri(),
                is(vertexC.uri())
        );
    }

    @Test
    public void kept_vertex_nb_public_neighbors_is_unchanged_when_previous_and_new_end_are_private() {
        RelationOperator edgeBetweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        edgeBetweenAAndB.changeDestination(vertexC.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test
    public void kept_vertex_nb_public_neighbors_is_unchanged_when_previous_and_new_end_are_public() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        RelationOperator edgeBetweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
        edgeBetweenAAndB.changeDestination(vertexC.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
    }


    @Test
    public void kept_vertex_nb_public_neighbors_increments_when_previous_end_is_private_and_new_is_public() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.makePublic();
        edgeAB.changeDestination(
                vertexC.uri(),
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PUBLIC);
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
    }

    @Test
    public void changing_destination_keeps_vertex_nb_public_neighbors_decrements_when_previous_end_is_public_and_new_is_private() {
        vertexB.makePublic();
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
        vertexC.makePrivate();
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        edgeAB.changeDestination(
                vertexC.uri(),
                ShareLevel.PUBLIC,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test
    public void previous_vertex_nb_public_neighbors_decrements_when_kept_vertex_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexB.getNbNeighbors().getPublic(),
                is(1)
        );
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        edgeAB.changeDestination(vertexC.uri(), ShareLevel.PRIVATE, ShareLevel.PUBLIC, ShareLevel.PRIVATE);
        assertThat(
                vertexB.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test
    public void new_vertex_nb_public_neighbors_increments_when_kept_vertex_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        edgeAB.changeDestination(
                vertexC.uri(),
                ShareLevel.PRIVATE,
                ShareLevel.PUBLIC,
                ShareLevel.PRIVATE
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(1)
        );
    }

    @Test
    public void can_use_as_tag_even_if_deleted() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        TagPojo edgeABTag = TestScenarios.tagFromFriendlyResource(
                edgeAB
        );
        edgeAB.remove();
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        assertThat(
                edgeBC.getTags().size(),
                is(0)
        );
        edgeBC.addTag(edgeABTag);
        assertThat(
                edgeBC.getTags().size(),
                is(1)
        );
    }

    @Test
    public void convert_to_group_relation_tags_new_group_relation_with_edge_tags() {
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        edgeBC.addTag(modelTestScenarios.toDo(), ShareLevel.PRIVATE);
        GroupRelationOperator groupRelationOperator = groupRelationFactory.withUri(
                edgeBC.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        edgeBC.getShareLevel(),
                        ""
                ).uri()
        );
        assertThat(
                groupRelationOperator.getTags().values().iterator().next().getExternalResourceUri(),
                is(modelTestScenarios.toDo().getExternalResourceUri())
        );
        assertThat(
                groupRelationOperator.getTags().size(),
                is(1)
        );
    }

    @Test
    public void convert_to_group_relation_tags_all_edge_tags_new_group_relation() {
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        edgeBC.addTag(modelTestScenarios.person(), ShareLevel.PRIVATE);
        edgeBC.addTag(modelTestScenarios.human(), ShareLevel.PRIVATE);
        GroupRelationOperator groupRelationOperator = groupRelationFactory.withUri(
                edgeBC.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        edgeBC.getShareLevel(),
                        ""
                ).uri()
        );
        assertThat(
                groupRelationOperator.getTags().size(),
                is(2)
        );
    }

    @Test
    public void convert_to_group_relation_untags_edge_and_tags_the_group_relation() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        TagPojo todo = modelTestScenarios.toDo();
        todo = relationFactory.withUri(
                edgeAB.uri()
        ).addTag(todo).values().iterator().next();
        assertTrue(
                relationFactory.withUri(edgeAB.uri()).getTags().keySet().contains(todo.getExternalResourceUri())
        );
        GroupRelationPojo groupRelationPojo = edgeAB.convertToGroupRelation(
                UUID.randomUUID().toString(),
                edgeAB.getShareLevel(),
                ""
        );
        assertFalse(
                relationFactory.withUri(edgeAB.uri()).getTags().keySet().contains(todo.getExternalResourceUri())
        );
        GroupRelationOperator groupRelationOperator = groupRelationFactory.withUri(groupRelationPojo.uri());
        assertTrue(
                groupRelationOperator.getTags().keySet().contains(todo.getExternalResourceUri())
        );
    }

    @Test
    public void convert_to_group_relation_edge_becomes_child_of_group_relation() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexA.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertTrue(
                subGraph.hasEdgeWithUri(edgeAB.uri())
        );
        GroupRelationPojo groupRelationPojo = edgeAB.convertToGroupRelation(
                UUID.randomUUID().toString(),
                edgeAB.getShareLevel(),
                ""
        );
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexA.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertFalse(
                subGraph.hasEdgeWithUri(edgeAB.uri())
        );
        subGraph = userGraph.aroundForkUriInShareLevels(
                groupRelationPojo.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edges().size(),
                is(1)
        );
        assertTrue(
                subGraph.hasEdgeWithUri(edgeAB.uri())
        );
    }

    @Test
    public void new_group_relation_has_2_neighbors() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        vertexA.makePublic();

        GroupRelationOperator groupRelationOperator = groupRelationFactory.withUri(
                edgeAB.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        vertexA.getShareLevel(),
                        ""
                ).uri()
        );
        NbNeighbors nbNeighbors = groupRelationOperator.getNbNeighbors();
        assertThat(
                nbNeighbors.getPublic(),
                is(2)
        );
        assertThat(
                nbNeighbors.getPrivate(),
                is(0)
        );
        assertThat(
                nbNeighbors.getFriend(),
                is(0)
        );
    }

    @Test
    public void can_change_destination_to_group_relation(){
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                edgeAB.sourceUri(),
                is(vertexA.uri())
        );
        edgeAB.changeDestination(groupRelation.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        assertThat(
                edgeAB.destinationUri(),
                is(groupRelation.uri())
        );
    }
}
