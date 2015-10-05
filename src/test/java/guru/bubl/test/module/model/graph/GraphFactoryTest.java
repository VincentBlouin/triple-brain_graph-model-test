/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GraphFactoryTest extends ModelTestResources {

    @Test
    public void creating_a_user_graph_creates_one_bubble_labeled_me(){
        User user = User.withEmail("a@example.com");
        UserGraph userGraph = graphFactory.createForUser(user);
        assertThat(
                userGraph.defaultVertex().label(),
                is("me")
        );
    }

    @Test
    public void the_default_created_bubble_has_1_center_bubble_visit(){
        User user = User.withEmail("a@example.com");
        graphFactory.createForUser(user);
        CenterGraphElementPojo centerGraphElement = centerGraphElementsOperatorFactory.forUser(
                user
        ).get().iterator().next();
        assertThat(
                centerGraphElement.getNumberOfVisits(),
                is(1)
        );
    }
}
