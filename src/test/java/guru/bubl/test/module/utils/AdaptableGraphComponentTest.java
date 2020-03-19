/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.model.*;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.inject.Inject;

public class AdaptableGraphComponentTest implements GraphComponentTest {

    protected UserGraph userGraph;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;
    protected VertexOperator vertexOfAnotherUser;

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    private GraphComponentTest graphComponentTest;

    @Inject
    protected GraphFactory graphMaker;

    @Inject
    public ModelTestScenarios modelTestScenarios;

    @Inject
    WholeGraph wholeGraph;

    public static Injector injector;


    public void beforeClass() {
    }

    @BeforeClass
    public static void realBeforeClass() {
        injector = Guice.createInjector(
                Neo4jModule.forTestingUsingEmbedded(),
                ModelModule.forTesting(),
                new ModelTestModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        requireBinding(ModelTestScenarios.class);
                    }
                }
        );
        injector.getInstance(GraphComponentTest.class)
                .beforeClass();
    }

    @Before
    public void before() {
        injector.injectMembers(this);
        graphComponentTest.before();
        userGraph = userGraph();
        vertexA = vertexA();
        vertexB = vertexB();
        vertexC = vertexC();
        vertexOfAnotherUser = vertexOfAnotherUser();
    }

    @After
    public void after() {
        graphComponentTest.after();
    }

    @Override
    public void afterClass() {
    }

    @AfterClass
    public static void realAfterClass() {
        injector.getInstance(GraphComponentTest.class)
                .afterClass();
    }

    @Override
    public int numberOfEdgesAndVertices() {
        return graphComponentTest.numberOfEdgesAndVertices();
    }

    @Override
    public SubGraphPojo wholeGraphAroundDefaultCenterVertex() {
        return graphComponentTest.wholeGraphAroundDefaultCenterVertex();
    }


    @Override
    public User user() {
        return graphComponentTest.user();
    }

    @Override
    public void user(User user) {
        graphComponentTest.user(user);
    }

    @Override
    public UserGraph userGraph() {
        return graphComponentTest.userGraph();
    }

    @Override
    public VertexOperator vertexA() {
        return graphComponentTest.vertexA();
    }

    @Override
    public void setDefaultVertexAkaVertexA(VertexOperator vertexA) {
        graphComponentTest.setDefaultVertexAkaVertexA(vertexA);
    }

    @Override
    public VertexOperator vertexB() {
        return graphComponentTest.vertexB();
    }

    @Override
    public VertexOperator vertexC() {
        return graphComponentTest.vertexC();
    }

    @Override
    public VertexOperator vertexOfAnotherUser() {
        return graphComponentTest.vertexOfAnotherUser();
    }


    @Override
    public SubGraphOperator wholeGraph() {
        return graphComponentTest.wholeGraph();
    }

    @Override
    public void removeWholeGraph() {
        graphComponentTest.removeWholeGraph();
    }


    @Override
    public VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return graphComponentTest.vertexInWholeConnectedGraph(vertex);
    }

    @Override
    public EdgePojo edgeInWholeGraph(Edge edge) {
        return graphComponentTest.edgeInWholeGraph(edge);
    }
}
