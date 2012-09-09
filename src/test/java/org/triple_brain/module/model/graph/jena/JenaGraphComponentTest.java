package org.triple_brain.module.model.graph.jena;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.junit.Ignore;
import org.triple_brain.graphmanipulator.jena.TripleBrainModel;
import org.triple_brain.graphmanipulator.jena.graph.JenaUserGraph;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.scenarios.TestScenarios;
import org.triple_brain.module.model.graph.scenarios.VerticesCalledABAndC;

import javax.inject.Inject;
import java.util.List;

import static org.triple_brain.graphmanipulator.jena.JenaConnection.closeConnection;

/*
* Copyright Mozilla Public License 1.1
*/
@Ignore
public class JenaGraphComponentTest implements GraphComponentTest {

    public static final Integer DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES = 10;

    @Inject
    protected TestScenarios testScenarios;


    protected JenaUserGraph userGraph;

    @Inject
    private GraphMaker graphMaker;

    protected Vertex vertexA;
    protected Vertex vertexB;
    protected Vertex vertexC;

    protected static User user;

    @Override
    public void beforeClass() {}

    @Override
    public void before(){
        user = User.withUsernameAndEmail(
                "roger_lamothe",
                "roger.lamothe@example.org"
        );
        userGraph = (JenaUserGraph) graphMaker.createForUser(user);
        makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC();
    }

    @Override
    public void after(){}

    @Override
    public void afterClass() {
        try{
            closeConnection();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    protected void makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(){
        VerticesCalledABAndC vertexABAndC = testScenarios.makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(
                graphMaker.createForUser(user)
        );
        vertexA = vertexABAndC.vertexA();
        vertexB = vertexABAndC.vertexB();
        vertexC = vertexABAndC.vertexC();
    }

    @Override
    public boolean graphContainsLabel(String label){
        List<RDFNode> allLabelsInModel = userGraph.model().listObjectsOfProperty(RDFS.label).toList();
        for(RDFNode rdfNode : allLabelsInModel){
            if(rdfNode.asLiteral().getString().equals(label)){
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

    protected Model model(){
        return userGraph.model();
    }

    @Override
    public int numberOfEdgesAndVertices(){
        return model().listSubjectsWithProperty(
                RDF.type,
                TripleBrainModel.withEnglobingModel(model()).TRIPLE_BRAIN_VERTEX()
        ).toList()
                .size() +
                model().listSubjectsWithProperty(
                        RDF.type,
                        TripleBrainModel.withEnglobingModel(model()).TRIPLE_BRAIN_EDGE()
                ).toList()
                        .size();
    }

    @Override
    public SubGraph wholeGraph(){
        return userGraph.graphWithDefaultVertexAndDepth(
                DEPTH_OF_SUB_VERTICES_COVERING_ALL_GRAPH_VERTICES
        );
    }
}
