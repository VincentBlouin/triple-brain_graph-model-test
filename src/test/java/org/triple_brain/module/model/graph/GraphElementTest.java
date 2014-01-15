package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.graph.vertex.Vertex;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/*
* Copyright Mozilla Public License 1.1
*/
public class GraphElementTest extends AdaptableGraphComponentTest{
    @Test
    public void cannot_identify_to_self(){
        String errorMessage = "identification cannot be the same";
        GraphElementOperator vertexAGraphElement = vertexA;
        try{
            vertexAGraphElement.addGenericIdentification(vertexA);
            fail();
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), is(errorMessage));
        }
        try{
            vertexAGraphElement.addSameAs(vertexA);
            fail();
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), is(errorMessage));
        }
        try{
            vertexAGraphElement.addType(vertexA);
            fail();
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), is(errorMessage));
        }
    }

    @Test
    public void an_exception_is_thrown_when_adding_already_existing_identification(){
        String errorMessage = "cannot have duplicate identifications";
        GraphElementOperator vertexAGraphElement = vertexA;
        Vertex vertexD = vertexA.addVertexAndRelation().destinationVertex();
        try{
            vertexAGraphElement.addGenericIdentification(vertexB);
            vertexAGraphElement.addGenericIdentification(vertexB);
            fail();
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), is(errorMessage));
        }
        try{
            vertexAGraphElement.addSameAs(vertexC);
            vertexAGraphElement.addSameAs(vertexC);
            fail();
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), is(errorMessage));
        }
        try{
            vertexAGraphElement.addType(vertexD);
            vertexAGraphElement.addType(vertexD);
            fail();
        }catch(IllegalArgumentException e){
            assertThat(e.getMessage(), is(errorMessage));
        }
        assertThat(
                vertexAGraphElement.getIdentifications().size(),
                is(3)
        );
    }

    @Test
    public void a_graph_element_is_not_identified_to_itself_if_used_as_an_identification_for_another_element(){
        assertTrue(vertexB.getIdentifications().isEmpty());
        GraphElementOperator vertexAGraphElement = vertexA;
        vertexAGraphElement.addSameAs(vertexB);
        assertTrue(vertexB.getIdentifications().isEmpty());
    }
}
