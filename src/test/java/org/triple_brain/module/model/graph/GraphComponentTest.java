package org.triple_brain.module.model.graph;

import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.graph.vertex.VertexOperator;

/*
* Copyright Mozilla Public License 1.1
*/
public interface GraphComponentTest {
    public int numberOfEdgesAndVertices();
    public SubGraphOperator wholeGraphAroundDefaultCenterVertex();
    public SubGraphOperator wholeGraph();
    public void removeWholeGraph();
    public boolean graphContainsLabel(String label);
    public User user();
    public void user(User user);
    public UserGraph userGraph();
    public VertexOperator vertexA();
    public void setDefaultVertexAkaVertexA(VertexOperator vertexA);
    public VertexOperator vertexB();
    public VertexOperator vertexC();
    public VertexInSubGraph vertexInWholeGraph(Vertex vertex);
    public void beforeClass();
    public void before();
    public void after();
    public void afterClass();
}
