/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.content.AllContent;
import guru.bubl.module.model.friend.friend_request_email.FriendRequestEmail;
import guru.bubl.test.module.model.user.FriendManagerTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        FriendManagerTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
