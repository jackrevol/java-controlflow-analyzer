package com.jackrevol.analyzer.parser;

import java.util.Hashtable;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class JavaASTParser {
	public ASTNode parseSourceFile(String sourceCode) {
		ASTParser parser;
		String sourceVersion;
		parser = ASTParser.newParser(AST.getJLSLatest());
		sourceVersion = String.valueOf(AST.getJLSLatest());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(sourceCode.toCharArray());
		parser.setResolveBindings(true);
		
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, sourceVersion);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, sourceVersion);
		options.put(JavaCore.COMPILER_SOURCE, sourceVersion);
		parser.setCompilerOptions(options);

		ASTNode astNode = parser.createAST(null);
		return astNode;
	}
}
