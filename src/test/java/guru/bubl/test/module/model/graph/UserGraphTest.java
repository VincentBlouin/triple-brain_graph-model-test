/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import com.google.common.collect.ImmutableSet;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.relation.RelationPojo;
import guru.bubl.module.model.graph.group_relation.GroupRelationOperator;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

public class UserGraphTest extends ModelTestResources {
    private static final int DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    @Inject
    protected VertexFactory vertexFactory;

    @Test
    public void can_get_graph_with_default_center_vertex() {
        SubGraph graph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(5));
        assertThat(graph.numberOfEdges(), is(4));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test

    public void can_get_graph_with_custom_center_vertex() {
        SubGraph graph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(graph, is(not(nullValue())));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexB.uri());
        assertThat(graph.numberOfEdges(), is(4));
        assertThat(graph.numberOfVertices(), is(5));
        assertThat(centerVertex.label(), is("vertex B"));
    }

    @Test
    public void correct_edges_are_in_graph() {
        Relation betweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        Relation betweenBAndC = vertexB.getEdgeToDestinationVertex(vertexC);
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edges().size(),
                is(4)
        );
        assertTrue(
                subGraph.containsEdge(betweenAAndB)
        );
        assertTrue(
                subGraph.containsEdge(betweenBAndC)
        );
    }

    @Test
    public void source_and_destination_vertex_are_in_edges() {
        Relation betweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Relation betweenAAndBFromSubGraph = subGraph.edgeWithIdentifier(
                betweenAAndB.uri()
        );
        assertTrue(
                betweenAAndBFromSubGraph.sourceUri().equals(
                        vertexA.uri()
                )
        );
        assertTrue(
                betweenAAndBFromSubGraph.destinationUri().equals(
                        vertexB.uri()
                )
        );
    }

    @Test
    public void elements_with_no_identifications_dont_have_identifications() {
        vertexA.addTag(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexBInSubgraph = subGraph.vertices().get(vertexB.uri());
        assertTrue(
                vertexBInSubgraph.getTags().isEmpty()
        );
    }

    @Test
    public void has_generic_identifications() {
        vertexA.addTag(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getTags().values().iterator().hasNext()
        );
    }

    @Test
    public void can_return_multiple_identifications_for_one_graph_element() {
        vertexA.addTag(
                modelTestScenarios.person()
        );
        vertexA.addTag(
                modelTestScenarios.timBernersLee()
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(vertexAInSubGraph.getTags().size(), is(2));
    }

    @Test
    public void has_number_of_references_to_an_identification() {
        vertexA.addTag(
                modelTestScenarios.person()
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        TagPojo tag = subGraph.vertexWithIdentifier(
                vertexA.uri()
        ).getTags().values().iterator().next();
        assertThat(
                tag.getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test
    public void has_vertices_images() {
        Image image1 = Image.withUrlForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Image image2 = Image.withUrlForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        vertexA.addImages(images);
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAInSubGraph.images().size(),
                is(2)
        );
        assertTrue(
                vertexAInSubGraph.images().contains(image1)
        );
        assertTrue(
                vertexAInSubGraph.images().contains(image2)
        );
    }

    @Test
    public void has_identification_images() {
        Image image1 = Image.withUrlForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Image image2 = Image.withUrlForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        TagPojo identification = modelTestScenarios.computerScientistType();
        identification.setImages(
                images
        );
        vertexA.addTag(
                identification
        );

        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        FriendlyResource identificationInSubGraph = vertexAInSubGraph.getTags().values().iterator().next();
        assertThat(
                identificationInSubGraph.images().size(),
                is(2)
        );
        assertTrue(
                identificationInSubGraph.images().contains(image1)
        );
        assertTrue(
                identificationInSubGraph.images().contains(image2)
        );
    }


    @Test
    public void can_get_circular_graph_with_default_center_vertex() {
        vertexC.addRelationToVertex(vertexA);
        SubGraph graph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                graph,
                is(not(nullValue()))
        );
        Vertex centerVertex = graph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                centerVertex.label(),
                is("vertex A")
        );
    }

    @Test
    public void can_get_a_limited_graph_with_default_center_vertex() throws Exception {
        SubGraph subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfEdges(), is(1));
        assertThat(subGraph.numberOfVertices(), is(2));
        assertFalse(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(vertexA));
    }

    @Test
    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        SubGraph subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexC.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfVertices(), is(2));
        assertThat(subGraph.numberOfEdges(), is(1));
        assertFalse(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    public void can_get_sub_graph_of_destination_vertex_of_center_vertex() {
        Relation newRelation = vertexC.addVertexAndRelation();
        SubGraph subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfEdges(), is(3));
        assertThat(subGraph.numberOfVertices(), is(4));

        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.vertices().containsKey(newRelation.destinationUri()));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex() {
        SubGraph subGraph;
        Relation newRelation = vertexA.addVertexAndRelation();
        subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfVertices(), is(4));
        assertThat(subGraph.numberOfEdges(), is(3));

        assertTrue(subGraph.vertices().containsKey(newRelation.destinationUri()));
        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex_having_also_a_circular_relation() {
        vertexC.addRelationToVertex(vertexA);
        Relation relationGoingOutOfC = vertexC.addVertexAndRelation();

        SubGraph subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(subGraph.vertices().containsKey(relationGoingOutOfC.destinationUri()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                0,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                0,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexB));
    }

    @Test
    public void can_create_new_vertex_out_of_nothing() {
        Vertex vertex = userGraph.createVertex();
        SubGraphOperator subGraph = wholeGraph();
        assertTrue(subGraph.containsVertex(vertex));
    }


    @Test
    public void vertex_details_are_not_included_in_edge_source_and_destination_vertex() {
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        GraphElement sourceVertexInEdge = subGraph.edges().values().iterator().next().getSource();
        assertThat(
                sourceVertexInEdge.label(),
                is(CoreMatchers.nullValue())
        );
        GraphElement destinationVertexInEdge = subGraph.edges().values().iterator().next().destination();
        assertThat(
                destinationVertexInEdge.label(),
                is(CoreMatchers.nullValue())
        );
    }

    @Test
    public void changing_edge_source_vertex_reflects_in_getting_subgraph() {
        RelationOperator edge = vertexB.getEdgeToDestinationVertex(vertexC);
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edgeWithIdentifier(
                        edge.uri()
                ).sourceUri(),
                is(vertexB.uri())
        );
        edge.changeSource(vertexA.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexA.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edgeWithIdentifier(
                        edge.uri()
                ).sourceUri(),
                is(vertexA.uri())
        );
    }

    @Test
    public void nb_public_neighbors_is_included() {
        vertexB.makePublic();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        VertexPojo vertexAPojo = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAPojo.getNbNeighbors().getPublic(),
                is(1)
        );
        VertexPojo vertexBPojo = subGraph.vertexWithIdentifier(
                vertexB.uri()
        );
        assertThat(
                vertexBPojo.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test
    public void nb_friends_is_included() {
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        VertexPojo vertexAPojo = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAPojo.getNbNeighbors().getFriend(),
                is(1)
        );
        VertexPojo vertexBPojo = subGraph.vertexWithIdentifier(
                vertexB.uri()
        );
        assertThat(
                vertexBPojo.getNbNeighbors().getFriend(),
                is(0)
        );
    }

    @Test
    public void can_extract_sub_graph_around_an_identifier() {
        TagPojo computerScientist = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexC.addTag(
                computerScientist
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                computerScientist.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertFalse(subGraph.containsVertex(vertexA));
    }

    @Test
    public void includes_tag_share_level() {
        TagPojo computerScientist = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        TagPojo tagInSubGraph = subGraph.vertexWithIdentifier(
                vertexB.uri()
        ).getTags().values().iterator().next();
        assertThat(
                tagInSubGraph.getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        tagFactory.withUri(
                computerScientist.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        tagInSubGraph = subGraph.vertexWithIdentifier(
                vertexB.uri()
        ).getTags().values().iterator().next();
        assertThat(
                tagInSubGraph.getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
    }

    @Test
    public void excludes_tag_not_in_same_share_level() {
        vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexB.makePublic();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.PUBLIC.getIndex()
        );
        assertThat(
                subGraph.vertexWithIdentifier(
                        vertexB.uri()
                ).getTags().size(),
                is(0)
        );
    }

    @Test
    public void it_does_not_fail_if_identifier_references_nothing() {
        TagPojo computerScientist = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexB.remove();
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                computerScientist.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(
                subGraph.vertices().isEmpty()
        );
    }

    @Test
    public void sub_graph_around_an_identifier_related_to_relations_include_vertices_and_relations() {
        RelationOperator edgeBetweenAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        TagPojo toDo = edgeBetweenAAndB.addTag(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        RelationOperator edgeBetweenBAndC = vertexB.getEdgeToDestinationVertex(vertexC);
        edgeBetweenBAndC.addTag(
                toDo
        );
        RelationPojo newEdge = vertexC.addVertexAndRelation();
        GraphElement newVertex = newEdge.destination();
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                toDo.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(subGraph.containsEdge(edgeBetweenAAndB));
        assertTrue(subGraph.containsEdge(edgeBetweenBAndC));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertFalse(subGraph.containsEdge(newEdge));
        assertFalse(subGraph.vertices().containsKey(newVertex.uri()));
    }

    @Test
    public void sub_graph_around_an_identifier_to_a_vertex_does_not_include_the_source_vertex() {
        TagPojo human = vertexB.addTag(
                modelTestScenarios.human()
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                human.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(
                subGraph.containsVertex(vertexB)
        );
        assertFalse(
                subGraph.containsVertex(vertexA)
        );
    }

    @Test
    public void can_get_meta_center_from_user_graph() {
        TagPojo human = vertexB.addTag(
                modelTestScenarios.human()
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                human.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        human = subGraph.getCenterMeta();
        assertThat(
                human.label(),
                is("Human")
        );
    }

    @Test
    public void does_not_fail_if_identifier_does_not_have_images() {
        TagPojo tagPojo = modelTestScenarios.human();
        tagPojo.images();
        vertexB.addTag(
                tagPojo
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(true);
    }

    @Test
    public void includes_children_indexes() {
        vertexB.setChildrenIndex(
                "test children indexes"
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(
                subGraph.vertexWithIdentifier(
                        vertexB.uri()
                ).getChildrenIndex().contains("test children indexes")
        );
    }

    @Test
    public void includes_children_indexes_for_tags() {
        TagPojo computerScientist = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexC.addTag(
                computerScientist
        );
        tagFactory.withUri(
                computerScientist.uri()
        ).setChildrenIndex("test children indexes");
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                computerScientist.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.getCenterMeta().getChildrenIndex(),
                is("test children indexes")
        );
    }

    @Test
    public void tags_can_have_no_surrounding_graph_elements() {
        TagPojo computerScientist = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexB.removeTag(
                computerScientist
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                computerScientist.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.getCenterMeta().label(),
                is("Computer Scientist")
        );
    }

    @Test
    public void center_tag_is_set_correctly_even_if_tag_shares_graph_element_with_another_tag() {
        TagPojo tag = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexB.addTag(modelTestScenarios.person());
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                tag.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.getCenterMeta().label(),
                is("Computer Scientist")
        );
    }

    @Test
    public void excludes_edges_around_tag_when_destination_or_source_vertex_is_private() {
        RelationOperator bToCEdge = vertexB.getEdgeToDestinationVertex(vertexC);
        TagPojo tag = bToCEdge.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        relationFactory.withUri(
                vertexB.addVertexAndRelation().uri()
        ).addTag(
                tag
        );
        vertexC.makePublic();
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                tag.uri(),
                ShareLevel.PUBLIC.getIndex(),
                ShareLevel.PUBLIC_WITH_LINK.getIndex()
        );
        assertThat(
                subGraph.getCenterMeta().label(),
                is("Person")
        );
        assertTrue(
                subGraph.edges().isEmpty()
        );
        assertThat(
                subGraph.vertices().size(),
                is(1)
        );
    }

    @Test
    public void include_edges_around_tag_when_destination_and_source_vertex_are_public() {
        RelationOperator bToCEdge = vertexB.getEdgeToDestinationVertex(vertexC);
        TagPojo tag = bToCEdge.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        relationFactory.withUri(
                vertexB.addVertexAndRelation().uri()
        ).addTag(
                tag
        );
        vertexB.makePublic();
        vertexC.makePublic();
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                tag.uri(),
                ShareLevel.PUBLIC.getIndex(),
                ShareLevel.PUBLIC_WITH_LINK.getIndex()
        );
        assertThat(
                subGraph.getCenterMeta().label(),
                is("Person")
        );
        assertThat(
                subGraph.edges().size(),
                is(1)
        );
        assertThat(
                subGraph.vertices().size(),
                is(2)
        );
    }

    @Test
    public void include_edges_around_tag_when_destination_and_source_vertex_are_all_public() {
        vertexA.remove();
        RelationOperator bToCEdge = vertexB.getEdgeToDestinationVertex(vertexC);
        RelationOperator newEdge = relationFactory.withUri(
                vertexB.addVertexAndRelation().uri()
        );
        TagPojo tag = newEdge.addTag(
                TestScenarios.tagFromFriendlyResource(bToCEdge)
        ).values().iterator().next();
        vertexFactory.withUri(
                newEdge.destinationUri()
        ).makePublic();
        vertexB.makePublic();
        vertexC.makePublic();
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                tag.uri(),
                ShareLevel.PUBLIC.getIndex(),
                ShareLevel.PUBLIC_WITH_LINK.getIndex()
        );
        assertThat(
                subGraph.getCenterMeta().label(),
                is("edge BC")
        );
        assertThat(
                subGraph.edges().size(),
                is(2)
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    @Test
    public void include_colors() {
        vertexB.setColors("blue");
        SubGraphPojo subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.vertexWithIdentifier(
                vertexB.uri()).getColors()
                , is("blue")
        );
    }

    @Test
    public void can_exclude_graph_elements_that_are_not_in_share_levels() {
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.PRIVATE.getIndex()
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
        assertThat(
                subGraph.edges().size(),
                is(2)
        );
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.PUBLIC.getIndex()
        );
        assertThat(
                subGraph.vertices().size(),
                is(0)
        );
        assertThat(
                subGraph.edges().size(),
                is(0)
        );
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        vertexC.setShareLevel(ShareLevel.FRIENDS);
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.FRIENDS.getIndex(),
                ShareLevel.PUBLIC.getIndex(),
                ShareLevel.PUBLIC_WITH_LINK.getIndex()
        );
        assertThat(
                subGraph.vertices().size(),
                is(2)
        );
        assertThat(
                subGraph.edges().size(),
                is(1)
        );
        vertexB.setShareLevel(ShareLevel.PRIVATE);
        vertexC.setShareLevel(ShareLevel.PRIVATE);
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.FRIENDS.getIndex(),
                ShareLevel.PUBLIC.getIndex(),
                ShareLevel.PUBLIC_WITH_LINK.getIndex()
        );
        assertThat(
                subGraph.vertices().size(),
                is(0)
        );
        assertThat(
                subGraph.edges().size(),
                is(0)
        );
        vertexB.setShareLevel(ShareLevel.PUBLIC);
        vertexC.setShareLevel(ShareLevel.PUBLIC);
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.PUBLIC.getIndex()
        );
        assertThat(
                subGraph.vertices().size(),
                is(2)
        );
        assertThat(
                subGraph.edges().size(),
                is(1)
        );
    }

    @Test
    public void returns_is_a_pattern_or_not() {
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexBInSubGraph = subGraph.vertexWithIdentifier(vertexB.uri());
        assertFalse(
                vertexBInSubGraph.isPattern()
        );
        vertexB.makePattern();
        subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        vertexBInSubGraph = subGraph.vertexWithIdentifier(vertexB.uri());
        assertTrue(
                vertexBInSubGraph.isPattern()
        );
    }

    @Test
    public void returns_pattern_uri() {
        vertexB.makePattern();
        URI newUri = patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        Vertex newCenter = userGraph.aroundForkUriInShareLevels(
                newUri,
                ShareLevel.allShareLevelsInt
        ).vertexWithIdentifier(newUri);
        assertEquals(
                newCenter.getPatternUri(),
                vertexB.uri()
        );
    }

    @Test
    public void returns_multiple_children_of_group_relation() {
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        TagPojo todo = modelTestScenarios.toDo();
        edgeBC.addTag(todo, ShareLevel.PRIVATE);
        GroupRelationOperator groupRelationOperator = groupRelationFactory.withUri(
                edgeBC.convertToGroupRelation(
                        UUID.randomUUID().toString(),
                        edgeBC.getShareLevel(),
                        "to do"
                ).uri()
        );
        groupRelationOperator.addVertexAndRelation();
        groupRelationOperator.addVertexAndRelation();
        groupRelationOperator.addVertexAndRelation();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                groupRelationOperator.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edges().size(),
                is(4)
        );
    }

    @Test
    public void can_get_destination_when_its_is_group_relation() {
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        edgeBC.changeDestination(
                groupRelation.uri(),
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertTrue(subGraph.containsEdge(edgeBC));
        RelationPojo edgeBCInSubGraph = subGraph.edgeWithIdentifier(edgeBC.uri());
        assertThat(
                edgeBCInSubGraph.destinationUri(),
                is(groupRelation.uri())
        );
    }
}
