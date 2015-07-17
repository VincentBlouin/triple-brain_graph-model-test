/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.module.model.graph;

import com.google.common.collect.ImmutableSet;
import guru.bubl.module.utils.ModelTestResources;
import org.junit.Test;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.Image;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class FriendlyResourceTest extends ModelTestResources {

    @Test
    public void an_exception_is_thrown_when_creating_with_empty_uri() {
        URI emptyUri = URI.create("");
        try {
            friendlyResourceFactory.withUri(
                    emptyUri
            );
            fail();
        } catch (Exception e) {
            //continue
        }
    }

    @Test
    public void setting_null_label_converts_to_empty_string() {
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
    public void setting_null_comment_converts_to_empty_string() {
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
    public void can_add_images() {
        Image image1 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Image image2 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_2")
        );
        Set<Image> images = ImmutableSet.of(
                image1,
                image2
        );
        vertexA.addImages(
                images
        );
        images = vertexA.images();
        assertThat(
                images.size(),
                is(2)
        );
        assertTrue(
                images.contains(image1)
        );
        assertTrue(
                images.contains(image2)
        );
    }

    @Test
    public void adding_a_set_of_images_does_not_erase_previous() {
        Image image1 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Set<Image> images = ImmutableSet.of(
                image1
        );
        vertexA.addImages(images);
        assertThat(
                vertexA.images().size(),
                is(1)
        );
        Image image2 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_2")
        );
        images = ImmutableSet.of(
                image2
        );
        vertexA.addImages(images);
        assertThat(
                vertexA.images().size(),
                is(2)
        );
        assertTrue(
                vertexA.images().contains(image1)
        );
        assertTrue(
                vertexA.images().contains(image2)
        );
    }

    @Test
    public void cannot_associate_to_same_image_twice() {
        Image image1 = Image.withBase64ForSmallAndUriForBigger(
                UUID.randomUUID().toString(),
                URI.create("/large_1")
        );
        Set<Image> images = ImmutableSet.of(
                image1
        );
        vertexA.addImages(
                images
        );
        images = vertexA.images();
        assertThat(
                images.size(), is(1)
        );
        vertexA.addImages(images);
        assertThat(
                images.size(), is(1)
        );
    }

//    @Test
//    @Ignore("the thinking over this functionality isn't over")
//    public void can_get_uris_that_identify_to_resource(){
//
//    }
}
