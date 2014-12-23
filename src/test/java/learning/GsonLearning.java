/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package learning;

import com.google.gson.Gson;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.vertex.VertexInSubGraphPojo;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class GsonLearning extends AdaptableGraphComponentTest{
    @Test
    public void can_convert_vertex()throws Exception{
        Gson gson = new Gson();
        IdentificationPojo timBernersLeePojo = modelTestScenarios.timBernersLee();
        vertexB.addSameAs(
                timBernersLeePojo
        );
        SubGraph graph = userGraph.graphWithDepthAndCenterVertexId(
                3,
                vertexB.uri());
        VertexInSubGraphPojo vertexBInSubGraphPojo = (VertexInSubGraphPojo) graph.vertexWithIdentifier(
                vertexB.uri()
        );
        String json = gson.toJson(vertexBInSubGraphPojo);
        System.out.println(
                json
        );
        JSONObject jsonObject = new JSONObject(json);
        VertexInSubGraphPojo vertexInSubGraphPojo = gson.fromJson(json, VertexInSubGraphPojo.class);
        System.out.println(vertexBInSubGraphPojo);
    }

    @Test
    public void map_converts_to_object_and_not_array()throws Exception{
        Map<String, String> map = new HashMap<>();
        map.put("pomme", "avion");
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject(
                gson.toJson(map)
        );
        assertThat(
                jsonObject.getString("pomme"),
                is("avion")
        );
        try{
            new JSONArray(
                    gson.toJson(map)
            );
            fail();
        }catch(Exception e){
           //success
        }
    }

}
