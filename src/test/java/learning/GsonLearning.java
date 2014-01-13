package learning;

import com.google.gson.Gson;
import org.junit.Test;
import org.triple_brain.module.model.FriendlyResource;
import org.triple_brain.module.model.graph.AdaptableGraphComponentTest;

/*
* Copyright Mozilla Public License 1.1
*/
public class GsonLearning extends AdaptableGraphComponentTest{
    @Test
    public void can_convert_vertex()throws Exception{
        Gson gson = new Gson();
        String json = gson.toJson(vertexA, FriendlyResource.class);
        System.out.println(
                json
        );
    };
}
