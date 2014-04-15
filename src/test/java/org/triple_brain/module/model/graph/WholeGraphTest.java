package org.triple_brain.module.model.graph;

import com.google.inject.Inject;
import org.junit.Test;
import org.triple_brain.module.model.WholeGraph;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphOperator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class WholeGraphTest extends AdaptableGraphComponentTest {

    @Inject
    WholeGraph wholeGraph;

    @Test
    public void there_are_no_duplicates_in_vertices(){
        assertTrue(wholeGraph.getAllVertices().hasNext());
        Set<Vertex> visitedVertices = new HashSet<Vertex>();
        Iterator<VertexInSubGraphOperator> vertexIterator = wholeGraph.getAllVertices();
        while(vertexIterator.hasNext()){
            Vertex vertex = vertexIterator.next();
            if(visitedVertices.contains(vertex)){
                fail();
            }
            visitedVertices.add(vertex);
        }
    }

    @Test
    public void can_get_edges(){
        int nbEdges = 0;
        Iterator<EdgeOperator> edgeIterator = wholeGraph.getAllEdges();
        while(edgeIterator.hasNext()){
            nbEdges++;
            edgeIterator.next();
        }
        assertThat(
                nbEdges,
                is(2)
        );
    }
}
