/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.subgraph;

import com.google.inject.Inject;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.identification.Identification;
import guru.bubl.module.model.graph.identification.IdentificationPojo;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphForker;
import guru.bubl.module.model.graph.subgraph.SubGraphForkerFactory;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.VertexSearchResult;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class SubGraphForkerTest extends ModelTestResources {

    @Test
    public void can_integrate_subgraph() {
        vertexA.label("ananas");
        vertexA.makePublic();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                vertexB.uri()
        );
        List<VertexSearchResult> results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
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
                is(1)
        );
    }

    @Test
    public void forking_subgraph_with_single_vertex_works() {
        vertexOfAnotherUser.makePublic();
        Integer numberOfVertices = numberOfVertices();
        forker.fork(
                anotherUserGraph.graphWithDepthAndCenterVertexId(
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
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                vertexB.uri()
        );
        anotherUserForker.fork(
                subGraph
        );
        List<VertexSearchResult> results = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
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
                is(1)
        );
        vertexB.makePublic();
        anotherUserForker.fork(
                userGraph.graphWithDepthAndCenterVertexId(
                        0,
                        vertexB.uri()
                )
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
    public void edges_are_included() {
        vertexOfAnotherUser.makePublic();
        vertexOfAnotherUser.addVertexAndRelation().destinationVertex().makePublic();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
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
        VertexOperator destinationVertex = vertexOfAnotherUser.addVertexAndRelation().destinationVertex();
        vertexOfAnotherUser.makePrivate();
        destinationVertex.makePublic();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterVertexId(
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
        IdentificationPojo originalVertexAsIdentifier = TestScenarios.identificationFromFriendlyResource(
                vertexOfAnotherUser
        );
        Set<GraphElementSearchResult> identifiedToOriginal = identifiedTo.getForIdentificationAndUser(
                originalVertexAsIdentifier,
                user
        );
        assertTrue(
                identifiedToOriginal.isEmpty()
        );
        forker.fork(
                anotherUserGraph.graphWithDepthAndCenterVertexId(
                        0,
                        vertexOfAnotherUser.uri()
                )
        );
        identifiedToOriginal = identifiedTo.getForIdentificationAndUser(
                originalVertexAsIdentifier,
                user
        );
        assertFalse(
                identifiedToOriginal.isEmpty()
        );
    }

    @Test
    public void identifications_are_included() {
        vertexOfAnotherUser.makePublic();
        IdentificationPojo identifier = vertexOfAnotherUser.addGenericIdentification(
                modelTestScenarios.human()
        ).values().iterator().next();
        Set<GraphElementSearchResult> identifiedToHuman = identifiedTo.getForIdentificationAndUser(
                identifier,
                user
        );
        assertTrue(
                identifiedToHuman.isEmpty()
        );
        forker.fork(
                anotherUserGraph.graphWithDepthAndCenterVertexId(
                        0,
                        vertexOfAnotherUser.uri()
                )
        );
        identifiedToHuman = identifiedTo.getForIdentificationAndUser(
                identifier,
                user
        );
        assertFalse(
                identifiedToHuman.isEmpty()
        );
    }

    @Test
    public void vertices_are_not_forked_more_than_once() {
        vertexA.makePublic();
        vertexB.makePublic();
        vertexC.makePublic();
        anotherUserForker.fork(
                userGraph.graphWithDepthAndCenterVertexId(
                        1,
                        vertexB.uri()
                )
        );
        IdentificationPojo vertexAIdentifier = TestScenarios.identificationFromFriendlyResource(
                vertexA
        );
        assertThat(
                identifiedTo.getForIdentificationAndUser(
                        vertexAIdentifier,
                        anotherUser
                ).size(),
                is(1)
        );
        IdentificationPojo vertexBIdentifier = TestScenarios.identificationFromFriendlyResource(
                vertexB
        );
        assertThat(
                identifiedTo.getForIdentificationAndUser(
                        vertexBIdentifier,
                        anotherUser
                ).size(),
                is(1)
        );
        IdentificationPojo vertexCIdentifier = TestScenarios.identificationFromFriendlyResource(
                vertexC
        );
        assertThat(
                identifiedTo.getForIdentificationAndUser(
                        vertexCIdentifier,
                        anotherUser
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
                anotherUserGraph.graphWithDepthAndCenterVertexId(
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
        IdentificationPojo vertexOfAnotherUserAsIdentifier = TestScenarios.identificationFromFriendlyResource(
                vertexOfAnotherUser
        );
        forker.fork(
                anotherUserGraph.graphWithDepthAndCenterVertexId(
                        2,
                        vertexOfAnotherUser.uri()
                )
        );
        VertexOperator forkedVertexOfAnotherUser = userGraph.vertexWithUri(
                identifiedTo.getForIdentificationAndUser(
                        vertexOfAnotherUserAsIdentifier,
                        user
                ).iterator().next().getGraphElement().uri()
        );
        assertThat(
                forkedVertexOfAnotherUser.getNumberOfConnectedEdges(),
                is(1)
        );
    }

    private void makeAnotherUserHave3LinearPublicVertices() {
        vertexOfAnotherUser.makePublic();
        VertexOperator another2 = vertexOfAnotherUser.addVertexAndRelation().destinationVertex();
        another2.label("another2");
        another2.makePublic();
        VertexOperator another3 = another2.addVertexAndRelation().destinationVertex();
        another3.label("another3");
        another3.makePublic();
    }
}
