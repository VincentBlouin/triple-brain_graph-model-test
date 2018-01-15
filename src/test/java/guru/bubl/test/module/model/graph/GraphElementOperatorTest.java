/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.graph.identification.Identifier;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;
import org.parboiled.common.StringUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static guru.bubl.module.model.graph.GraphElementOperator.colorProps.background;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphElementOperatorTest extends ModelTestResources {

    @Test
    public void cannot_have_same_identification_twice() {
        GraphElementOperator vertexAGraphElement = vertexA;
        Integer numberOfGenericIdentifications = vertexAGraphElement.getIdentifications().size();
        vertexAGraphElement.addMeta(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addMeta(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getIdentifications().size(),
                is(
                        numberOfGenericIdentifications + 1
                )
        );
    }

    @Test
    public void adding_identification_returns_identification_created_fields() {
        IdentifierPojo identification = vertexA.addMeta(
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
    public void users_identification_have_their_own_uri_for_same_identification() {
        IdentifierPojo identificationOfAnotherUser = vertexOfAnotherUser.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        IdentifierPojo identification = vertexA.addMeta(
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
        IdentifierPojo identification = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        IdentifierPojo identification2 = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(identification.uri().equals(identification2.uri()));
    }

    @Test
    public void adding_existing_identification_keeps_existing_images() {
        IdentifierPojo identification = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                identification.images().isEmpty()
        );
        Set<Image> images = new HashSet<>();
        images.add(Image.withBase64ForSmallAndUriForBigger(
                "dummy base 64",
                URI.create("/big_image")
        ));
        identification = modelTestScenarios.computerScientistType();
        identification.setImages(
                images
        );
        IdentifierPojo sameIdentification = vertexA.addMeta(
                identification
        ).values().iterator().next();
        assertThat(sameIdentification.images().size(), is(0));
    }

    @Test
    public void identifications_can_have_images() {
        IdentifierPojo computerScientist = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                computerScientist.images().isEmpty()
        );
        Set<Image> images = new HashSet<>();
        images.add(Image.withBase64ForSmallAndUriForBigger(
                "dummy base 64",
                URI.create("/big_image")
        ));
        IdentifierPojo person = modelTestScenarios.person();
        person.setImages(
                images
        );
        person = vertexA.addMeta(
                person
        ).values().iterator().next();
        assertFalse(
                person.images().isEmpty()
        );
    }

    @Test
    public void adding_existing_identification_returns_existing_description() {
        IdentifierPojo identification = vertexA.addMeta(
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
        IdentifierPojo sameIdentification = vertexA.addMeta(
                identification
        ).values().iterator().next();
        assertTrue(sameIdentification.comment().isEmpty());
    }

    @Test
    public void can_remove_identification_having_no_external_uri() {
        IdentifierPojo identification = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertTrue(
                vertexA.getIdentifications().containsValue(identification)
        );
        identification.setExternalResourceUri(null);
        vertexA.removeIdentification(identification);
        assertFalse(
                vertexA.getIdentifications().containsValue(identification)
        );
    }

    @Test
    public void can_remove_self_identifier() {
        IdentifierPojo vertexBAsIdentifier = TestScenarios.identificationFromFriendlyResource(
                vertexB
        );
        IdentifierPojo createdVertexBAsIdentifier = vertexA.addMeta(
                vertexBAsIdentifier
        ).values().iterator().next();
        SubGraphPojo subGraph = neo4jSubGraphExtractorFactory.withCenterVertexAndDepth(
                createdVertexBAsIdentifier.uri(),
                1
        ).load();
        assertTrue(
                subGraph.vertices().containsKey(
                        vertexB.uri()
                )
        );
        vertexB.removeIdentification(createdVertexBAsIdentifier);
        subGraph = neo4jSubGraphExtractorFactory.withCenterVertexAndDepth(
                createdVertexBAsIdentifier.uri(),
                1
        ).load();
        assertFalse(
                subGraph.vertices().containsKey(
                        vertexB.uri()
                )
        );
    }
    @Test
    public void identifications_do_not_apply_for_all_elements() {
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        assertFalse(
                vertexA.getIdentifications().isEmpty()
        );
        assertTrue(
                vertexB.getIdentifications().isEmpty()
        );
    }

    @Test
    public void on_creation_identifications_have_1_reference() {
        IdentifierPojo identification = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(1)
        );
    }

    @Test
    public void adding_an_identification_increments_number_of_references() {
        IdentifierPojo identification = vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(1)
        );
        identification = vertexB.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(2)
        );
    }

    @Test
    public void removing_an_identification_decrements_number_of_references() {
        vertexA.addMeta(
                modelTestScenarios.computerScientistType()
        );
        vertexB.addMeta(
                modelTestScenarios.computerScientistType()
        );
        IdentifierPojo identification = vertexB.getIdentifications().values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(2)
        );
        vertexA.removeIdentification(
                vertexA.getIdentifications().values().iterator().next()
        );
        identification = vertexB.getIdentifications().values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(1)
        );
    }

    @Test
    public void removing_a_vertex_decrements_number_of_metas_related_to_the_connected_edges() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        IdentificationOperator metaOperator = identificationFactory.withUri(
                edgeBetweenAAndB.addMeta(
                        modelTestScenarios.creatorPredicate()
                ).values().iterator().next().uri()
        );
        assertThat(
                metaOperator.getNbReferences(),
                is(1)
        );
        vertexA.remove();
        assertThat(
                metaOperator.getNbReferences(),
                is(0)
        );
    }

    @Test
    public void graph_element_becomes_identified_to_itself_if_used_as_identifier() {
        IdentifierPojo identification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        assertTrue(
                vertexA.getIdentifications().containsKey(
                        identification.getExternalResourceUri()
                )
        );
        assertTrue(
                vertexB.getIdentifications().containsKey(
                        identification.getExternalResourceUri()
                )
        );
    }

    @Test
    public void when_identified_to_a_graph_element_the_number_of_references_to_the_new_identifier_is_2() {
        IdentifierPojo identification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        assertThat(
                identification.getNbReferences(),
                is(2)
        );
    }

    @Test
    public void can_identify_to_an_identification_where_the_external_uri_is_the_identification_uri() {
        IdentifierPojo vertexBAsIdentification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();

        vertexBAsIdentification.setExternalResourceUri(vertexBAsIdentification.uri());
        vertexBAsIdentification.setUri(null);
        assertFalse(
                vertexC.getIdentifications().containsKey(
                        vertexB.uri()
                )
        );
        vertexC.addMeta(
                vertexBAsIdentification
        );
        assertTrue(
                vertexC.getIdentifications().containsKey(
                        vertexB.uri()
                )
        );
    }

    @Test
    public void when_identifying_to_an_identification_the_uri_and_external_uri_are_set_correctly() {
        IdentifierPojo vertexBAsIdentification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        URI vertexBAsAnIdentificationUri = vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        vertexC.addMeta(
                vertexBAsIdentification
        );
        IdentifierPojo identificationFromIdentification = vertexC.getIdentifications().values().iterator().next();
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
    public void an_identification_does_identify_to_itself() {
        IdentifierPojo vertexBAsIdentification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        URI vertexBAsAnIdentificationUri = vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        Identifier createdIdentification = vertexC.addMeta(
                vertexBAsIdentification
        ).values().iterator().next();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                createdIdentification.uri()
        );
        assertThat(
                subGraph.vertices().size(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_an_identification_the_number_of_references_increases_by_1() {
        IdentifierPojo vertexBAsIdentification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexB
                )
        ).values().iterator().next();
        URI vertexBAsAnIdentificationUri = vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        Identifier createdIdentification = vertexC.addMeta(
                vertexBAsIdentification
        ).values().iterator().next();
        assertThat(
                createdIdentification.getNbReferences(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_a_schema_the_number_of_references_for_first_identification_is_2_and_the_next_increase_by_1() {
        SchemaOperator schema = createSchema();
        IdentifierPojo schemaAsIdentification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        schema
                )
        ).values().iterator().next();
        assertThat(
                schemaAsIdentification.getNbReferences(),
                is(2)
        );
        schemaAsIdentification = vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        schema
                )
        ).values().iterator().next();
        assertThat(
                schemaAsIdentification.getNbReferences(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_a_schema_property_the_number_of_references_increases_by_1() {
        GraphElementOperator property = createSchema().addProperty();
        IdentifierPojo propertyAsIdentification = vertexA.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        property
                )
        ).values().iterator().next();
        assertThat(
                propertyAsIdentification.getNbReferences(),
                is(2)
        );
        propertyAsIdentification = vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        property
                )
        ).values().iterator().next();
        assertThat(
                propertyAsIdentification.getNbReferences(),
                is(3)
        );
    }

    @Test
    public void identifying_to_a_graph_element_also_identifies_to_its_identifiers() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        edgeBetweenAAndB.addMeta(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(
                vertexC
        );
        edgeBetweenBAndC.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        edgeBetweenAAndB
                )
        );
        assertTrue(
                edgeBetweenBAndC.getIdentifications().containsKey(edgeBetweenAAndB.uri())
        );
        assertTrue(
                edgeBetweenBAndC.getIdentifications().containsKey(
                        modelTestScenarios.possessionIdentification().getExternalResourceUri()
                )
        );
        assertThat(
                edgeBetweenBAndC.getIdentifications().size(),
                is(2)
        );
    }

    @Test
    public void identifying_to_a_graph_element_that_has_multiple_identifiers_returns_them_all() {
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        edgeBetweenAAndB.addMeta(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(
                vertexC
        );
        Map<URI, IdentifierPojo> returnedIdentifiers = edgeBetweenBAndC.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        edgeBetweenAAndB
                )
        );
        assertTrue(
                returnedIdentifiers.containsKey(edgeBetweenAAndB.uri())
        );
        assertTrue(
                returnedIdentifiers.containsKey(
                        modelTestScenarios.possessionIdentification().getExternalResourceUri()
                )
        );
        assertThat(
                returnedIdentifiers.size(),
                is(2)
        );
    }

    @Test
    public void new_identifications_from_identifying_to_a_graph_element_that_has_multiple_identifiers_increments_their_number_of_references() {
        IdentifierPojo personIdentification = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        assertThat(
                personIdentification.getNbReferences(),
                is(1)
        );
        personIdentification = vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexA
                )
        ).get(personIdentification.getExternalResourceUri());
        assertThat(
                personIdentification.getNbReferences(),
                is(2)
        );
    }

    @Test
    public void identifying_to_a_graph_element_that_has_another_identification_that_shares_the_other_graph_element_does_not_increment_the_number_of_references() {
        vertexA.addMeta(
                modelTestScenarios.person()
        );
        IdentifierPojo personIdentification = vertexB.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        assertThat(
                personIdentification.getNbReferences(),
                is(2)
        );
        personIdentification = vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexA
                )
        ).get(personIdentification.getExternalResourceUri());
        assertThat(
                personIdentification.getNbReferences(),
                is(2)
        );
    }

    @Test
    public void when_identifying_to_graph_element_the_relation_type_is_correct() {
        assertFalse(
                vertexB.getIdentifications().containsKey(vertexA.uri())
        );
        vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexA
                )
        );
        assertTrue(
                vertexB.getIdentifications().containsKey(vertexA.uri())
        );
    }

    @Test
    public void can_keep_tag_removed_from_the_reference_after_tagging_to_it_again(){
        IdentifierPojo vertexATag = vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexA
                )
        ).values().iterator().next();
        assertTrue(
                vertexA.getIdentifications().containsKey(
                        vertexATag.getExternalResourceUri()
                )
        );
        vertexA.removeIdentification(vertexATag);
        assertFalse(
                vertexA.getIdentifications().containsKey(
                        vertexATag.getExternalResourceUri()
                )
        );
        vertexC.addMeta(
                vertexATag
        );
        assertFalse(
                vertexA.getIdentifications().containsKey(
                        vertexATag.getExternalResourceUri()
                )
        );
    }

    @Test
    public void can_set_colors() {
        assertFalse(
                vertexB.getColors().containsKey(background)
        );
        Map<GraphElementOperator.colorProps, String> colors = new HashMap<>();
        colors.put(background, "blue");
        vertexB.setColors(colors);
        assertTrue(
                vertexB.getColors().containsKey(background)
        );
        assertThat(vertexB.getColors().get(background), is("blue"));
    }

    @Test
    public void can_children_indexes() {
        assertFalse(
                vertexB.getChildrenIndex().contains("test children indexes")
        );
        vertexB.setChildrenIndex("test children indexes");
        assertTrue(
                vertexB.getChildrenIndex().contains("test children indexes")
        );
    }
}


