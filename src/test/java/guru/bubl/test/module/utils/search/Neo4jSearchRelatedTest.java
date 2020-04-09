/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils.search;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.model.test.scenarios.GraphElementsOfTestScenario;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Before;

import javax.inject.Inject;

public class Neo4jSearchRelatedTest extends ModelTestResources {

    @Inject
    GraphFactory graphMaker;

    @Inject
    protected TestScenarios testScenarios;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;
    protected VertexOperator pineApple;
    protected User user;
    protected User user2;
    protected Vertex vertexOfUser2;


    @Before
    public void beforeSearchRelatedTest() {
        user = User.withEmail(
                "test@2example.org"
        ).setUsername("test2");
        user2 = User.withEmail(
                "test@example.org"
        ).setUsername("test");
        deleteAllDocs();
        makeGraphHave3SerialVerticesWithLongLabels();
        vertexOfUser2 = graphMaker.loadForUser(user2).createVertex();
        pineApple = testScenarios.addPineAppleVertexToVertex(vertexC);
    }

    protected void makeGraphHave3SerialVerticesWithLongLabels() {
        UserGraph userGraph = graphMaker.loadForUser(user);
        userGraph.createVertex();
        GraphElementsOfTestScenario vertexABAndC = testScenarios.changeTestScenarioVerticesToLongLabels(
                userGraph
        );
        vertexA = vertexABAndC.getVertexA();
        vertexB = vertexABAndC.getVertexB();
        vertexC = vertexABAndC.getVertexC();
    }

    protected void deleteAllDocs() {
    }


    protected void indexGraph() {
        wholeGraphAdmin.reindexAll();
    }

    protected void indexVertex(VertexOperator vertex) {

    }
}
