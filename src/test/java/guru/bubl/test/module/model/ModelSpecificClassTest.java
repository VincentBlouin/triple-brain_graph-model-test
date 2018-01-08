/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.test.module.model.graph.*;
import guru.bubl.test.module.model.graph.meta.IdentificationOperatorTest;
import guru.bubl.test.module.model.graph.search.GraphIndexerTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        IdentificationOperatorTest.class

})
public class ModelSpecificClassTest extends ModelTestRunner {}
