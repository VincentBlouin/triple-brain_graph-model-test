/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils;

import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.FriendlyResourceFactory;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.Edge;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.identification.IdentificationFactory;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.pattern.PatternUserFactory;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.subgraph.SubGraphForker;
import guru.bubl.module.model.graph.subgraph.SubGraphForkerFactory;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.Vertex;
import guru.bubl.module.model.graph.vertex.VertexInSubGraph;
import guru.bubl.module.model.graph.vertex.VertexInSubGraphPojo;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.meta.UserMetasOperatorFactory;
import guru.bubl.module.model.suggestion.SuggestionPojo;
import guru.bubl.module.model.test.SubGraphOperator;
import guru.bubl.module.model.test.scenarios.TestScenarios;
import guru.bubl.module.model.test.scenarios.VerticesCalledABAndC;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.UserGraphFactoryNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.WholeGraphNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.extractor.subgraph.SubGraphExtractorFactoryNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.vertex.VertexFactoryNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.search.GraphIndexerNeo4j;
import guru.bubl.module.neo4j_graph_manipulator.graph.search.GraphSearchNeo4j;
import guru.bubl.module.repository.user.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.graphdb.Transaction;

import javax.inject.Inject;
import java.net.URI;
import java.sql.Statement;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ModelTestResources {

    @Inject
    protected UserMetasOperatorFactory userMetasOperatorFactory;

    @Inject
    protected CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    @Inject
    protected CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @Inject
    protected FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    protected Driver driver;

    @Inject
    public ModelTestScenarios modelTestScenarios;

    @Inject
    protected TestScenarios testScenarios;

    @Inject
    protected SubGraphExtractorFactoryNeo4j neo4jSubGraphExtractorFactory;

    @Inject
    public WholeGraphNeo4j wholeGraph;

    @Inject
    protected EdgeFactory edgeFactory;

    @Inject
    protected VertexFactoryNeo4j vertexFactory;

    @Inject
    protected UserGraphFactoryNeo4j neo4jUserGraphFactory;

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected GraphSearchNeo4j graphSearch;

    @Inject
    protected GraphIndexerNeo4j graphIndexer;

    @Inject
    protected IdentificationFactory identificationFactory;

    @Inject
    SubGraphForkerFactory subGraphForkerFactory;

    @Inject
    protected PatternUserFactory patternUserFactory;

    @Inject
    protected UserRepository userRepository;

    protected VertexOperator vertexA;
    protected VertexOperator vertexB;
    protected VertexOperator vertexC;

    protected static User user;

    protected static User anotherUser;
    protected static UserGraph anotherUserGraph;
    protected static VertexOperator vertexOfAnotherUser;

    protected static SubGraphForker forker;
    protected static SubGraphForker anotherUserForker;

    private Transaction transaction;

    protected UserGraph userGraph;

    protected User thirdUser;
    protected VertexOperator thirdUserVertex;

    @Before
    public void before() {
        ModelTestRunner.injector.injectMembers(this);
//        transaction = ModelTestRunner.graphDatabaseService.beginTx();
        removeAllUsers();
        user = User.withEmail(
                "roger.lamothe@example.org"
        ).setUsername("roger_lamothe").setPreferredLocales("[en]").password("12345678");
        user = userRepository.createUser(user);
        forker = subGraphForkerFactory.forUser(user);
        anotherUser = User.withEmail(
                "colette.armande@example.org"
        ).setUsername("colette_armande").setPreferredLocales("[fr]").password("12345678");
        userRepository.createUser(anotherUser);
        anotherUserForker = subGraphForkerFactory.forUser(anotherUser);
        userGraph = neo4jUserGraphFactory.withUser(user);
        VerticesCalledABAndC verticesCalledABAndC = testScenarios.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(
                userGraph
        );
        vertexA = verticesCalledABAndC.vertexA();
        vertexB = verticesCalledABAndC.vertexB();
        vertexC = verticesCalledABAndC.vertexC();
        anotherUserGraph = neo4jUserGraphFactory.withUser(anotherUser);
        vertexOfAnotherUser = vertexFactory.withUri(
                anotherUserGraph.createVertex().uri()
        );
        vertexOfAnotherUser.label("vertex of another user");
    }

    @After
    public void after() {
//        transaction.failure();
//        transaction.close();
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
        return wholeGraphAroundDefaultCenterVertex().vertexWithIdentifier(
                vertex.uri()
        );
    }

    public Map<URI, SuggestionPojo> suggestionsToMap(SuggestionPojo... suggestions) {
        return modelTestScenarios.suggestionsToMap(suggestions);
    }

    protected EdgePojo edgeInWholeGraph(Edge edge) {
        return wholeGraphAroundDefaultCenterVertex().edgeWithIdentifier(
                edge.uri()
        );
    }

    public User user() {
        return user;
    }

    protected SubGraphOperator wholeGraph() {
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

    protected void testThatRemovingGraphElementRemovesTheNumberOfReferencesToItsIdentification(GraphElementOperator graphElement) {
        IdentifierPojo computerScientist = graphElement.addMeta(
                modelTestScenarios.computerScientistType()
        ).values().iterator().next();
        IdentifierPojo personIdentification = graphElement.addMeta(
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

    protected VertexInSubGraphPojo getVertexWithLabel(SubGraphPojo subGraph, String label) {
        VertexInSubGraphPojo vertexWithLabel = null;
        for (VertexInSubGraphPojo vertex : subGraph.vertices().values()) {
            if (vertex.label().equals(label)) {
                vertexWithLabel = vertex;
            }
        }
        return vertexWithLabel;
    }

    protected SchemaOperator createSchema() {
        return userGraph.schemaOperatorWithUri(
                userGraph.createSchema().uri()
        );
    }

    protected void removeAllUsers() {
        try ( Session session = driver.session() ){
            session.run(
                    "MATCH (n:User) DETACH DELETE n"
            );
        }
    }

    protected void setupThirdUser(){
        thirdUser = User.withEmail(
                "tres.usuario@example.org"
        ).setUsername("tres").setPreferredLocales("[es]").password("12345678");
        thirdUser = userRepository.createUser(thirdUser);
        thirdUserVertex = vertexFactory.withUri(
                neo4jUserGraphFactory.withUser(thirdUser).createVertex().uri()
        );
        thirdUserVertex.label("vértice");
    }
}