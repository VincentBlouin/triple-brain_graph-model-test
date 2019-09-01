/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import com.google.inject.Inject;
import com.sendgrid.helpers.mail.Mail;
import guru.bubl.module.model.User;
import guru.bubl.module.model.forgot_password.email.ForgotPasswordEmail;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONArray;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ForgotPasswordEmailTest extends ModelTestResources {

    @Inject
    ForgotPasswordEmail forgotPasswordEmail;

    @Test
    public void sent_to_correct_recipient() throws Exception {
        Mail msg = forgotPasswordEmail.send(
                forgetPasswordUser(),
                ""
        );
        assertThat(
                msg.getPersonalization().get(0).getTos().get(0).getEmail(),
                is("test@example.org")
        );
    }

    @Test
    public void has_correct_from() throws Exception {
        Mail msg = forgotPasswordEmail.send(
                forgetPasswordUser(),
                ""
        );
        assertThat(
                msg.from.getEmail(),
                is("mindrespect.com <no-reply@mindrespect.com>")
        );
    }

    @Test
    public void has_correct_body() throws Exception {
        Mail msg = forgotPasswordEmail.send(
                forgetPasswordUser(),
                ""
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "Follow this link to reset your password"
                )
        );
    }

    @Test
    public void if_user_preferred_locale_contains_french_it_translate_to_french() throws Exception {
        User user = forgetPasswordUser().setPreferredLocales(
                new JSONArray().put("fr").toString()
        );
        Mail msg = forgotPasswordEmail.send(
                user,
                ""
        );
        assertFalse(
                msg.getContent().get(0).getValue().contains(
                        "Follow this link to reset your password:"
                )
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "Suivez ce lien pour modifier votre mot de passe"
                )
        );
    }

    @Test
    public void reset_url_is_in_email()throws Exception{
         Mail msg = forgotPasswordEmail.send(
                forgetPasswordUser(),
                "http://domain-url/reset/user-name/token"
        );
        assertTrue(
                msg.getContent().get(0).getValue().contains(
                        "http://domain-url/reset/user-name/token"
                )
        );
    }

    private User forgetPasswordUser(){
        return User.withEmailAndUsername(
                "test@example.org",
                "test_username"
        );
    }
}
