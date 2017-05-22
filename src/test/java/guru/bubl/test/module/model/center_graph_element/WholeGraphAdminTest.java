/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import com.google.inject.Inject;
import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.admin.WholeGraphAdminFactory;
import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WholeGraphAdminTest extends ModelTestResources {

    @Inject
    WholeGraphAdminFactory wholeGraphAdminFactory;

    @Inject
    protected WholeGraph wholeGraph;

    @Test
    public void can_refresh_identifications_number_of_references() {
        vertexB.addMeta(
                modelTestScenarios.possessionIdentification()
        );
        IdentifierPojo identificationPojo = vertexA.addMeta(
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
    public void sets_number_of_reference_to_zero_for_meta_having_zero_references() {
        IdentifierPojo meta = vertexB.addMeta(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).refreshNumberOfReferencesToAllIdentifications();
        IdentificationOperator metaOperator = identificationFactory.withUri(meta.uri());
        assertThat(
                metaOperator.getNbReferences(),
                is(1)
        );
        vertexB.removeIdentification(meta);
        metaOperator.setNbReferences(1);
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).refreshNumberOfReferencesToAllIdentifications();
        assertThat(
                metaOperator.getNbReferences(),
                is(0)
        );
    }

    @Test
    public void can_remove_metas_having_zero_references() {
        IdentifierPojo possessionMeta = vertexB.addMeta(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        vertexB.addMeta(
                modelTestScenarios.creatorPredicate()
        ).values().iterator().next();
        assertThat(
                wholeGraph.getAllIdentifications().size(),
                is(2)
        );
        vertexB.removeIdentification(possessionMeta);
        assertThat(
                wholeGraph.getAllIdentifications().size(),
                is(2)
        );
        wholeGraphAdminFactory.withWholeGraph(
                wholeGraph
        ).removeMetasHavingZeroReferences();
        assertThat(
                wholeGraph.getAllIdentifications().size(),
                is(1)
        );
    }

    @Test
    public void does_not_duplicate_identifications_when_re_adding() {
        vertexB.addMeta(
                modelTestScenarios.possessionIdentification()
        );
        assertThat(
                vertexB.getIdentifications().size(),
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
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
    }

    @Test
    public void does_not_duplicate_identifications_when_re_adding_even_if_identification_is_a_graph_element() {
        vertexB.addMeta(
                TestScenarios.identificationFromFriendlyResource(
                        vertexA
                )
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getIdentifications().size(),
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
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexB.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getIdentifications().size(),
                is(1)
        );
        assertThat(
                vertexA.getIdentifications().size(),
                is(1)
        );
    }
}
