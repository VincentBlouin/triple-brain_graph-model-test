/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class GraphFactoryTest extends ModelTestResources {

    @Test
    public void the_default_created_bubble_has_1_center_bubble_visit(){
        User user = User.withEmail("a@example.com");
        graphFactory.createForUser(user);
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        assertThat(
                centerGraphElement.getNumberOfVisits(),
                is(1)
        );
    }

    @Test
    public void the_default_created_bubble_has_last_center_date(){
        User user = User.withEmail("a@example.com");
        graphFactory.createForUser(user);
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate().iterator().next();
        assertThat(
                centerGraphElement.getLastCenterDate(),
                is(not(Matchers.nullValue()))
        );
    }
}
