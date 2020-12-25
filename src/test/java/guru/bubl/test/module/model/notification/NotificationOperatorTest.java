package guru.bubl.test.module.model.notification;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.Tree;
import guru.bubl.module.model.graph.tree_copier.TreeCopier;
import guru.bubl.module.model.graph.tree_copier.TreeCopierFactory;
import guru.bubl.module.model.notification.Notification;
import guru.bubl.module.model.notification.NotificationOperator;
import guru.bubl.test.module.utils.ModelTestResources;
import org.joda.time.DateTime;
import org.junit.Test;
import org.neo4j.driver.Session;

import javax.inject.Inject;

import java.util.List;

import static guru.bubl.module.model.test.scenarios.TestScenarios.tagFromFriendlyResource;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.neo4j.driver.Values.parameters;

public class NotificationOperatorTest extends ModelTestResources {

    @Inject
    TreeCopierFactory treeCopierFactory;

    @Inject
    NotificationOperator notificationOperator;

    @Test
    public void notifies_when_changing_label_of_root() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        vertexA.label("pear");
        List<Notification> notificationList = notificationOperator.listForUserAndNbSkip(anotherUser, 0);
        assertThat(
                notificationList.size(),
                is(1)
        );
        Notification notification = notificationList.iterator().next();
        assertThat(
                notification.getAction(),
                is("label")
        );
    }

    @Test
    public void notifies_only_users_who_watch() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        User anotherUser2 = createAnotherUser2();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser2);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        vertexA.label("pear");
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
    }

    @Test
    public void can_notify_multiple_users() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        User anotherUser2 = createAnotherUser2();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser2);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser2, 0).size(),
                is(0)
        );
        vertexA.label("pear");
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(1)
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser2, 0).size(),
                is(1)
        );
    }

    @Test
    public void notifies_when_changing_label_of_non_root() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        vertexB.label("pear");
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(1)
        );
    }

    @Test
    public void does_not_notify_when_changing_unrelated_bubble() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        vertexC.makePrivate();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        vertexC.label("pear");
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
    }

    @Test
    public void notifies_when_changing_description() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        vertexC.comment("a comment");
        List<Notification> notificationList = notificationOperator.listForUserAndNbSkip(anotherUser, 0);
        assertThat(
                notificationList.size(),
                is(1)
        );
        Notification notification = notificationList.iterator().next();
        assertThat(
                notification.getAction(),
                is("description")
        );
    }

    @Test
    public void notifies_when_adding_relation() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(0)
        );
        vertexC.addVertexAndRelation();
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(1)
        );
    }

    @Test
    public void includes_root_and_watch_uri() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        vertexC.addVertexAndRelation();
        Notification notification = notificationOperator.listForUserAndNbSkip(anotherUser, 0).iterator().next();
        assertThat(
                notification.getWatchUri(),
                is(vertexC.uri())
        );
        assertThat(
                notification.getRootUri(),
                is(vertexA.uri())
        );
    }

    @Test
    public void it_waits_a_day_between_2_triggers() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        vertexC.addVertexAndRelation();
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(1)
        );
        vertexC.addVertexAndRelation();
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(1)
        );
        setLastModificationDate(
                vertexC.uri(),
                new DateTime().minusDays(1).toDate()
        );
        vertexC.addVertexAndRelation();
        assertThat(
                notificationOperator.listForUserAndNbSkip(anotherUser, 0).size(),
                is(2)
        );
    }

    @Test
    public void includes_watch_label() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        vertexB.addVertexAndRelation();
        Notification notification = notificationOperator.listForUserAndNbSkip(anotherUser, 0).get(0);
        assertThat(
                notification.getWatchLabel(),
                is("vertex B")
        );
    }

    @Test
    public void includes_uri() {
        makeAllPublic();
        setLastModificationDateToMoreThanADayBefore();
        TreeCopier treeCopier = treeCopierFactory.forCopier(anotherUser);
        treeCopier.copyTreeOfUser(
                Tree.withUrisOfGraphElementsAndRootUriAndTag(
                        graphElementsOfTestScenario.allGraphElementsToUris(),
                        vertexA.uri(),
                        tagFromFriendlyResource(vertexA)
                ), user
        );
        vertexB.addVertexAndRelation();
        Notification notification = notificationOperator.listForUserAndNbSkip(anotherUser, 0).get(0);
//        System.out.println(notification.getUri());
        assertThat(
                notification.getUri(),
                is(not(nullValue()))
        );
    }

    private User createAnotherUser2() {
        User anotherUser2 = User.withEmail(
                "colette2.armande@example.org"
        ).setUsername("colette2_armande").setPreferredLocales("[fr]").password("12345678");
        userRepository.createUser(anotherUser2);
        return anotherUser2;
    }

    private void setLastModificationDateToMoreThanADayBefore() {
        Long twoDaysAgo = new DateTime().minusDays(2).toDate().getTime();
        try (Session session = driver.session()) {
            session.run(
                    "MATCH (n:Resource) set n.last_modification_date=$lastModificationDate",
                    parameters(
                            "lastModificationDate", twoDaysAgo
                    )
            );
        }
    }
}
