package com.jackrevol.analyzer;

import com.jackrevol.analyzer.source.CompilationUnitAnalyzer;
import com.jackrevol.models.SourceFile;

public class JavaAnalyzer {
	private CompilationUnitAnalyzer compilationUnitAnalyzer;

	public JavaAnalyzer() {
		this.compilationUnitAnalyzer = new CompilationUnitAnalyzer();
	}

	public SourceFile analyze(byte[] contents, String encoding, String fileName) {
		try {
			return compilationUnitAnalyzer.analyze(contents, encoding, fileName);
		} catch (Exception e) {
			SourceFile sourceFile = new SourceFile();
			sourceFile.setResult(false);
			sourceFile.setMessage(e.getMessage());
			e.printStackTrace();
			return sourceFile;
		}
	}
}