/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.search.GraphSearch;
import guru.bubl.test.module.model.graph.GraphElementOperatorTest;
import guru.bubl.test.module.model.graph.UserGraphTest;
import guru.bubl.test.module.model.graph.meta.UserMetasOperatorTest;
import guru.bubl.test.module.model.graph.search.GraphSearchTest;
import guru.bubl.test.module.model.graph.subgraph.SubGraphForkerTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserMetasOperatorTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
