/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.center_graph_element;

import com.google.inject.Inject;
import guru.bubl.module.model.admin.WholeGraphAdmin;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.module.model.graph.tag.TagOperator;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class WholeGraphAdminTest extends ModelTestResources {

    @Inject
    WholeGraphAdmin wholeGraphAdmin;

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
    public void reindex_all_sets_private_context() {
        wholeGraphAdmin.reindexAll();
        assertThat(
                vertexB.getPrivateContext(),
                is("vertex C{{vertex A")
        );
        assertThat(
                vertexA.getPrivateContext(),
                is("vertex B")
        );
        assertThat(
                vertexC.getPrivateContext(),
                is("vertex B{{vertex D{{vertex E")
        );
    }

    @Test
    

    public void reindex_all_sets_context_even_if_no_connected_edges() {
        VertexOperator newVertex = vertexFactory.withUri(
                userGraph.createVertex().uri()
        );
        newVertex.addRelationToVertex(vertexB);
        wholeGraphAdmin.reindexAll();
        assertThat(
                newVertex.getPrivateContext(),
                is("vertex B")
        );
        newVertex.getEdgeToDestinationVertex(vertexB).remove();
        wholeGraphAdmin.reindexAll();
        assertThat(
                newVertex.getPrivateContext(),
                is("")
        );
    }

    @Test
    
    public void index_vertex_sets_its_private_surround_graph() {
        assertThat(
                vertexB.getPrivateContext(),
                is("")
        );
        wholeGraphAdmin.reindexAll();
        assertThat(
                vertexB.getPrivateContext(),
                is("vertex C{{vertex A")
        );
    }

    @Test
    public void limits_the_context_size_of_vertices() {
        for (int i = 0; i < 10; i++) {
            vertexFactory.withUri(
                    vertexB.addVertexAndRelation().destinationUri()
            ).label("vertex " + i);
        }
        wholeGraphAdmin.reindexAll();
        assertThat(
                vertexB.getPrivateContext().length(),
                is(110)
        );
    }

    @Test
    
    public void filters_empty_label_from_context() {
        for (int i = 0; i < 5; i++) {
            vertexB.addVertexAndRelation();
        }
        wholeGraphAdmin.reindexAll();
        assertThat(
                vertexB.getPrivateContext(),
                is("vertex C{{vertex A")
        );
    }

    @Test
    

    public void context_can_have_quotes() {
        vertexA.label("\"some\" label");
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                vertexB.label()
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().split("\\{\\{").length,
                is(2)
        );
    }

    @Test
    

    public void context_prioritize_vertices_with_most_child() {
        for (int i = 4; i <= 10; i++) {
            VertexOperator destinationVertex = vertexFactory.withUri(
                    vertexB.addVertexAndRelation().destinationUri()
            );
            vertexFactory.withUri(
                    destinationVertex.uri()
            ).label("vertex " + i);
            for (int j = 0; j <= i; j++) {
                destinationVertex.addVertexAndRelation();
            }
        }
        wholeGraphAdmin.reindexAll();
        String[] context = vertexB.getPrivateContext().split("\\{\\{");
        assertThat(
                context[0],
                is("vertex 10")
        );
        assertThat(
                context[1],
                is("vertex 9")
        );
        assertThat(
                context[4],
                is("vertex 6")
        );
    }

    @Test
    

    public void surround_graph_does_not_include_all_vertices() {
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult vertexSearchResult = graphSearchFactory.usingSearchTerm(
                vertexA.label()
        ).searchForAllOwnResources(
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().split("\\{\\{").length,
                is(1)
        );
    }

    @Test
    

    public void index_vertex_sets_its_public_surround_graph() {
        vertexB.makePublic();
        vertexA.makePublic();
        wholeGraphAdmin.reindexAll();
        CenterGraphElementOperator vertexBAsCenter = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        vertexBAsCenter.incrementNumberOfVisits();
        vertexBAsCenter.updateLastCenterDate();
        CenterGraphElementPojo center = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().iterator().next();
        assertThat(
                center.getContext().split("\\{\\{").length,
                is(1)
        );
        vertexC.makePublic();
        wholeGraphAdmin.reindexAll();
        center = centerGraphElementsOperatorFactory.usingDefaultLimits().getAllPublic().iterator().next();
        assertThat(
                center.getContext().split("\\{\\{").length,
                is(2)
        );
    }

    @Test
    

    public void context_does_not_include_self_vertex() {
        wholeGraphAdmin.reindexAll();
        assertFalse(
                vertexB.getPrivateContext().contains(
                        vertexB.label()
                )
        );
    }


    @Test
    

    public void index_relation_sets_source_and_destination_vertex_as_context() {
        RelationOperator edgeAAndB = vertexA.getEdgeToDestinationVertex(vertexB);
        assertThat(
                edgeAAndB.getPrivateContext(),
                is("")
        );
        wholeGraphAdmin.reindexAll();
        assertThat(
                edgeAAndB.getPrivateContext(),
                is("vertex B{{vertex A")
        );
    }


    @Test
    
    public void meta_context_includes_label_of_surround_vertices() {
        vertexA.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult graphElementSearchResult = graphSearchFactory.usingSearchTerm(
                "Person"
        ).searchRelationsForAutoCompletionByLabel(
                user
        ).iterator().next();
        assertThat(
                graphElementSearchResult.getContext(),
                is("vertex A")
        );
    }

    @Test
    public void meta_related_to_relation_context_includes_label_of_surround_vertices() {
        RelationOperator edge = vertexB.getEdgeToDestinationVertex(vertexC);
        edge.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        wholeGraphAdmin.reindexAll();
        GraphElementSearchResult graphElementSearchResult = graphSearchFactory.usingSearchTerm(
                "Person"
        ).searchRelationsForAutoCompletionByLabel(
                user
        ).iterator().next();
        assertThat(
                graphElementSearchResult.getContext(),
                is("vertex C")
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
        assertThat(
                graphElementSearchResult.getContext(),
                is(
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

    @Test
    


    public void can_set_nb_neighbors_to_zero() {
        vertexA.getEdgeToDestinationVertex(vertexB).remove();
        vertexA.getNbNeighbors().setPrivate(10);
        wholeGraphAdmin.refreshNbNeighbors();
        assertThat(
                vertexA.getNbNeighbors().getPrivate(),
                is(0)
        );
    }


}
