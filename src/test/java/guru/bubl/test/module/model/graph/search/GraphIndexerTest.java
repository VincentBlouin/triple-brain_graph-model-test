/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.search;

import com.google.inject.Inject;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphElementPojo;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.GraphIndexer;
import guru.bubl.test.module.utils.search.Neo4jSearchRelatedTest;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class GraphIndexerTest extends Neo4jSearchRelatedTest {

    @Inject
    GraphIndexer graphIndexer;

    @Test
    public void index_vertex_sets_its_private_surround_graph() {
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                user
        ).iterator().next();
        assertTrue(
                vertexSearchResult.getContext().isEmpty()
        );
        graphIndexer.indexVertex(vertexB);
        vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                user
        ).iterator().next();
        assertFalse(
                vertexSearchResult.getContext().isEmpty()
        );
    }

    @Test
    public void limits_the_context_size_of_vertices() {
        for(int i = 0; i < 5; i++){
            vertexFactory.withUri(
                    vertexB.addVertexAndRelation().destinationVertex().uri()
            ).label("vertex " + i);
        }
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(5)
        );
    }

    @Test
    public void filters_empty_label_from_context() {
        for(int i = 0; i < 5; i++){
            vertexB.addVertexAndRelation();
        }
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(2)
        );
    }

    @Test
    public void context_can_have_quotes() {
        vertexA.label("\"some\" label");
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(2)
        );
    }

    @Test
    public void surround_graph_does_not_include_all_vertices() {
        graphIndexer.indexVertex(vertexA);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexA.label(),
                user
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(1)
        );
    }

    @Test
    public void index_vertex_sets_its_public_surround_graph() {
        vertexB.makePublic();
        vertexA.makePublic();
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                anotherUser
        ).iterator().next();
        assertThat(
                vertexSearchResult.getContext().size(),
                is(1)
        );
        vertexC.makePublic();
        graphIndexer.indexVertex(vertexB);
        vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                anotherUser
        ).iterator().next();
        assertFalse(
                vertexSearchResult.getContext().isEmpty()
        );
        assertThat(
                vertexSearchResult.getContext().size(),
                is(2)
        );
    }

    @Test
    public void context_does_not_include_self_vertex() {
        graphIndexer.indexVertex(vertexB);
        GraphElementSearchResult vertexSearchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                vertexB.label(),
                user
        ).iterator().next();
        assertFalse(
                vertexSearchResult.getContext().containsKey(
                        vertexB.uri()
                )
        );
    }

    @Test
    public void index_schema_sets_the_properties_as_context() {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        graphIndexer.indexSchema(userGraph.schemaPojoWithUri(
                schema.uri()
        ));
        GraphElementSearchResult searchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        ).iterator().next();
        assertTrue(
                searchResult.getContext().isEmpty()
        );
        schema.addProperty().label("property 1");
        schema.addProperty().label("property 2");
        graphIndexer.indexSchema(userGraph.schemaPojoWithUri(
                schema.uri()
        ));
        searchResult = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "schema",
                user
        ).iterator().next();
        assertFalse(
                searchResult.getContext().isEmpty()
        );
    }

    @Test
    public void index_relation_sets_source_and_destination_vertex_as_context() {
        EdgeOperator edgeAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        GraphElementSearchResult searchResult = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                edgeAAndB.label(),
                user
        ).iterator().next();
        assertTrue(
                searchResult.getContext().isEmpty()
        );
        graphIndexer.indexRelation(edgeAAndB);
        searchResult = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                edgeAAndB.label(),
                user
        ).iterator().next();
        assertTrue(
                searchResult.getContext().containsValue(
                        vertexA.label()
                )
        );
        assertTrue(
                searchResult.getContext().containsValue(
                        vertexB.label()
                )
        );
    }

    @Test
    public void public_context_of_relation_is_empty_if_relation_is_private() {
        vertexA.makePublic();
        vertexB.makePublic();
        EdgeOperator edgeAAndB = vertexA.getEdgeThatLinksToDestinationVertex(vertexB);
        graphIndexer.indexRelation(edgeAAndB);
        GraphElementSearchResult searchResult = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                edgeAAndB.label(),
                anotherUser
        ).iterator().next();
        assertFalse(
                searchResult.getContext().isEmpty()
        );
        vertexA.makePrivate();
        graphIndexer.indexRelation(edgeAAndB);

        List<GraphElementSearchResult> graphElementSearchResults = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                edgeAAndB.label(),
                anotherUser
        );
        assertTrue(
                graphElementSearchResults.isEmpty()
        );
    }

    @Test
    public void index_property_sets_schema_as_context() {
        SchemaOperator schema = createSchema(userGraph.user());
        schema.label("schema1");
        GraphElementOperator property = schema.addProperty();
        property.label("a property");
        SchemaPojo schemaPojo = userGraph.schemaPojoWithUri(
                schema.uri()
        );
        GraphElementPojo propertyPojo = schemaPojo.getProperties().values().iterator().next();
        graphIndexer.indexProperty(propertyPojo, schemaPojo);
        GraphElementSearchResult graphElementSearchResult = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
                "a property",
                user
        ).iterator().next();
        assertTrue(
                graphElementSearchResult.getContext().containsValue(
                        "schema1"
                )
        );
    }
}