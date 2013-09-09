package org.triple_brain.module.model.graph;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/*
* Copyright Mozilla Public License 1.1
*/
public class GraphElementTest extends AdaptableGraphComponentTest{
    @Test
    public void cannot_add_same_identification_twice(){
        GraphElement vertexAGraphElement = vertexA;
        assertThat(
                vertexAGraphElement.getIdentifications().size(),
                is(0)
        );
        vertexAGraphElement.addGenericIdentification(
                modelTestScenarios.extraterrestrial()
        );
        assertThat(
                vertexAGraphElement.getIdentifications().size(),
                is(1)
        );
        vertexAGraphElement.addGenericIdentification(
                modelTestScenarios.extraterrestrial()
        );
        assertThat(
                vertexAGraphElement.getIdentifications().size(),
                is(1)
        );
    }

}
