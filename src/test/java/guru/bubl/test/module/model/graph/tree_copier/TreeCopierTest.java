package guru.bubl.test.module.model.graph.tree_copier;

import guru.bubl.module.model.Image;
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
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TreeCopierTest extends ModelTestResources {
    @Inject
    TreeCopierFactory treeCopierFactory;

    @Test
    public void can_copy() {
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
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUri(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri()
        );
        treeCopier.ofAnotherUser(copiedTree, user);
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
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        Tree copiedTree = Tree.withUrisOfGraphElementsAndRootUri(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexA.uri()
        );
        URI newCenterUri = treeCopier.ofAnotherUser(copiedTree, user);
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
    @Ignore
    public void copies_tags() {
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
        ).ofAnotherUser(Tree.withUrisOfGraphElementsAndRootUri(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexE.uri()
        ), user);
        SubGraph subGraph = userGraphFactory.withUser(anotherUser).aroundForkUriInShareLevels(
                copyUri,
                ShareLevel.allShareLevelsInt
        );
        Tag copiedTag = subGraph.vertexWithIdentifier(copyUri).getTags().values().iterator().next();
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
        ).ofAnotherUser(Tree.withUrisOfGraphElementsAndRootUri(
                graphElementsOfTestScenario.allGraphElementsToUris(),
                vertexE.uri()
        ), user);
        SubGraph subGraph = userGraphFactory.withUser(anotherUser).aroundForkUriInShareLevels(
                copyUri,
                ShareLevel.allShareLevelsInt
        );
        Collection<TagPojo> tags = subGraph.vertexWithIdentifier(copyUri).getTags().values();
        assertThat(tags.size(), is(2));
    }

    @Test
    public void integrates_tags_into_user_own_tags() {
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
        ).ofAnotherUser(Tree.withUrisOfGraphElementsAndRootUri(
                urisToCopy,
                vertexB.uri()
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

}
