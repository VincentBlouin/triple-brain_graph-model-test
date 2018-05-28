/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import com.google.inject.Inject;
import guru.bubl.module.model.FriendlyResource;
import guru.bubl.module.model.center_graph_element.CenterGraphElement;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.content.project.ProjectContentFactory;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONArray;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ProjectContentTest extends ModelTestResources {

    @Inject
    ProjectContentFactory projectContentFactory;

    @Test
    public void adds_content() {
        List<GraphElementSearchResult> searchResults = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "Project",
                user
        );
        assertTrue(
                searchResults.isEmpty()
        );
        projectContentFactory.forUserGraph(userGraph).add();
        searchResults = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "Project",
                user
        );
        assertFalse(
                searchResults.isEmpty()
        );
    }

    @Test
    public void content_is_in_right_locale() {
        projectContentFactory.forUserGraph(userGraph).add();
        List<GraphElementSearchResult> searchResults = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "Projet",
                user
        );
        assertTrue(
                searchResults.isEmpty()
        );
        user.setPreferredLocales(
                new JSONArray().put(
                        "fr_CA"
                ).toString()
        );
        projectContentFactory.forUserGraph(userGraph).add();
        searchResults = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "Projet",
                user
        );
        assertFalse(
                searchResults.isEmpty()
        );
    }

    @Test
    public void center_is_in_central_bubbles() {
        FriendlyResource center = projectContentFactory.forUserGraph(userGraph).add();
        assertThat(
                centerGraphElementOperatorFactory.usingFriendlyResource(
                        center
                ).getLastCenterDate(),
                is(notNullValue())
        );
    }
}
