/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.admin.WholeGraphAdminDailyJob;
import guru.bubl.module.model.content.AllContent;
import guru.bubl.module.model.friend.friend_request_email.FriendRequestEmail;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.module.neo4j_graph_manipulator.graph.center_graph_element.CenterGraphElementsOperatorNeo4j;
import guru.bubl.module.neo4j_user_repository.UserRepositoryNeo4j;
import guru.bubl.test.module.model.center_graph_element.CenterGraphElementOperatorTest;
import guru.bubl.test.module.model.center_graph_element.CenterGraphElementsOperatorTest;
import guru.bubl.test.module.model.center_graph_element.WholeGraphAdminTest;
import guru.bubl.test.module.model.graph.*;
import guru.bubl.test.module.model.graph.meta.IdentificationOperatorTest;
import guru.bubl.test.module.model.graph.meta.UserMetasOperatorTest;
import guru.bubl.test.module.model.graph.search.GraphIndexerTest;
import guru.bubl.test.module.model.graph.search.GraphSearchTest;
import guru.bubl.test.module.model.graph.subgraph.SubGraphForkerTest;
import guru.bubl.test.module.model.user.FriendManagerTest;
import guru.bubl.test.module.model.user.UserRepositoryTest;
import guru.bubl.test.module.utils.ModelTestRunner;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses({
        CenterGraphElementsOperatorTest.class
})
public class ModelSpecificClassTest extends ModelTestRunner {}
