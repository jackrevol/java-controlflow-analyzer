package com.jackrevol.analyzer.codeblock;

import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BreakStatement;

import java.util.List;

public class BreakCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public BreakCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		BreakStatement breakNode = (BreakStatement) astNodes.remove(0);

		// break
		CodeBlock breakBlock = new CodeBlock(function);
		breakBlock.addStatementNode(breakNode);
		breakBlock.addSuccessor(loopExit);
		loopExit.addPredecessor(breakBlock);

		function.addToCodeBlocks(breakBlock);
		return breakBlock;
	}

}
