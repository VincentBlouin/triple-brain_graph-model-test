/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph;

import guru.bubl.module.model.IdentifiedTo;
import guru.bubl.test.module.model.graph.search.GraphSearchTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EdgeOperatorTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
