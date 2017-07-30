/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.test.module.model.graph.UserGraphTest;
import guru.bubl.test.module.model.graph.VertexOperatorTest;
import guru.bubl.test.module.model.graph.subgraph.SubGraphForkerTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        SubGraphForkerTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
