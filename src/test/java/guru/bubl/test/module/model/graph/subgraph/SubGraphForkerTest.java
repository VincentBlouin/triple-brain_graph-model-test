/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.subgraph;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphElementType;
import guru.bubl.module.model.graph.identification.Identifier;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.*;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jFriendlyResource;
import guru.bubl.module.neo4j_graph_manipulator.graph.Relationships;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.identification.Neo4jIdentification;
import guru.bubl.module.neo4j_graph_manipulator.graph.search.SearchResultGetter;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class SubGraphForkerTest extends ModelTestResources {

    @Test
    public void can_integrate_subgraph() {
        vertexA.label("ananas");
        vertexA.makePublic();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        List<GraphElementSearchResult> results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "ananas", anotherUser
        );
        assertThat(
                results.size(),
                is(0)
        );
        anotherUserForker.fork(
                subGraph
        );
        results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "ananas", anotherUser
        );
        assertThat(
                results.size(),
                is(2)
        );
    }

    @Test
    public void forking_subgraph_with_single_vertex_works() {
        vertexOfAnotherUser.makePublic();
        Integer numberOfVertices = numberOfVertices();
        forker.fork(
                anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                        0,
                        vertexOfAnotherUser.uri()
                )
        );
        assertThat(
                numberOfVertices(),
                is(numberOfVertices + 1)
        );
    }

    @Test
    public void cannot_fork_a_vertex_that_is_not_public() {
        vertexA.label("ananas");
        vertexB.label("ananas2");
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        anotherUserForker.fork(
                subGraph
        );
        List<GraphElementSearchResult> results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "ananas", anotherUser
        );
        assertThat(
                results.size(),
                is(0)
        );
        vertexA.makePublic();
        anotherUserForker.fork(
                subGraph
        );
        results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "ananas", anotherUser
        );
        assertThat(
                results.size(),
                is(2)
        );
        vertexB.makePublic();
        anotherUserForker.fork(
                userGraph.graphWithDepthAndCenterBubbleUri(
                        0,
                        vertexB.uri()
                )
        );
        results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "ananas", anotherUser
        );
        assertThat(
                results.size(),
                is(4)
        );
    }

    @Test
    public void edges_are_included() {
        vertexOfAnotherUser.makePublic();
        VertexOperator newVertex = vertexFactory.withUri(
                vertexOfAnotherUser.addVertexAndRelation().destinationVertex().uri()
        );
        newVertex.makePublic();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexOfAnotherUser.uri()
        );
        Integer numberOfEdges = numberOfEdges();
        forker.fork(
                subGraph
        );
        assertThat(
                numberOfEdges(),
                is(numberOfEdges + 1)
        );
    }

    @Test
    public void edges_between_any_private_vertex_are_not_forked() {
        VertexOperator destinationVertex = vertexFactory.withUri(
                vertexOfAnotherUser.addVertexAndRelation().destinationVertex().uri()
        );
        vertexOfAnotherUser.makePrivate();
        destinationVertex.makePublic();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexOfAnotherUser.uri()
        );
        Integer numberOfEdges = numberOfEdges();
        forker.fork(
                subGraph
        );
        assertThat(
                numberOfEdges(),
                is(numberOfEdges)
        );
        destinationVertex.makePrivate();
        vertexOfAnotherUser.makePublic();
        forker.fork(
                subGraph
        );
        assertThat(
                numberOfEdges(),
                is(numberOfEdges)
        );
    }

    @Test
    public void fork_is_identified_to_original() {
        vertexOfAnotherUser.makePublic();
        Vertex forkVertex = forker.fork(
                anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                        0,
                        vertexOfAnotherUser.uri()
                )
        ).values().iterator().next();
        VertexInSubGraphPojo vertex = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                forkVertex.uri()
        ).vertices().values().iterator().next();
        assertTrue(
                vertex.getIdentifications().containsKey(
                        vertexOfAnotherUser.uri()
                )
        );
    }

    @Test
    public void identifications_are_included() {
        vertexOfAnotherUser.makePublic();
        IdentifierPojo identifier = vertexOfAnotherUser.addMeta(
                modelTestScenarios.human()
        ).values().iterator().next();
        Vertex forkVertex = forker.fork(
                anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                        0,
                        vertexOfAnotherUser.uri()
                )
        ).values().iterator().next();
        VertexInSubGraphPojo vertex = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                forkVertex.uri()
        ).vertices().values().iterator().next();
        assertTrue(
                vertex.getIdentifications().containsKey(
                        identifier.getExternalResourceUri()
                )
        );
    }

    @Test
    public void vertices_are_not_forked_more_than_once() {
        vertexA.label("abracadaba");
        vertexA.makePublic();
        vertexB.label("barbe");
        vertexB.makePublic();
        vertexC.label("carcason");
        vertexC.makePublic();
        anotherUserForker.fork(
                userGraph.graphWithDepthAndCenterBubbleUri(
                        1,
                        vertexB.uri()
                )
        );
        assertThat(
                getOnlyVerticesInSearchResults(
                        graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                                "barbe",
                                anotherUser
                        )
                ).size(),
                is(1)
        );

        assertThat(
                getOnlyVerticesInSearchResults(
                        graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                                "abracadaba",
                                anotherUser
                        )
                ).size(),
                is(1)
        );

        assertThat(
                getOnlyVerticesInSearchResults(
                        graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                                "carcason",
                                anotherUser
                        )
                ).size(),
                is(1)
        );
    }

    @Test
    public void sub_graph_with_depth_includes_all_elements() {
        makeAnotherUserHave3LinearPublicVertices();
        Integer numberOfVerticesBefore = numberOfVertices();
        Integer numberOfEdgesBefore = numberOfEdges();
        forker.fork(
                anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                        2,
                        vertexOfAnotherUser.uri()
                )
        );
        assertThat(
                numberOfVertices(),
                is(numberOfVerticesBefore + 3)
        );
        assertThat(
                numberOfEdges(),
                is(numberOfEdgesBefore + 2)
        );
    }

    @Test
    public void forked_vertices_have_the_right_number_of_connected_edges() {
        makeAnotherUserHave3LinearPublicVertices();
        Vertex vertex = forker.fork(
                anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                        2,
                        vertexOfAnotherUser.uri()
                )
        ).get(vertexOfAnotherUser.uri());
        VertexOperator forkedVertexOfAnotherUser = userGraph.vertexWithUri(
                vertex.uri()
        );
        assertThat(
                forkedVertexOfAnotherUser.getNumberOfConnectedEdges(),
                is(1)
        );
    }

    private void makeAnotherUserHave3LinearPublicVertices() {
        vertexOfAnotherUser.makePublic();
        VertexOperator another2 = vertexFactory.withUri(
                vertexOfAnotherUser.addVertexAndRelation().destinationVertex().uri()
        );
        another2.label("another2");
        another2.makePublic();
        VertexOperator another3 = vertexFactory.withUri(
                another2.addVertexAndRelation().destinationVertex().uri()
        );
        another3.label("another3");
        another3.makePublic();
    }

    private List<GraphElementSearchResult> getOnlyVerticesInSearchResults(List<GraphElementSearchResult> searchResults) {
        return searchResults.stream().filter(
                p -> p.getType() == GraphElementType.vertex
        ).collect(Collectors.toList());
    }
}
