/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.search;

import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.graph.graph_element.GraphElement;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.search.Neo4jSearchRelatedTest;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphSearchTest extends Neo4jSearchRelatedTest {

    @Test
    public void can_use_parenthesis() {
        vertexA.label("z(arg");
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "z(arg"
        ).searchForAllOwnResources(
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
        ).searchForAllOwnResources(
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
        ).searchForAllOwnResources(
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("a'test")
        );
    }

    @Test
    public void search_queries_can_have_special_characters() {
        vertexA.label("a\\(test*");
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        ).incrementNumberOfVisits();
        indexGraph();
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "a\\(test*"
        ).searchForAllOwnResources(
                user
        );
        GraphElement vertex = vertices.get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.label(),
                is("a\\(test*")
        );
    }

    @Test
    public void can_search_vertices_for_auto_completion() {
        indexGraph();
        indexVertex(pineApple);
        List<GraphElementSearchResult> vertices;
        vertices = graphSearchFactory.usingSearchTerm("vert").searchOnlyForOwnVerticesForAutoCompletionByLabel(user);
        assertThat(vertices.size(), is(5));
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
    public void cannot_search_for_the_identifiers_of_another_user() {
        vertexA.addTag(
                modelTestScenarios.computerScientistType()
        );
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "Computer"
        ).searchForAllOwnResources(
                user
        );
        assertThat(
                searchResults.size(),
                is(1)
        );
        searchResults = graphSearchFactory.usingSearchTerm(
                "Computer "
        ).searchForAllOwnResources(
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
        ).searchForAllOwnResources(
                user
        );
        assertTrue(vertices.size() > 0);
        vertices = graphSearchFactory.usingSearchTerm(
                "Vert"
        ).searchForAllOwnResources(
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
        ).searchForAllOwnResources(
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
        ).searchForAllOwnResources(
                user
        );
        assertThat(vertices.size(), is(2));
    }

    @Test
    public void can_search_relations() {
        indexGraph();
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "edge"
        ).searchRelationsForAutoCompletionByLabel(
                user
        );
        assertThat(results.size(), is(4));
    }

    @Test
    public void relation_source_and_destination_vertex_label_and_uri_are_included_in_result() {
        indexGraph();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA.getEdgeToDestinationVertex(vertexB)
        ).incrementNumberOfVisits();
        List<GraphElementSearchResult> relations = graphSearchFactory.usingSearchTerm(
                "edge AB"
        ).searchRelationsForAutoCompletionByLabel(
                user
        );
        String context = relations.get(0).getContext();
        assertThat(
                context,
                is("vertex Bareau{{vertex Azure")
        );
    }

    @Test
    public void search_results_have_tags() {
        indexGraph();
        GraphElement vertex = graphSearchFactory.usingSearchTerm(
                vertexA.label()
        ).searchForAllOwnResources(
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getTags().size(),
                is(0)
        );
        vertexA.addTag(
                modelTestScenarios.computerScientistType()
        );
        indexGraph();
        vertex = graphSearchFactory.usingSearchTerm(
                "Azure"
        ).searchForAllOwnResources(
                user
        ).get(0).getGraphElementSearchResult().getGraphElement();
        assertThat(
                vertex.getTags().size(),
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
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Azure"
        ).searchOnlyForOwnVerticesForAutoCompletionByLabel(
                user
        ).iterator().next();
        assertThat(
                searchResult.getContext(),
                is("vertex Bareau")
        );
    }


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
    @Test
    public void does_not_include_private_vertices_of_another_user() {
        vertexB.makePublic();
        vertexA.makePublic();
        vertexC.makePublic();
        wholeGraphAdmin.reindexAll();
        String surroundVertices = graphSearchFactory.usingSearchTerm(
                "Bareau"
        ).searchForAllOwnResources(user).iterator().next().getContext();
        assertThat(
                surroundVertices.split("\\{\\{").length,
                is(2)
        );
        vertexC.makePrivate();
        wholeGraphAdmin.reindexAll();
        List<GraphElementSearchResult> searchResult = graphSearchFactory.usingSearchTerm(
                "Bareau"
        ).searchForAllOwnResources(user2);
        assertThat(
                searchResult.size(),
                is(0)
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
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        String vertices = vertexSearchResult.getContext();
        assertThat(
                vertices.split("\\{\\{").length,
                is(2)
        );
        vertexC.makePrivate();
        vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "vertex Bareau"
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        vertices = vertexSearchResult.getContext();
        assertThat(
                vertices.split("\\{\\{").length,
                is(2)
        );
    }

    @Test
    public void tags_are_included() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addTag(vertexBAsIdentifier);
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "Azure"
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getGraphElement().getTags().size(),
                is(1)
        );
    }

    @Test
    public void tag_neighbors_is_included() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        vertexB.addTag(
                tag
        );
        vertexC.addTag(
                tag
        );
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getTotal(),
                is(3)
        );
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Person"
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                searchResult.getGraphElement().label(),
                is("Person")
        );
        assertThat(
                searchResult.getNbNeighbors().getTotal(),
                is(3)
        );
        assertThat(
                searchResult.getGraphElement().getTags().values().iterator().next().getNbNeighbors().getTotal(),
                is(3)
        );
    }

    @Test
    public void can_search_for_tags_only() {
        vertexA.label("Computer Scientist");
        vertexB.addTag(
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
        vertexA.addTag(vertexBAsIdentifier);
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                "Bareau"
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().split("\\{\\{").length,
                is(2)
        );
    }

    @Test
    public void can_search_for_identifiers() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(vertexB);
        vertexBAsIdentifier.setLabel(
                "some identifier"
        );
        vertexA.addTag(vertexBAsIdentifier);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "some identifier"
        ).searchForAllOwnResources(
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
        vertexA.addTag(vertexBAsIdentifier);
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "some identifier"
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                searchResult.getGraphElement().getTags().values().iterator().next().getExternalResourceUri(),
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
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getNbVisits(),
                is(1)
        );
    }

    @Test

    public void meta_context_is_description_if_it_has_one() {
        TagPojo meta = vertexA.addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Possession"
        ).searchForAllOwnResources(
                user
        ).get(0);
        assertThat(
                searchResult.getContext(),
                is("In law, possession is the control a person intentionally exercises toward a thing. In all cases, to possess something, a person must have an intention to possess it. A person may be in possession of some property. Like ownership, the possession of things is commonly regulated by states under property law.")
        );
    }

    @Test

    public void meta_context_is_surround_elements_if_it_has_no_description() {
        TagPojo meta = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult searchResult = graphSearchFactory.usingSearchTerm(
                "Computer"
        ).searchForAllOwnResources(
                user
        ).get(0);
        assertThat(
                searchResult.getContext(),
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

    @Test
    public void can_search_all_patterns() {
        vertexB.makePublic();
        List<GraphElementSearchResult> vertices = graphSearchFactory.usingSearchTerm(
                "vertex B"
        ).searchAllPatterns();
        assertTrue(
                vertices.isEmpty()
        );
        vertexB.makePattern();
        vertices = graphSearchFactory.usingSearchTerm(
                "vertex B"
        ).searchAllPatterns();
        assertThat(
                vertices.size(),
                is(1)
        );
    }

}
