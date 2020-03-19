/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.meta;

import guru.bubl.module.model.graph.ShareLevel;
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
        TagPojo identificationPojo = vertexA.addTag(
                modelTestScenarios.tShirt()
        ).values().iterator().next();
        TagOperator tagOperator = tagFactory.withUri(
                identificationPojo.uri()
        );
        assertThat(
                tagOperator.getNbNeighbors().getPrivate(),
                is(1)
        );
        tagOperator.getNbNeighbors().setPrivate(5);
        assertThat(
                tagOperator.getNbNeighbors().getPrivate(),
                is(5)
        );
    }

    @Test
    public void can_build_pojo() {
        TagPojo tag = vertexA.addTag(
                modelTestScenarios.location()
        ).values().iterator().next();
        URI identifierUri = tag.uri();
        TagOperator tagOperator = tagFactory.withUri(
                identifierUri
        );
        tag = tagOperator.buildPojo();
        assertThat(
                tag.label(),
                is("Location")
        );
        assertThat(
                tag.getNbNeighbors().getPrivate(),
                is(1)
        );
        assertThat(
                tag.uri(),
                is(identifierUri)
        );
        assertThat(
                tag.getExternalResourceUri().toString(),
                is("http://rdf.freebase.com/rdf/m/01n7")
        );
        assertThat(
                tag.comment(),
                is("The Location type is used for any topic with a fixed location on the planet Earth. It includes geographic features such as oceans and mountains, political entities like cities and man-made objects like buildings.Guidelines for filling in location properties:geolocation: the longitude and latitude (in decimal notation) of the feature, or of the geographical center (centroid) fo the feature.contains and contained by: these properties can be used to show spatial relationships between different locations, such as an island contained by a body of water (which is equivalent to saying the body of water contains the island), a state contained by a country, a mountain within the borders of a national park, etc. For geopolitical locations,   containment two levels up and down is the ideal minimum. For example, the next two levels up for the city of Detroit are Wayne County and the state of Michigan.adjoins: also used to show spatial relations, in this case between locations that share a border.USBG Name: A unique name given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. More information can be found on their website. GNIS ID: A unique id given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. GNIS stands for Geographic Names Information System. More information can be found on their website.GEOnet Feature ID: The UFI (Unique Feature ID) used by GeoNet for features outside of the United States. More information can be found on their website.")
        );
    }

    @Test
    public void mergeTo_removes_tag() {
        TagPojo personFromFreebase = vertexA.addTag(
                modelTestScenarios.personFromFreebase()
        ).values().iterator().next();

        TagPojo personTag = vertexA.addTag(
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
        TagPojo personFromFreebase = vertexA.addTag(
                modelTestScenarios.personFromFreebase()
        ).values().iterator().next();
        TagPojo personTag = vertexB.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        vertexC.addTag(
                personFromFreebase
        );
        assertThat(
                tagFactory.withUri(
                        personTag.uri()
                ).getNbNeighbors().getPrivate(),
                is(1)
        );
        assertFalse(
                vertexA.getTags().containsKey(
                        personTag.getExternalResourceUri()
                )
        );
        tagFactory.withUri(
                personFromFreebase.uri()
        ).mergeTo(personTag);
        assertThat(
                tagFactory.withUri(
                        personTag.uri()
                ).getNbNeighbors().getPrivate(),
                is(3)
        );
        assertTrue(
                vertexA.getTags().containsKey(
                        personTag.getExternalResourceUri()
                )
        );
    }

    @Test
    public void mergeTo_excludes_non_related_graph_elements() {
        vertexC.addTag(modelTestScenarios.computerScientistType());
        vertexB.addVertexAndRelation();
        TagPojo personFromFreebase = vertexA.addTag(
                modelTestScenarios.personFromFreebase()
        ).values().iterator().next();
        TagPojo person = vertexB.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        tagFactory.withUri(
                personFromFreebase.uri()
        ).mergeTo(person);
        assertThat(
                tagFactory.withUri(
                        person.uri()
                ).getNbNeighbors().getPrivate(),
                is(2)
        );
        assertFalse(
                vertexC.getTags().containsKey(
                        person.getExternalResourceUri()
                )
        );
    }

    @Test
    public void can_set_share_level() {
        URI tagUri = vertexA.addTag(
                modelTestScenarios.location()
        ).values().iterator().next().uri();
        assertThat(
                tagFactory.withUri(
                        tagUri
                ).getShareLevel(),
                is(ShareLevel.PRIVATE)
        );
        tagFactory.withUri(tagUri).setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                tagFactory.withUri(
                        tagUri
                ).getShareLevel(),
                is(ShareLevel.FRIENDS)
        );
    }

//    @Test
//    @Ignore("the complexisity of implementing this is not worth the value")
//    public void cannot_change_share_level_when_connected_to_a_pattern() {
//        TagPojo tag = vertexB.addMeta(
//                modelTestScenarios.location()
//        ).values().iterator().next();
//        vertexA.makePattern();
//        assertThat(
//                tagFactory.withUri(
//                        tag.uri()
//                ).getShareLevel(),
//                is(ShareLevel.PUBLIC)
//        );
//        Boolean success = tagFactory.withUri(
//                tag.uri()
//        ).setShareLevel(ShareLevel.PRIVATE);
//        assertFalse(
//                success
//        );
//        assertThat(
//                tagFactory.withUri(
//                        tag.uri()
//                ).getShareLevel(),
//                is(ShareLevel.PUBLIC)
//        );
//    }
}
