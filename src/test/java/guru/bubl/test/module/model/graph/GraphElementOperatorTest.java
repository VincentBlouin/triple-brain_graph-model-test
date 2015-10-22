/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.Image;
import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.IdentificationPojo;
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
    public void a_graph_element_is_not_identified_to_itself_if_used_as_an_identification_for_another_element() {
        assertTrue(vertexB.getIdentifications().isEmpty());
        GraphElementOperator vertexAGraphElement = vertexA;
        IdentificationPojo vertexBPojo = new IdentificationPojo(
                vertexB.uri(),
                new FriendlyResourcePojo(
                        vertexB.uri(),
                        vertexB.label()
                )
        );
        vertexAGraphElement.addSameAs(vertexBPojo);
        assertTrue(vertexB.getIdentifications().isEmpty());
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
}
