package org.triple_brain.module.model.graph.neo4j;

import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.triple_brain.module.model.TripleBrainUris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.scenarios.TestScenarios;
import org.triple_brain.module.model.graph.scenarios.VerticesCalledABAndC;
import org.triple_brain.module.neo4j_graph_manipulator.graph.*;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class Neo4JGraphComponentTest implements GraphComponentTest{

    @Inject
    protected TestScenarios testScenarios;

    protected Vertex vertexA;
    protected Vertex vertexB;
    protected Vertex vertexC;

    protected static User user;

    protected Transaction transaction;

    @Inject
    protected GraphDatabaseService graphDb;

    @Inject
    protected Neo4JUserGraphFactory neo4JUserGraphFactory;


    protected UserGraph userGraph;

    @Override
    public void beforeClass() {}

    @Override
    public void before() {
        user = User.withUsernameAndEmail(
                "roger_lamothe",
                "roger.lamothe@example.org"
        );
        transaction = graphDb.beginTx();
        userGraph = neo4JUserGraphFactory.withUser(user);
        VerticesCalledABAndC verticesCalledABAndC = testScenarios.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(
                userGraph
        );
        vertexA = verticesCalledABAndC.vertexA();
        vertexB = verticesCalledABAndC.vertexB();
        vertexC = verticesCalledABAndC.vertexC();
    }

    @Override
    public void after()
    {
        transaction.finish();
    }

    @Override
    public void afterClass(){
        graphDb.shutdown();
    }


    @Override
    public int numberOfEdgesAndVertices(){
        return numberOfVertices() +
                numberOfEdges();
    }

    @Override
    public SubGraph wholeGraph() {
        return Neo4JSubGraph.withVerticesAndEdges(
                allVertices(),
                allEdges()
        );
    }

    @Override
    public void removeWholeGraph() {
        for(Vertex vertex: allVertices()){
            vertex.remove();
        }
    }

    @Override
    public boolean graphContainsLabel(String label) {
        return anyVertexContainsLabel(label) ||
                anyEdgeContainsLabel(label);
    }

    protected boolean anyVertexContainsLabel(String label){
        for(Vertex vertex : wholeGraph().vertices()){
            if(vertex.label().equals(label)){
                return true;
            }
        }
        return false;
    }

    protected boolean anyEdgeContainsLabel(String label){
        for(Edge edge : wholeGraph().edges()){
            if(edge.label().equals(label)){
                return true;
            }
        }
        return false;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public UserGraph userGraph() {
        return userGraph;
    }

    @Override
    public Vertex vertexA() {
        return vertexA;
    }

    @Override
    public Vertex vertexB() {
        return vertexB;
    }

    @Override
    public Vertex vertexC() {
        return vertexC;
    }

    protected Set<Vertex> allVertices(){
        Set<Vertex> vertices = new HashSet<Vertex>();
        ExecutionEngine engine = new ExecutionEngine(graphDb);
        ExecutionResult result = engine.execute(
                "START n = node(*) " +
                        "MATCH n-[:"+
                        Relationships.TYPE+
                        "]->({"+
                        Neo4JUserGraph.URI_PROPERTY_NAME +
                        ":\""+
                        TripleBrainUris.TRIPLE_BRAIN_VERTEX +
                        "\"})" +
                        " RETURN n"
        );
        while(result.hasNext()){
            vertices.add(
                Neo4JVertex.loadUsingNodeOfOwner(
                        (Node) result.next().get("n").get(),
                        user
                )
            );
        }
        return vertices;
    }

    protected int numberOfVertices(){
        return allVertices().size();
    }

    protected Set<Edge> allEdges(){
        Set<Edge> edges = new HashSet<Edge>();
        for(Vertex vertex: allVertices()){
            edges.addAll(
                    vertex.connectedEdges()
            );
        }
        return edges;
    }

    protected  int numberOfEdges(){
        return allEdges().size();
    }

}
