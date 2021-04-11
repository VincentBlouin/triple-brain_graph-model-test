package guru.bubl.test.module.model.export;

import com.google.inject.Inject;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.ExportToMarkdown;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.ExportToMarkdownFactory;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.MdFile;
import guru.bubl.test.module.utils.ModelTestResources;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.neo4j.cypher.internal.expressions.In;

public class ExportToMarkdownTest extends ModelTestResources {

    @Inject
    ExportToMarkdownFactory exportToMarkdownFactory;

    @Test 
    public void returns_a_string_for_every_center() {
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        LinkedHashMap<URI, MdFile> pages = exportToMarkdown.exportStrings();
        assertThat(
                pages.size(),
                is(0)
        );
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
//        pages = exportToMarkdown.exportStrings();
//        assertThat(
//                pages.size(),
//                is(1)
//        );
        centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        pages = exportToMarkdown.exportStrings();
        Iterator<MdFile> iterator = pages.values().iterator();
        System.out.println("center 1\n" + iterator.next().getContent());
        System.out.println("center 2\n" + iterator.next().getContent());
        exportToMarkdown.export();
        assertThat(
                pages.size(),
                is(2)
        );
    }

    private static Integer testQuantity = 0;

    @Test 
    public void center_is_a_header() {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        String page = exportToMarkdown.exportStrings().values().iterator().next().getContent();
        System.out.println(page);
        Parser parser = Parser.builder().build();
        Node node = parser.parse(page);
        testQuantity = 0;
        AbstractVisitor visitor = new AbstractVisitor() {
            @Override
            public void visit(Heading heading) {
                super.visit(heading);
                testQuantity++;
            }
        };
        node.accept(visitor);
        assertThat(
                testQuantity,
                is(1)
        );
    }

    @Test 
    public void has_a_line_for_every_children() {
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        Parser parser = Parser.builder().build();
        String page = exportToMarkdown.exportStrings().values().iterator().next().getContent();
        Node node = parser.parse(page);
        testQuantity = 0;
        AbstractVisitor visitor = new AbstractVisitor() {
            @Override
            public void visit(ListItem listItem) {
                super.visit(listItem);
                testQuantity++;
            }
        };
        node.accept(visitor);
        assertThat(
                testQuantity,
                is(5)
        );
    }

    @Test 
    public void ignores_center_tags() {
        TagPojo meta = vertexA.addTag(
                modelTestScenarios.person()
        ).values().iterator().next();
        centerGraphElementOperatorFactory.usingFriendlyResource(
                meta
        ).updateLastCenterDate();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        LinkedHashMap<URI, MdFile> pages = exportToMarkdown.exportStrings();
        assertThat(
                pages.size(),
                is(1)
        );
    }

    @Test 
    public void ignores_center_relations() {
        Relation relation = vertexB.getEdgeToDestinationVertex(vertexC);
        centerGraphElementOperatorFactory.usingFriendlyResource(
                relation
        ).updateLastCenterDate();
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        LinkedHashMap<URI, MdFile> pages = exportToMarkdown.exportStrings();
        assertThat(
                pages.size(),
                is(1)
        );
    }
    
    @Test 
    public void can_handle_circular_graphs() {
        vertexB.addRelationToFork(vertexE.uri(), ShareLevel.PRIVATE, ShareLevel.PRIVATE);
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        LinkedHashMap<URI, MdFile> pages = exportToMarkdown.exportStrings();
        assertThat(
                pages.size(),
                is(1)
        );
    }


    @Test
    public void can_have_french_accents_in_file_name() {
        vertexA.label("églantier");
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
        assertThat(
                file.getName(),
                is("églantier")
        );
    }


//    @Test 
//    public void is_in_hierarchical(){
//        Parser parser = Parser.builder().build();
//        Node node = parser.parse("Example\n=======\n\nSome more text");
//        WordCountVisitor visitor = new WordCountVisitor();
//        node.accept(visitor);
//        visitor.wordCount;  // 4
//
//        class WordCountVisitor extends AbstractVisitor {
//            int wordCount = 0;
//
//            @Override
//            public void visit(Text text) {
//                // This is called for all Text nodes. Override other visit methods for other node types.
//
//                // Count words (this is just an example, don't actually do it this way for various reasons).
//                wordCount += text.getLiteral().split("\\W+").length;
//
//                // Descend into children (could be omitted in this case because Text nodes don't have children).
//                visitChildren(text);
//            }
//        }
//    }


}
