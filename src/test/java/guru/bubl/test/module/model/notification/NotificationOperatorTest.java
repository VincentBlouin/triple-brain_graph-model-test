package guru.bubl.test.module.model.notification;

import guru.bubl.module.model.graph.Tree;
import guru.bubl.module.model.graph.tree_copier.TreeCopier;
import guru.bubl.module.model.graph.tree_copier.TreeCopierFactory;
import guru.bubl.module.model.notification.NotificationOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;

import static guru.bubl.module.model.test.scenarios.TestScenarios.tagFromFriendlyResource;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class NotificationOperatorTest extends ModelTestResources {

    @Inject
    TreeCopierFactory treeCopierFactory;

    @Inject
    NotificationOperator notificationOperator;

    @Test
    public void notifies_when_changing_label_of_root() {
        makeAllPublic();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUser(anotherUser).size(),
                is(0)
        );
        vertexA.label("pear");
        assertThat(
                notificationOperator.listForUser(anotherUser).size(),
                is(1)
        );
    }
}
