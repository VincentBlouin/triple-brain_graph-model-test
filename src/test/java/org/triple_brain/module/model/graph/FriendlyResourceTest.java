package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.Image;

import javax.inject.Inject;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/*
* Copyright Mozilla Public License 1.1
*/
public class FriendlyResourceTest extends AdaptableGraphComponentTest{

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    @Test
    public void an_exception_is_thrown_when_creating_with_empty_uri(){
        URI emptyUri = URI.create("");
        try{
            friendlyResourceFactory.createOrLoadFromUri(
                    emptyUri
            );
            fail();
        }catch(Exception e){
            //continue
        }
    }

    @Test
    public void setting_null_label_converts_to_empty_string(){
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                URI.create("/some_uri"),
                null,
                new HashSet<Image>(),
                "",
                new Date(),
                new Date()
        );
        FriendlyResource friendlyResource = friendlyResourceFactory.createOrLoadUsingPojo(
                friendlyResourcePojo
        );
        assertThat(
                friendlyResource.label(), is("")
        );
    }

    @Test
    public void setting_null_comment_converts_to_empty_string(){
        FriendlyResourcePojo friendlyResourcePojo = new FriendlyResourcePojo(
                URI.create("/some_uri"),
                "",
                new HashSet<Image>(),
                null,
                new Date(),
                new Date()
        );
        FriendlyResource friendlyResource = friendlyResourceFactory.createOrLoadUsingPojo(
                friendlyResourcePojo
        );
        assertThat(
                friendlyResource.comment(), is("")
        );
    }

    @Test
    //todo
    public void resources_label_are_associated_to_a_locale(){
        GraphElement vertexAGraphElement = vertexA;
        vertexAGraphElement.label();
    }

}
