/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.search;

import com.google.common.collect.Sets;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.search.*;
import guru.bubl.test.module.utils.ModelTestScenarios;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.test.module.utils.search.Neo4jSearchRelatedTest;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphSearchTest extends Neo4jSearchRelatedTest {

    @Test
    public void can_search_vertices_for_auto_completion() throws Exception {
        indexGraph();
        indexVertex(pineApple);
        List<VertexSearchResult> vertices;
        vertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel("vert", user);
        assertThat(vertices.size(), is(3));
        vertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel("vertex Cad", user);
        assertThat(vertices.size(), is(1));
        GraphElement firstVertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(firstVertex.label(), is("vertex Cadeau"));
        vertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel("pine A", user);
        assertThat(vertices.size(), is(1));
    }

    @Test
    public void cant_search_in_vertices_of_another_user() throws Exception {
        indexGraph();
        indexVertex(pineApple);
        List<VertexSearchResult> vertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "vert",
                user
        );
        assertTrue(vertices.size() > 0);
        vertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "vert",
                user2
        );
        assertFalse(vertices.size() > 0);
    }

    @Test
    public void vertex_comment_is_not_included_in_search_result() throws Exception {
        vertexA.comment("A description");
        indexGraph();
        List<VertexSearchResult> searchResults = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                vertexA.label(),
                user
        );
        GraphElement vertex = searchResults.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.comment(),
                is("")
        );
    }

    @Test
    public void can_search_for_other_users_public_vertices() {
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vert",
                user2
        );
        assertTrue(vertices.isEmpty());
        vertexA.makePublic();
        indexVertex(vertexA);
        vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vert",
                user2
        );
        assertFalse(vertices.isEmpty());
    }


    @Test
    public void searching_for_own_vertices_only_does_not_return_vertices_of_other_users() {
        vertexA.makePublic();
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vert",
                user2
        );
        assertTrue(vertices.size() > 0);
        List<VertexSearchResult> privateVertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "vert",
                user2
        );
        assertFalse(privateVertices.size() > 0);
    }

    @Test
    public void searching_for_own_vertices_does_not_return_schemas() {
        SchemaOperator schema = createSchema(user);
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        List<GraphElementSearchResult> searchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        );
        assertFalse(searchResult.isEmpty());
        List<VertexSearchResult> privateSearchResult = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "schema",
                user
        );
        assertTrue(privateSearchResult.isEmpty());
    }

    @Test
    public void search_is_case_insensitive() {
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vert",
                user
        );
        assertTrue(vertices.size() > 0);
        vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "Vert",
                user
        );
        assertTrue(vertices.size() > 0);
    }

    @Test
    public void case_is_preserved_when_getting_label() {
        vertexA.label("Vertex Azure");
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex azure",
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("Vertex Azure")
        );
    }

    @Test
    public void search_goes_beyond_two_first_words() {
        vertexA.label(
                "bonjour monsieur proute"
        );
        vertexB.label(
                "bonjour monsieur pratte"
        );
        vertexC.label(
                "bonjour monsieur avion"
        );
        indexGraph();

        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "bonjour monsieur pr",
                user
        );
        assertThat(vertices.size(), is(2));
    }

    @Test
    public void can_search_relations() {
        indexGraph();
        List<GraphElementSearchResult> results = graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "between vert",
                user
        );
        assertThat(results.size(), is(2));
    }

    @Test
    public void relation_source_and_destination_vertex_label_and_uri_are_included_in_result() {
        indexGraph();
        List<GraphElementSearchResult> relations = graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "between vertex A and B",
                user
        );
        Edge edge = ((EdgeSearchResult) relations.get(0)).getEdge();
        Vertex sourceVertex = edge.sourceVertex();
        Vertex destinationVertex = edge.destinationVertex();
        assertThat(
                sourceVertex.uri(),
                is(vertexA.uri())
        );
        assertThat(
                sourceVertex.label(),
                is("vertex Azure")
        );
        assertThat(
                destinationVertex.uri(),
                is(vertexB.uri())
        );
        assertThat(
                destinationVertex.label(),
                is("vertex Bareau")
        );

        edge.sourceVertex().uri().equals(vertexA.uri());
        assertFalse(
                null == edge.sourceVertex().uri()
        );
        assertFalse(
                null == edge.destinationVertex().uri()
        );
    }

    @Test
    public void schemas_are_included_in_relations_search() {
        List<GraphElementSearchResult> results = graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "schema1",
                user
        );
        assertTrue(results.isEmpty());
        SchemaOperator schema = createSchema(user);
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        results = graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "schema1",
                user
        );
        assertFalse(results.isEmpty());
    }

    @Test
    public void can_search_schema() {
        SchemaOperator schema = createSchema(user);
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        List<GraphElementSearchResult> results = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        );
        assertThat(results.size(), is(1));
        GraphElementPojo schemaAsSearchResult = results.iterator().next().getGraphElement();
        assertThat(
                schemaAsSearchResult.uri(),
                is(
                        schema.uri()
                )
        );
    }

    @Test
    public void schema_properties_can_be_retrieved() throws Exception {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        List<GraphElementSearchResult> searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                userGraph.user()
        );
        VertexSearchResult result = (VertexSearchResult) searchResults.get(0);
        Map<URI, GraphElementPojo> properties = result.getProperties();
        assertTrue(
                properties.isEmpty()
        );
        GraphElementOperator property1 = schema.addProperty();
        property1.label("prop1");
        GraphElementOperator property2 = schema.addProperty();
        property2.label("prop2");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                userGraph.user()
        );
        result = (VertexSearchResult) searchResults.get(0);
        properties = result.getProperties();
        assertThat(
                properties.size(),
                is(2)
        );
        assertTrue(
                properties.containsKey(property1.uri())
        );
        assertTrue(
                properties.containsKey(property2.uri())
        );
    }

    @Test
    public void can_search_schema_property() {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        assertTrue(
                graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                        "prop",
                        userGraph.user()
                ).isEmpty()
        );
        GraphElementOperator property1 = schema.addProperty();
        property1.label("prop1");
        GraphElementOperator property2 = schema.addProperty();
        property2.label("prop2");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        assertThat(
                graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                        "prop1",
                        userGraph.user()
                ).size(),
                is(1)
        );
    }

    @Test
    public void schema_label_and_uri_are_included_in_property_search_result() {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        GraphElementOperator property1 = schema.addProperty();
        property1.label("prop1");
        PropertySearchResult searchResult = (PropertySearchResult) graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "prop1",
                userGraph.user()
        ).get(0);
        assertThat(
                searchResult.getSchema().label(),
                is("schema1")
        );
        assertThat(
                searchResult.getSchema().uri(),
                is(schema.uri())
        );
    }

    @Test
    public void can_search_not_owned_schema() {
        SchemaOperator schema = createSchema(user);
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        List<GraphElementSearchResult> searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user2
        );
        assertFalse(searchResults.isEmpty());
    }

    @Test
    public void can_search_not_owned_schema_property() {
        SchemaOperator schema = createSchema(user);
        GraphElementOperator property1 = schema.addProperty();
        property1.label("prop");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        List<GraphElementSearchResult> searchResults = graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "prop",
                user2
        );
        assertFalse(searchResults.isEmpty());
    }

    @Test
    public void can_search_for_only_owned_schemas() {
        SchemaOperator schema = createSchema(user);
        schema.label("schema1");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        SchemaOperator schema2 = createSchema(user2);
        schema2.label("schema2");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema2.uri()
                )
        );
        graphIndexer.commit();
        List<GraphElementSearchResult> searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        );
        assertThat(
                searchResults.size(),
                is(2)
        );

        List<VertexSearchResult> privateSearchResults = graphSearch.searchOnlyForOwnVerticesOrSchemasForAutoCompletionByLabel(
                "schema",
                user
        );
        assertThat(
                privateSearchResults.size(),
                is(1)
        );
    }


    @Test
    public void schema_search_results_dont_have_comment() {
        SchemaOperator schema = createSchema(user);
        schema.label("schema1");
        schema.comment("test comment");
        graphIndexer.indexSchema(
                userGraph.schemaPojoWithUri(
                        schema.uri()
                )
        );
        graphIndexer.commit();
        GraphElementSearchResult result = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        ).get(0);
        assertThat(
                result.getGraphElement().comment(),
                is("")
        );
    }


    @Test
    public void search_queries_can_have_special_characters() {
        vertexA.label("a\\(test*");
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "a\\(test*",
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("a\\(test*")
        );
    }

    @Test
    public void search_queries_can_have_single_quotes() {
        vertexA.label("a'test");
        List<GraphElementSearchResult> vertices = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "a'test",
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("a'test")
        );
    }


    @Test
    public void search_results_dont_have_identifications() {
        indexGraph();
        GraphElement vertex = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexA.label(),
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getIdentifications().size(),
                is(0)
        );
        vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        indexGraph();
        vertex = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexA.label(),
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getIdentifications().size(),
                is(0)
        );
    }

    @Test
    public void can_get_more_details_for_element_with_uri() {
        indexGraph();
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        GraphElement vertex = searchResult.getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is(vertexA.label())
        );
    }

    @Test
    public void elements_with_no_identifications_dont_have_identifications() {
        vertexB.addGenericIdentification(
                new ModelTestScenarios().computerScientistType()
        );
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        assertTrue(
                searchResult.getGraphElement().getIdentifications().isEmpty()
        );
    }

    @Test
    public void can_get_property_details() {
        SchemaOperator schema = createSchema(userGraph.user());
        GraphElementOperator property1 = schema.addProperty();
        property1.label("prop1");
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                property1.uri(),
                userGraph.user()
        );
        assertThat(
                searchResult.getGraphElement().label(),
                is("prop1")
        );
    }

    @Test
    public void more_details_contains_comment() {
        vertexA.comment("A description");
        indexGraph();
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        assertThat(
                searchResult.getGraphElement().comment(),
                is("A description")
        );
    }

    @Test
    public void more_details_contains_image() {
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        assertThat(
                searchResult.getGraphElement().images().size(),
                is(0)
        );
        Image image1 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        vertexA.addImages(Sets.newHashSet(
                image1
        ));
        searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        assertThat(
                searchResult.getGraphElement().images().size(),
                is(1)
        );
    }

    @Test
    public void more_details_contains_identification_image_if_has_none() {
        IdentificationPojo identification = modelTestScenarios.computerScientistType();
        String identificationImage = UUID.randomUUID().toString();
        identification.setImages(Sets.newHashSet(
                Image.withBase64ForSmallAndUriForBigger(
                        identificationImage,
                        URI.create("/large_1")
                )
        ));
        vertexA.addGenericIdentification(
                identification
        );
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        Image image = searchResult.getGraphElement().images().iterator().next();
        assertThat(
                image.base64ForSmall(),
                is(identificationImage)
        );
        String vertexImage = UUID.randomUUID().toString();
        vertexA.addImages(Sets.newHashSet(
                Image.withBase64ForSmallAndUriForBigger(
                        vertexImage,
                        URI.create("/large_1")
                )
        ));
        searchResult = graphSearch.getDetails(
                vertexA.uri(),
                user
        );
        image = searchResult.getGraphElement().images().iterator().next();
        assertThat(
                image.base64ForSmall(),
                is(vertexImage)
        );
    }

    @Test
    public void cannot_get_by_uri_if_not_owner_of_non_public_element() {
        GraphElementSearchResult result = graphSearch.getDetails(
                vertexA.uri(),
                user2
        );
        assertNull(result);
    }

    @Test
    public void vertices_have_their_edges_label_and_uri_in_result() {
        VertexSearchResult searchResult = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                vertexA.label(),
                user
        ).iterator().next();
        assertThat(
                searchResult.getProperties().size(),
                is(1)
        );
        Edge edge = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        assertThat(
                searchResult.getProperties().get(edge.uri()).label(),
                is("between vertex A and vertex B")
        );
    }

    @Test
    public void there_is_a_limit_5_related_elements() {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        for (int i = 1; i <= 12; i++) {
            schema.addProperty();
        }
        VertexSearchResult searchResult = (VertexSearchResult) graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        ).iterator().next();
        assertThat(
                searchResult.getProperties().size(),
                is(5)
        );
    }

    @Test
    public void results_limit_is_on_number_of_results_not_number_of_related_elements() {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        for (int i = 1; i <= 12; i++) {
            schema.addProperty();
        }
        SchemaOperator schema2 = createSchema(userGraph.user());
        schema2.label("schema2");
        for (int i = 1; i <= 12; i++) {
            schema2.addProperty();
        }
        List<GraphElementSearchResult> searchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        );
        assertThat(
                searchResult.size(),
                is(2)
        );
    }

    @Test
    public void can_search_public_vertices_as_anonymous_user() {
        List<GraphElementSearchResult> results = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vert",
                user
        );
        assertThat(
                results.size(),
                is(3)
        );
        vertexB.makePublic();
        List<VertexSearchResult> publicVerticesOnlyResult = graphSearch.searchPublicVerticesOnly(
                "vert"
        );
        assertThat(
                publicVerticesOnlyResult.size(),
                is(1)
        );
    }

    @Test
    public void can_get_search_details_of_public_resource_as_anonymous_user() {
        vertexB.comment("some comment");
        vertexB.makePublic();
        GraphElementSearchResult searchResult = graphSearch.getDetailsAnonymously(
                vertexB.uri()
        );
        assertThat(
                searchResult.getGraphElement().comment(),
                is("some comment")
        );
    }

    @Test
    public void cannot_get_search_details_of_private_resource_as_anonymous_user() {
        vertexB.comment("some comment");
        vertexB.makePrivate();
        GraphElementSearchResult searchResult = graphSearch.getDetailsAnonymously(
                vertexB.uri()
        );
        assertNull(searchResult);
    }

    @Test
    public void does_not_include_private_edges_of_another_user() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        Map<URI, GraphElementPojo> relations = graphSearch.searchPublicVerticesOnly(
                "vertex Bareau"
        ).iterator().next().getProperties();
        assertThat(
                relations.size(),
                is(2)
        );
        vertexC.makePrivate();
        relations = graphSearch.searchPublicVerticesOnly(
                "vertex Bareau"
        ).iterator().next().getProperties();
        assertThat(
                relations.size(),
                is(1)
        );
    }

    @Test
    public void includes_private_edges_if_owner() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        VertexSearchResult vertexSearchResult = (VertexSearchResult) graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        ).iterator().next();
        Map<URI, GraphElementPojo> relations = vertexSearchResult.getProperties();
        assertThat(
                relations.size(),
                is(2)
        );
        vertexC.makePrivate();
        vertexSearchResult = (VertexSearchResult) graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        ).iterator().next();
        relations = vertexSearchResult.getProperties();
        assertThat(
                relations.size(),
                is(2)
        );
    }

    @Test
    public void identifications_to_graph_elements_are_included_in_search_results() {
        List<GraphElementSearchResult> results = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        );
        assertThat(
                results.size(),
                is(1)
        );
        vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );
        results = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        );
        assertThat(
                results.size(),
                is(2)
        );
    }

    @Test
    public void identification_search_result_have_number_of_references() {
        IdentificationPojo vertexBAsIdentifier = identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "identifier of vertex Bareau"
        );
        vertexA.addGenericIdentification(
                vertexBAsIdentifier
        );
        IdentificationSearchResult identificationSearchResult = (IdentificationSearchResult) graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "identifier",
                user
        ).iterator().next();
        assertThat(
                identificationSearchResult.getNbReferences(),
                is(2)
        );
    }
    @Test
    public void identification_result_as_the_right_label() {
        IdentificationPojo vertexBAsIdentifier = identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "identifier of vertex Bareau"
        );
        vertexA.addGenericIdentification(vertexBAsIdentifier);
        IdentificationSearchResult identificationSearchResult = (IdentificationSearchResult) graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "identifier",
                user
        ).iterator().next();
        assertThat(
                identificationSearchResult.getGraphElement().label(),
                is("identifier of vertex Bareau")
        );
    }

    @Test
    public void can_get_detail_search_result_of_an_identifier(){
        vertexB.comment("description of vertex b");
        IdentificationPojo vertexBAsIdentifier = identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "identifier of vertex Bareau"
        );
        vertexA.addGenericIdentification(vertexBAsIdentifier);
        IdentificationSearchResult identificationSearchResult = (IdentificationSearchResult) graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "identifier",
                user
        ).iterator().next();
        GraphElementSearchResult searchResult = graphSearch.getDetails(
                identificationSearchResult.getGraphElement().uri(),
                user
        );
        assertThat(
                searchResult.getGraphElement().comment(),
                is("description of vertex b")
        );
    }
    @Test
    public void identifiers_are_included_in_searching_relations_for_identification(){
        IdentificationPojo edgeBetweenAAndBAsAnIdentification = identificationFromFriendlyResource(
                vertexA.getEdgeThatLinksToDestinationVertex(vertexB)
        );
        edgeBetweenAAndBAsAnIdentification.setLabel("identifier");
        vertexB.getEdgeThatLinksToDestinationVertex(vertexC).addGenericIdentification(
                edgeBetweenAAndBAsAnIdentification
        );

        GraphElementSearchResult result = graphSearch.searchRelationsPropertiesSchemasOrIdentifiersForAutoCompletionByLabel(
                "identifier",
                user
        ).iterator().next();
        assertThat(
                result.getType(),
                is("identification")
        );
    }


}