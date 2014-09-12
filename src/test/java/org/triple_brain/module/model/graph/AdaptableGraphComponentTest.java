/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

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
import org.triple_brain.module.model.test.SubGraphOperator;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgePojo;
import org.triple_brain.module.model.test.scenarios.TestScenarios;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphPojo;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.test.GraphComponentTest;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jModule;

import javax.inject.Inject;

public class AdaptableGraphComponentTest implements GraphComponentTest {

    protected UserGraph userGraph;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;
    protected VertexOperator vertexOfAnotherUser;

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    private GraphComponentTest graphComponentTest;

    @Inject
    protected GraphFactory graphMaker;

    @Inject
    public ModelTestScenarios modelTestScenarios;

    protected static Injector injector;


    public void beforeClass(){}

    @BeforeClass
    public static void realBeforeClass() {
        injector = Guice.createInjector(
                Neo4jModule.forTestingUsingEmbedded(),
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
        vertexOfAnotherUser = vertexOfAnotherUser();
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
    public SubGraphPojo wholeGraphAroundDefaultCenterVertex() {
        return graphComponentTest.wholeGraphAroundDefaultCenterVertex();
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
    public boolean graphContainsLabel(String label) {
        return graphComponentTest.graphContainsLabel(label);
    }

    @Override
    public VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return graphComponentTest.vertexInWholeConnectedGraph(vertex);
    }

    @Override
    public EdgePojo edgeInWholeGraph(Edge edge) {
        return graphComponentTest.edgeInWholeGraph(edge);
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
