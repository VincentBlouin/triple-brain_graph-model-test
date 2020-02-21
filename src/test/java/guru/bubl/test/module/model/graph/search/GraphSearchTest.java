/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.search;

import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.search.*;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.search.Neo4jSearchRelatedTest;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.*;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphSearchTest extends Neo4jSearchRelatedTest {

    @Test
    public void can_use_parenthesis() {
        vertexA.label("z(arg");
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "z(arg"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void can_use_accent() {
        vertexC.label("tâche");
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "tâche"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void search_queries_can_have_single_quotes() {
        vertexA.label("a'test");
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "a'test"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("a'test")
        );
    }

//    @Test 
//    ("Todo")
//    public void search_queries_can_have_special_characters() {
//        vertexA.label("a\\(test*");
//        centerGraphElementOperatorFactory.usingFriendlyResource(
//                vertexA
//        ).incrementNumberOfVisits();
//        indexGraph();
//        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
//                "a\\(test*"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        );
//        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
//        assertThat(
//                vertex.label(),
//                is("a\\(test*")
//        );
//    }

    @Test
    public void can_search_vertices_for_auto_completion() {
        indexGraph();
        indexVertex(pineApple);
        List<GraphElementSearchResult> vertices;
        vertices = graphSearchFactory.usingSearchTerm("vert").searchOnlyForOwnVerticesForAutoCompletionByLabel(user);
        assertThat(vertices.size(), is(3));
        vertices = graphSearchFactory.usingSearchTerm("Cad").searchOnlyForOwnVerticesForAutoCompletionByLabel(user);
        assertThat(vertices.size(), is(1));
        GraphElement firstVertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(firstVertex.label(), is("vertex Cadeau"));
        vertices = graphSearchFactory.usingSearchTerm("Appl").searchOnlyForOwnVerticesForAutoCompletionByLabel(user);
        assertThat(vertices.size(), is(1));
    }

    @Test
    public void cant_search_in_vertices_of_another_user() {
        indexGraph();
        indexVertex(pineApple);
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm("vert").searchOnlyForOwnVerticesForAutoCompletionByLabel(
                user
        );
        assertTrue(vertices.size() > 0);
        vertices = graphSearchFactory.usingSearchTerm("vert").searchOnlyForOwnVerticesForAutoCompletionByLabel(
                user2
        );
        assertFalse(vertices.size() > 0);
    }

    @Test
    public void vertex_comment_is_not_included_in_search_result() {
        vertexA.comment("A description");
        indexGraph();
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                vertexA.label()
        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
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
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "vert"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user2
        );
        assertTrue(vertices.isEmpty());
        vertexA.makePublic();
        indexVertex(vertexA);
        vertices = graphSearchFactory.usingSearchTerm("vert").searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user2
        );
        assertFalse(vertices.isEmpty());
    }

    @Test
    public void search_for_other_users_public_vertices_does_not_include_private_tags() {
        vertexA.makePublic();
        Tag tag = vertexA.addMeta(modelTestScenarios.computerScientistType()).values().iterator().next();
        GraphElementSearchResult result = graphSearchFactory.usingSearchTerm(
                "vert"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user2
        ).iterator().next();
        assertTrue(
                result.getGraphElement().getIdentifications().isEmpty()
        );
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(
                ShareLevel.PUBLIC
        );
        result = graphSearchFactory.usingSearchTerm("vert").searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user2
        ).iterator().next();
        assertFalse(
                result.getGraphElement().getIdentifications().isEmpty()
        );
    }


    @Test
    public void searching_for_own_vertices_only_does_not_return_vertices_of_other_users() {
        vertexA.makePublic();
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "vert"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user2
        );
        assertTrue(vertices.size() > 0);
        List<GraphElementSearchResult> privateVertices = graphSearchFactory.usingSearchTerm(
                "vert"
        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
                user2
        );
        assertFalse(privateVertices.size() > 0);
    }

