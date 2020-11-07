package guru.bubl.test.module.model.graph.tree_copier;

import guru.bubl.module.model.Image;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.Tree;
import guru.bubl.module.model.graph.subgraph.SubGraph;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.tree_copier.TreeCopier;
import guru.bubl.module.model.graph.tree_copier.TreeCopierFactory;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.bubl.module.model.test.scenarios.TestScenarios.tagFromFriendlyResource;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TreeCopierTest extends ModelTestResources {
    @Inject
    TreeCopierFactory treeCopierFactory;

    @Test
    public void can_copy() {
        makeAllPublic();
        vertexB.label("balboa");
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "balboa"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        treeCopier.copyTreeOfUser(copiedTree, user);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(11)
        );
        searchResults = graphSearchFactory.usingSearchTerm("balboa").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(1)
        );
    }

    @Test
    public void returns_cloned_root_uri() {
        makeAllPublic();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        URI newCenterUri = treeCopier.copyTreeOfUser(copiedTree, user);
        assertNotEquals(
                newCenterUri,
                vertexA.uri()
        );
        SubGraph subGraph = userGraphFactory.withUser(anotherUser).aroundForkUriInShareLevels(
                newCenterUri,
                ShareLevel.allShareLevelsInt
        );
        Vertex vertexACopy = subGraph.vertexWithIdentifier(newCenterUri);
        assertThat(
                vertexACopy.getOwnerUsername(),
                is("colette_armande")
        );
        assertThat(
                vertexACopy.label(),
                is("vertex A")
        );
    }


    @Test
    public void copies_tags() {
        makeAllPublic();
        TagPojo tag = new TagPojo(
                URI.create("/some-external-uri")
        );
        tag.setLabel("some label");
        tag.setComment("some description");
        tag.setImages(Stream.of(
                Image.withUrlForSmallAndUriForBigger(
                        "/small-uri",
                        URI.create("/big-uri")
                )
        ).collect(Collectors.toCollection(HashSet::new)));
        vertexE.addTag(tag, ShareLevel.PRIVATE);
        URI copyUri = treeCopierFactory.forCopier(
                anotherUser
        ).copyTreeOfUser(Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexE.uri(),
                tagFromFriendlyResource(vertexE)
        ), user);
        SubGraph subGraph = userGraphFactory.withUser(anotherUser).aroundForkUriInShareLevels(
                copyUri,
                ShareLevel.allShareLevelsInt
        );
        Tag copiedTag = subGraph.vertexWithIdentifier(copyUri).getTags().get(URI.create("/some-external-uri"));
        assertNotEquals(
                tag.uri(),
                copiedTag.uri()
        );
        assertThat(
                copiedTag.getExternalResourceUri(),
                is(URI.create("/some-external-uri"))
        );
        assertThat(
                copiedTag.label(),
                is("some label")
        );
        assertThat(
                copiedTag.comment(),
                is("some description")
        );
        Image tagImage = copiedTag.images().iterator().next();
        assertThat(
                tagImage.urlForSmall(),
                is("/small-uri")
        );
    }

    @Test
    public void can_copy_multiple_tags() {
        makeAllPublic();
        TagPojo tag = new TagPojo(
                URI.create("/some-external-uri")
        );
        tag.setLabel("some label");
        tag.setComment("some description");
        tag.setImages(Stream.of(
                Image.withUrlForSmallAndUriForBigger(
                        "/small-uri",
                        URI.create("/big-uri")
                )
        ).collect(Collectors.toCollection(HashSet::new)));
        TagPojo tag2 = new TagPojo(
                URI.create("/some-external-uri-2")
        );
        tag2.setLabel("some label 2");
        tag2.setComment("some description 2");
        tag2.setImages(Stream.of(
                Image.withUrlForSmallAndUriForBigger(
                        "/small-uri-2",
                        URI.create("/big-uri-2")
                )
        ).collect(Collectors.toCollection(HashSet::new)));
        vertexE.addTag(tag, ShareLevel.PRIVATE);
        vertexE.addTag(tag2, ShareLevel.PRIVATE);
        URI copyUri = treeCopierFactory.forCopier(
                anotherUser
        ).copyTreeOfUser(Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexE.uri(),
                tagFromFriendlyResource(vertexE)
        ), user);
        SubGraph subGraph = userGraphFactory.withUser(anotherUser).aroundForkUriInShareLevels(
                copyUri,
                ShareLevel.allShareLevelsInt
        );
        Collection<TagPojo> tags = subGraph.vertexWithIdentifier(copyUri).getTags().values();
        assertThat(tags.size(), is(3));
    }

    @Test
    public void integrates_tags_into_user_own_tags() {
        makeAllPublic();
        TagPojo tag = new TagPojo(
                URI.create("/some-external-uri")
        );
        vertexE.addTag(tag, ShareLevel.PRIVATE);
        TagPojo integratedTag = vertexOfAnotherUser.addTag(
                tag,
                ShareLevel.PRIVATE
        ).values().iterator().next();
        assertThat(
                integratedTag.getNbNeighbors().getTotal(),
                Matchers.is(1)
        );
        Set<URI> urisToCopy = graphElementsOfTestScenario.allGraphElementsToUris();
        urisToCopy.remove(vertexA.uri());
        urisToCopy.remove(graphElementsOfTestScenario.getEdgeAB().uri());
        treeCopierFactory.forCopier(
                anotherUser
        ).copyTreeOfUser(Tree.withUrisOfGraphElementsAndRootUriAndTag(
                urisToCopy,
                vertexB.uri(),
                tagFromFriendlyResource(vertexB)
        ), user);
        assertThat(
                tagFactory.withUri(integratedTag.uri()).getNbNeighbors().getTotal(),
                Matchers.is(2)
        );
        assertThat(
                tagFactory.withUri(integratedTag.uri()).getNbNeighbors().getTotal(),
                Matchers.is(2)
        );
    }

    @Test
    public void adds_root_as_tag_to_copied_root() {
        makeAllPublic();
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        URI copiedCenterUri = treeCopier.copyTreeOfUser(copiedTree, user);
        System.out.println(graphElementOperatorFactory.withUri(copiedCenterUri).getTags().keySet());
        assertTrue(
                graphElementOperatorFactory.withUri(copiedCenterUri).getTags().containsKey(
                        vertexA.uri()
                )
        );
    }

    @Test
    public void cannot_copy_private_vertices() {
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "balboa"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        treeCopier.copyTreeOfUser(copiedTree, user);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        searchResults = graphSearchFactory.usingSearchTerm("balboa").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }

    @Test
    public void copies_nothing_if_some_graph_elements_are_not_allowed_to_be_copied() {
        vertexA.makePublic();
        vertexB.makePublic();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "balboa"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        treeCopier.copyTreeOfUser(copiedTree, user);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        searchResults = graphSearchFactory.usingSearchTerm("balboa").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }

    @Test
    public void cannot_copy_friend_bubbles_if_not_friend() {
        makeAllPublic();
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "balboa"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        treeCopier.copyTreeOfUser(copiedTree, user);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        searchResults = graphSearchFactory.usingSearchTerm("balboa").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }

    @Test
    public void can_copy_friend_bubbles_with_friend() {
        makeAllPublic();
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        vertexB.setShareLevel(ShareLevel.FRIENDS);
        vertexB.label("balboa");
        friendManagerFactory.forUser(user).add(anotherUser);
        friendManagerFactory.forUser(anotherUser).confirm(user);
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "balboa"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        URI copiedUri = treeCopier.copyTreeOfUser(copiedTree, user);
        assertNotNull(copiedUri);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(11)
        );
        searchResults = graphSearchFactory.usingSearchTerm("balboa").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(1)
        );
    }

    @Test
    public void tests_ownership_of_copied_graph_elements() {
        makeAllPublic();
        User user3 = User.withEmailAndUsername("gigi@popo.com", "gigi");
        userRepository.createUser(user3);
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        List<GraphElementSearchResult> searchResults = graphSearchFactory.usingSearchTerm(
                "balboa"
        ).searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        treeCopier.copyTreeOfUser(copiedTree, user3);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        searchResults = graphSearchFactory.usingSearchTerm("balboa").searchForAllOwnResources(anotherUser);
        assertThat(
                searchResults.size(),
                is(0)
        );
    }

    @Test
    public void user_can_copy_his_own_private_graph_elements() {
        TreeCopier treeCopier = treeCopierFactory.forCopier(user);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(6)
        );
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexE.uri(),
                        tagFromFriendlyResource(vertexE)
                ),
                user
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(11)
        );
    }

    private void makeAllPublic() {
        vertexA.makePublic();
        vertexB.makePublic();
        vertexC.makePublic();
        vertexD.makePublic();
        vertexE.makePublic();
        groupRelation.makePublic();
    }

}
