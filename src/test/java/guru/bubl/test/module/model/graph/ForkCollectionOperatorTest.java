package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.graph_element.ForkCollectionOperator;
import guru.bubl.module.model.graph.graph_element.ForkCollectionOperatorFactory;
import guru.bubl.module.model.graph.relation.RelationOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ForkCollectionOperatorTest extends ModelTestResources {
    @Inject
    protected ForkCollectionOperatorFactory forkCollectionOperatorFactory;

    @Test
    public void can_remove_multiple_vertices() {
        assertThat(
                vertexB.connectedEdges().size(),
                is(2)
        );
        ForkCollectionOperator forkCollectionOperator = forkCollectionOperatorFactory.withUris(urisToSet(
                vertexA.uri(),
                vertexC.uri()
        ));
        forkCollectionOperator.remove();
        assertThat(
                vertexB.connectedEdges().size(),
                is(0)
        );
    }

    @Test
    public void remove_edges_does_not_remove_their_related_vertices() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        assertThat(
                vertexB.connectedEdges().size(),
                is(2)
        );
        ForkCollectionOperator forkCollectionOperator = forkCollectionOperatorFactory.withUris(urisToSet(
                edgeAB.uri(),
                edgeBC.uri()
        ));
        forkCollectionOperator.remove();
        assertThat(
                vertexB.connectedEdges().size(),
                is(0)
        );
        assertTrue(
                userGraph.haveElementWithId(vertexA.uri())
        );
        assertTrue(
                userGraph.haveElementWithId(vertexB.uri())
        );
        assertTrue(
                userGraph.haveElementWithId(vertexC.uri())
        );
    }

    @Test
    public void can_remove_vertices_and_their_related_edges_at_the_same_time() {
        RelationOperator edgeAB = vertexA.getEdgeToDestinationVertex(vertexB);
        RelationOperator edgeBC = vertexB.getEdgeToDestinationVertex(vertexC);
        ForkCollectionOperator forkCollectionOperator = forkCollectionOperatorFactory.withUris(urisToSet(
                edgeAB.uri(),
                vertexA.uri(),
                vertexB.uri(),
                edgeBC.uri()
        ));
        forkCollectionOperator.remove();
        assertThat(
                vertexB.connectedEdges().size(),
                is(0)
        );
        assertFalse(
                userGraph.haveElementWithId(vertexA.uri())
        );
        assertFalse(
                userGraph.haveElementWithId(vertexB.uri())
        );
        assertTrue(
                userGraph.haveElementWithId(vertexC.uri())
        );
        assertFalse(
                userGraph.haveElementWithId(edgeAB.uri())
        );
        assertFalse(
                userGraph.haveElementWithId(edgeBC.uri())
        );
    }


    @Test
    public void remove_vertices_does_not_remove_their_surround_group_relations() {
        ForkCollectionOperator forkCollectionOperator = forkCollectionOperatorFactory.withUris(urisToSet(
                vertexC.uri(),
                vertexD.uri(),
                vertexE.uri()
        ));
        forkCollectionOperator.remove();
        assertTrue(
                userGraph.haveElementWithId(groupRelation.uri())
        );
    }

    private Set<URI> urisToSet(URI... uris) {
        return Stream.of(
                uris
        ).collect(Collectors.toSet());
    }
}