//    @Test 
//    ("schema feature is suspended")
//    public void searching_for_own_vertices_does_not_return_schemas() {
//        SchemaOperator schema = createSchema(user);
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        List<GraphElementSearchResult> searchResult = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        );
//        assertFalse(searchResult.isEmpty());
//        List<GraphElementSearchResult> privateSearchResult = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
//                user
//        );
//        assertTrue(privateSearchResult.isEmpty());
//    }

    @Test
    public void cannot_search_for_the_identifiers_of_another_user() {
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "Computer"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertThat(
                searchResults.size(),
                is(1)
        );
        searchResults = graphSearchFactory.usingSearchTerm(
                "Computer "
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
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
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "vert"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertTrue(vertices.size() > 0);
        vertices = graphSearchFactory.usingSearchTerm(
                "Vert"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertTrue(vertices.size() > 0);
    }

    @Test
    public void case_is_preserved_when_getting_label() {
        vertexA.label("Azure");
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "azure"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("Azure")
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

        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "pr"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertThat(vertices.size(), is(2));
    }


    @Test
    public void can_search_relations() {
        indexGraph();
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "between"
        ).searchRelationsForAutoCompletionByLabel(
                user
        );
        assertThat(results.size(), is(2));
    }

    @Test
    public void relation_source_and_destination_vertex_label_and_uri_are_included_in_result() {
        indexGraph();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA.getEdgeThatLinksToDestinationVertex(vertexB)
        ).incrementNumberOfVisits();
        List<GraphElementSearchResult> relations = graphSearchFactory.usingSearchTerm(
                "between vertex A and B"
        ).searchRelationsForAutoCompletionByLabel(
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

//    @Test 
//    ("schema feature is suspended")
//    public void schemas_are_included_in_relations_search() {
//        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
//                "schema1"
//        ).searchRelationsForAutoCompletionByLabel(
//                user
//        );
//        assertTrue(results.isEmpty());
//        SchemaOperator schema = createSchema(user);
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        results = graphSearchFactory.usingSearchTerm(
//                "schema1"
//        ).searchRelationsForAutoCompletionByLabel(
//                user
//        );
//        assertFalse(results.isEmpty());
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void can_search_schema() {
//        SchemaOperator schema = createSchema(user);
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        );
//        assertThat(results.size(), is(1));
//        GraphElementPojo schemaAsSearchResult = results.iterator().next().getGraphElement();
//        assertThat(
//                schemaAsSearchResult.uri(),
//                is(
//                        schema.uri()
//                )
//        );
//    }

//    @Test 
//    ("schema feature is suspended")
//    public void schema_properties_can_be_retrieved() {
//        SchemaOperator schema = createSchema(userGraph.user());
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                userGraph.user()
//        );
//        GraphElementSearchResult result = searchResults.get(0);
//        Collection<String> properties = result.getContext().values();
//        assertTrue(
//                properties.isEmpty()
//        );
//        GraphElementOperator property1 = schema.addProperty();
//        property1.label("prop1");
//        GraphElementOperator property2 = schema.addProperty();
//        property2.label("prop2");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        searchResults = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                userGraph.user()
//        );
//        result = searchResults.get(0);
//        properties = result.getContext().values();
//        assertThat(
//                properties.size(),
//                is(2)
//        );
//        assertTrue(
//                properties.contains(
//                        "prop1"
//                )
//        );
//        assertTrue(
//                properties.contains(
//                        "prop2"
//                )
//        );
//    }

//    @Test 
//    ("schema feature is suspended")
//    public void can_search_schema_property() {
//        SchemaOperator schema = createSchema(userGraph.user());
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        assertTrue(
//                graphSearchFactory.usingSearchTerm(
//                        "prop"
//                ).searchRelationsForAutoCompletionByLabel(
//                        userGraph.user()
//                ).isEmpty()
//        );
//        GraphElementOperator property1 = schema.addProperty();
//        property1.label("prop1");
//        GraphElementOperator property2 = schema.addProperty();
//        property2.label("prop2");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        assertThat(
//                graphSearchFactory.usingSearchTerm(
//                        "prop1"
//                ).searchRelationsForAutoCompletionByLabel(
//                        userGraph.user()
//                ).size(),
//                is(1)
//        );
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void schema_label_and_uri_are_included_in_property_search_result() {
//        SchemaOperator schema = createSchema(userGraph.user());
//        schema.label("schema1");
//        GraphElementOperator property1 = schema.addProperty();
//        property1.label("prop1");
//        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
//                schema.uri()
//        );
//        graphIndexer.indexProperty(
//                schemaPojo.getProperties().values().iterator().next(),
//                schemaPojo
//        );
//        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
//                "prop1"
//        ).searchRelationsForAutoCompletionByLabel(
//                userGraph.user()
//        ).get(0);
//        assertThat(
//                searchResult.getContext().values().iterator().next(),
//                is("schema1")
//        );
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void can_search_not_owned_schema() {
//        SchemaOperator schema = createSchema(user);
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user2
//        );
//        assertFalse(searchResults.isEmpty());
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void can_search_not_owned_schema_property() {
//        SchemaOperator schema = createSchema(user);
//        GraphElementOperator property1 = schema.addProperty();
//        property1.label("prop");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
//                "prop"
//        ).searchRelationsForAutoCompletionByLabel(
//                user2
//        );
//        assertFalse(searchResults.isEmpty());
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void can_search_for_only_owned_schemas() {
//        SchemaOperator schema = createSchema(user);
//        schema.label("schema1");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        SchemaOperator schema2 = createSchema(user2);
//        schema2.label("schema2");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema2.uri()
//                )
//        );
//        graphIndexer.commit();
//        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        );
//        assertThat(
//                searchResults.size(),
//                is(2)
//        );
//
//        List<GraphElementSearchResult> privateSearchResults = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
//                user
//        );
//        assertThat(
//                privateSearchResults.size(),
//                is(1)
//        );
//    }
//
//
//    @Test 
//    ("schema feature is suspended")
//    public void schema_search_results_dont_have_comment() {
//        SchemaOperator schema = createSchema(user);
//        schema.label("schema1");
//        schema.comment("test comment");
//        graphIndexer.indexSchema(
//                userGraph.schemaPojoWithUri(
//                        schema.uri()
//                )
//        );
//        graphIndexer.commit();
//        GraphElementSearchResult result = graphSearchFactory.usingSearchTerm("schema").searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        ).get(0);
//        assertThat(
//                result.getGraphElement().comment(),
//                is("")
//        );
//    }


    @Test

    public void search_results_have_identifiers() {
        indexGraph();
        GraphElement vertex = graphSearchFactory.usingSearchTerm(
                vertexA.label()
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
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
        vertex = graphSearchFactory.usingSearchTerm(
                "Azure"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getIdentifications().size(),
                is(1)
        );
    }

//    @Test 
//    ("getDetails is suspended")
//    public void can_get_more_details_for_element_with_uri() {
//        indexGraph();
//        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
//                "Azure"
//        ).getDetails(
//                vertexA.uri(),
//                user
//        );
//        GraphElement vertex = searchResult.getGraphElementSearchResult().getGraphElement();
//        assertThat(
//                vertex.label(),
//                is(vertexA.label())
//        );
//    }

//    @Test 
//    ("getDetails is suspended")
//    public void elements_with_no_identifications_dont_have_identifications() {
//        vertexB.addMeta(
//                new ModelTestScenarios().computerScientistType()
//        );
//        GraphElementSearchResult searchResult = graphSearch.getDetails(
//                vertexA.uri(),
//                user
//        );
//        assertTrue(
//                searchResult.getGraphElement().getIdentifications().isEmpty()
//        );
//    }

//    @Test 
//    ("schema feature is suspended")
//    public void can_get_property_details() {
//        SchemaOperator schema = createSchema(userGraph.user());
//        GraphElementOperator property1 = schema.addProperty();
//        property1.label("prop1");
//        GraphElementSearchResult searchResult = graphSearch.getDetails(
//                property1.uri(),
//                userGraph.user()
//        );
//        assertThat(
//                searchResult.getGraphElement().label(),
//                is("prop1")
//        );
//    }

//    @Test 
//    ("getDetails is suspended")
//    public void more_details_contains_comment() {
//        vertexA.comment("A description");
//        indexGraph();
//        GraphElementSearchResult searchResult = graphSearch.getDetails(
//                vertexA.uri(),
//                user
//        );
//        assertThat(
//                searchResult.getGraphElement().comment(),
//                is("A description")
//        );
//    }

//    @Test 
//    ("getDetails is suspended")
//    public void more_details_contains_image() {
//        GraphElementSearchResult searchResult = graphSearchFactory.getDetails(
//                vertexA.uri(),
//                user
//        );
//        assertThat(
//                searchResult.getGraphElement().images().size(),
//                is(0)
//        );
//        Image image1 = Image.withUrlForSmallAndUriForBigger(
//                UUID.randomUUID().toString(),
//                URI.create("/large_1")
//        );
//        vertexA.addImages(Sets.newHashSet(
//                image1
//        ));
//        searchResult = graphSearch.getDetails(
//                vertexA.uri(),
//                user
//        );
//        assertThat(
//                searchResult.getGraphElement().images().size(),
//                is(1)
//        );
//    }

//    @Test 
//    ("getDetails is suspended")
//    public void more_details_contains_identification_image_if_has_none() {
//        IdentifierPojo identification = modelTestScenarios.computerScientistType();
//        String identificationImage = UUID.randomUUID().toString();
//        identification.setImages(Sets.newHashSet(
//                Image.withUrlForSmallAndUriForBigger(
//                        identificationImage,
//                        URI.create("/large_1")
//                )
//        ));
//        vertexA.addMeta(
//                identification
//        );
//        GraphElementSearchResult searchResult = graphSearch.getDetails(
//                vertexA.uri(),
//                user
//        );
//        Image image = searchResult.getGraphElement().images().iterator().next();
//        assertThat(
//                image.urlForSmall(),
//                is(identificationImage)
//        );
//        String vertexImage = UUID.randomUUID().toString();
//        vertexA.addImages(Sets.newHashSet(
//                Image.withUrlForSmallAndUriForBigger(
//                        vertexImage,
//                        URI.create("/large_1")
//                )
//        ));
//        searchResult = graphSearch.getDetails(
//                vertexA.uri(),
//                user
//        );
//        image = searchResult.getGraphElement().images().iterator().next();
//        assertThat(
//                image.urlForSmall(),
//                is(vertexImage)
//        );
//    }

//    @Test 
//    ("getDetails is suspended")
//    public void cannot_get_by_uri_if_not_owner_of_non_public_element() {
//        GraphElementSearchResult result = graphSearch.getDetails(
//                vertexA.uri(),
//                user2
//        );
//        assertNull(result);
//    }

    @Test

    public void vertices_have_their_surround_vertices_label_and_uri_in_result() {
        graphIndexer.indexVertex(vertexA);
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Azure"
        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
                user
        ).iterator().next();
        assertThat(
                searchResult.getContext().get(
                        vertexB.uri()
                ),
                is("vertex Bareau")
        );
    }

//    @Test 
//    ("schema feature is suspended")
//    public void results_limit_is_on_number_of_results_not_number_of_related_elements() {
//        SchemaOperator schema = createSchema(userGraph.user());
//        schema.label("schema1");
//        for (int i = 1; i <= 12; i++) {
//            schema.addProperty();
//        }
//        SchemaOperator schema2 = createSchema(userGraph.user());
//        schema2.label("schema2");
//        for (int i = 1; i <= 12; i++) {
//            schema2.addProperty();
//        }
//        List<GraphElementSearchResult> searchResult = graphSearchFactory.usingSearchTerm(
//                "schema"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        );
//        assertThat(
//                searchResult.size(),
//                is(2)
//        );
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void can_search_public_vertices_as_anonymous_user() {
//        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
//                "vert"
//        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
//                user
//        );
//        assertThat(
//                results.size(),
//                is(5)
//        );
//        vertexB.makePublic();
//        List<GraphElementSearchResult> publicVerticesOnlyResult = graphSearchFactory.usingSearchTerm(
//                "vert"
//        ).searchPublicVerticesOnly();
//        assertThat(
//                publicVerticesOnlyResult.size(),
//                is(1)
//        );
//    }
//
//    @Test 
//    ("getDetails is suspended")
//    public void can_get_search_details_of_public_resource_as_anonymous_user() {
////        vertexB.comment("some comment");
////        vertexB.makePublic();
////        GraphElementSearchResult searchResult = graphSearch.getDetailsAnonymously(
////                vertexB.uri()
////        );
////        assertThat(
////                searchResult.getGraphElement().comment(),
////                is("some comment")
////        );
//    }
//
//    @Test 
//    ("schema feature is suspended")
//    public void cannot_get_search_details_of_private_resource_as_anonymous_user() {
//        vertexB.comment("some comment");
//        vertexB.makePrivate();
//        GraphElementSearchResult searchResult = graphSearch.getDetailsAnonymously(
//                vertexB.uri()
//        );
//        assertNull(searchResult);
//    }

    @Test

    public void does_not_include_private_vertices_of_another_user() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        graphIndexer.indexVertex(vertexB);
        Map<URI, String> surroundVertices = graphSearchFactory.usingSearchTerm(
                "Bareau"
        ).searchPublicVerticesOnly().iterator().next().getContext();
        assertThat(
                surroundVertices.size(),
                is(2)
        );
        vertexC.makePrivate();
        graphIndexer.indexVertex(vertexB);
        surroundVertices = graphSearchFactory.usingSearchTerm(
                "Bareau"
        ).searchPublicVerticesOnly().iterator().next().getContext();
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
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "vertex Bareau"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).iterator().next();
        Map<URI, String> vertices = vertexSearchResult.getContext();
        assertThat(
                vertices.size(),
                is(2)
        );
        vertexC.makePrivate();
        vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "vertex Bareau"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).iterator().next();
        vertices = vertexSearchResult.getContext();
        assertThat(
                vertices.size(),
                is(2)
        );
    }

    @Test
    public void tags_are_included() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "Azure"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getGraphElement().getIdentifications().size(),
                is(1)
        );
    }

    @Test
    public void tag_nb_references_is_included() {
        TagPojo tag = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        vertexB.addMeta(
                tag
        );
        vertexC.addMeta(
                tag
        );
        assertThat(
                tagFactory.withUri(tag.uri()).getNbReferences(),
                is(3)
        );
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Person"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).iterator().next();
        assertThat(
                searchResult.getGraphElement().label(),
                is("Person")
        );
        assertThat(
                searchResult.getNbReferences(),
                is(3)
        );
        assertThat(
                searchResult.getGraphElement().getIdentifications().values().iterator().next().getNbReferences(),
                is(3)
        );
    }

    @Test
    public void can_search_for_tags_only() {
        vertexA.label("Computer Scientist");
        vertexB.addMeta(
                modelTestScenarios.computerScientistType()
        );
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "Computer Scientist"
        ).searchForAllOwnResources(user);
        assertThat(
                results.size(),
                is(2)
        );
        results = graphSearchFactory.usingSearchTerm(
                "Computer Scientist"
        ).searchOwnTagsForAutoCompletionByLabel(user);
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void related_elements_do_not_include_the_identifier() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "Bareau"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(2)
        );
    }

    @Test
    public void can_search_for_identifiers() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "some identifier"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        );
        assertThat(
                searchResults.size(),
                is(1)
        );
    }

    @Test

    public void identifiers_have_their_external_uri() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addMeta(vertexBAsIdentifier);
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "some identifier"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
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
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "vertex Cadeau"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getNbVisits(),
                is(1)
        );
    }

    @Test

    public void meta_context_is_description_if_it_has_one() {
        TagPojo meta = vertexA.addMeta(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        graphIndexer.indexMeta(meta);
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Possession"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).get(0);
        assertThat(
                searchResult.getContext().values().iterator().next(),
                is("In law, possession is the control a person intentionally exercises toward a thing. In all cases, to possess something, a person must have an intention to possess it. A person may be in possession of some property. Like ownership, the possession of things is commonly regulated by states under property law.")
        );
    }

    @Test

    public void meta_context_is_surround_elements_if_it_has_no_description() {
        TagPojo meta = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        graphIndexer.indexMeta(meta);
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Computer"
        ).searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                user
        ).get(0);
        assertThat(
                searchResult.getContext().values().iterator().next(),
                is("vertex Azure")
        );
    }

    @Test
    public void can_skip_search_results() {
        CenterGraphElementOperator vertexACenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        vertexACenter.incrementNumberOfVisits();
        vertexACenter.incrementNumberOfVisits();
        CenterGraphElementOperator vertexBCenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        vertexBCenter.incrementNumberOfVisits();
        GraphElementSearchResult firstResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex",
                0,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertThat(
                firstResult.getGraphElement().label(),
                is("vertex Azure")
        );
        firstResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex",
                1,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertThat(
                firstResult.getGraphElement().label(),
                is("vertex Bareau")
        );
    }

    @Test
    public void search_result_includes_share_level() {
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex Azure",
                0,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertThat(
                searchResult.getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        vertexA.makePublic();
        searchResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex Azure",
                0,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertThat(
                searchResult.getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
    }

    @Test
    public void includes_colors() {
        vertexA.setColors("colors");
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex Azure",
                0,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertThat(
                searchResult.getGraphElement().getColors(),
                is("colors")
        );
    }

    @Test
    public void includes_is_pattern() {
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex Azure",
                0,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertFalse(
                searchResult.isPattern()
        );
        vertexA.makePattern();
        searchResult = graphSearchFactory.usingSearchTermSkipAndLimit(
                "vertex Azure",
                0,
                10
        ).searchForAllOwnResources(user).iterator().next();
        assertTrue(
                searchResult.isPattern()
        );
    }

    @Test
    public void excludes_graph_elements_under_pattern() {
        vertexB.label("soleil");
        assertFalse(
                vertexB.isUnderPattern()
        );
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTermSkipAndLimit(
                "soleil",
                0,
                10
        ).searchForAllOwnResources(user);
        assertThat(
                searchResults.size(),
                is(1)
        );
        vertexA.makePattern();
        assertTrue(
                vertexB.isUnderPattern()
        );
        searchResults = graphSearchFactory.usingSearchTermSkipAndLimit(
                "soleil",
                0,
                10
        ).searchForAllOwnResources(user);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }
}
