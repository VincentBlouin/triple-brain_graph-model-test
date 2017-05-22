/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.test.module.model.center_graph_element.WholeGraphAdminTest;
import guru.bubl.test.module.model.graph.meta.IdentificationOperatorTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        WholeGraphAdminTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
