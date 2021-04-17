package guru.bubl.test.module.model.export;

import com.google.inject.Inject;
import guru.bubl.module.model.ModelTestScenarios;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperator;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.relation.Relation;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.ExportToMarkdown;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.ExportToMarkdownFactory;
import guru.bubl.module.neo4j_graph_manipulator.graph.export.MdFile;
import guru.bubl.test.module.utils.ModelTestResources;
import org.codehaus.jettison.json.JSONObject;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void file_names_can_have_spaces() {
        vertexA.label("vertex A");
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
        assertThat(
                file.getName(),
                is("vertex A")
        );
    }

    @Test
    public void sorts_children() throws Exception {
        JSONObject childrenIndexes = new JSONObject().put(
                vertexC.uri().toString(),
                new JSONObject().put(
                        "index",
                        0
                )
        ).put(
                vertexA.uri().toString(),
                new JSONObject().put(
                        "index",
                        1
                )
        );
        vertexB.setChildrenIndex(childrenIndexes.toString());
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexB
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
//        System.out.println(file.getContent());
        Parser parser = Parser.builder().build();
        Node node = parser.parse(file.getContent());
        SortTestMdVisitor sortTestMdVisitor = new SortTestMdVisitor();
        node.accept(sortTestMdVisitor);
//        System.out.println("vertex A index " + sortTestMdVisitor.getVertexAIndex());
//        System.out.println("vertex C index " + sortTestMdVisitor.getVertexCIndex());
        assertThat(
                sortTestMdVisitor.getVertexCIndex(),
                is(1)
        );
        assertThat(
                sortTestMdVisitor.getVertexAIndex(),
                is(5)
        );
    }

    @Test
    public void sorts_children_group_relations() throws Exception {
        JSONObject childrenIndexes = new JSONObject().put(
                groupRelation.uri().toString(),
                new JSONObject().put(
                        "index",
                        0
                )
        ).put(
                vertexB.uri().toString(),
                new JSONObject().put(
                        "index",
                        1
                )
        );
        vertexC.setChildrenIndex(childrenIndexes.toString());
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexC
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
        Parser parser = Parser.builder().build();
        Node node = parser.parse(file.getContent());
        SortTestMdVisitor sortTestMdVisitor = new SortTestMdVisitor();
        node.accept(sortTestMdVisitor);
//        System.out.println("vertex A index " + sortTestMdVisitor.getVertexAIndex());
//        System.out.println("vertex B index " + sortTestMdVisitor.getVertexBIndex());
        assertThat(
                sortTestMdVisitor.getGroupRelationIndex(),
                is(1)
        );
        assertThat(
                sortTestMdVisitor.getVertexBIndex(),
                is(4)
        );
    }

    @Test
    public void sorts_children_of_group_relation() throws Exception {
        JSONObject childrenIndexes = new JSONObject().put(
                vertexE.uri().toString(),
                new JSONObject().put(
                        "index",
                        0
                )
        ).put(
                vertexD.uri().toString(),
                new JSONObject().put(
                        "index",
                        1
                )
        );
        groupRelation.setChildrenIndex(childrenIndexes.toString());
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexC
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
//        System.out.println(file.getContent());
        Parser parser = Parser.builder().build();
        Node node = parser.parse(file.getContent());
        SortTestMdVisitor sortTestMdVisitor = new SortTestMdVisitor();
        node.accept(sortTestMdVisitor);
//        System.out.println("vertex A index " + sortTestMdVisitor.getVertexAIndex());
//        System.out.println("vertex B index " + sortTestMdVisitor.getVertexBIndex());
        assertThat(
                sortTestMdVisitor.getVertexEIndex(),
                is(4)
        );
        assertThat(
                sortTestMdVisitor.getVertexDIndex(),
                is(5)
        );
    }

    @Test
    public void includes_tags() {
        vertexA.addTag(modelTestScenarios.book(), ShareLevel.PRIVATE);
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
        System.out.println(file.getContent());
        assertTrue(
                file.getContent().contains("# vertex A #Book")
        );
    }

    @Test
    public void includes_note_as_footnote() {
        vertexA.comment("a note");
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
        System.out.println(file.getContent());
        assertTrue(
                file.getContent().contains("# vertex A [^1]")
        );
        assertTrue(
                file.getContent().endsWith("[^1]: a note\n")
        );
    }

    @Test
    public void html_notes_are_rendered_as_markdown() {
        vertexA.comment("<span>a note</span> with <strong>bold</strong>");
        CenterGraphElementOperator centerGraphElementOperator = centerGraphElementOperatorFactory.usingFriendlyResource(
                vertexA
        );
        centerGraphElementOperator.updateLastCenterDate();
        centerGraphElementOperator.incrementNumberOfVisits();
        ExportToMarkdown exportToMarkdown = exportToMarkdownFactory.withUsername("roger_lamothe");
        MdFile file = exportToMarkdown.exportStrings().values().iterator().next();
        System.out.println(file.getContent());
        assertTrue(
                file.getContent().endsWith("[^1]: a note with **bold**\n")
        );
    }


    private class SortTestMdVisitor extends AbstractVisitor {
        private Integer index = 0;
        Map<String, Integer> indexes = new HashMap<>();

        @Override
        public void visit(Text text) {
            indexes.put(text.getLiteral(), index);
            index++;
        }

        public Integer getVertexAIndex() {
            return indexes.get("(edge AB) vertex A");
        }

        public Integer getVertexBIndex() {
            return indexes.get("(edge BC) vertex B");
        }

        public Integer getVertexCIndex() {
            return indexes.get("(edge BC) vertex C");
        }

        public Integer getGroupRelationIndex() {
            return indexes.get("(to do) #To-do");
        }

        public Integer getVertexDIndex() {
            return indexes.get("(edge CD) vertex D");
        }

        public Integer getVertexEIndex() {
            return indexes.get("(edge CE) vertex E");
        }
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
