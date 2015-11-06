/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.graph.Identification;
import guru.bubl.test.module.model.center_graph_element.CenterGraphElementOperatorTest;
import guru.bubl.test.module.model.center_graph_element.WholeGraphAdminTest;
import guru.bubl.test.module.model.graph.*;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
       GraphElementOperatorTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
