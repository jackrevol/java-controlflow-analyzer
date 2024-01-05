package com.jackrevol.analyzer;

import com.jackrevol.models.SourceFile;
import org.junit.jupiter.api.Test;

public class AnalyzerTest {

    @Test
    public void testProcessSourceCode() {
        String inputSourceCode = "public class Example {\n" +
                "    public void method() {\n" +
                "        int a = 5;\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";

        JavaAnalyzer javaAnalyzer = new JavaAnalyzer();
        SourceFile sourceFile =  javaAnalyzer.analyze(inputSourceCode);

        String t =  sourceFile.getFunctions().get(0).getCodeBlocks().get(2).getStatementsString();

        System.out.println(t);

    }


}
