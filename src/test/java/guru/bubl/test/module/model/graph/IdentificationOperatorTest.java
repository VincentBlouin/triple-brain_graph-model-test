/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.graph.IdentificationOperator;
import guru.bubl.module.model.graph.IdentificationPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IdentificationOperatorTest extends ModelTestResources {

    @Test
    public void can_set_number_of_references(){
        IdentificationPojo identificationPojo = vertexA.addGenericIdentification(
                modelTestScenarios.tShirt()
        );
        IdentificationOperator identificationOperator = identificationFactory.withUri(
                identificationPojo.uri()
        );
        assertThat(
                identificationOperator.getNbReferences(),
                is(1)
        );
        identificationOperator.setNbReferences(5);
        assertThat(
                identificationOperator.getNbReferences(),
                is(5)
        );
    }

}
