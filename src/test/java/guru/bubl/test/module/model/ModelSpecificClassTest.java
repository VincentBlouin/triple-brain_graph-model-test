/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.test.module.model.graph.UserGraphTest;
import guru.bubl.test.module.model.graph.pattern.PatternUserTest;
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
