package org.triple_brain.module.model.graph.scenarios;

import org.triple_brain.module.model.graph.vertex.VertexOperator;

/*
* Copyright Mozilla Public License 1.1
*/
public class VerticesCalledABAndC {

    private VertexOperator vertexA;
    private VertexOperator vertexB;
    private VertexOperator vertexC;

    public VerticesCalledABAndC(VertexOperator vertexA, VertexOperator vertexB, VertexOperator vertexC){
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.vertexC = vertexC;
    }

    public VertexOperator vertexA(){
        return vertexA;
    }

    public VertexOperator vertexB(){
        return vertexB;
    }

    public VertexOperator vertexC(){
        return vertexC;
    }
}
