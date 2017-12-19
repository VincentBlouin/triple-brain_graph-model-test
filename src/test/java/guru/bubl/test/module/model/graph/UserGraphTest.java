/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import com.google.common.collect.ImmutableSet;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementPojo;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.exceptions.InvalidDepthOfSubVerticesException;
import guru.bubl.module.model.graph.exceptions.NonExistingResourceException;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.schema.Schema;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.*;
import guru.bubl.module.model.suggestion.Suggestion;
import guru.bubl.module.model.suggestion.SuggestionOrigin;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

public class UserGraphTest extends ModelTestResources {
    private static final int DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    @Inject
    protected VertexFactory vertexFactory;

    @Test
    public void can_get_graph_with_default_center_vertex() {
        SubGraph graph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        assertThat(graph, is(not(nullValue())));
        assertThat(graph.numberOfVertices(), is(3));
        assertThat(graph.numberOfEdges(), is(2));
        assertTrue(graph.containsVertex(vertexA));
    }

    @Test
    public void can_get_graph_with_custom_center_vertex() {
        SubGraph graph = userGraph.graphWithDepthAndCenterBubbleUri(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                vertexB.uri());
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
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
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
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
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
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        Vertex vertexBInSubgraph = subGraph.vertices().get(vertexB.uri());
        assertTrue(
                vertexBInSubgraph.getIdentifications().isEmpty()
        );
    }

