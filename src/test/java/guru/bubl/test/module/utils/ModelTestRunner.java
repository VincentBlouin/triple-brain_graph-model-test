/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.model.ModelModule;
import guru.bubl.module.model.ModelTestModule;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.neo4j.graphdb.GraphDatabaseService;

public class ModelTestRunner {
    public static Injector injector;


    public static GraphDatabaseService graphDatabaseService;

    @BeforeClass
    public static void realBeforeClass() {
        injector = Guice.createInjector(
                Neo4jModule.forTestingUsingEmbedded(),
                ModelModule.forTesting(),
                new ModelTestModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        requireBinding(ModelTestScenarios.class);
                    }
                }
        );
        graphDatabaseService = injector.getInstance(GraphDatabaseService.class);
        injector.getInstance(GraphComponentTest.class)
                .beforeClass();
    }

    @AfterClass
    public static void realAfterClass(){
        graphDatabaseService.shutdown();
        Neo4jModule.clearDb();
    }
}
