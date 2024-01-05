
package com.jackrevol.analyzer.source;

import com.jackrevol.analyzer.clazz.TypeDeclarationAnalyzer;
import com.jackrevol.analyzer.parser.JavaASTParser;
import com.jackrevol.models.Function;
import com.jackrevol.models.SourceFile;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class CompilationUnitAnalyzer {

	public SourceFile analyze(String sourceCode){
		SourceFile sourceFile = new SourceFile();
		sourceFile.setSourceCode(sourceCode);
		return generateCompilationUnit(sourceFile);
	}

	private SourceFile generateCompilationUnit(SourceFile sourceFile) {
		JavaASTParser parser = new JavaASTParser();
		ASTNode astNode;
		astNode = parser.parseSourceFile(sourceFile.getSourceCode());

		List<Function> functionAnalysisInfos = sourceFile.getFunctions();
		if (astNode instanceof CompilationUnit) {
			CompilationUnit compilationUnit = (CompilationUnit) astNode;
			SourceCodePicker.getInstance(compilationUnit, sourceFile.getSourceCode());

			// check error while parsing
			Boolean isError = false;
			StringBuilder errorMessage = new StringBuilder();
			for (IProblem problem : compilationUnit.getProblems()) {
				if (problem.isError()) {
					isError = true;
					errorMessage.append(String.format("line(%d): %s", problem.getSourceLineNumber(), problem.getMessage()));
				}
			}
			if (isError) {
				sourceFile.setResult(false);
				sourceFile.setMessage(errorMessage.toString());
				return sourceFile;
			}

			// analyze class
			TypeDeclarationAnalyzer typeDeclarationAnalyzer = new TypeDeclarationAnalyzer(sourceFile);
			functionAnalysisInfos.addAll(typeDeclarationAnalyzer.analyze(compilationUnit.types()));

			// record analyze success
			sourceFile.setResult(true);
			sourceFile.setMessage("SUCCESSFUL");
			return sourceFile;
		} else {
			// analyze result is not CompilationUnit
			sourceFile.setResult(false);
			sourceFile.setMessage("FAIL_TO_CREATE_COMPILATION_UNIT");
			return sourceFile;
		}
	}
}
