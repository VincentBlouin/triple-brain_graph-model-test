/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import com.google.inject.Inject;
import guru.bubl.module.model.admin.WholeGraphAdminFactory;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.module.model.test.scenarios.TestScenarios;
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
        ).values().iterator().next();
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

    @Test
    public void does_not_duplicate_identifications_when_re_adding() {
        vertexB.addSameAs(
                modelTestScenarios.possessionIdentification()
        );
        assertThat(
                vertexB.getSameAs().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).reAddIdentifications();
        assertThat(
                vertexB.getSameAs().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
    }

    @Test
    public void does_not_duplicate_identifications_when_re_adding_even_if_identification_is_a_graph_element() {
        vertexB.addType(
                TestScenarios.identificationFromFriendlyResource(
                        vertexA
                )
        );
        assertThat(
                vertexB.getAdditionalTypes().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getGenericIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getIdentifications().size(),
                is(1)
        );
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).reAddIdentifications();
        assertThat(
                vertexB.getAdditionalTypes().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getGenericIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getIdentifications().size(),
                is(1)
        );
    }
}
