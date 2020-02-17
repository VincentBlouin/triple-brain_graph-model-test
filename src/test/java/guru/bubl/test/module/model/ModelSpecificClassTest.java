/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.graph.pattern.PatternUser;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.GraphElementOperatorNeo4j;
import guru.bubl.test.module.model.center_graph_element.CenterGraphElementOperatorTest;
import guru.bubl.test.module.model.center_graph_element.CenterGraphElementsOperatorTest;
import guru.bubl.test.module.model.graph.GraphElementOperatorTest;
import guru.bubl.test.module.model.graph.UserGraphTest;
import guru.bubl.test.module.model.graph.VertexOperatorTest;
import guru.bubl.test.module.model.graph.meta.TagOperatorTest;
import guru.bubl.test.module.model.graph.pattern.PatternUserTest;
import guru.bubl.test.module.model.graph.search.GraphIndexerTest;
import guru.bubl.test.module.model.graph.search.GraphSearchTest;
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
