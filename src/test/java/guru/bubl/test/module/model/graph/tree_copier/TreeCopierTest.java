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
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.*;
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


    public void copied_graph_elements_have_relationships() {
        makeAllPublic();
        assertTrue(
                vertexA.getEdgeToDestinationVertex(vertexB).isPublic()
        );
        vertexA.addTag(modelTestScenarios.computerScientistType());
        SubGraph subGraph = userGraphFactory.withUser(user).aroundForkUriInShareLevels(
                vertexA.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.vertices().size(), is(2)
        );
        assertThat(
                subGraph.edges().size(), is(1)
        );
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUriAndTag(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri(),
                tagFromFriendlyResource(vertexA)
        );
        URI newCenterUri = treeCopier.copyTreeOfUser(copiedTree, user).get(vertexA.uri());
        assertNotEquals(
                newCenterUri,
                vertexA.uri()
        );
        subGraph = userGraphFactory.withUser(anotherUser).aroundForkUriInShareLevels(
                newCenterUri,
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.vertices().size(), is(2)
        );
        assertThat(
                subGraph.edges().size(), is(1)
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
        ), user).get(vertexE.uri());
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
        ), user).get(vertexE.uri());
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
        URI copiedCenterUri = treeCopier.copyTreeOfUser(copiedTree, user).get(vertexA.uri());
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
        URI copiedUri = treeCopier.copyTreeOfUser(copiedTree, user).get(vertexA.uri());
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

    @Test

    public void root_can_be_a_group_relation() {
        Vertex newVertex = userGraphFactory.withUser(user).createVertex();
        TreeCopier treeCopier = treeCopierFactory.forCopier(user);
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(7)
        );
        treeCopier.copyTreeOfUserWithNewParentUri(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        groupRelation.uri(),
                        tagFromFriendlyResource(groupRelation)
                ),
                user,
                newVertex.uri()
        );
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(12)
        );
    }

    @Test

    public void when_root_is_a_group_relation_its_linked_to_new_parent_uri() {
        Vertex newVertex = userGraphFactory.withUser(user).createVertex();
        TreeCopier treeCopier = treeCopierFactory.forCopier(user);
        assertThat(
                userGraph.aroundForkUriInShareLevels(
                        newVertex.uri(),
                        ShareLevel.allShareLevelsInt
                ).getGroupRelations().size(),
                is(0)
        );
        assertThat(
                userGraph.aroundForkUriInShareLevels(
                        groupRelation.uri(),
                        ShareLevel.allShareLevelsInt
                ).vertices().size(),
                is(3)
        );
        URI newGroupRelationUri = treeCopier.copyTreeOfUserWithNewParentUri(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        groupRelation.uri(),
                        tagFromFriendlyResource(groupRelation)
                ),
                user,
                newVertex.uri()
        ).get(groupRelation.uri());
        assertThat(
                userGraph.aroundForkUriInShareLevels(
                        newVertex.uri(),
                        ShareLevel.allShareLevelsInt
                ).getGroupRelations().size(),
                is(1)
        );
        assertThat(
                userGraph.aroundForkUriInShareLevels(
                        newGroupRelationUri,
                        ShareLevel.allShareLevelsInt
                ).vertices().size(),
                is(3)
        );
    }

    @Test

    public void has_to_be_owner_of_new_parent() {
        Vertex newVertex = userGraphFactory.withUser(anotherUser).createVertex();
        TreeCopier treeCopier = treeCopierFactory.forCopier(user);
        treeCopier.copyTreeOfUserWithNewParentUri(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        groupRelation.uri(),
                        tagFromFriendlyResource(groupRelation)
                ),
                user,
                newVertex.uri()
        );
        assertThat(
                userGraph.aroundForkUriInShareLevels(
                        newVertex.uri(),
                        ShareLevel.allShareLevelsInt
                ).getGroupRelations().size(),
                is(0)
        );
    }

    @Test
    public void parent_nb_neighbors_increments() {
        VertexOperator newVertex = vertexFactory.withUri(
                userGraphFactory.withUser(user).createVertex().uri()
        );
        TreeCopier treeCopier = treeCopierFactory.forCopier(user);
        assertThat(
                newVertex.getNbNeighbors().getTotal(),
                is(0)
        );
        treeCopier.copyTreeOfUserWithNewParentUri(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        groupRelation.uri(),
                        tagFromFriendlyResource(groupRelation)
                ),
                user,
                newVertex.uri()
        );
        assertThat(
                newVertex.getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test
    public void share_level_of_copy_doesnt_change_when_owner() {
        vertexB.setShareLevel(ShareLevel.PUBLIC);
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        vertexC.setShareLevel(ShareLevel.PRIVATE);
        Map<URI, URI> newUris = treeCopierFactory.forCopier(user).copyTreeOfUserWithNewParentUri(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        groupRelation.uri(),
                        tagFromFriendlyResource(groupRelation)
                ),
                user,
                vertexB.uri()
        );
        assertThat(
                vertexFactory.withUri(
                        newUris.get(vertexB.uri())
                ).getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
        assertThat(
                vertexFactory.withUri(
                        newUris.get(vertexA.uri())
                ).getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
        assertThat(
                vertexFactory.withUri(
                        newUris.get(vertexC.uri())
                ).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
    }

    @Test
    public void can_copy_in_share_level() {
        makeAllPublic();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        URI newVertexBUri = treeCopier.copyTreeOfUserInShareLevel(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexB.uri(),
                        tagFromFriendlyResource(groupRelation)
                ),
                user,
                ShareLevel.FRIENDS
        ).get(vertexB.uri());
        assertThat(
                vertexFactory.withUri(newVertexBUri).getShareLevel(),
                is(ShareLevel.FRIENDS)
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
