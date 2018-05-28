/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import com.google.inject.Inject;
import com.sendgrid.Mail;
import guru.bubl.module.model.User;
import guru.bubl.module.model.friend.friend_request_email.FriendRequestEmail;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FriendRequestEmailTest extends ModelTestResources {

    @Inject
    FriendRequestEmail friendRequestEmail;

    @Test
    public void sent_to_correct_recipient() {
        Mail msg = friendRequestEmail.sendToUserFromUser(
                destinationUser(),
                requestUser(),
                "url"
        );
        assertThat(
                msg.getPersonalization().get(0).getTos().get(0).getEmail(),
                is("destination@example.org")
        );
    }

    @Test
    public void has_correct_from(){
        Mail msg = friendRequestEmail.sendToUserFromUser(
                destinationUser(),
                requestUser(),
                ""
        );
        assertThat(
                msg.from.getEmail(),
                is("mindrespect.com <no-reply@mindrespect.com>")
        );
    }

    @Test
    public void has_correct_body(){
        Mail msg = friendRequestEmail.sendToUserFromUser(
                destinationUser(),
                requestUser(),
                ""
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "Follow this link to accept the friend request"
                )
        );
    }

    @Test
    public void if_user_preferred_locale_contains_french_it_translate_to_french(){
        User destinationUser = destinationUser().setPreferredLocales(
                new JSONArray().put("fr").toString()
        );
        Mail msg = friendRequestEmail.sendToUserFromUser(
                destinationUser,
                requestUser(),
                ""
        );
        assertFalse(
                msg.getContent().get(0).getValue().contains(
                        "Follow this link to accept the friend request"
                )
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "Suivez ce lien pour accepter sa requête d'amitié"
                )
        );
    }

    @Test
    public void reset_url_is_in_email(){
        Mail msg = friendRequestEmail.sendToUserFromUser(
                destinationUser(),
                requestUser(),
                "http://domain-url/confirm"
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "http://domain-url/confirm"
                )
        );
    }

    private User requestUser() {
        return User.withEmailAndUsername(
                "request@example.org",
                "request_user"
        );
    }

    private User destinationUser() {
        return User.withEmailAndUsername(
                "destination@example.org",
                "destination_user"
        );
    }
}
