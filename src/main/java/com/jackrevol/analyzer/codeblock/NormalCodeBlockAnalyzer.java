package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class NormalCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public NormalCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {


		// normal
		CodeBlock normalBlock = new CodeBlock(function);
		AdditionalInfoExtractor statementAnalyzer = new AdditionalInfoExtractor(normalBlock);
		for (ASTNode normalNode : astNodes) {
			normalBlock.addStatementNode(normalNode);
			statementAnalyzer.extractAdditionalInfos(normalNode);
		}
		normalBlock.addSuccessor(exitBlock);
		exitBlock.addPredecessor(normalBlock);

		function.addToCodeBlocks(normalBlock);
		return normalBlock;
	}

}
