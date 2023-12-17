package com.jackrevol.analyzer.codeblock;

import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ContinueStatement;

import java.util.List;

public class ContinueCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public ContinueCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		ContinueStatement continueNode = (ContinueStatement) astNodes.remove(0);


		CodeBlock continueBlock = new CodeBlock(function);
		continueBlock.addStatementNode(continueNode);

		function.addToCodeBlocks(continueBlock);
		return continueBlock;
	}

}
