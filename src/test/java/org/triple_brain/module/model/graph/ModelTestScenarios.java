package org.triple_brain.module.model.graph;

import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.suggestion.SuggestionPojo;

import java.net.URI;

/*
* Copyright Mozilla Public License 1.1
*/
public class ModelTestScenarios {

    public IdentificationPojo personType() {
        return new IdentificationPojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                new FriendlyResourcePojo(
                        "Person"
                )
        );
    }

    public IdentificationPojo computerScientistType() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/computer.computer_scientist"
                ),
                new FriendlyResourcePojo(
                        "Computer Scientist"
                )
        );
    }

    public IdentificationPojo timBernersLee() {
        return new IdentificationPojo(
                URI.create(
                        "http://www.w3.org/People/Berners-Lee/card#i"
                ),
                new FriendlyResourcePojo(
                        "Tim Berners-Lee"
                )
        );
    }

    public IdentificationPojo creatorPredicate() {
        return new IdentificationPojo(
                URI.create(
                        "http://purl.org/dc/terms/creator"
                ),
                new FriendlyResourcePojo(
                        "Creator"
                ));
    }

    public IdentificationPojo timBernersLeeInFreebase() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/en/tim_berners-lee"
                ),
                new FriendlyResourcePojo(                        
                        "Tim Berners-Lee"
                )
        );
    }

    public IdentificationPojo extraterrestrial() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.example.org/extraterrestrial"
                ),
                new FriendlyResourcePojo(
                        "Extraterrestrial"
                )
        );
    }

    public IdentificationPojo person() {
        return new IdentificationPojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                new FriendlyResourcePojo(
                        "Person"
                )
        );
    }

    public IdentificationPojo event() {
        return new IdentificationPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/time/event"
                ),
                new FriendlyResourcePojo(
                        "Event"
                )
        );
    }

    public SuggestionPojo nameSuggestionFromPersonIdentification(User user) {
        URI personUri = Uris.get("http://xmlns.com/foaf/0.1/Person");
        return SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                URI.create("http://xmlns.com/foaf/0.1/name"),
                personUri,
                "Name",
                personUri.toString(),
                user
        );
    }

    public SuggestionPojo nameSuggestionFromSymbolIdentification(User user) {
        URI symbolUri = Uris.get("http://rdf.freebase.com/rdf/m/09ddf");
        return SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                URI.create("http://xmlns.com/foaf/0.1/name"),
                symbolUri,
                "Name",
                symbolUri.toString(),
                user
        );
    }

    public SuggestionPojo startDateSuggestionFromEventIdentification(User user) {
        return SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                URI.create("http://rdf.freebase.com/rdf/time/event/start_date"),
                URI.create("http://rdf.freebase.com/rdf/type/datetime"),
                "Start date",
                "http://rdf.freebase.com/rdf/time/event",
                user
        );
    }

    private URI generateUriForUser(User user) {
        return new UserUris(user).generateIdentificationUri();
    }

}
