/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import com.google.inject.Inject;
import guru.bubl.module.model.WholeGraph;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
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
    public void can_refresh_tags_number_of_references() {
        vertexB.addTag(
                modelTestScenarios.possessionIdentification()
        );
        TagPojo identificationPojo = vertexA.addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        tagFactory.withUri(
                identificationPojo.uri()
        ).getNbNeighbors().setPrivate(5);
        assertThat(
                tagFactory.withUri(
                        identificationPojo.uri()
                ).getNbNeighbors().getPrivate(),
                is(5)
        );
        wholeGraphAdmin.refreshNbNeighborsToAllTags();
        assertThat(
                tagFactory.withUri(
                        identificationPojo.uri()
                ).getNbNeighbors().getPrivate(),
                is(2)
        );
    }

    @Test
    public void sets_number_of_reference_to_zero_for_tag_having_zero_references() {
        TagPojo meta = vertexB.addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        wholeGraphAdmin.refreshNbNeighborsToAllTags();
        TagOperator metaOperator = tagFactory.withUri(meta.uri());
        assertThat(
                metaOperator.getNbNeighbors().getPrivate(),
                is(1)
        );
        vertexB.removeTag(meta);
        metaOperator.getNbNeighbors().setPrivate(1);
        wholeGraphAdmin.refreshNbNeighborsToAllTags();
        assertThat(
                metaOperator.getNbNeighbors().getPrivate(),
                is(0)
        );
    }

    @Test
    public void can_remove_tags_having_zero_references() {
        TagPojo possessionTag = vertexB.addTag(
                modelTestScenarios.possessionIdentification()
        ).values().iterator().next();
        vertexB.addTag(
                modelTestScenarios.creatorPredicate()
        ).values().iterator().next();
        assertThat(
                wholeGraph.getAllTags().size(),
                is(2)
        );
        TagOperator possesionTagOperator = tagFactory.withUri(possessionTag.uri());
        possesionTagOperator.getNbNeighbors().setPrivate(0);
        assertThat(
                possesionTagOperator.getNbNeighbors().getTotal(),
                is(0)
        );
        assertThat(
                wholeGraph.getAllTags().size(),
                is(2)
        );
        wholeGraphAdmin.removeMetasHavingZeroReferences();
        assertThat(
                wholeGraph.getAllTags().size(),
                is(1)
        );
    }

    @Test
    public void does_not_duplicate_tags_when_re_adding() {
        vertexB.addTag(
                modelTestScenarios.possessionIdentification()
        );
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        wholeGraphAdmin.reAddIdentifications();
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
    }

    @Test
    public void does_not_duplicate_tags_when_re_adding_even_if_tag_is_a_graph_element() {
        vertexB.addTag(
                TestScenarios.tagFromFriendlyResource(
                        vertexA
                )
        );
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        wholeGraphAdmin.reAddIdentifications();
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
        assertThat(
                vertexB.getTags().size(),
                is(1)
        );
    }

    @Test
    public void index_all_includes_tags() {
        vertexA.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult graphElementSearchResult = graphSearchFactory.usingSearchTerm(
                "Person"
        ).searchRelationsForAutoCompletionByLabel(
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

        vertexB.getNbNeighbors().setPrivate(8);
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(8)
        );
        wholeGraphAdmin.refreshNbNeighbors();
        assertThat(
                vertexA.getNbNeighbors().getTotal(),
                is(1)
        );
        assertThat(
                vertexB.getNbNeighbors().getTotal(),
                is(2)
        );
    }

    @Test
    public void can_refresh_number_of_public_connected_edges() {
        vertexB.makePublic();
        vertexC.makePublic();
        vertexB.getNbNeighbors().setPublic(8);
        assertThat(
                vertexB.getNbNeighbors().getPublic(),
                is(8)
        );
        wholeGraphAdmin.refreshNbNeighbors();
        assertThat(
                vertexB.getNbNeighbors().getPublic(),
                is(1)
        );
    }
}
