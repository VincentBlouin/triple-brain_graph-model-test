/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.Image;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.tag.Tag;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.fork.NbNeighbors;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;
import org.parboiled.common.StringUtils;

import java.net.URI;
import java.util.*;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class GraphElementOperatorTest extends ModelTestResources {

    @Test
    public void can_remove_self_identifier() {
        TagPojo vertexBAsIdentifier = TestScenarios.tagFromFriendlyResource(
                vertexB
        );
        TagPojo createdVertexBAsIdentifier = vertexA.addTag(
                vertexBAsIdentifier
        ).values().iterator().next();
        SubGraphPojo subGraph = neo4jSubGraphExtractorFactory.withCenterVertexInShareLevels(
                createdVertexBAsIdentifier.uri(),
                ShareLevel.allShareLevelsInt
        ).load();
        assertTrue(
                subGraph.vertices().containsKey(
                        vertexB.uri()
                )
        );
        vertexB.removeTag(createdVertexBAsIdentifier);
        subGraph = neo4jSubGraphExtractorFactory.withCenterVertexInShareLevels(
                createdVertexBAsIdentifier.uri(),
                ShareLevel.allShareLevelsInt
        ).load();
        assertFalse(
                subGraph.vertices().containsKey(
                        vertexB.uri()
                )
        );
    }

    @Test
    public void graph_element_becomes_identified_to_itself_if_used_as_identifier() {
        TagPojo identification = vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        assertTrue(
                vertexA.getTags().containsKey(
                        identification.getExternalResourceUri()
                )
        );
        assertTrue(
                vertexB.getTags().containsKey(
                        identification.getExternalResourceUri()
                )
        );
    }

    @Test
    public void when_identified_to_a_graph_element_the_number_of_references_to_the_new_identifier_is_2() {
        TagPojo tag = vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        assertThat(
                tag.getNbNeighbors().getTotal(),
                is(2)
        );
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getTotal(),
                is(2)
        );
    }

    @Test

    public void an_identification_does_identify_to_itself() {
        TagPojo vertexBAsIdentification = vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        URI vertexBAsAnIdentificationUri = vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        Tag createdIdentification = vertexC.addTag(
                vertexBAsIdentification
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.aroundForkUriInShareLevels(
                createdIdentification.uri(),
                ShareLevel.allShareLevelsInt
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    @Test
    public void can_keep_tag_removed_from_the_reference_after_tagging_to_it_again() {
        TagPojo vertexATag = vertexB.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexA
                )
        ).values().iterator().next();
        assertTrue(
                vertexA.getTags().containsKey(
                        vertexATag.getExternalResourceUri()
                )
        );
        vertexA.removeTag(vertexATag);
        assertFalse(
                vertexA.getTags().containsKey(
                        vertexATag.getExternalResourceUri()
                )
        );
        vertexC.addTag(
                vertexATag
        );
        assertFalse(
                vertexA.getTags().containsKey(
                        vertexATag.uri()
                )
        );
    }

    @Test
    public void cannot_have_same_identification_twice() {
        GraphElementOperator vertexAGraphElement = vertexA;
        Integer numberOfGenericIdentifications = vertexAGraphElement.getTags().size();
        vertexAGraphElement.addTag(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addTag(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getTags().size(),
                is(
                        numberOfGenericIdentifications + 1
                )
        );
    }

    @Test
    public void adding_identification_returns_identification_created_fields() {
        TagPojo identification = vertexA.addTag(
                modelTestScenarios.timBernersLee()
        ).values().iterator().next();
        assertNotNull(
                identification.creationDate()
        );
        assertNotNull(
                identification.lastModificationDate()
        );
    }

    @Test
    public void add_tag_returns_share_level() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.event()
        ).values().iterator().next();
        assertThat(
                tag.getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        tagFactory.withUri(
                tag.uri()
        ).setShareLevel(ShareLevel.PUBLIC);
        tag = vertexB.addTag(
                modelTestScenarios.event()
        ).values().iterator().next();
        assertThat(
                tag.getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
    }

    @Test
    public void users_identification_have_their_own_uri_for_same_identification() {
        TagPojo identificationOfAnotherUser = vertexOfAnotherUser.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        TagPojo identification = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                identificationOfAnotherUser.getExternalResourceUri().equals(
                        identification.getExternalResourceUri()
                )
        );
        assertFalse(
                identificationOfAnotherUser.uri().equals(
                        identification.uri()
                )
        );
    }

    @Test

    public void uri_of_identification_does_not_change_if_added_twice() {
        TagPojo identification = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        TagPojo identification2 = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(identification.uri().equals(identification2.uri()));
    }

    @Test

    public void adding_existing_identification_keeps_existing_images() {
        TagPojo identification = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                identification.images().isEmpty()
        );
        Set<Image> images = new HashSet<>();
        images.add(Image.withUrlForSmallAndUriForBigger(
                "dummy base 64",
                URI.create("/big_image")
        ));
        identification = modelTestScenarios.computerScientistType();
        identification.setImages(
                images
        );
        TagPojo sameIdentification = vertexA.addTag(
                identification
        ).values().iterator().next();
        assertThat(sameIdentification.images().size(), is(0));
    }

    @Test

    public void identifications_can_have_images() {
        TagPojo computerScientist = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                computerScientist.images().isEmpty()
        );
        Set<Image> images = new HashSet<>();
        images.add(Image.withUrlForSmallAndUriForBigger(
                "dummy base 64",
                URI.create("/big_image")
        ));
        TagPojo person = modelTestScenarios.person();
        person.setImages(
                images
        );
        person = vertexA.addTag(
                person
        ).values().iterator().next();
        assertFalse(
                person.images().isEmpty()
        );
    }

    @Test

    public void adding_existing_identification_returns_existing_description() {
        TagPojo identification = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                StringUtils.isEmpty(
                        identification.comment()
                )
        );
        identification = modelTestScenarios.computerScientistType();
        identification.setComment(
                "A computer scientist is ..."
        );
        TagPojo sameIdentification = vertexA.addTag(
                identification
        ).values().iterator().next();
        assertTrue(sameIdentification.comment().isEmpty());
    }

    @Test
    public void add_new_tag_share_level_is_private() {
        TagPojo tag = modelTestScenarios.computerScientistType();
        tag = vertexA.addTag(
                tag
        ).values().iterator().next();
        assertThat(
                tag.getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
    }

    @Test

    public void add_new_tag_can_set_share_level() {
        TagPojo tag = modelTestScenarios.computerScientistType();
        tag.setShareLevel(ShareLevel.PUBLIC);
        tag = vertexA.addTag(
                tag
        ).values().iterator().next();
        assertThat(
                tag.getShareLevel(),
                is(ShareLevel.PUBLIC)
        );
    }

    @Test
    public void can_remove_identification_having_no_external_uri() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                vertexA.getTags().containsValue(tag)
        );
        tag.setExternalResourceUri(null);
        vertexA.removeTag(tag);
        assertFalse(
                vertexA.getTags().containsValue(tag)
        );
    }

    @Test
    public void remove_tag_decrements_nb_neighbors() {
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        NbNeighbors nbNeighbors = tagFactory.withUri(tag.uri()).getNbNeighbors();
        assertThat(
                nbNeighbors.getFriend(),
                is(1)
        );
        assertThat(
                nbNeighbors.getTotal(),
                is(1)
        );
        vertexA.removeTag(tag);
        assertThat(
                nbNeighbors.getFriend(),
                is(0)
        );
        assertThat(
                nbNeighbors.getTotal(),
                is(0)
        );
    }

    @Test
    public void identifications_do_not_apply_for_all_elements() {
        vertexA.addTag(
                modelTestScenarios.computerScientistType()
        );
        assertFalse(
                vertexA.getTags().isEmpty()
        );
        assertTrue(
                vertexB.getTags().isEmpty()
        );
    }

    @Test

    public void on_creation_identifications_have_1_reference() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getTotal(),
                is(1)
        );
    }

    @Test

    public void adding_an_identification_increments_number_of_references() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getTotal(),
                is(1)
        );
        tag = vertexB.addTag(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(tag.uri()).getNbNeighbors().getTotal(),
                is(2)
        );
    }

    @Test
    public void can_identify_to_an_identification_where_the_external_uri_is_the_identification_uri() {
        TagPojo vertexBAsIdentification = vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();

        vertexBAsIdentification.setExternalResourceUri(vertexBAsIdentification.uri());
        vertexBAsIdentification.setUri(null);
        assertFalse(
                vertexC.getTags().containsKey(
                        vertexB.uri()
                )
        );
        vertexC.addTag(
                vertexBAsIdentification
        );
        assertTrue(
                vertexC.getTags().containsKey(
                        vertexB.uri()
                )
        );
    }

    @Test

    public void when_identifying_to_an_identification_the_uri_and_external_uri_are_set_correctly() {
        TagPojo vertexBAsIdentification = vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        URI vertexBAsAnIdentificationUri = vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        vertexC.addTag(
                vertexBAsIdentification
        );
        TagPojo identificationFromIdentification = vertexC.getTags().values().iterator().next();
        assertThat(
                identificationFromIdentification.uri(),
                is(vertexBAsAnIdentificationUri)
        );
        assertThat(
                identificationFromIdentification.getExternalResourceUri(),
                is(vertexB.uri())
        );
    }

    @Test
    public void when_identifying_to_an_identification_the_number_of_references_increases_by_1() {
        TagPojo vertexBAsIdentification = vertexA.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(vertexBAsIdentification.uri()).getNbNeighbors().getTotal(),
                is(2)
        );
        URI vertexBAsAnIdentificationUri = vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        Tag createdIdentification = vertexC.addTag(
                vertexBAsIdentification
        ).values().iterator().next();
        assertThat(
                tagFactory.withUri(createdIdentification.uri()).getNbNeighbors().getTotal(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_graph_element_the_relation_type_is_correct() {
        assertFalse(
                vertexB.getTags().containsKey(vertexA.uri())
        );
        vertexB.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexA
                )
        );
        assertTrue(
                vertexB.getTags().containsKey(vertexA.uri())
        );
    }


    @Test
    public void removing_tag_from_the_reference_changes_the_external_uri_of_the_tag() {
        TagPojo vertexATag = vertexB.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexA
                )
        ).values().iterator().next();
        assertTrue(vertexB.getTags().containsKey(
                vertexA.uri()
        ));
        assertFalse(vertexB.getTags().containsKey(
                vertexATag.uri()
        ));
        vertexA.removeTag(vertexATag);
        assertFalse(vertexB.getTags().containsKey(
                vertexA.uri()
        ));
        assertTrue(vertexB.getTags().containsKey(
                vertexATag.uri()
        ));
    }

    @Test
    public void can_tag_using_no_reference() {
        Tag tag = new TagPojo(
                URI.create(
                        "/service/users/" + user.username() + "/void/ref/" + UUID.randomUUID().toString()
                ),
                new FriendlyResourcePojo(
                        "Void tag"
                )
        );
        Collection<TagPojo> tags = vertexA.addTag(
                tag
        ).values();
        assertThat(
                tags.iterator().next(),
                is(notNullValue())
        );
    }


    @Test
    public void can_set_colors() {
        assertFalse(
                vertexB.getColors().contains("blue")
        );
        vertexB.setColors("blue");
        assertTrue(
                vertexB.getColors().contains("blue")
        );
    }

    @Test
    public void can_set_children_indexes() {
        assertFalse(
                vertexB.getChildrenIndex().contains("test children indexes")
        );
        vertexB.setChildrenIndex("test children indexes");
        assertTrue(
                vertexB.getChildrenIndex().contains("test children indexes")
        );
    }

    @Test
    public void add_additional_self_tag_returns_identifications() {
        TagPojo tag = TestScenarios.tagFromFriendlyResource(
                vertexA
        );
        tag.setExternalResourceUri(
                URI.create(tag.getExternalResourceUri() + "/" + UUID.randomUUID())
        );
        Map<URI, TagPojo> tags = vertexA.addTag(
                tag
        );
        assertThat(
                tags.get(tag.getExternalResourceUri()),
                is(notNullValue())
        );
        assertThat(
                tags.get(tag.getExternalResourceUri()).uri(),
                is(notNullValue())
        );
    }

    @Test
    public void can_use_custom_uri() {
        TagPojo tag = TestScenarios.tagFromFriendlyResource(
                vertexA
        );
        URI customUri = new UserUris(user).generateTagUri();
        tag.setUri(customUri);
        tag.setExternalResourceUri(
                URI.create(tag.getExternalResourceUri() + "/" + UUID.randomUUID())
        );
        vertexA.addTag(
                tag
        );
        assertThat(
                tagFactory.withUri(
                        customUri
                ).label(),
                is("vertex A")
        );
    }

    @Test
    public void checks_if_custom_uri_as_right_owner() {
        TagPojo tag = TestScenarios.tagFromFriendlyResource(
                vertexA
        );
        URI customUri = new UserUris(anotherUser).generateTagUri();
        tag.setUri(customUri);
        tag.setExternalResourceUri(
                URI.create(tag.getExternalResourceUri() + "/" + UUID.randomUUID())
        );
        Map<URI, TagPojo> addedTags = vertexA.addTag(
                tag
        );
        assertTrue(
                addedTags.isEmpty()
        );
        assertTrue(
                vertexA.getTags().isEmpty()
        );
    }
}


