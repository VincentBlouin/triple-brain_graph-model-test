package org.triple_brain.module.model.graph;

import org.triple_brain.module.common_utils.Uris;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.suggestion.SuggestionFactory;
import org.triple_brain.module.model.suggestion.SuggestionOperator;
import org.triple_brain.module.model.suggestion.SuggestionPojo;

import javax.inject.Inject;
import java.net.URI;

/*
* Copyright Mozilla Public License 1.1
*/
public class ModelTestScenarios {

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    @Inject
    SuggestionFactory suggestionFactory;

    public FriendlyResourceOperator personType() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://xmlns.com/foaf/0.1/Person"),
                "Person"
        );
    }

    public FriendlyResourceOperator computerScientistType() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://rdf.freebase.com/rdf/computer.computer_scientist"),
                "Computer Scientist"
        );
    }

    public FriendlyResourceOperator timBernersLee() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://www.w3.org/People/Berners-Lee/card#i"),
                "Tim Berners-Lee"
        );
    }

    public FriendlyResourceOperator creatorPredicate() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://purl.org/dc/terms/creator"),
                "Creator"
        );
    }

    public FriendlyResourceOperator timBernersLeeInFreebase() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://rdf.freebase.com/rdf/en/tim_berners-lee"),
                "Tim Berners-Lee"
        );
    }

    public FriendlyResourceOperator person() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://xmlns.com/foaf/0.1/Person"),
                "Person"
        );
    }

    public FriendlyResourceOperator extraterrestrial() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://rdf.example.org/extraterrestrial"),
                "Extraterrestrial"
        );
    }

    public FriendlyResourceOperator event() {
        return friendlyResourceFactory.createOrLoadUsingUriAndLabel(
                Uris.get("http://rdf.freebase.com/rdf/time/event"),
                "Event"
        );
    }

    public SuggestionOperator nameSuggestion() {
        URI personUri = Uris.get("http://xmlns.com/foaf/0.1/Person");
        return suggestionFromSameAsDomainLabelAndOrigins(
                Uris.get("http://xmlns.com/foaf/0.1/name"),
                personUri,
                "Name",
                personUri
        );
    }

    public SuggestionOperator startDateSuggestion() {
        return suggestionFromSameAsDomainLabelAndOrigins(
                Uris.get("http://rdf.freebase.com/rdf/time/event/start_date"),
                Uris.get("http://rdf.freebase.com/rdf/type/datetime"),
                "Start date",
                Uris.get("http://rdf.freebase.com/rdf/time/event")
        );
    }

    private SuggestionOperator suggestionFromSameAsDomainLabelAndOrigins(
            URI sameAsUri,
            URI domainUri,
            String label,
            URI originUri
    ) {
        return suggestionFactory.createFromPojo(
                SuggestionPojo.fromSameAsAndDomainUriLabelAndOrigin(
                        sameAsUri.toString(),
                        domainUri.toString(),
                        label,
                        originUri.toString()
                )
        );
    }
}
