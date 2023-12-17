package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ReturnStatement;

import java.util.List;

public class ReturnCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public ReturnCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		ReturnStatement returnNode = (ReturnStatement) astNodes.remove(0);

		CodeBlock returnBlock = new CodeBlock(function);
		returnBlock.addStatementNode(returnNode);
		AdditionalInfoExtractor statementAnalyzer = new AdditionalInfoExtractor(returnBlock);
		statementAnalyzer.extractAdditionalInfos(returnNode);
		returnBlock.addSuccessor(function.getExitCodeBlock());
		function.getExitCodeBlock().addPredecessor(returnBlock);

		function.addToCodeBlocks(returnBlock);
		return returnBlock;
	}

}
