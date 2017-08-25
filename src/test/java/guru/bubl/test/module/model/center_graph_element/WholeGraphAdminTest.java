/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import com.google.inject.Inject;
import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class WholeGraphAdminTest extends ModelTestResources {

    @Inject
    WholeGraphAdmin wholeGraphAdmin;

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
        wholeGraphAdmin.refreshNumberOfReferencesToAllIdentifications();
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
        wholeGraphAdmin.refreshNumberOfReferencesToAllIdentifications();
        IdentificationOperator metaOperator = identificationFactory.withUri(meta.uri());
        assertThat(
                metaOperator.getNbReferences(),
                is(1)
        );
        vertexB.removeIdentification(meta);
        metaOperator.setNbReferences(1);
        wholeGraphAdmin.refreshNumberOfReferencesToAllIdentifications();
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
        wholeGraphAdmin.removeMetasHavingZeroReferences();
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
        wholeGraphAdmin.reAddIdentifications();
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
        wholeGraphAdmin.reAddIdentifications();
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

    @Test
    public void index_all_includes_metas() {
        vertexA.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult graphElementSearchResult = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                "Person",
                user
        ).iterator().next();
        assertTrue(
                graphElementSearchResult.getContext().values().iterator().next().equals(
                        "vertex A"
                )
        );
    }

    @Test
    public void can_refresh_number_of_connected_edges() {
        vertexB.setNumberOfConnectedEdges(8);
        assertThat(
                vertexB.getNumberOfConnectedEdges(),
                is(8)
        );
        wholeGraphAdmin.refreshNumberOfConnectedEdges();
        assertThat(
                vertexB.getNumberOfConnectedEdges(),
                is(2)
        );
    }

    @Test
    public void can_refresh_number_of_public_connected_edges() {
        vertexB.makePublic();
        vertexC.makePublic();
        vertexB.setNumberOfPublicConnectedEdges(8);
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(8)
        );
        wholeGraphAdmin.refreshNumberOfConnectedEdges();
        assertThat(
                vertexB.getNbPublicNeighbors(),
                is(1)
        );
    }
}
