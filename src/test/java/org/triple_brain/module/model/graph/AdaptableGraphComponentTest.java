package org.triple_brain.module.model.graph;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;
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

    @Inject
    ModelTestScenarios modelTestScenarios;

    protected static Injector injector;


    public void beforeClass(){}

    @BeforeClass
    public static void realBeforeClass() {
        injector = Guice.createInjector(
                new Neo4JTestModule(),
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
        injector.getInstance(GraphDatabaseService.class)
                .shutdown();
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
    public void setDefaultVertexAkaVertexA(Vertex vertexA) {
        graphComponentTest.setDefaultVertexAkaVertexA(vertexA);
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

    @Override
    public VertexInSubGraph vertexInWholeGraph(Vertex vertex) {
        return graphComponentTest.vertexInWholeGraph(vertex);
    }

    protected JSONArray verticesAsArray(JSONObject verticesAsObject){
        JSONArray verticesAsArray = new JSONArray();
        JSONArray keys = verticesAsObject.names();
        for(int i = 0; i < keys.length(); i++){
            try{
                JSONObject vertex = verticesAsObject.getJSONObject(keys.getString(i));
                verticesAsArray.put(vertex);
            }catch(JSONException e){
                throw new RuntimeException(e);
            }
        }
        return verticesAsArray;
    }
}
