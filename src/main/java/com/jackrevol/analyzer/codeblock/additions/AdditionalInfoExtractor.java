package com.jackrevol.analyzer.codeblock.additions;

import com.google.common.collect.Lists;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Decision;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;

public class AdditionalInfoExtractor {
	private CodeBlock codeBlock;
	private Function function;

	public AdditionalInfoExtractor(CodeBlock codeBlock) {
		this.codeBlock = codeBlock;
		this.function = codeBlock.getParentFunction();
	}

	public void extractAdditionalInfos(ASTNode statement) {
		CompilationUnit cu = (CompilationUnit) statement.getRoot();

		// Visitor execution
		AdditionalInfoExtractorVisitor visitor = new AdditionalInfoExtractorVisitor();
		statement.accept(visitor);

		// Collection of anonymous functions in syntax
		List<Function> anonymousFunctionInfos = Lists.newArrayList();
		anonymousFunctionInfos.addAll(visitor.getLamdaFunctions());
		anonymousFunctionInfos.addAll(visitor.getLocalClasses());

		// Register additional information in Code Block
		codeBlock.addAllAnonymousFunctionInfo(anonymousFunctionInfos);
		List<Decision> decisions = visitor.getConditionalExpressions();
		for (Decision decision : decisions) {
			codeBlock.addToDecisions(decision);
		}

		for (Function anonymousFunctionInfo : anonymousFunctionInfos) {
			// collect statement blocks in anonymous functions
			List<CodeBlock>  anonymousFunctionCodeBlocks = anonymousFunctionInfo.getCodeBlocks();
			for (CodeBlock anonymousFunctionCodeBlock : anonymousFunctionCodeBlocks) {
				anonymousFunctionCodeBlock.setParentFunction(function);
				function.addToCodeBlocks(anonymousFunctionCodeBlock);
			}
		}
	}

}
