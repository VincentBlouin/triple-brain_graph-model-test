package org.triple_brain.module.model.graph.scenarios;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.ReadableIndex;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.ExternalFriendlyResource;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.suggestion.SuggestionOrigin;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.*;

import javax.inject.Inject;
import java.net.URI;
import java.util.UUID;

/*
* Copyright Mozilla Public License 1.1
*/
public class TestScenarios {

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected GraphComponentTest graphComponentTest;

    @Inject
    private ReadableIndex<Node> nodeIndex;

    public static ExternalFriendlyResource personType() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://xmlns.com/foaf/0.1/Person"),
                "Person"
        );
    }

    public static ExternalFriendlyResource computerScientistType() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://rdf.freebase.com/rdf/computer.computer_scientist"),
                "Computer Scientist"
        );
    }

    public static ExternalFriendlyResource timBernersLee() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://www.w3.org/People/Berners-Lee/card#i"),
                "Tim Berners-Lee"
        );
    }

    public static ExternalFriendlyResource timBernersLeeInFreebase() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://rdf.freebase.com/rdf/en.tim_berners-lee"),
                "Tim Berners-Lee"
        );
    }

    public static ExternalFriendlyResource person() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://xmlns.com/foaf/0.1/Person"),
                "Person"
        );
    }

    public static ExternalFriendlyResource extraterrestrial() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://rdf.example.org/extraterrestrial"),
                "Extraterrestrial"
        );
    }

    public static ExternalFriendlyResource event() {
        return ExternalFriendlyResource.withUriAndLabel(
                Uris.get("http://rdf.freebase.com/rdf/time/event"),
                "Event"
        );
    }

    public static Suggestion nameSuggestion() {
        URI personUri = Uris.get("http://xmlns.com/foaf/0.1/Person");
        return Suggestion.withSameAsDomainLabelAndOrigins(
                Uris.get("http://xmlns.com/foaf/0.1/name"),
                personUri,
                "Name",
                SuggestionOrigin.fromIdentificationWithUri(
                        personUri
                )
        );
    }

    public static Suggestion startDateSuggestion() {
        return Suggestion.withSameAsDomainLabelAndOrigins(
                Uris.get("http://rdf.freebase.com/rdf/time/event/start_date"),
                Uris.get("http://rdf.freebase.com/rdf/type/datetime"),
                "Start date",
                SuggestionOrigin.fromIdentificationWithUri(
                        Uris.get("http://rdf.freebase.com/rdf/time/event")
                )
        );
    }

    public VerticesCalledABAndC makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(UserGraph userGraph) {
        graphComponentTest.removeWholeGraph();
        graphFactory.createForUser(userGraph.user());
        Vertex vertexA = userGraph.defaultVertex();
        vertexA.label("vertex A");
        Vertex vertexB = vertexA.addVertexAndRelation().destinationVertex();
        vertexB.label("vertex B");
        Vertex vertexC = vertexB.addVertexAndRelation().destinationVertex();
        vertexC.label("vertex C");
        Edge betweenAAndB = vertexA.edgeThatLinksToDestinationVertex(vertexB);
        betweenAAndB.label("between vertex A and vertex B");
        Edge betweenBAndC = vertexB.edgeThatLinksToDestinationVertex(vertexC);
        betweenBAndC.label("between vertex B and vertex C");
        return new VerticesCalledABAndC(
                vertexA,
                vertexB,
                vertexC
        );
    }

    public VerticesCalledABAndC makeGraphHave3SerialVerticesWithLongLabels(UserGraph userGraph) throws Exception {
        VerticesCalledABAndC verticesCalledABAndC = makeGraphHave3VerticesABCWhereAIsDefaultCenterVertexAndAPointsToBAndBPointsToC(userGraph);
        verticesCalledABAndC.vertexA().label("vertex Azure");
        verticesCalledABAndC.vertexB().label("vertex Bareau");
        verticesCalledABAndC.vertexC().label("vertex Cadeau");
        return verticesCalledABAndC;
    }

    public Vertex addPineAppleVertexToVertex(Vertex vertex) {
        Edge newEdge = vertex.addVertexAndRelation();
        Vertex pineApple = newEdge.destinationVertex();
        pineApple.label("pine Apple");
        return pineApple;
    }

    public static User randomUser() {
        return User.withUsernameAndEmail(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString() + "@example.org"
        );
    }

}
