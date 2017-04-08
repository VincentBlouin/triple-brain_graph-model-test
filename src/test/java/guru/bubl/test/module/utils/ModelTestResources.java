/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils;

import guru.bubl.module.model.FriendlyResourceFactory;
import guru.bubl.module.model.IdentifiedTo;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphForker;
import guru.bubl.module.model.graph.subgraph.SubGraphForkerFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.identification.IdentificationFactory;
import guru.bubl.module.model.graph.identification.IdentificationPojo;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.suggestion.SuggestionPojo;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.model.test.scenarios.VerticesCalledABAndC;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.Neo4jUserGraphFactory;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.Neo4jWholeGraph;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.extractor.subgraph.Neo4jSubGraphExtractorFactory;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.vertex.Neo4jVertexFactory;
import guru.bubl.module.neo4j_graph_manipulator.graph.search.Neo4jGraphIndexer;
import guru.bubl.module.neo4j_graph_manipulator.graph.search.Neo4jGraphSearch;
import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import javax.inject.Inject;
import java.net.URI;
import java.sql.Connection;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ModelTestResources {

    @Inject
    public CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    @Inject
    public CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @Inject
    public IdentifiedTo identifiedTo;

    @Inject
    public FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    protected GraphFactory graphMaker;

    @Inject
    public ModelTestScenarios modelTestScenarios;

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    protected Neo4jSubGraphExtractorFactory neo4jSubGraphExtractorFactory;

    @Inject
    public Neo4jWholeGraph wholeGraph;

    @Inject
    GraphDatabaseService graphDatabaseService;

    @Inject
    protected EdgeFactory edgeFactory;

    @Inject
    protected Neo4jVertexFactory vertexFactory;

    @Inject
    protected Neo4jUserGraphFactory neo4jUserGraphFactory;

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    Connection connection;

    @Inject
    protected Neo4jGraphSearch graphSearch;

    @Inject
    protected Neo4jGraphIndexer graphIndexer;

    @Inject
    protected IdentificationFactory identificationFactory;

    @Inject
    SubGraphForkerFactory subGraphForkerFactory;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;

    protected static User user;

    protected static User anotherUser;
    protected static UserGraph anotherUserGraph;
    protected static VertexOperator vertexOfAnotherUser;

    protected static SubGraphForker forker;
    protected static SubGraphForker anotherUserForker;

    protected Transaction transaction;

    protected UserGraph userGraph;

    @Before
    public void before() {
        ModelTestRunner.injector.injectMembers(this);
        transaction = ModelTestRunner.graphDatabaseService.beginTx();
        user = User.withEmail(
                "roger.lamothe@example.org"
        ).setUsername("roger_lamothe");
        forker = subGraphForkerFactory.forUser(user);
        anotherUser = User.withEmail(
                "colette.armande@example.org"
        ).setUsername("colette_armande");
        anotherUserForker = subGraphForkerFactory.forUser(anotherUser);
        userGraph = neo4jUserGraphFactory.withUser(user);
        VerticesCalledABAndC verticesCalledABAndC = testScenarios.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(
                userGraph
        );
        vertexA = verticesCalledABAndC.vertexA();
        vertexB = verticesCalledABAndC.vertexB();
        vertexC = verticesCalledABAndC.vertexC();
        anotherUserGraph = neo4jUserGraphFactory.withUser(anotherUser);
        graphFactory.createForUser(anotherUserGraph.user());
        vertexOfAnotherUser = anotherUserGraph.defaultVertex();
        vertexOfAnotherUser.label("vertex of another user");
    }

    @After
    public void after() {
        transaction.failure();
        transaction.close();
    }

    protected SubGraphPojo wholeGraphAroundDefaultCenterVertex() {
        Integer depthThatShouldCoverWholeGraph = 1000;
        return neo4jSubGraphExtractorFactory.withCenterVertexAndDepth(
                vertexA.uri(),
                depthThatShouldCoverWholeGraph
        ).load();
    }

    protected int numberOfEdgesAndVertices() {
        return numberOfVertices() +
                numberOfEdges();
    }

    protected VertexInSubGraphPojo vertexInWholeConnectedGraph(Vertex vertex) {
        return (VertexInSubGraphPojo) wholeGraphAroundDefaultCenterVertex().vertexWithIdentifier(
                vertex.uri()
        );
    }

    public Map<URI, SuggestionPojo> suggestionsToMap(SuggestionPojo ... suggestions){
        return modelTestScenarios.suggestionsToMap(suggestions);
    }

    public EdgePojo edgeInWholeGraph(Edge edge) {
        return (EdgePojo) wholeGraphAroundDefaultCenterVertex().edgeWithIdentifier(
                edge.uri()
        );
    }

    public User user() {
        return user;
    }

    public SubGraphOperator wholeGraph() {
        return SubGraphOperator.withVerticesAndEdges(
                wholeGraph.getAllVertices(),
                wholeGraph.getAllEdges()
        );
    }

    protected int numberOfVertices() {
        return wholeGraph.getAllVertices().size();
    }

    protected int numberOfEdges() {
        return wholeGraph.getAllEdges().size();
    }

    protected void testThatRemovingGraphElementRemovesTheNumberOfReferencesToItsIdentification(GraphElementOperator graphElement){
        IdentificationPojo computerScientist = graphElement.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        IdentificationPojo personIdentification = graphElement.addMeta(
                modelTestScenarios.person()
        ).values().iterator().next();
        vertexB.addMeta(
                modelTestScenarios.person()
        );
        computerScientist = graphElement.getIdentifications().get(computerScientist.getExternalResourceUri());
        assertThat(
                computerScientist.getNbReferences(),
                is(1)
        );
        personIdentification = graphElement.getIdentifications().get(personIdentification.getExternalResourceUri());
        assertThat(
                personIdentification.getNbReferences(),
                is(2)
        );
        graphElement.remove();
        assertThat(
                identificationFactory.withUri(
                        computerScientist.uri()
                ).getNbReferences(),
                is(0)
        );
        assertThat(
                identificationFactory.withUri(
                        personIdentification.uri()
                ).getNbReferences(),
                is(1)
        );
    }

    protected SchemaOperator createSchema() {
        return userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
    }

}
