/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.user;

import guru.bubl.module.model.friend.FriendManager;
import guru.bubl.module.model.friend.FriendManagerFactory;
import guru.bubl.module.model.friend.FriendStatus;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FriendManagerTest extends ModelTestResources {

    @Inject
    FriendManagerFactory friendManagerFactory;

    @Test
    public void can_add_friend() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        assertTrue(
                friendManager.list().isEmpty()
        );
        friendManager.add(anotherUser);
        assertFalse(
                friendManager.list().isEmpty()
        );
    }

    @Test
    public void adding_friend_that_already_made_a_request_confirms_the_friendship() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        FriendManager otherFriendManager = friendManagerFactory.forUser(anotherUser);
        otherFriendManager.add(user);
        assertThat(
                friendManager.getStatusWithUser(anotherUser),
                is(FriendStatus.confirmed)
        );
    }

    @Test
    public void can_confirm_friendship_without_a_token() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        assertThat(
                friendManager.list().values().iterator().next().getStatus(),
                is(FriendStatus.waiting)
        );
        friendManagerFactory.forUser(anotherUser).confirm(
                user
        );
        assertThat(
                friendManager.list().values().iterator().next().getStatus(),
                is(FriendStatus.confirmed)
        );
    }

    @Test
    public void can_confirm_friendship_with_token() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        String confirmToken = friendManager.add(anotherUser);
        assertThat(
                friendManager.list().values().iterator().next().getStatus(),
                is(FriendStatus.waiting)
        );
        Boolean hasConfirmed = friendManagerFactory.forUser(anotherUser).confirmWithToken(
                user,
                confirmToken
        );
        assertTrue(hasConfirmed);
        assertThat(
                friendManager.list().values().iterator().next().getStatus(),
                is(FriendStatus.confirmed)
        );
    }

    @Test
    public void prevents_friendship_confirmation_if_wrong_token() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        assertThat(
                friendManager.list().values().iterator().next().getStatus(),
                is(FriendStatus.waiting)
        );
        Boolean hasConfirmed = friendManagerFactory.forUser(anotherUser).confirmWithToken(
                user,
                "wrong token"
        );
        assertFalse(hasConfirmed);
        assertThat(
                friendManager.list().values().iterator().next().getStatus(),
                is(FriendStatus.waiting)
        );
    }

    @Test
    public void can_get_status_with_other_user() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        FriendStatus friendStatus = friendManager.getStatusWithUser(anotherUser);
        assertThat(
                friendStatus,
                is(
                        FriendStatus.none
                )
        );
        friendManager.add(anotherUser);
        friendStatus = friendManager.getStatusWithUser(anotherUser);
        assertThat(
                friendStatus,
                is(
                        FriendStatus.waiting
                )
        );
        FriendManager anotherFriendManager = friendManagerFactory.forUser(anotherUser);
        anotherFriendManager.confirm(user);
        friendStatus = friendManager.getStatusWithUser(anotherUser);
        assertThat(
                friendStatus,
                is(
                        FriendStatus.confirmed
                )
        );
    }

    @Test
    public void status_with_another_user_explicits_which_user_is_waiting() {
        FriendManager friendManager = friendManagerFactory.forUser(user);
        friendManager.add(anotherUser);
        assertThat(
                friendManager.getStatusWithUser(anotherUser),
                is(FriendStatus.waiting)
        );
        FriendManager anotherFriendManager = friendManagerFactory.forUser(anotherUser);
        assertThat(
                anotherFriendManager.getStatusWithUser(user),
                is(FriendStatus.waitingForYourAnswer)
        );
    }
}
