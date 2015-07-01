/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.module.model.graph;

import com.google.inject.Inject;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphOperator;
import org.junit.Test;
import guru.bubl.module.model.WholeGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class WholeGraphTest extends AdaptableGraphComponentTest {

    @Inject
    WholeGraph wholeGraph;

    @Test
    public void there_are_no_duplicates_in_vertices() {
        assertTrue(wholeGraph.getAllVertices().hasNext());
        Set<Vertex> visitedVertices = new HashSet<Vertex>();
        Iterator<VertexInSubGraphOperator> vertexIterator = wholeGraph.getAllVertices();
        while (vertexIterator.hasNext()) {
            Vertex vertex = vertexIterator.next();
            if (visitedVertices.contains(vertex)) {
                fail();
            }
            visitedVertices.add(vertex);
        }
    }

    @Test
    public void can_get_all_vertices() {
        int nbVertices = 0;
        Iterator<VertexInSubGraphOperator> vertexIterator = wholeGraph.getAllVertices();
        while (vertexIterator.hasNext()) {
            nbVertices++;
            vertexIterator.next();
        }
        assertThat(
                nbVertices,
                is(4)
        );
    }

    @Test
    public void schemas_are_not_included_in_vertices() {
        createSchema();
        int nbVertices = 0;
        Iterator<VertexInSubGraphOperator> vertexIterator = wholeGraph.getAllVertices();
        while (vertexIterator.hasNext()) {
            nbVertices++;
            vertexIterator.next();
        }
        assertThat(
                nbVertices,
                is(4)
        );
    }

    @Test
    public void can_get_edges() {
        int nbEdges = 0;
        Iterator<EdgeOperator> edgeIterator = wholeGraph.getAllEdges();
        while (edgeIterator.hasNext()) {
            nbEdges++;
            edgeIterator.next();
        }
        assertThat(
                nbEdges,
                is(2)
        );
    }

    @Test
    public void can_get_schemas() {
        createSchema();
        createSchema();
        createSchema();
        createSchema();
        int nbSchemas = 0;
        Iterator<SchemaOperator> schemaIterator = wholeGraph.getAllSchemas();
        while (schemaIterator.hasNext()) {
            nbSchemas++;
            schemaIterator.next();
        }
        assertThat(
                nbSchemas,
                is(4)
        );
    }

    @Test
    public void can_get_all_graph_elements(){
        createSchema().addProperty();
        int nbGraphElements = 0;
        Iterator<GraphElementOperator> graphElementIterator = wholeGraph.getAllGraphElements();
        while (graphElementIterator.hasNext()) {
            nbGraphElements++;
            graphElementIterator.next();
        }
        assertThat(
                nbGraphElements,
                is(8)
        );
    }

    private SchemaOperator createSchema() {
        return userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
    }
}
