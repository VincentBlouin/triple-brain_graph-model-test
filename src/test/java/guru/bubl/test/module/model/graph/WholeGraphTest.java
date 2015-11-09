/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphOperator;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class WholeGraphTest extends ModelTestResources {

    @Test
    public void there_are_no_duplicates_in_vertices() {
        assertFalse(wholeGraph.getAllVertices().isEmpty());
        Set<Vertex> visitedVertices = new HashSet<Vertex>();
        Set<VertexInSubGraphOperator> vertices = wholeGraph.getAllVertices();
        for (VertexInSubGraph vertex : vertices) {
            if (visitedVertices.contains(vertex)) {
                fail();
            }
            visitedVertices.add(vertex);
        }
    }

    @Test
    public void can_get_all_vertices() {
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(4)
        );
    }

    @Test
    public void schemas_are_not_included_in_vertices() {
        createSchema();
        assertThat(
                wholeGraph.getAllVertices().size(),
                is(4)
        );
    }

    @Test
    public void can_get_edges() {
        assertThat(
                wholeGraph.getAllEdges().size(),
                is(2)
        );
    }

    @Test
    public void can_get_schemas() {
        createSchema();
        createSchema();
        createSchema();
        createSchema();
        assertThat(
                wholeGraph.getAllSchemas().size(),
                is(4)
        );
    }

    @Test
    public void can_get_all_graph_elements() {
        createSchema().addProperty();
        assertThat(
                wholeGraph.getAllGraphElements().size(),
                is(8)
        );
    }

    @Test
    public void can_get_all_identifications() {
        assertTrue(
                wholeGraph.getAllIdentifications().isEmpty()
        );
        vertexA.addType(
                modelTestScenarios.human()
        );
        vertexA.addGenericIdentification(
                modelTestScenarios.person()
        );
        vertexA.addSameAs(
                modelTestScenarios.timBernersLee()
        );
        assertThat(
                wholeGraph.getAllIdentifications().size(),
                is(3)
        );
    }
}
