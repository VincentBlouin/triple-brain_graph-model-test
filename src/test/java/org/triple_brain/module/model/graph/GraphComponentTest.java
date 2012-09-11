package org.triple_brain.module.model.graph;

import org.triple_brain.module.model.User;

/*
* Copyright Mozilla Public License 1.1
*/
public interface GraphComponentTest {
    public int numberOfEdgesAndVertices();
    public SubGraph wholeGraph();
    public void removeWholeGraph();
    public boolean graphContainsLabel(String label);
    public User user();
    public UserGraph userGraph();
    public Vertex vertexA();
    public Vertex vertexB();
    public Vertex vertexC();

    public void beforeClass();
    public void before();
    public void after();
    public void afterClass();
}
