package org.triple_brain.module.model.graph;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.neo4j.Neo4JTestModule;
import org.triple_brain.module.model.graph.scenarios.TestScenarios;

import javax.inject.Inject;

/*
* Copyright Mozilla Public License 1.1
*/
public class AdaptableGraphComponentTest implements GraphComponentTest {

    protected UserGraph userGraph;

    protected Vertex vertexA;
    protected Vertex vertexB;
    protected Vertex vertexC;

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    private GraphComponentTest graphComponentTest;

    @Inject
    protected GraphFactory graphMaker;

    protected static Injector injector;


    public void beforeClass(){}

    @BeforeClass
    public static void realBeforeClass() {
        injector = Guice.createInjector(
                new Neo4JTestModule()
        );
        injector.getInstance(GraphComponentTest.class)
                .beforeClass();
    }

    @Before
    public void before(){
        injector.injectMembers(this);
        graphComponentTest.before();
        userGraph = userGraph();
        vertexA = vertexA();
        vertexB = vertexB();
        vertexC = vertexC();
    }

    @After
    public void after(){
        graphComponentTest.after();
    }

    @Override
    public void afterClass(){}

    @AfterClass
    public static void realAfterClass(){
        injector.getInstance(GraphComponentTest.class)
                .afterClass();
    }

    @Override
    public int numberOfEdgesAndVertices() {
        return graphComponentTest.numberOfEdgesAndVertices();
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
    public Vertex vertexA() {
        return graphComponentTest.vertexA();
    }

    @Override
    public Vertex vertexB() {
        return graphComponentTest.vertexB();
    }

    @Override
    public Vertex vertexC() {
        return graphComponentTest.vertexC();
    }

    @Override
    public SubGraph wholeGraph() {
        return graphComponentTest.wholeGraph();
    }

    @Override
    public void removeWholeGraph() {
        graphComponentTest.removeWholeGraph();
    }

    @Override
    public boolean graphContainsLabel(String label) {
        return graphComponentTest.graphContainsLabel(label);
    }
}
