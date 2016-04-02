/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model.graph.schema;

import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.schema.SchemaList;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.schema.SchemaPojo;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SchemaListTest extends ModelTestResources {

    @Inject
    SchemaList schemaList;

    @Test
    public void can_list() {
        assertThat(
                schemaList.get().size(),
                is(0)
        );
        createSchema();
        assertThat(
                schemaList.get().size(),
                is(1)
        );
    }

    @Test
    public void labels_are_included() {
        createSchema().label(
                "some label"
        );
        SchemaPojo someSchema = schemaList.get().iterator().next();
        assertThat(
                someSchema.label(),
                is("some label")
        );
    }

    @Test
    public void uris_are_included() {
        URI uri = createSchema().uri();
        SchemaPojo someSchema = schemaList.get().iterator().next();
        assertThat(
                someSchema.uri(),
                is(
                        uri
                )
        );
    }

    @Test
    public void properties_label_are_included() {
        GraphElementOperator property = createSchema().addProperty();
        property.label("a property");
        SchemaPojo someSchema = schemaList.get().iterator().next();
        assertThat(
                someSchema.getProperties().values().iterator().next().label(),
                is("a property")
        );
    }
}
