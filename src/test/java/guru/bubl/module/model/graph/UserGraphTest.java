/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.module.model.graph;

import com.google.common.collect.ImmutableSet;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.exceptions.InvalidDepthOfSubVerticesException;
import guru.bubl.module.model.graph.exceptions.NonExistingResourceException;
import guru.bubl.module.model.graph.schema.Schema;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.module.model.graph.vertex.*;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.FriendlyResourceFactory;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.suggestion.Suggestion;
import guru.bubl.module.model.suggestion.SuggestionOrigin;
import guru.bubl.module.model.test.SubGraphOperator;

import javax.inject.Inject;
import java.net.URI;
import java.util.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;

public class UserGraphTest extends AdaptableGraphComponentTest {
    public static final int DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected FriendlyResourceFactory friendlyResourceFactory;

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
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
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
    public void has_generic_identifications() {
        vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getGenericIdentifications().values().iterator().hasNext()
        );
    }

    @Test
    public void has_same_as() {
        vertexA.addSameAs(
                modelTestScenarios.computerScientistType()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getSameAs().values().iterator().hasNext()
        );
    }

    @Test
    public void has_types() {
        vertexA.addType(
                modelTestScenarios.personType()
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        assertTrue(
                vertexAInSubGraph.getAdditionalTypes().entrySet().iterator().hasNext()
        );
        FriendlyResource additionalType = vertexAInSubGraph.getAdditionalTypes().values().iterator().next();
        assertThat(additionalType.label(), is("Person"));
    }

    @Test
    public void can_return_multiple_identifications_for_one_graph_element() {
        vertexA.addGenericIdentification(
                modelTestScenarios.personType()
        );
        vertexA.addGenericIdentification(
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

    @Test
    @Ignore("to complete")
    public void has_suggestion_multiple_origins() {
        vertexA.setSuggestions(
                suggestionsToMap(
                        modelTestScenarios.nameSuggestionFromPersonIdentification(user()),
                        modelTestScenarios.nameSuggestionFromSymbolIdentification(user())
                )
        );
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        Suggestion suggestionInSubGraph = vertexAInSubGraph.getSuggestions().values().iterator().next();
        assertThat(
                suggestionInSubGraph.origins().size(),
                is(2)
        );
    }

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
        newVertex.addRelationToVertex(vertexA);
        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
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
        IdentificationPojo identification = modelTestScenarios.computerScientistType();
        identification.setImages(
                images
        );
        vertexA.addGenericIdentification(
                identification
        );

        SubGraphPojo subGraph = userGraph.graphWithAnyVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
        VertexInSubGraph vertexAInSubGraph = subGraph.vertexWithIdentifier(
                vertexA.uri()
        );
        FriendlyResource identificationInSubGraph = vertexAInSubGraph.getGenericIdentifications().values().iterator().next();
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
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
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
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2,
                vertexA.uri()
        );
        assertThat(subGraph.numberOfEdges(), is(2));
        assertThat(subGraph.numberOfVertices(), is(3));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDepthAndCenterVertexId(
                1, vertexA.uri()
        );
        assertThat(subGraph.numberOfEdges(), is(1));
        assertThat(subGraph.numberOfVertices(), is(2));
        assertFalse(subGraph.containsVertex(vertexC));
        assertTrue(subGraph.containsVertex(vertexA));
    }

    @Test

    public void can_get_a_limited_graph_with_a_custom_center_vertex() {
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
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
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
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
        subGraph = userGraph.graphWithDepthAndCenterVertexId(
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

        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                2,
                vertexA.uri()
        );
        assertTrue(subGraph.containsVertex(edgeGoingOutOfC.destinationVertex()));
    }

    @Test
    public void with_a_depth_of_sub_vertices_of_zero_only_central_vertex_is_returned() {
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                0,
                vertexA.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexA));

        subGraph = userGraph.graphWithDepthAndCenterVertexId(
                0, vertexB.uri()
        );
        assertThat(subGraph.numberOfVertices(), is(1));
        assertThat(subGraph.numberOfEdges(), is(0));
        assertTrue(subGraph.containsVertex(vertexB));
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_default_center_vertex_with_negative_depth() {
        try {
            userGraph.graphWithDepthAndCenterVertexId(
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
            userGraph.graphWithDepthAndCenterVertexId(-1, vertexB.uri());
            fail();
        } catch (InvalidDepthOfSubVerticesException e) {
            assertThat(e.getMessage(), Is.is("Invalid depth of sub vertices. Depth was:-1 and center vertex uri was:" + vertexB.uri()));
        }
    }

    @Test
    public void an_exception_is_thrown_when_getting_graph_with_non_existing_center_vertex() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        try {
            userGraph.graphWithDepthAndCenterVertexId(1, Uris.get("/invalid_uri"));
            fail();
        } catch (NonExistingResourceException e) {
            assertThat(e.getMessage(), is("Resource with URI: /invalid_uri not found"));
        }
        assertThat(numberOfEdgesAndVertices(), is(numberOfEdgesAndVertices));
    }

    @Test
    @Ignore("to implement later, not a priority")
    public void can_get_rdf_xml_representation_of_graph() {
        assertThat(userGraph.toRdfXml(), is(not(nullValue())));
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
        IdentificationPojo createdComputerScientistType = schemaOperator.addType(
                modelTestScenarios.computerScientistType()
        );
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schemaOperator.uri()
        );
        IdentificationPojo identificationPojo = schemaPojo.getIdentifications().values().iterator().next();
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
        IdentificationPojo createdComputerScientistType = createdProperty.addType(
                modelTestScenarios.computerScientistType()
        );
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schemaOperator.uri()
        );
        GraphElementPojo property = schemaPojo.getProperties().values().iterator().next();
        IdentificationPojo identificationPojo = property.getIdentifications().values().iterator().next();
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

    @Override
    public VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return (VertexInSubGraphPojo) userGraph.graphWithAnyVertexAndDepth(
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
