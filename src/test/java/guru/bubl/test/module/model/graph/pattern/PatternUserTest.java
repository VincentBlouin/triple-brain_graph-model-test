package guru.bubl.test.module.model.graph.pattern;

import guru.bubl.module.model.graph.graph_element.GraphElement;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class PatternUserTest extends ModelTestResources {

    @Test
    public void can_clone() {
        Integer numberOfEdgesAndVertices = numberOfEdgesAndVertices();
        SubGraph subGraph = userGraph.aroundForkUriWithDepthInShareLevels(
                vertexB.uri(),
                10,
                ShareLevel.allShareLevelsInt
        );
        vertexB.label("maple syrup");
        vertexB.makePattern();
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm("maple syrup").searchOnlyForOwnVerticesForAutoCompletionByLabel(user);
        assertThat(
                results.size(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        results = graphSearchFactory.usingSearchTerm("maple syrup").searchOnlyForOwnVerticesForAutoCompletionByLabel(user);
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
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm("maple syrup").searchForAllOwnResources(user);
        assertThat(
                results.size(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexB.uri()
        ).use();
        vertexB.label("original B");
        GraphElement clonedB = graphSearchFactory.usingSearchTerm("maple syrup").searchForAllOwnResources(user).iterator().next().getGraphElement();
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
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm("maple syrup").searchOnlyForOwnVerticesForAutoCompletionByLabel(anotherUser);
        URI clonedUri = results.iterator().next().getGraphElement().uri();
        SubGraphPojo subGraph = anotherUserGraph.aroundForkUriWithDepthInShareLevels(
                clonedUri,
                1,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexBCloned = subGraph.vertexWithIdentifier(clonedUri);
        assertThat(
                vertexBCloned.getOwnerUsername(),
                is(anotherUser.username())
        );
        Relation relationCloned = subGraph.edges().values().iterator().next();
        assertThat(
                relationCloned.getOwnerUsername(),
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
        SubGraphPojo subGraph = anotherUserGraph.aroundForkUriWithDepthInShareLevels(
                cloneUri,
                1,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexInSubGraph = getVertexWithLabel(subGraph, "vertex B");
        assertThat(
                vertexInSubGraph.getNbNeighbors().getTotal(),
                is(2)
        );
        subGraph = anotherUserGraph.aroundForkUriWithDepthInShareLevels(
                vertexInSubGraph.uri(),
                1,
                ShareLevel.allShareLevelsInt
        );

        Vertex vertexC = getVertexWithLabel(subGraph, "cayman island");
        assertThat(
                vertexC.label(),
                is("cayman island")
        );
    }

    @Test
    public void integrates_tags_into_user_own_tags() {
        vertexB.makePattern();
        TagPojo tag = new TagPojo(
                URI.create("/some-external-uri")
        );
        vertexA.addTag(tag);
        TagPojo integratedTag = vertexOfAnotherUser.addTag(
                tag
        ).values().iterator().next();
        assertThat(
                integratedTag.getNbNeighbors().getTotal(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        assertThat(
                tagFactory.withUri(integratedTag.uri()).getNbNeighbors().getTotal(),
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
        GraphElement clonedVertexB = graphSearchFactory.usingSearchTerm("maple syrup").searchForAllOwnResources(user).get(0).getGraphElement();
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
        GraphElement clonedVertexB = graphSearchFactory.usingSearchTerm("apricot tree").searchForAllOwnResources(user).get(0).getGraphElement();
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
        vertexB.addTag(modelTestScenarios.computerScientistType());
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        patternUserFactory.forUserAndPatternUri(
                user,
                vertexOfAnotherUser.uri()
        ).use();
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
    }

    @Test
    public void does_not_clone_beyond_tags() {
        vertexC.label("carrot");
        vertexA.addTag(modelTestScenarios.computerScientistType());
        vertexB.addTag(modelTestScenarios.computerScientistType());
        vertexC.addTag(modelTestScenarios.computerScientistType());
        vertexB.makePattern();
        vertexB.getEdgeToDestinationVertex(vertexC).remove();
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm("carrot").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }

    @Test
    public void increments_nb_pattern_usage() {
        vertexB.makePattern();
        patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        assertThat(
                vertexB.getNbPatternUsage(),
                is(1)
        );
    }

    @Test
    public void are_not_considered_under_pattern() {
        vertexB.makePattern();
        URI centerUri = patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        SubGraph subGraph = anotherUserGraph.aroundForkUriWithDepthInShareLevels(
                centerUri,
                1,
                ShareLevel.allShareLevelsInt
        );
        subGraph.vertices().remove(centerUri);
        VertexOperator vertexUnder = vertexFactory.withUri(
                subGraph.vertices().values().iterator().next().uri()
        );
        assertFalse(
                vertexUnder.isUnderPattern()
        );
        vertexUnder.label("apricot tree");
        List<GraphElementSearchResult> results = graphSearchFactory.usingSearchTerm(
                "apricot tree"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                results.size(),
                is(1)
        );
        assertThat(
                results.iterator().next().getGraphElement().label(),
                is("apricot tree")
        );
    }

    @Test
    public void root_vertex_is_tagged_to_the_pattern() {
        vertexB.comment("vertex b comment");
        vertexB.makePattern();
        URI centerUri = patternUserFactory.forUserAndPatternUri(
                anotherUser,
                vertexB.uri()
        ).use();
        SubGraph subGraph = userGraph.aroundForkUriInShareLevels(
                centerUri,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexBCopy = subGraph.vertexWithIdentifier(centerUri);
        TagPojo patternTag = vertexBCopy.getTags().values().iterator().next();
        assertThat(
                patternTag.getExternalResourceUri(),
                is(vertexB.uri())
        );
        assertThat(
                patternTag.label(),
                is("vertex B")
        );
        assertThat(
                patternTag.comment(),
                is("vertex b comment")
        );
    }
}
