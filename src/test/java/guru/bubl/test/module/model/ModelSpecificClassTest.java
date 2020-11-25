/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.test.module.model.center_graph_element.WholeGraphAdminTest;
import guru.bubl.test.module.model.graph.*;
import guru.bubl.test.module.model.graph.fork.ForkOperatorTest;
import guru.bubl.test.module.model.graph.meta.TagOperatorTest;
import guru.bubl.test.module.model.graph.pattern.PatternUserTest;
import guru.bubl.test.module.model.graph.tree_copier.TreeCopierTest;
import guru.bubl.test.module.model.user.FriendManagerTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PatternUserTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
