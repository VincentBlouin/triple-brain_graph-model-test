/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.module.model.graph;

import org.triple_brain.module.model.User;
import org.triple_brain.module.model.UserUris;
import org.triple_brain.module.model.suggestion.SuggestionPojo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

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
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://xmlns.com/foaf/0.1/name"),
                        "Name"
                ),
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/type/text"),
                        "Text"
                ),
                "http://xmlns.com/foaf/0.1/Person",
                user
        );
    }

    public SuggestionPojo nameSuggestionFromSymbolIdentification(User user) {
        String symbolUri = "http://rdf.freebase.com/rdf/m/09ddf";
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://xmlns.com/foaf/0.1/name"),
                        "Name"
                ),
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/type/text"),
                        "Text"
                ),
                symbolUri,
                user
        );
    }

    public SuggestionPojo startDateSuggestionFromEventIdentification(User user) {
        return SuggestionPojo.forSameAsTypeAndOrigin(
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/time/event/start_date"),
                        "Start date"
                ),
                new FriendlyResourcePojo(
                        URI.create("http://rdf.freebase.com/rdf/type/datetime"),
                        "Date"
                ),
                "http://rdf.freebase.com/rdf/time/event",
                user
        );
    }

    public Map<URI, SuggestionPojo> suggestionsToMap(SuggestionPojo ... suggestions){
        Map<URI, SuggestionPojo> suggestionPojoMap = new HashMap<>();
        for(SuggestionPojo suggestionPojo : suggestions){
            suggestionPojoMap.put(
                    suggestionPojo.uri(),
                    suggestionPojo
            );
        }
        return suggestionPojoMap;
    }
}
