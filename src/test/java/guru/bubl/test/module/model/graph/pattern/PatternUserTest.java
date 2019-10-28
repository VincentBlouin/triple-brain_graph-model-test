package guru.bubl.test.module.model.graph.pattern;

import guru.bubl.module.model.graph.GraphElement;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.pattern.PatternUserFactory;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class PatternUserTest extends ModelTestResources {

    @Test
    public void can_clone() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        SubGraph subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                vertexB.uri()
        );
        vertexB.label("maple syrup");
        vertexB.makePattern();
        List<GraphElementSearchResult> results = graphSearch.searchForAllOwnResources("maple syrup", user);
        assertThat(
                results.size(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        results = graphSearch.searchForAllOwnResources("maple syrup", user);
        assertThat(
                results.size(),
                is(2)
        );
        Integer newNumberOfEdgesAndVertices = numberOfEdgesAndVertices();
        assertThat(
                newNumberOfEdgesAndVertices,
                is(numberOfEdgesAndVertices + subGraph.numberOfVertices() + subGraph.numberOfEdges())
        );
    }

    @Test
    public void clones_have_their_own_uri() {
        vertexB.label("maple syrup");
        vertexB.makePattern();
        List<GraphElementSearchResult> results = graphSearch.searchForAllOwnResources("maple syrup", user);
        assertThat(
                results.size(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        vertexB.label("original B");
        GraphElement clonedB = graphSearch.searchForAllOwnResources("maple syrup", user).iterator().next().getGraphElement();
        assertNotEquals(
                clonedB.uri(),
                "null"
        );
    }

    @Test
    public void clones_owner_changes() {
        vertexB.label("maple syrup");
        vertexB.makePattern();
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        List<GraphElementSearchResult> results = graphSearch.searchForAllOwnResources("maple syrup", anotherUser);
        URI clonedUri = results.iterator().next().getGraphElement().uri();
        SubGraphPojo subGraph = anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                1,
                clonedUri
        );
        Vertex vertexBCloned = subGraph.vertexWithIdentifier(clonedUri);
        assertThat(
                vertexBCloned.getOwnerUsername(),
                is(anotherUser.username())
        );
        Edge edgeCloned = subGraph.edges().values().iterator().next();
        assertThat(
                edgeCloned.getOwnerUsername(),
                is(anotherUser.username())
        );
    }

    @Test
    public void clones_deep() {
        vertexA.label("apricot beer");
        vertexC.label("cayman island");
        vertexA.makePattern();
        URI cloneUri = patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexA.uri()
        ).use();
        SubGraphPojo subGraph = anotherUserGraph.graphWithDepthAndCenterBubbleUri(
                1,
                cloneUri
        );
        VertexInSubGraph vertexInSubGraph = getVertexWithLabel(subGraph, "vertex B");
        assertThat(
                vertexInSubGraph.getNumberOfConnectedEdges(),
                is(2)
        );
        List<GraphElementSearchResult> results = graphSearch.searchForAllOwnResources("caymand island", anotherUser);
        assertThat(
                results.size(),
                is(1)
        );
    }

    @Test
    public void integrates_tags_into_user_own_tags() {
        vertexB.makePattern();
        IdentifierPojo tag = new IdentifierPojo(
                URI.create("/some-external-uri")
        );
        vertexA.addMeta(tag);
        IdentifierPojo integratedTag = vertexOfAnotherUser.addMeta(
                tag
        ).values().iterator().next();
        assertThat(
                integratedTag.getNbReferences(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        assertThat(
                identificationFactory.withUri(integratedTag.uri()).getNbReferences(),
                is(2)
        );
    }

    @Test
    public void not_a_pattern_after_its_cloned() {
        vertexB.label("maple syrup");
        vertexB.makePattern();
        assertTrue(
                vertexB.isPattern()
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        vertexB.label("original B");
        GraphElement clonedVertexB = graphSearch.searchForAllOwnResources("maple syrup", user).get(0).getGraphElement();
        assertTrue(vertexB.isPattern());
        assertFalse(
                vertexFactory.withUri(clonedVertexB.uri()).isPattern()
        );
    }


    @Test
    public void share_level_is_private_after_clone() {
        vertexA.label("apricot tree");
        vertexA.makePublic();
        vertexB.makePattern();
        assertTrue(
                vertexB.isPattern()
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        vertexA.label("original A");
        GraphElement clonedVertexB = graphSearch.searchForAllOwnResources("apricot tree", user).get(0).getGraphElement();
        assertTrue(vertexA.isPublic());
        assertFalse(
                vertexFactory.withUri(clonedVertexB.uri()).isPublic()
        );
        assertThat(
                vertexFactory.withUri(clonedVertexB.uri()).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
    }

    @Test
    public void returns_the_new_center_uri() {
        vertexB.label("maple syrup");
        vertexB.makePattern();
        URI newUri = patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        assertNotEquals(
                vertexB.uri().toString(),
                newUri.toString()
        );
        VertexOperator newCenter = vertexFactory.withUri(newUri);
        assertEquals(
                newCenter.label(),
                "maple syrup"

        );
    }

    @Test
    public void does_not_remove_user_other_patterns() {
        vertexB.makePattern();
        vertexOfAnotherUser.makePattern();
        assertTrue(
                vertexB.isPattern()
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexOfAnotherUser.uri()
        ).use();
        assertTrue(
                vertexB.isPattern()
        );
    }

    @Test
    public void does_not_remove_user_tags() {
        vertexOfAnotherUser.makePattern();
        vertexB.addMeta(modelTestScenarios.computerScientistType());
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexOfAnotherUser.uri()
        ).use();
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
    }

    @Test
    public void does_not_clone_beyond_tags() {
        vertexC.label("carrot");
        vertexA.addMeta(modelTestScenarios.computerScientistType());
        vertexB.addMeta(modelTestScenarios.computerScientistType());
        vertexC.addMeta(modelTestScenarios.computerScientistType());
        vertexB.makePattern();
        vertexB.getEdgeThatLinksToDestinationVertex(vertexC).remove();
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        List<GraphElementSearchResult> searchResults = graphSearch.searchForAllOwnResources("carrot", anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }
}
