package org.triple_brain.module.model.graph;

import org.junit.Test;
import org.triple_brain.module.model.FriendlyResourceFactory;

import javax.inject.Inject;
import java.net.URI;

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
    //todo
    public void resources_label_are_associated_to_a_locale(){
        GraphElement vertexAGraphElement = vertexA;
        vertexAGraphElement.label();
    }

}
