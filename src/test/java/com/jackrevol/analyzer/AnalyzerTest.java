package com.jackrevol.analyzer;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import com.jackrevol.models.SourceFile;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.images.Resolutions;
import org.graphstream.ui.swing.util.SwingFileSinkImages;
import org.graphstream.ui.view.Viewer;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AnalyzerTest {

    @Test
    public void testProcessSourceCode() {
        String path = "src\\test\\resources\\testcode\\SimpleTestCode.java";
        JavaAnalyzer javaAnalyzer = new JavaAnalyzer();

        try {
            SourceFile sourceFile = javaAnalyzer.analyze(Files.toString(new File(path), Charsets.UTF_8));
            String t =  sourceFile.getFunctions().get(1).getCodeBlocks().get(2).getStatementsString();
            System.out.println(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void visualizeGraph(){
        String path = "src\\test\\resources\\testcode\\SimpleTestCode.java";
        JavaAnalyzer javaAnalyzer = new JavaAnalyzer();

        try {
            SourceFile sourceFile = javaAnalyzer.analyze(Files.toString(new File(path), Charsets.UTF_8));
            Function function = sourceFile.getFunctions().get(1);
            Graph graphstreamGraph = new SingleGraph(function.getName());

            for(CodeBlock codeBlock: function.getCodeBlocks()){
                Node node = graphstreamGraph.addNode(codeBlock.getId().toString());
                node.setAttribute("ui.label", codeBlock.getStatementsString());
            }
            for(CodeBlock from: function.getCodeBlocks()){
                for(CodeBlock to : from.getSuccessors()){
                    graphstreamGraph.addEdge(UUID.randomUUID().toString(),from.getId().toString(),to.getId().toString(),true);
                }
            }

            SwingFileSinkImages pic = new SwingFileSinkImages(FileSinkImages.OutputType.PNG, Resolutions.VGA);

            pic.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);

            try {
                pic.writeAll(graphstreamGraph, "src\\test\\output\\sample.png");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
