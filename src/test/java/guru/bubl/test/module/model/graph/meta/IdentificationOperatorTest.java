/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.meta;

import guru.bubl.module.model.graph.identification.IdentificationOperator;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IdentificationOperatorTest extends ModelTestResources {

    @Test
    public void can_set_number_of_references(){
        IdentifierPojo identificationPojo = vertexA.addMeta(
                modelTestScenarios.tShirt()
        ).values().iterator().next();
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
