/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.test.module.model.graph.meta.TagOperatorTest;
import guru.bubl.test.module.model.graph.search.GraphIndexerTest;
import guru.bubl.test.module.model.graph.search.GraphSearchTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GraphIndexerTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