    @Test
    public void elements_with_no_included_vertices_dont_have_included_vertices() {
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        newVertex.addRelationToVertex(vertexA);
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                newVertex.uri()
        );
        VertexInSubGraph vertexAInSubgraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubgraph.getIncludedVertices().isEmpty()
        );
    }

    @Test
    public void schemas_with_no_identifications_dont_have_identifications() {
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                userGraph.createSchema().uri()
        );
        assertTrue(
                schemaPojo.getIdentifications().isEmpty()
        );
    }

    @Test
    public void has_generic_identifications() {
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getIdentifications().values().iterator().hasNext()
        );
    }

    @Test
    public void can_return_multiple_identifications_for_one_graph_element() {
        vertexA.addMeta(
                modelTestScenarios.person()
        );
        vertexA.addMeta(
                modelTestScenarios.timBernersLee()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(vertexAInSubGraph.getIdentifications().size(), is(2));
    }

    @Test
    public void has_number_of_references_to_an_identification() {
        vertexA.addMeta(
                modelTestScenarios.person()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        IdentifierPojo identification = subGraph.vertexWithIdentifier(
                vertexA.uri()
        ).getIdentifications().values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(1)
        );
    }

    @Test
    public void vertex_suggestions_have_their_properties_sub_graph() {
        vertexA.setSuggestions(
                suggestionsToMap(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(
                                user()
                        )
                )
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        Suggestion suggestion = vertexAInSubGraph.getSuggestions().values().iterator().next();
        assertThat(suggestion.label(), is("Start date"));
    }

    @Test
    public void suggestions_have_their_own_label() {
        vertexA.setSuggestions(
                suggestionsToMap(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user()),
                        modelTestScenarios.nameSuggestionFromPersonIdentification(user())
                )
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        List<String> labels = new ArrayList<>();
        for (Suggestion suggestion : vertexAInSubGraph.getSuggestions().values()) {
            labels.add(suggestion.label());
        }
        assertTrue(labels.contains("Start date"));
        assertTrue(labels.contains("Name"));
    }

    @Test
    public void has_suggestions_origin() {
        vertexA.setSuggestions(
                suggestionsToMap(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user())
                )
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        Suggestion suggestion = vertexAInSubGraph.getSuggestions().values().iterator().next();
        SuggestionOrigin origin = suggestion.origins().iterator().next();
        FriendlyResourcePojo identification = new FriendlyResourcePojo(
                URI.create("http://rdf.freebase.com/rdf/time/event")
        );
        assertTrue(
                origin.isRelatedToFriendlyResource(
                        identification
                )
        );
    }

//    @Test
//    @Ignore("to complete")
//    public void has_suggestion_multiple_origins() {
//        vertexA.setSuggestions(
//                suggestionsToMap(
//                        modelTestScenarios.nameSuggestionFromPersonIdentification(user()),
//                        modelTestScenarios.nameSuggestionFromSymbolIdentification(user())
//                )
//        );
//        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
//                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
//        );
//        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
//                vertexA.uri()
//        );
//        Suggestion suggestionInSubGraph = vertexAInSubGraph.getSuggestions().values().iterator().next();
//        assertThat(
//                suggestionInSubGraph.origins().size(),
//                is(2)
//        );
//    }

    @Test
    public void can_get_multiple_suggestions_in_sub_graph() {
        vertexA.setSuggestions(
                suggestionsToMap(
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user()),
                        modelTestScenarios.nameSuggestionFromPersonIdentification(user())
                )
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAInSubGraph.getSuggestions().size(),
                is(2)
        );
    }

    @Test
    public void has_included_vertices_and_edges() {
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                newVertex.uri()
        );
        VertexInSubGraph compositeVertexInSubGraph = subGraph.vertexWithIdentifier(
                newVertex.uri()
        );
        assertThat(
                compositeVertexInSubGraph.getIncludedVertices().size(),
                is(2)
        );
        assertThat(
                compositeVertexInSubGraph.getIncludedEdges().size(),
                is(1)
        );
    }

    @Test
    public void included_edges_have_source_and_destination_vertices() {
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );
        newVertex.addRelationToVertex(vertexA);
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        Edge edge = subGraph.vertexWithIdentifier(
                newVertex.uri()
        ).getIncludedEdges().values().iterator().next();
        assertNotNull(
                edge.sourceVertex()
        );
        assertNotNull(
                edge.destinationVertex()
        );
    }

    @Test
    public void included_edges_only_hold_source_and_destination_vertex_uris() {
        Edge includedEdge = edgeBetweenBAndCInSet().iterator().next();
        Vertex includedEdgeSourceVertex = includedEdge.sourceVertex();
        Vertex includedEdgeDestinationVertex = includedEdge.destinationVertex();
        assertFalse(
                includedEdgeSourceVertex.label().isEmpty()
        );
        assertFalse(
                includedEdgeDestinationVertex.label().isEmpty()
        );
        VertexOperator newVertex = vertexFactory.createFromGraphElements(
                vertexBAndC(),
                edgeBetweenBAndCInSet()
        );

        newVertex.addRelationToVertex(vertexA);
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        includedEdge = subGraph.vertexWithIdentifier(
                newVertex.uri()
        ).getIncludedEdges().values().iterator().next();
        includedEdgeSourceVertex = includedEdge.sourceVertex();
        includedEdgeDestinationVertex = includedEdge.destinationVertex();
        assertTrue(
                includedEdgeSourceVertex.label().isEmpty()
        );
        assertTrue(
                includedEdgeDestinationVertex.label().isEmpty()
        );
    }

    @Test
    public void has_vertices_images() {
        Image image1 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Image image2 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        vertexA.addImages(images);
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
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
        Image image1 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Image image2 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        IdentifierPojo identification = modelTestScenarios.computerScientistType();
        identification.setImages(
                images
        );
        vertexA.addMeta(
                identification
        );

        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        FriendlyResource identificationInSubGraph = vertexAInSubGraph.getIdentifications().values().iterator().next();
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
        SubGraph graph = userGraph.graphWithDepthAndCenterBubbleUri(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES,
                vertexA.uri()
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
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                2,
                vertexA.uri()
        );
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1, vertexA.uri()
        );
        assertThat(subGraph.numberOfEdges(), is(1));
        assertThat(subGraph.numberOfVertices(), is(2));
        assertFalse(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(vertexA));
    }

    @Test

    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexC.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(2));
        assertThat(subGraph.numberOfEdges(), is(1));
        assertFalse(subGraph.containsVertex(vertexA));
        assertTrue(subGraph.containsVertex(vertexC));
    }

    @Test

    public void can_get_sub_graph_of_destination_vertex_of_center_vertex() {
        Edge newEdge = vertexC.addVertexAndRelation();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                2, vertexB.uri()
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
        subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                2, vertexB.uri()
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

        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                2,
                vertexA.uri()
        );
        assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                0,
                vertexA.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                0, vertexB.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexB));
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_default_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDepthAndCenterBubbleUri(
                    -1,
                    vertexA.uri()
            );
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), Is.is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexA.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_custom_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDepthAndCenterBubbleUri(-1, vertexB.uri());
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), Is.is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexB.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_non_existing_center_vertex() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        try {
            userGraph.graphWithDepthAndCenterBubbleUri(1, Uris.get("/invalid_uri"));
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: /invalid_uri not found"));
        }
        assertThat(numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    public void can_create_new_vertex_out_of_nothing() {
        Vertex vertex = userGraph.createVertex();
        SubGraphOperator subGraph = wholeGraph();
        assertTrue(subGraph.containsVertex(vertex));
    }

    @Test
    public void can_create_schema() {
        Schema schema = userGraph.createSchema();
        assertThat(schema.uri(), is(notNullValue()));
    }

    @Test
    public void can_get_schema() {
        Schema schema = userGraph.createSchema();
        URI originalUri = schema.uri();
        schema = userGraph.schemaPojoWithUri(originalUri);
        assertThat(schema.uri(), is(originalUri));
    }

    @Test
    public void schemas_have_their_identifications() {
        SchemaOperator schemaOperator = userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
        IdentifierPojo createdComputerScientistType = schemaOperator.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schemaOperator.uri()
        );
        IdentifierPojo identificationPojo = schemaPojo.getIdentifications().values().iterator().next();
        assertThat(
                identificationPojo,
                is(createdComputerScientistType)
        );
    }

    @Test
    public void schema_contains_its_properties() {
        SchemaOperator schemaOperator = userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
        schemaOperator.label("patate");
        GraphElement createdProperty = schemaOperator.addProperty();
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schemaOperator.uri()
        );
        assertThat(
                schemaPojo.getProperties().values().iterator().next(),
                is(createdProperty)
        );
    }

    @Test
    public void schema_properties_includes_their_identifications() {
        SchemaOperator schemaOperator = userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
        GraphElementOperator createdProperty = schemaOperator.addProperty();
        IdentifierPojo createdComputerScientistType = createdProperty.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schemaOperator.uri()
        );
        GraphElementPojo property = schemaPojo.getProperties().values().iterator().next();
        IdentifierPojo identificationPojo = property.getIdentifications().values().iterator().next();
        assertThat(
                identificationPojo,
                is(createdComputerScientistType)
        );
    }

    @Test
    public void vertex_details_are_not_included_in_edge_source_and_destination_vertex() {
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
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
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        assertThat(
                subGraph.edgeWithIdentifier(
                        edge.uri()
                ).sourceVertex(),
                is(vertexB)
        );
        edge.changeSourceVertex(vertexA);
        subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexA.uri()
        );
        assertThat(
                subGraph.edgeWithIdentifier(
                        edge.uri()
                ).sourceVertex(),
                is(vertexA)
        );
    }

    @Test
    public void sort_and_move_date_are_included() {
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        VertexInSubGraphPojo vertexCInSubGraph = subGraph.vertexWithIdentifier(vertexC.uri());
        assertThat(
                vertexCInSubGraph.getGraphElement().getSortDate(),
                is(nullValue())
        );
        assertThat(
                vertexCInSubGraph.getGraphElement().getMoveDate(),
                is(nullValue())
        );
        Date sortDate = new DateTime().minusMillis(10).toDate();
        Date moveDate = new Date();
        vertexC.setSortDate(sortDate, moveDate);
        subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        vertexCInSubGraph = subGraph.vertexWithIdentifier(vertexC.uri());
        assertThat(
                vertexCInSubGraph.getGraphElement().getSortDate(),
                is(sortDate)
        );
        assertThat(
                vertexCInSubGraph.getGraphElement().getMoveDate(),
                is(moveDate)
        );
    }

    @Test
    public void nb_public_neighbors_is_included() {
        vertexB.makePublic();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        VertexInSubGraphPojo vertexAPojo = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertThat(
                vertexAPojo.getNbPublicNeighbors(),
                is(1)
        );
        VertexInSubGraphPojo vertexBPojo = subGraph.vertexWithIdentifier(
                vertexB.uri()
        );
        assertThat(
                vertexBPojo.getNbPublicNeighbors(),
                is(0)
        );
    }

    @Test
    public void can_extract_sub_graph_around_an_identifier() {
        IdentifierPojo computerScientist = vertexB.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexC.addMeta(
                computerScientist
        );
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                computerScientist.uri()
        );
        assertTrue(subGraph.containsVertex(vertexB));
        assertTrue(subGraph.containsVertex(vertexC));
        assertFalse(subGraph.containsVertex(vertexA));
    }

    @Test
    public void it_does_not_fail_if_identifier_references_nothing() {
        IdentifierPojo computerScientist = vertexB.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        vertexB.remove();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                computerScientist.uri()
        );
        assertTrue(
                subGraph.vertices().isEmpty()
        );
    }

    @Test
    public void sub_graph_around_an_identifier_related_to_relations_include_vertices_and_relations() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        IdentifierPojo toDo = edgeBetweenAAndB.addMeta(
                modelTestScenarios.toDo()
        ).values().iterator().next();
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(vertexC);
        edgeBetweenBAndC.addMeta(
                toDo
        );
        EdgePojo newEdge = vertexC.addVertexAndRelation();
        Vertex newVertex = newEdge.destinationVertex();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                toDo.uri()
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
        IdentifierPojo human = vertexB.addMeta(
                modelTestScenarios.human()
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                human.uri()
        );
        assertTrue(
                subGraph.containsVertex(vertexB)
        );
        assertFalse(
                subGraph.containsVertex(vertexA)
        );
    }

    @Test
    @Ignore
    public void can_get_meta_center_from_user_graph() {
        IdentifierPojo human = vertexB.addMeta(
                modelTestScenarios.human()
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                human.uri()
        );
        human = subGraph.getCenterMeta();
        assertThat(
                human.label(),
                is("Human")
        );
    }

    @Test
    public void does_not_fail_if_identifier_does_not_have_images() {
        IdentifierPojo identifierPojo = modelTestScenarios.human();
        identifierPojo.images();
        vertexB.addMeta(
                identifierPojo
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        assertTrue(true);
    }

    @Test
    public void includes_children_indexes() {
        vertexB.setChildrenIndex(
                "test children indexes"
        );
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        assertTrue(
                subGraph.vertexWithIdentifier(
                        vertexB.uri()
                ).getChildrenIndex().contains("test children indexes")
        );
    }

    @Override
    public VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
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
