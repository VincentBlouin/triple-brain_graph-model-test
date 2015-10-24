/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import com.google.inject.Inject;
import guru.bubl.module.model.admin.WholeGraphAdminFactory;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WholeGraphAdminTest extends ModelTestResources {

    @Inject
    WholeGraphAdminFactory wholeGraphAdminFactory;

    @Test
    public void can_refresh_identifications_number_of_references() {
        vertexB.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        IdentificationPojo identificationPojo = vertexA.addGenericIdentification(
                modelTestScenarios.possessionIdentification()
        );
        identificationFactory.withUri(
                identificationPojo.uri()
        ).setNbReferences(5);
        assertThat(
                identificationFactory.withUri(
                        identificationPojo.uri()
                ).getNbReferences(),
                is(5)
        );
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).refreshNumberOfReferencesToAllIdentifications();
        assertThat(
                identificationFactory.withUri(
                        identificationPojo.uri()
                ).getNbReferences(),
                is(2)
        );
    }
}
