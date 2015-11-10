/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.Identification;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;
import org.parboiled.common.StringUtils;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphElementOperatorTest extends ModelTestResources {

    @Test
    public void cannot_identify_to_self() {
        String errorMessage = "identification cannot be the same";
        GraphElementOperator vertexAGraphElement = vertexA;
        try {
            vertexAGraphElement.addGenericIdentification(
                    identificationFromFriendlyResource(
                            vertexA
                    )
            );
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
        try {
            vertexAGraphElement.addSameAs(
                    identificationFromFriendlyResource(
                            vertexA
                    )
            );
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
        try {
            vertexAGraphElement.addType(identificationFromFriendlyResource(
                    vertexA
            ));
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is(errorMessage));
        }
    }

    @Test
    public void cannot_have_same_identification_twice() {
        GraphElementOperator vertexAGraphElement = vertexA;
        Integer numberOfGenericIdentifications = vertexAGraphElement.getGenericIdentifications().size();
        vertexAGraphElement.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getGenericIdentifications().size(),
                is(
                        numberOfGenericIdentifications + 1
                )
        );
        Integer numberOfSameAs = vertexAGraphElement.getSameAs().size();
        vertexAGraphElement.addSameAs(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addSameAs(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getSameAs().size(),
                is(
                        numberOfSameAs + 1
                )
        );
        Integer numberOfTypes = vertexAGraphElement.getAdditionalTypes().size();
        vertexAGraphElement.addType(
                modelTestScenarios.computerScientistType()
        );
        vertexAGraphElement.addType(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                vertexAGraphElement.getAdditionalTypes().size(),
                is(
                        numberOfTypes + 1
                )
        );
    }

    @Test
    public void adding_identification_returns_identification_created_fields() {
        IdentificationPojo identification = vertexA.addSameAs(
                modelTestScenarios.timBernersLee()
        );
        assertNotNull(
                identification.creationDate()
        );
        assertNotNull(
                identification.lastModificationDate()
        );
    }

    @Test
    public void users_identification_have_their_own_uri_for_same_identification() {
        IdentificationPojo identificationOfAnotherUser = vertexOfAnotherUser.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
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
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        IdentificationPojo identification2 = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertTrue(identification.uri().equals(identification2.uri()));
    }

    @Test
    public void adding_existing_identification_keeps_existing_images() {
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
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
        IdentificationPojo sameIdentification = vertexA.addGenericIdentification(
                identification
        );
        assertThat(sameIdentification.images().size(), is(0));
    }

    @Test
    public void identifications_can_have_images() {
        IdentificationPojo computerScientist = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertTrue(
                computerScientist.images().isEmpty()
        );
        Set<Image> images = new HashSet<>();
        images.add(Image.withBase64ForSmallAndUriForBigger(
                "dummy base 64",
                URI.create("/big_image")
        ));
        IdentificationPojo person = modelTestScenarios.person();
        person.setImages(
                images
        );
        person = vertexA.addGenericIdentification(
                person
        );
        assertFalse(
                person.images().isEmpty()
        );
    }

    @Test
    public void adding_existing_identification_returns_existing_description() {
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertTrue(
                StringUtils.isEmpty(
                        identification.comment()
                )
        );
        identification = modelTestScenarios.computerScientistType();
        identification.setComment(
                "A computer scientist is ..."
        );
        IdentificationPojo sameIdentification = vertexA.addGenericIdentification(
                identification
        );
        assertTrue(sameIdentification.comment().isEmpty());
    }

    @Test
    public void can_remove_identification_having_no_external_uri() {
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
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
    public void identifications_do_not_apply_for_all_elements() {
        vertexA.addGenericIdentification(
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
    public void on_creation_identifications_have_1_reference(){
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                identification.getNbReferences(),
                is(1)
        );
    }

    @Test
    public void adding_an_identification_increments_number_of_references(){
        IdentificationPojo identification = vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                identification.getNbReferences(),
                is(1)
        );
        identification = vertexB.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        assertThat(
                identification.getNbReferences(),
                is(2)
        );
    }

    @Test
    public void removing_an_identification_decrements_number_of_references(){
        vertexA.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        vertexB.addGenericIdentification(
                modelTestScenarios.computerScientistType()
        );
        IdentificationPojo identification = vertexB.getIdentifications().values().iterator().next();
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
    public void graph_element_becomes_identified_to_itself_if_used_as_identifier(){
        IdentificationPojo identification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );
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
    public void when_identified_to_a_graph_element_the_number_of_references_to_the_new_identification_is_2(){
        IdentificationPojo identification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );
        assertThat(
                identification.getNbReferences(),
                is(2)
        );
    }

    @Test
    public void can_identify_to_an_identification_where_the_external_uri_is_the_identification_uri(){
        IdentificationPojo vertexBAsIdentification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );

        vertexBAsIdentification.setExternalResourceUri(vertexBAsIdentification.uri());
        vertexBAsIdentification.setUri(null);
        assertFalse(
                vertexC.getGenericIdentifications().containsKey(
                        vertexB.uri()
                )
        );
        vertexC.addGenericIdentification(
                vertexBAsIdentification
        );
        assertTrue(
                vertexC.getGenericIdentifications().containsKey(
                        vertexB.uri()
                )
        );
    }

    @Test
    public void when_identifying_to_an_identification_the_uri_and_external_uri_are_set_correctly(){
        IdentificationPojo vertexBAsIdentification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );
        URI vertexBAsAnIdentificationUri =  vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        vertexC.addGenericIdentification(
                vertexBAsIdentification
        );
        IdentificationPojo identificationFromIdentification = vertexC.getGenericIdentifications().values().iterator().next();
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
    public void an_identification_does_identify_to_itself(){
        IdentificationPojo vertexBAsIdentification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );
        URI vertexBAsAnIdentificationUri =  vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        Identification createdIdentification = vertexC.addGenericIdentification(
                vertexBAsIdentification
        );
        Set<GraphElementSearchResult> relatedIdentifications = identifiedTo.getForIdentificationAndUser(
                createdIdentification,
                user
        );
        assertThat(
                relatedIdentifications.size(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_an_identification_the_number_of_references_increases_by_1(){
        IdentificationPojo vertexBAsIdentification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(vertexB)
        );
        URI vertexBAsAnIdentificationUri =  vertexBAsIdentification.uri();
        vertexBAsIdentification.setExternalResourceUri(vertexBAsAnIdentificationUri);
        vertexBAsIdentification.setUri(null);
        Identification createdIdentification = vertexC.addGenericIdentification(
                vertexBAsIdentification
        );
        assertThat(
                createdIdentification.getNbReferences(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_a_schema_the_number_of_references_for_first_identification_is_2_and_the_next_increase_by_1(){
        SchemaOperator schema = createSchema();
        IdentificationPojo schemaAsIdentification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(schema)
        );
        assertThat(
                schemaAsIdentification.getNbReferences(),
                is(2)
        );
        schemaAsIdentification = vertexB.addGenericIdentification(
                identificationFromFriendlyResource(schema)
        );
        assertThat(
                schemaAsIdentification.getNbReferences(),
                is(3)
        );
    }

    @Test
    public void when_identifying_to_a_schema_property_the_number_of_references_increases_by_1(){
        GraphElementOperator property = createSchema().addProperty();
        IdentificationPojo propertyAsIdentification = vertexA.addGenericIdentification(
                identificationFromFriendlyResource(property)
        );
        assertThat(
                propertyAsIdentification.getNbReferences(),
                is(2)
        );
        propertyAsIdentification = vertexB.addGenericIdentification(
                identificationFromFriendlyResource(property)
        );
        assertThat(
                propertyAsIdentification.getNbReferences(),
                is(3)
        );
    }

    @Test
    public void identifying_to_a_graph_element_also_identifies_to_its_identifiers(){
        EdgeOperator edgeBetweenAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        edgeBetweenAAndB.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        EdgeOperator edgeBetweenBAndC = vertexB.getEdgeThatLinksToDestinationVertex(
                vertexC
        );
        edgeBetweenBAndC.addSameAs(
                identificationFromFriendlyResource(edgeBetweenAAndB)
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
    public void when_identifying_to_graph_element_the_relation_type_is_correct(){
        assertFalse(
                vertexB.getSameAs().containsKey(vertexA.uri())
        );
        vertexB.addSameAs(
                identificationFromFriendlyResource(vertexA)
        );
        assertTrue(
                vertexB.getSameAs().containsKey(vertexA.uri())
        );
    }
}
