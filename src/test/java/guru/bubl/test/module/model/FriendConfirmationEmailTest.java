/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import com.google.inject.Inject;
import com.sendgrid.helpers.mail.Mail;
import guru.bubl.module.model.User;
import guru.bubl.module.model.friend.friend_confirmation_email.FriendConfirmationEmail;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FriendConfirmationEmailTest extends ModelTestResources {

    @Inject
    FriendConfirmationEmail friendConfirmationEmail;

    @Test
    public void sent_to_correct_recipient() {
        Mail msg = friendConfirmationEmail.sendForUserToUser(
                destinationUser(),
                requestUser(),
                "url"
        );
        assertThat(
                msg.getPersonalization().get(0).getTos().get(0).getEmail(),
                is("request@example.org")
        );
    }

    @Test
    public void has_correct_from(){
        Mail msg = friendConfirmationEmail.sendForUserToUser(
                destinationUser(),
                requestUser(),
                "url"
        );
        assertThat(
                msg.from.getEmail(),
                is("mindrespect.com <no-reply@mindrespect.com>")
        );
    }

    @Test
    public void has_correct_body(){
        Mail msg = friendConfirmationEmail.sendForUserToUser(
                destinationUser(),
                requestUser(),
                "url"
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "You can now see more bubbles at this link"
                )
        );
    }

    @Test
    public void if_user_preferred_locale_contains_french_it_translate_to_french(){
        User destinationUser = destinationUser().setPreferredLocales(
                new JSONArray().put("fr").toString()
        );
        Mail msg = friendConfirmationEmail.sendForUserToUser(
                destinationUser,
                requestUser(),
                "url"
        );
        assertFalse(
                msg.getContent().get(0).getValue().contains(
                        "You can now see more bubbles at this link"
                )
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "Vous pouvez maintenant voir plus de ses bulles à partir ce lien"
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
