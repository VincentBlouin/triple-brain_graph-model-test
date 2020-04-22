package guru.bubl.test.module.model.graph.fork;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ForkOperatorTest extends ModelTestResources {
    @Test
    public void adding_a_relation_to_existing_vertices_increments_number_of_connected_edges() {
        int nbEdgesForVertexA = vertexA.getNbNeighbors().getTotal();
        int nbEdgesForVertexC = vertexC.getNbNeighbors().getTotal();
        vertexC.addRelationToFork(
                vertexA.uri(),
                vertexC.getShareLevel(),
                vertexA.getShareLevel()
        );
        assertThat(
                vertexA.getNbNeighbors().getTotal(),
                is(nbEdgesForVertexA + 1)
        );
        assertThat(
                vertexC.getNbNeighbors().getTotal(),
                is(nbEdgesForVertexC + 1)
        );
    }

    @Test
    public void adding_a_relation_to_existing_vertices_does_not_increment_nb_public_neighbors_if_both_are_private() {
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.addRelationToFork(
                vertexA.uri(),
                vertexC.getShareLevel(),
                vertexA.getShareLevel()
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test
    public void adding_a_relation_to_existing_vertices_increments_nb_public_neighbors_to_source_if_destination_is_public() {
        vertexA.makePublic();
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.addRelationToFork(
                vertexA.uri(),
                vertexC.getShareLevel(),
                vertexA.getShareLevel()
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(1)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
    }

    @Test
    public void adding_a_relation_to_existing_vertices_increments_nb_public_neighbors_to_destination_if_source_is_public() {
        vertexC.makePublic();
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(0)
        );
        vertexC.addRelationToFork(
                vertexA.uri(),
                vertexC.getShareLevel(),
                vertexA.getShareLevel()
        );
        assertThat(
                vertexC.getNbNeighbors().getPublic(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getPublic(),
                is(1)
        );
    }

    @Test
    public void adding_a_relation_to_existing_vertices_increments_nb_friend_neighbors_to_source_if_destination_is_friend() {
        vertexA.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
        vertexC.addRelationToFork(
                vertexA.uri(),
                vertexC.getShareLevel(),
                vertexA.getShareLevel()
        );
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(1)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
    }

    @Test
    public void adding_a_relation_to_existing_vertices_increments_nb_friend_neighbors_to_destination_if_source_is_friend() {
        vertexC.setShareLevel(ShareLevel.FRIENDS);
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(0)
        );
        vertexC.addRelationToFork(
                vertexA.uri(),
                vertexC.getShareLevel(),
                vertexA.getShareLevel()
        );
        assertThat(
                vertexC.getNbNeighbors().getFriend(),
                is(0)
        );
        assertThat(
                vertexA.getNbNeighbors().getFriend(),
                is(1)
        );
    }

    @Test
    public void add_relation_increment_if_source_is_group_relation(){
        assertThat(
                groupRelation.getNbNeighbors().getTotal(),
                is(3)
        );
        groupRelation.addRelationToFork(
                vertexA.uri(),
                ShareLevel.PRIVATE,
                ShareLevel.PRIVATE
        );
        assertThat(
                groupRelation.getNbNeighbors().getTotal(),
                is(4)
        );
    }
}
