/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.search;

import com.google.common.collect.Sets;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.schema.Schema;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.module.model.search.*;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestScenarios;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.test.module.utils.search.Neo4jSearchRelatedTest;
import org.junit.Test;

import java.net.URI;
import java.util.*;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphSearchTest extends Neo4jSearchRelatedTest {

    @Test
    public void can_search_vertices_for_auto_completion() throws Exception {
        indexGraph();
        indexVertex(pineApple);
        List<GraphElementSearchResult> vertices;
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
        List<GraphElementSearchResult> vertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
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
        List<GraphElementSearchResult> searchResults = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
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
        List<GraphElementSearchResult> privateVertices = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
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
        List<GraphElementSearchResult> privateSearchResult = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "schema",
                user
        );
        assertTrue(privateSearchResult.isEmpty());
    }

    @Test
    public void cannot_search_for_the_identifiers_of_another_user() {
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        List<GraphElementSearchResult> searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "Computer ",
                user
        );
        assertThat(
                searchResults.size(),
                is(1)
        );
        searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "Computer ",
                user2
        );
        assertThat(
                searchResults.size(),
                is(0)
        );
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
        List<GraphElementSearchResult> results = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                "between vert",
                user
        );
        assertThat(results.size(), is(2));
    }

    @Test
    public void relation_source_and_destination_vertex_label_and_uri_are_included_in_result() {
        indexGraph();
        List<GraphElementSearchResult> relations = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                "between vertex A and B",
                user
        );
        Map<URI, String> context = relations.get(0).getContext();
        assertThat(
                context.get(vertexA.uri()),
                is("vertex Azure")
        );
        assertThat(
                context.get(vertexB.uri()),
                is("vertex Bareau")
        );
    }

    @Test
    public void schemas_are_included_in_relations_search() {
        List<GraphElementSearchResult> results = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
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
        results = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
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
        GraphElementSearchResult result = searchResults.get(0);
        Collection<String> properties = result.getContext().values();
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
        result = searchResults.get(0);
        properties = result.getContext().values();
        assertThat(
                properties.size(),
                is(2)
        );
        assertTrue(
                properties.contains(
                        "prop1"
                )
        );
        assertTrue(
                properties.contains(
                        "prop2"
                )
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
                graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
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
                graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
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
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schema.uri()
        );
        graphIndexer.indexProperty(
                schemaPojo.getProperties().values().iterator().next(),
                schemaPojo
        );
        GraphElementSearchResult searchResult = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                "prop1",
                userGraph.user()
        ).get(0);
        assertThat(
                searchResult.getContext().values().iterator().next(),
                is("schema1")
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
        List<GraphElementSearchResult> searchResults = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
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

        List<GraphElementSearchResult> privateSearchResults = graphSearch.searchOnlyForOwnVerticesOrSchemasForAutoCompletionByLabel(
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
    public void search_results_have_identifiers() {
        indexGraph();
        GraphElement vertex = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexA.label(),
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getIdentifications().size(),
                is(0)
        );
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        indexGraph();
        vertex = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexA.label(),
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getIdentifications().size(),
                is(1)
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
        vertexB.addMeta(
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
        IdentifierPojo identification = modelTestScenarios.computerScientistType();
        String identificationImage = UUID.randomUUID().toString();
        identification.setImages(Sets.newHashSet(
                Image.withBase64ForSmallAndUriForBigger(
                        identificationImage,
                        URI.create("/large_1")
                )
        ));
        vertexA.addMeta(
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
    public void vertices_have_their_surround_vertices_label_and_uri_in_result() {
        graphIndexer.indexVertex(vertexA);
        GraphElementSearchResult searchResult = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                vertexA.label(),
                user
        ).iterator().next();
        assertThat(
                searchResult.getContext().get(
                        vertexB.uri()
                ),
                is("vertex Bareau")
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
        List<GraphElementSearchResult> publicVerticesOnlyResult = graphSearch.searchPublicVerticesOnly(
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
    public void does_not_include_private_vertices_of_another_user() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        graphIndexer.indexVertex(vertexB);
        Map<URI, String> surroundVertices = graphSearch.searchPublicVerticesOnly(
                "vertex Bareau"
        ).iterator().next().getContext();
        assertThat(
                surroundVertices.size(),
                is(2)
        );
        vertexC.makePrivate();
        graphIndexer.indexVertex(vertexB);
        surroundVertices = graphSearch.searchPublicVerticesOnly(
                "vertex Bareau"
        ).iterator().next().getContext();
        assertThat(
                surroundVertices.size(),
                is(1)
        );
    }

    @Test
    public void includes_private_surround_vertices_if_owner() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        indexGraph();
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        ).iterator().next();
        Map<URI, String> vertices = vertexSearchResult.getContext();
        assertThat(
                vertices.size(),
                is(2)
        );
        vertexC.makePrivate();
        vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        ).iterator().next();
        vertices = vertexSearchResult.getContext();
        assertThat(
                vertices.size(),
                is(2)
        );
    }

    @Test
    public void identifiers_are_included() {
        IdentifierPojo vertexBAsIdentifier = TestScenarios.identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getGraphElement().getIdentifications().size(),
                is(1)
        );
    }

    @Test
    public void related_elements_do_not_include_the_identifier() {
        IdentifierPojo vertexBAsIdentifier = TestScenarios.identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Bareau",
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(2)
        );
    }

    @Test
    public void can_search_for_identifiers() {
        IdentifierPojo vertexBAsIdentifier = TestScenarios.identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        List<GraphElementSearchResult> searchResults = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "some identifier",
                user
        );
        assertThat(
                searchResults.size(),
                is(1)
        );
    }

    @Test
    public void identifiers_have_their_external_uri() {
        IdentifierPojo vertexBAsIdentifier = TestScenarios.identificationFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        GraphElementSearchResult searchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "some identifier",
                user
        ).iterator().next();
        assertThat(
                searchResult.getGraphElement().getIdentifications().values().iterator().next().getExternalResourceUri(),
                is(vertexBAsIdentifier.getExternalResourceUri())
        );
    }

    @Test
    public void search_elements_have_the_number_of_times_they_were_centered() {
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexC
        ).incrementNumberOfVisits();
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "vertex Cadeau",
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getNbVisits(),
                is(1)
        );
    }
}