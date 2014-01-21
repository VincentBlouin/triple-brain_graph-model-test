package org.triple_brain.module.model.graph.neo4j;

import com.hp.hpl.jena.vocabulary.RDFS;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.graphdb.*;
import org.neo4j.kernel.logging.BufferingLogger;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphComponentTest;
import org.triple_brain.module.model.graph.SubGraph;
import org.triple_brain.module.model.graph.SubGraphOperator;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgeFactory;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.scenarios.TestScenarios;
import org.triple_brain.module.model.graph.scenarios.VerticesCalledABAndC;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraph;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphOperator;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jSubGraphExtractorFactory;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jUserGraphFactory;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jVertexFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

/*
* Copyright Mozilla Public License 1.1
*/
public class Neo4JGraphComponentTest implements GraphComponentTest {

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    protected Neo4jSubGraphExtractorFactory neo4jSubGraphExtractorFactory;

    @Inject
    protected EdgeFactory edgeFactory;

    @Inject
    protected GraphDatabaseService graphDb;

    @Inject
    protected Neo4jVertexFactory vertexFactory;

    @Inject
    protected Neo4jUserGraphFactory neo4jUserGraphFactory;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;

    protected static User user;

    protected Transaction transaction;

    protected UserGraph userGraph;

    @Override
    public void beforeClass() {
    }

    @Override
    public void before() {
        user = User.withUsernameEmailAndLocales(
                "roger_lamothe",
                "roger.lamothe@example.org",
                "[fr]"
        );
        startTransaction();
        userGraph = neo4jUserGraphFactory.withUser(user);
        VerticesCalledABAndC verticesCalledABAndC = testScenarios.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(
                userGraph
        );
        vertexA = verticesCalledABAndC.vertexA();
        vertexB = verticesCalledABAndC.vertexB();
        vertexC = verticesCalledABAndC.vertexC();
    }

    @Override
    public void after() {
        transaction.finish();
    }

    @Override
    public void afterClass() {
        graphDb.shutdown();
    }


    @Override
    public int numberOfEdgesAndVertices() {
        return numberOfVertices() +
                numberOfEdges();
    }

    @Override
    public SubGraphOperator wholeGraphAroundDefaultCenterVertex() {
        Integer depthThatShouldCoverWholeGraph = 1000;
        SubGraph subGraph = neo4jSubGraphExtractorFactory.withCenterVertexAndDepth(
                vertexA,
                depthThatShouldCoverWholeGraph
        ).load();
        return SubGraphOperator.withVerticesAndEdges(
                convertVertexSetToOperator(subGraph.vertices()),
                convertEdgeSetToOperator(subGraph.edges())
        );
    }


    @Override
    public SubGraphOperator wholeGraph() {
        return SubGraphOperator.withVerticesAndEdges(
                allVertices(),
                allEdges()
        );
    }

    @Override
    public void removeWholeGraph() {
        for (VertexOperator vertex : allVertices()) {
            vertex.remove();
        }
    }

    @Override
    public boolean graphContainsLabel(String label) {
        return anyNodeContainsLabel(label) ||
                anyRelationshipContainsLabel(label);
    }

    protected boolean anyNodeContainsLabel(String label) {
        for (Node node : allNodes()) {
            if (hasLabel(node, label)) {
                return true;
            }
        }
        return false;
    }

    protected boolean anyRelationshipContainsLabel(String label) {
        for (Relationship relationship : allRelationships()) {
            if (hasLabel(relationship, label)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasLabel(PropertyContainer propertyContainer, String label) {
        try {
            String labelProperty = RDFS.label.getURI();
            return propertyContainer.hasProperty(
                    labelProperty
            ) &&
                    propertyContainer.getProperty(labelProperty).equals(label);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public void user(User user) {
        this.user = user;
    }

    @Override
    public UserGraph userGraph() {
        return userGraph;
    }

    @Override
    public VertexOperator vertexA() {
        return vertexA;
    }

    @Override
    public void setDefaultVertexAkaVertexA(VertexOperator vertexA) {
        this.vertexA = vertexA;
    }

    @Override
    public VertexOperator vertexB() {
        return vertexB;
    }

    @Override
    public VertexOperator vertexC() {
        return vertexC;
    }

    @Override
    public VertexInSubGraph vertexInWholeGraph(Vertex vertex) {
        return wholeGraphAroundDefaultCenterVertex().vertexWithIdentifier(vertex.uri());
    }

    protected Set<VertexInSubGraphOperator> allVertices() {
        Set<VertexInSubGraphOperator> vertices = new HashSet<VertexInSubGraphOperator>();
        ExecutionEngine engine = new ExecutionEngine(graphDb, new BufferingLogger());
        ExecutionResult result = engine.execute(
                "START n = node(*) " +
                        "MATCH n:vertex " +
                        "RETURN n"
        );
        while (result.hasNext()) {
            VertexInSubGraphOperator vertex = vertexFactory.createOrLoadUsingNode(
                    (Node) result.next().get("n").get()
            );
            vertices.add(
                    vertex
            );
        }
        return vertices;
    }

    protected Set<Node> allNodes() {
        Set<Node> nodes = new HashSet<Node>();
        ExecutionEngine engine = new ExecutionEngine(graphDb, new BufferingLogger());
        ExecutionResult result = engine.execute(
                "START n = node(*) " +
                        " RETURN n"
        );
        while (result.hasNext()) {
            nodes.add(
                    (Node) result.next().get("n").get()
            );
        }
        return nodes;
    }

    protected Set<Relationship> allRelationships() {
        Set<Relationship> relationships = new HashSet<Relationship>();
        ExecutionEngine engine = new ExecutionEngine(graphDb, new BufferingLogger());
        ExecutionResult result = engine.execute(
                "START r = relationship(*) " +
                        " RETURN r"
        );
        while (result.hasNext()) {
            relationships.add(
                    (Relationship) result.next().get("r").get()
            );
        }
        return relationships;
    }

    protected int numberOfVertices() {
        return allVertices().size();
    }

    protected Set<EdgeOperator> allEdges() {
        Set<EdgeOperator> edges = new HashSet<EdgeOperator>();
        for (VertexOperator vertex : allVertices()) {
            edges.addAll(
                    vertex.connectedEdges()
            );
        }
        return edges;
    }

    protected int numberOfEdges() {
        return allEdges().size();
    }

    private void commit() {
        finishTransaction();
        startTransaction();
    }

    private void startTransaction() {
        transaction = graphDb.beginTx();
    }

    private void finishTransaction() {
        transaction.success();
    }

    private Set<VertexInSubGraphOperator> convertVertexSetToOperator(Set<VertexInSubGraph> vertices) {
        Set<VertexInSubGraphOperator> verticesOperator = new HashSet<VertexInSubGraphOperator>();
        for (VertexInSubGraph vertexInSubGraph : vertices) {
            VertexInSubGraphOperator vertexInSubGraphOperator = vertexFactory.createOrLoadUsingUri(
                    vertexInSubGraph.uri()
            );
            vertexInSubGraphOperator.setMinDistanceFromCenterVertex(
                    vertexInSubGraph.minDistanceFromCenterVertex()
            );
            verticesOperator.add(
                    vertexInSubGraphOperator
            );
        }
        return verticesOperator;
    }

    private Set<EdgeOperator> convertEdgeSetToOperator(Set<Edge> edges) {
        Set<EdgeOperator> edgesOperator = new HashSet<EdgeOperator>();
        for(Edge edge: edges){
            edgesOperator.add(
                    edgeFactory.createOrLoadUsingUri(
                            edge.uri()
                    ));
        }
        return edgesOperator;
    }
}
