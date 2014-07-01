package org.triple_brain.module.model.graph;

import com.hp.hpl.jena.vocabulary.RDFS;
import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.suggestion.*;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jUtils;

import javax.inject.Inject;
import java.net.URI;

import static org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jRestApiUtils.map;

/*
* Copyright Mozilla Public License 1.1
*/
public class ModelTestScenarios {

    public FriendlyResourcePojo personType() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                "Person"
        );
    }

    public FriendlyResourcePojo computerScientistType() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/computer.computer_scientist"
                ),
                "Computer Scientist"
        );
    }

    public FriendlyResourcePojo timBernersLee() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://www.w3.org/People/Berners-Lee/card#i"
                ),
                "Tim Berners-Lee"
        );
    }

    public FriendlyResourcePojo creatorPredicate() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://purl.org/dc/terms/creator"
                ),
                "Creator"
        );
    }

    public FriendlyResourcePojo timBernersLeeInFreebase() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/en/tim_berners-lee"
                ),
                "Tim Berners-Lee"
        );
    }

    public FriendlyResourcePojo extraterrestrial() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://rdf.example.org/extraterrestrial"
                ),
                "Extraterrestrial"
        );
    }

    public FriendlyResourcePojo person() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                "Person"
        );
    }

    public FriendlyResourcePojo event() {
        return new FriendlyResourcePojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/time/event"
                ),
                "Event"
        );
    }

    public SuggestionPojo nameSuggestionFromPersonIdentification(User user) {
        URI personUri = Uris.get("http://xmlns.com/foaf/0.1/Person");
        return SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                "http://xmlns.com/foaf/0.1/name",
                personUri.toString(),
                "Name",
                personUri.toString(),
                user
        );
    }

    public SuggestionPojo nameSuggestionFromSymbolIdentification(User user) {
        URI symbolUri = Uris.get("http://rdf.freebase.com/rdf/m/09ddf");
        return SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                "http://xmlns.com/foaf/0.1/name",
                symbolUri.toString(),
                "Name",
                symbolUri.toString(),
                user
        );
    }

    public SuggestionPojo startDateSuggestionFromEventIdentification(User user) {
        return SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                "http://rdf.freebase.com/rdf/time/event/start_date",
                "http://rdf.freebase.com/rdf/type/datetime",
                "Start date",
                "http://rdf.freebase.com/rdf/time/event",
                user
        );
    }

}
