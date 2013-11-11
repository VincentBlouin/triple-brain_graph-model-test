package org.triple_brain.module.model.graph.neo4j;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseSetting;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.kernel.logging.BufferingLogger;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.WholeGraph;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.suggestion.Suggestion;
import org.triple_brain.module.model.suggestion.SuggestionFactory;
import org.triple_brain.module.neo4j_graph_manipulator.graph.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class Neo4JTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WholeGraph.class).to(Neo4JWholeGraph.class);
        GraphDatabaseService graphDb = new TestGraphDatabaseFactory()
                .newImpermanentDatabaseBuilder()
                .setConfig(GraphDatabaseSettings.node_keys_indexable, Neo4JUserGraph.URI_PROPERTY_NAME)
                .setConfig(GraphDatabaseSettings.node_auto_indexing, GraphDatabaseSetting.TRUE)
                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, Neo4JUserGraph.URI_PROPERTY_NAME )
                .setConfig( GraphDatabaseSettings.relationship_auto_indexing, GraphDatabaseSetting.TRUE )
                .newGraphDatabase();

        bind(GraphDatabaseService.class).toInstance(
                graphDb
        );
        bind(ExecutionEngine.class).toInstance(
                new ExecutionEngine(graphDb, new BufferingLogger())
        );

        FactoryModuleBuilder factoryModuleBuilder = new FactoryModuleBuilder();

        install(factoryModuleBuilder
                .build(Neo4JEdgeFactory.class));

        install(factoryModuleBuilder
                .build(Neo4JUserGraphFactory.class));

        install(factoryModuleBuilder
                .implement(VertexInSubGraph.class, Neo4JVertexInSubGraph.class)
                .build(VertexFactory.class));

        install(factoryModuleBuilder
                .implement(Edge.class, Neo4JEdge.class)
                .build(EdgeFactory.class));

        install(factoryModuleBuilder
                .build(Neo4JVertexFactory.class));

        install(factoryModuleBuilder
                .build(Neo4JSubGraphExtractorFactory.class));

        install(factoryModuleBuilder
                .build(Neo4JGraphElementFactory.class));

        install(factoryModuleBuilder
                .implement(FriendlyResource.class, Neo4JFriendlyResource.class)
                .build(FriendlyResourceFactory.class)
        );
        install(factoryModuleBuilder
                .build(Neo4JFriendlyResourceFactory.class)
        );
        install(factoryModuleBuilder
                .implement(Suggestion.class, Neo4JSuggestion.class)
                .build(SuggestionFactory.class)
        );
        install(factoryModuleBuilder
                .build(Neo4JSuggestionFactory.class)
        );
        install(factoryModuleBuilder
                .build(Neo4JSuggestionOriginFactory.class)
        );
        bind(GraphComponentTest.class).toInstance(
                new Neo4JGraphComponentTest()
        );
        bind(new TypeLiteral<ReadableIndex<Node>>() {
        }).toInstance(
                graphDb.index()
                        .getNodeAutoIndexer()
                        .getAutoIndex()
        );

        bind(new TypeLiteral<ReadableIndex<Relationship>>() {
        }).toInstance(
                graphDb.index()
                        .getRelationshipAutoIndexer()
                        .getAutoIndex()
        );

        bind(GraphFactory.class).to(Neo4JGraphFactory.class);
        requireBinding(Neo4JUtils.class);
    }
}
