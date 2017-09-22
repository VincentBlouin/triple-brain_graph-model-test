/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.content.CommonContent;
import guru.bubl.module.model.content.CommonContentFactory;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CommonContentTest extends ModelTestResources {

    @Inject
    CommonContentFactory commonContentFactory;

    @Test
    public void adding_two_tasks_add_tag_to_center_list(){
        Set<CenterGraphElementPojo> centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centerGraphElements.size(),
                is(0)
        );
        commonContentFactory.usingLocale(Locale.FRENCH).addTwoTasksToVertex(vertexB);
        centerGraphElements = centerGraphElementsOperatorFactory.forUser(
                user
        ).getPublicAndPrivate();
        assertThat(
                centerGraphElements.size(),
                is(1)
        );
    }

}
