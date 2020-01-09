/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.meta;

import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import java.net.URI;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class TagOperatorTest extends ModelTestResources {

    @Test
    public void can_set_number_of_references() {
        TagPojo identificationPojo = vertexA.addMeta(
                modelTestScenarios.tShirt()
        ).values().iterator().next();
        TagOperator tagOperator = tagFactory.withUri(
                identificationPojo.uri()
        );
        assertThat(
                tagOperator.getNbReferences(),
                is(1)
        );
        tagOperator.setNbReferences(5);
        assertThat(
                tagOperator.getNbReferences(),
                is(5)
        );
    }

    @Test
    public void can_build_pojo() {
        TagPojo identificationPojo = vertexA.addMeta(
                modelTestScenarios.location()
        ).values().iterator().next();
        URI identifierUri = identificationPojo.uri();
        TagOperator tagOperator = tagFactory.withUri(
                identifierUri
        );
        identificationPojo = tagOperator.buildPojo();
        assertThat(
                identificationPojo.label(),
                is("Location")
        );
        assertThat(
                identificationPojo.getNbReferences(),
                is(1)
        );
        assertThat(
                identificationPojo.uri(),
                is(identifierUri)
        );
        assertThat(
                identificationPojo.getExternalResourceUri().toString(),
                is("http://rdf.freebase.com/rdf/m/01n7")
        );
        assertThat(
                identificationPojo.comment(),
                is("The Location type is used for any topic with a fixed location on the planet Earth. It includes geographic features such as oceans and mountains, political entities like cities and man-made objects like buildings.Guidelines for filling in location properties:geolocation: the longitude and latitude (in decimal notation) of the feature, or of the geographical center (centroid) fo the feature.contains and contained by: these properties can be used to show spatial relationships between different locations, such as an island contained by a body of water (which is equivalent to saying the body of water contains the island), a state contained by a country, a mountain within the borders of a national park, etc. For geopolitical locations,   containment two levels up and down is the ideal minimum. For example, the next two levels up for the city of Detroit are Wayne County and the state of Michigan.adjoins: also used to show spatial relations, in this case between locations that share a border.USBG Name: A unique name given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. More information can be found on their website. GNIS ID: A unique id given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. GNIS stands for Geographic Names Information System. More information can be found on their website.GEOnet Feature ID: The UFI (Unique Feature ID) used by GeoNet for features outside of the United States. More information can be found on their website.")
        );
    }

    @Test
    public void mergeTo_removes_tag() {
        TagPojo personFromFreebase = vertexA.addMeta(
                modelTestScenarios.personFromFreebase()
        ).values().iterator().next();

        TagPojo personTag = vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        assertTrue(
                userGraph.haveElementWithId(
                        personFromFreebase.uri()
                )
        );
        tagFactory.withUri(
                personFromFreebase.uri()
        ).mergeTo(personTag);
        assertFalse(
                userGraph.haveElementWithId(
                        personFromFreebase.uri()
                )
        );
    }

    @Test
    public void mergeTo_includes_tagged_graph_elements() {
        TagPojo personFromFreebase = vertexA.addMeta(
                modelTestScenarios.personFromFreebase()
        ).values().iterator().next();
        TagPojo personTag = vertexB.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        vertexC.addMeta(
                personFromFreebase
        );
        assertThat(
                tagFactory.withUri(
                        personTag.uri()
                ).getNbReferences(),
                is(1)
        );
        assertFalse(
                vertexA.getIdentifications().containsKey(
                        personTag.getExternalResourceUri()
                )
        );
        tagFactory.withUri(
                personFromFreebase.uri()
        ).mergeTo(personTag);
        assertThat(
                tagFactory.withUri(
                        personTag.uri()
                ).getNbReferences(),
                is(3)
        );
        assertTrue(
                vertexA.getIdentifications().containsKey(
                        personTag.getExternalResourceUri()
                )
        );
    }

    @Test
    public void mergeTo_excludes_non_related_graph_elements() {
        vertexC.addMeta(modelTestScenarios.computerScientistType());
        vertexB.addVertexAndRelation();
        TagPojo personFromFreebase = vertexA.addMeta(
                modelTestScenarios.personFromFreebase()
        ).values().iterator().next();
        TagPojo person = vertexB.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        tagFactory.withUri(
                personFromFreebase.uri()
        ).mergeTo(person);
        assertThat(
                tagFactory.withUri(
                        person.uri()
                ).getNbReferences(),
                is(2)
        );
        assertFalse(
                vertexC.getIdentifications().containsKey(
                        person.getExternalResourceUri()
                )
        );
    }
}
