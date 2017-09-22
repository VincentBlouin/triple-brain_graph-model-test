/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.test.module.model;

import guru.bubl.module.model.content.AllContentFactory;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.assertFalse;

public class AllContentTest extends ModelTestResources {

    @Inject
    AllContentFactory allContentFactory;

    @Test
    public void indexes_all_for_the_user(){
        allContentFactory.forUserGraph(userGraph).add();
        GraphElementSearchResult searchResult = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "Project",
                user
        ).iterator().next();
        assertFalse(
                searchResult.getContext().isEmpty()
        );
    }
}
