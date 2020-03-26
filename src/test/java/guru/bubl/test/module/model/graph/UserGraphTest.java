/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import com.google.common.collect.ImmutableSet;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.HashSet;
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
        SubGraph graph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(graph.numberOfEdges(), is(2));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        SubGraph graph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexB.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(graph, is(not(nullValue())));
        Vertex centerVertex = graph.vertexWithIdentifier(vertexB.uri());
        assertThat(graph.numberOfEdges(), is(2));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(centerVertex.label(), is("vertex B"));
    }

    @Test
    public void correct_edges_are_in_graph() {
        Edge betweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        Edge betweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edges().size(),
                is(2)
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
        Edge betweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Edge betweenAAndBFromSubGraph = subGraph.edgeWithIdentifier(
                betweenAAndB.uri()
        );
        assertTrue(
                betweenAAndBFromSubGraph.sourceVertex().equals(
                        vertexA
                )
        );
        assertTrue(
                betweenAAndBFromSubGraph.destinationVertex().equals(
                        vertexB
                )
        );
    }

    @Test
    public void elements_with_no_identifications_dont_have_identifications() {
        vertexA.addTag(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(vertexAInSubGraph.getTags().size(), is(2));
    }

    @Test


    public void has_number_of_references_to_an_identification() {
        vertexA.addTag(
                modelTestScenarios.person()
        );
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
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

        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
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
        SubGraph graph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraph subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraph subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        Edge newEdge = vertexC.addVertexAndRelation();
        SubGraph subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexB.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfEdges(), is(3));
        assertThat(subGraph.numberOfVertices(), is(4));

        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex() {
        SubGraph subGraph;
        Edge newEdge = vertexA.addVertexAndRelation();
        subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexB.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfVertices(), is(4));
        assertThat(subGraph.numberOfEdges(), is(3));

        assertTrue(subGraph.containsVertex(newEdge.destinationVertex()));
        assertTrue(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test
    public void can_get_sub_graph_of_source_vertex_of_center_vertex_having_also_a_circular_relation() {
        vertexC.addRelationToVertex(vertexA);
        Edge edgeGoingOutOfC = vertexC.addVertexAndRelation();

        SubGraph subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                2,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                0,
                ShareLevel.allShareLevelsInt
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        );
        Vertex sourceVertexInEdge = subGraph.edges().values().iterator().next().sourceVertex();
        assertThat(
                sourceVertexInEdge.label(),
                is(CoreMatchers.nullValue())
        );
        Vertex destinationVertexInEdge = subGraph.edges().values().iterator().next().destinationVertex();
        assertThat(
                destinationVertexInEdge.label(),
                is(CoreMatchers.nullValue())
        );
    }

    @Test


    public void changing_edge_source_vertex_reflects_in_getting_subgraph() {
        EdgeOperator edge = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexB.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edgeWithIdentifier(
                        edge.uri()
                ).sourceVertex(),
                is(vertexB)
        );
        edge.changeSourceVertex(vertexA);
        subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.edgeWithIdentifier(
                        edge.uri()
                ).sourceVertex(),
                is(vertexA)
        );
    }

    @Test
    public void nb_public_neighbors_is_included() {
        vertexB.makePublic();
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        VertexInSubGraphPojo vertexAPojo = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAPojo.getNbNeighbors().getPublic(),
                is(1)
        );
        VertexInSubGraphPojo vertexBPojo = subGraph.vertexWithIdentifier(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        VertexInSubGraphPojo vertexAPojo = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAPojo.getNbNeighbors().getFriend(),
                is(1)
        );
        VertexInSubGraphPojo vertexBPojo = subGraph.vertexWithIdentifier(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
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
        subGraph = userGraph.aroundVertexUriInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        TagPojo toDo = edgeBetweenAAndB.addTag(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        edgeBetweenBAndC.addTag(
                toDo
        );
        EdgePojo newEdge = vertexC.addVertexAndRelation();
        Vertex newVertex = newEdge.destinationVertex();
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
                toDo.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );
        assertTrue(subGraph.containsEdge(edgeBetweenAAndB));
        assertTrue(subGraph.containsEdge(edgeBetweenBAndC));
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertFalse(subGraph.containsEdge(newEdge));
        assertFalse(subGraph.containsVertex(newVertex));
    }

    @Test


    public void sub_graph_around_an_identifier_to_a_vertex_does_not_include_the_source_vertex() {
        TagPojo human = vertexB.addTag(
                modelTestScenarios.human()
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        EdgeOperator bToCEdge = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        TagPojo tag = bToCEdge.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        edgeFactory.withUri(
                vertexB.addVertexAndRelation().uri()
        ).addTag(
                tag
        );
        vertexC.makePublic();
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
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
        EdgeOperator bToCEdge = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        TagPojo tag = bToCEdge.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        edgeFactory.withUri(
                vertexB.addVertexAndRelation().uri()
        ).addTag(
                tag
        );
        vertexB.makePublic();
        vertexC.makePublic();
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
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
        EdgeOperator bToCEdge = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        EdgeOperator newEdge = edgeFactory.withUri(
                vertexB.addVertexAndRelation().uri()
        );
        TagPojo tag = newEdge.addTag(
                TestScenarios.tagFromFriendlyResource(bToCEdge)
        ).values().iterator().next();
        newEdge.destinationVertex().makePublic();
        vertexB.makePublic();
        vertexC.makePublic();
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
                tag.uri(),
                ShareLevel.PUBLIC.getIndex(),
                ShareLevel.PUBLIC_WITH_LINK.getIndex()
        );
        assertThat(
                subGraph.getCenterMeta().label(),
                is("between vertex B and vertex C")
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriWithDepthInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
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
        subGraph = userGraph.aroundVertexUriInShareLevels(
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
        subGraph = userGraph.aroundVertexUriInShareLevels(
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
        subGraph = userGraph.aroundVertexUriInShareLevels(
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
        subGraph = userGraph.aroundVertexUriInShareLevels(
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
        SubGraphPojo subGraph = userGraph.aroundVertexUriInShareLevels(
                vertexB.uri(),
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexBInSubGraph = subGraph.vertexWithIdentifier(vertexB.uri());
        assertFalse(
                vertexBInSubGraph.isPattern()
        );
        vertexB.makePattern();
        subGraph = userGraph.aroundVertexUriInShareLevels(
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
        Vertex newCenter = userGraph.aroundVertexUriInShareLevels(
                newUri,
                ShareLevel.allShareLevelsInt
        ).vertexWithIdentifier(newUri);
        assertEquals(
                newCenter.getPatternUri(),
                vertexB.uri()
        );
    }

    @Override
    public VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return userGraph.aroundVertexUriWithDepthInShareLevels(
                vertexA.uri(),
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                ShareLevel.allShareLevelsInt
        ).vertexWithIdentifier(vertex.uri());
    }

    private Set<Vertex> vertexBAndC() {
        Set<Vertex> vertexBAndC = new HashSet<>();
        vertexBAndC.add(vertexB);
        vertexBAndC.add(vertexC);
        return vertexBAndC;
    }

    private Set<Edge> edgeBetweenBAndCInSet() {
        Set<Edge> edges = new HashSet<>();
        edges.add(
                vertexB.getEdgeThatLinksToDestinationVertex(
                        vertexC
                )
        );
        return edges;
    }
}
