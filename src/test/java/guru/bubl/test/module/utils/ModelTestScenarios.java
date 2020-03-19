/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils;

import guru.bubl.module.model.graph.FriendlyResourcePojo;
import guru.bubl.module.model.graph.tag.TagPojo;

import java.net.URI;

public class ModelTestScenarios {

    public static URI SAME_AS = URI.create("same-as");
    public static URI TYPE = URI.create("type");
    public static URI GENERIC = URI.create("generic");

    public TagPojo computerScientistType() {
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/computer.computer_scientist"
                ),
                new FriendlyResourcePojo(
                        "Computer Scientist"
                )
        );
    }

    public TagPojo timBernersLee() {
        return new TagPojo(
                URI.create(
                        "http://www.w3.org/People/Berners-Lee/card#i"
                ),
                new FriendlyResourcePojo(
                        "Tim Berners-Lee"
                )
        );
    }

    public TagPojo creatorPredicate() {
        return new TagPojo(
                URI.create(
                        "http://purl.org/dc/terms/creator"
                ),
                new FriendlyResourcePojo(
                        "Creator"
                ));
    }

    public TagPojo possessionIdentification() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "Possession"
        );
        friendlyResourcePojo.setComment(
                "In law, possession is the control a person intentionally exercises toward a thing. In all cases, to possess something, a person must have an intention to possess it. A person may be in possession of some property. Like ownership, the possession of things is commonly regulated by states under property law."
        );
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/m/0613q"
                ),
                friendlyResourcePojo
        );
    }

    public TagPojo timBernersLeeInFreebase() {
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/en/tim_berners-lee"
                ),
                new FriendlyResourcePojo(
                        "Tim Berners-Lee"
                )
        );
    }

    public TagPojo extraterrestrial() {
        return new TagPojo(
                URI.create(
                        "http://rdf.example.org/extraterrestrial"
                ),
                new FriendlyResourcePojo(
                        "Extraterrestrial"
                )
        );
    }

    public TagPojo human() {
        return new TagPojo(
                URI.create(
                        "/human"
                ),
                new FriendlyResourcePojo(
                        "Human"
                )
        );
    }

    public TagPojo toDo() {
        return new TagPojo(
                URI.create(
                        "/to-do"
                ),
                new FriendlyResourcePojo(
                        "To do"
                )
        );
    }

    public TagPojo person() {
        return new TagPojo(
                URI.create(
                        "http://xmlns.com/foaf/0.1/Person"
                ),
                new FriendlyResourcePojo(
                        "Person"
                )
        );
    }

    public TagPojo personFromFreebase() {
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/people/person"
                ),
                new FriendlyResourcePojo(
                        "Person"
                )
        );
    }

    public TagPojo location() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "Location"
        );
        friendlyResourcePojo.setComment(
                "The Location type is used for any topic with a fixed location on the planet Earth. It includes geographic features such as oceans and mountains, political entities like cities and man-made objects like buildings.Guidelines for filling in location properties:geolocation: the longitude and latitude (in decimal notation) of the feature, or of the geographical center (centroid) fo the feature.contains and contained by: these properties can be used to show spatial relationships between different locations, such as an island contained by a body of water (which is equivalent to saying the body of water contains the island), a state contained by a country, a mountain within the borders of a national park, etc. For geopolitical locations,   containment two levels up and down is the ideal minimum. For example, the next two levels up for the city of Detroit are Wayne County and the state of Michigan.adjoins: also used to show spatial relations, in this case between locations that share a border.USBG Name: A unique name given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. More information can be found on their website. GNIS ID: A unique id given to geographic features within the U.S. and its territories by the United States Board on Geographic Names. GNIS stands for Geographic Names Information System. More information can be found on their website.GEOnet Feature ID: The UFI (Unique Feature ID) used by GeoNet for features outside of the United States. More information can be found on their website."
        );
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/m/01n7"
                ),
                friendlyResourcePojo
        );
    }

    public TagPojo event() {
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/time/event"
                ),
                new FriendlyResourcePojo(
                        "Event"
                )
        );
    }

    public TagPojo tShirt() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "T-shirt"
        );
        friendlyResourcePojo.setComment(
                "A T-shirt is a style of fabric shirt, named after the T shape of the body and sleeves."
        );
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/m/013s93"
                ),
                friendlyResourcePojo
        );
    }

    public TagPojo book() {
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                "Book"
        );
        friendlyResourcePojo.setComment(
                "medium for a collection of words and/or pictures to represent knowledge or a fictional story, often manifested in bound paper and ink, or in e-books"
        );
        return new TagPojo(
                URI.create(
                        "https://www.wikidata.org/wiki/Q571"
                ),
                friendlyResourcePojo
        );
    }

    public TagPojo startDateIdentification() {
        return new TagPojo(
                URI.create(
                        "http://rdf.freebase.com/rdf/time/event/start_date"
                ),
                new FriendlyResourcePojo(
                        "Start Date"
                )
        );
    }
}
