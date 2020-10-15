/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import guru.bubl.module.model.ModelModule;
import guru.bubl.module.model.ModelTestModule;
import guru.bubl.module.model.ModelTestScenarios;
import guru.bubl.module.model.test.GraphComponentTest;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import guru.bubl.module.neo4j_graph_manipulator.graph.embedded.admin.Neo4jModuleForTests;

public class JsTestScenariosStarter {

    protected static Injector injector;

    public static void main(String[] args) throws Exception {
        injector = Guice.createInjector(
                Neo4jModuleForTests.usingEmbedded(),
                Neo4jModule.usingEmbedded(),
                ModelModule.forTesting(),
                new ModelTestModule(),
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        requireBinding(ModelTestScenarios.class);
                    }
                }
        );
        injector.injectMembers(JsTestScenariosBuilder.class);
        JsTestScenariosBuilder jsTestScenariosBuilder = injector.getInstance(
                JsTestScenariosBuilder.class
        );
        jsTestScenariosBuilder.build(injector);
        System.exit(0);
    }

}
