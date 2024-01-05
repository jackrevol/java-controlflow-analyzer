package com.jackrevol.analyzer;

import com.jackrevol.analyzer.source.CompilationUnitAnalyzer;
import com.jackrevol.models.SourceFile;

import java.io.UnsupportedEncodingException;

public class JavaAnalyzer {
	private CompilationUnitAnalyzer compilationUnitAnalyzer;

	public JavaAnalyzer() {
		this.compilationUnitAnalyzer = new CompilationUnitAnalyzer();
	}

	public SourceFile analyze(byte[] contents, String encoding) {
        try {
            return analyze(new String(contents, encoding));
        } catch (UnsupportedEncodingException e) {
			SourceFile sourceFile = new SourceFile();
			sourceFile.setResult(false);
			sourceFile.setMessage(e.getMessage());
			return sourceFile;
        }
    }

	public SourceFile analyze(String sourceCode) {
		return compilationUnitAnalyzer.analyze(sourceCode);
	}

}