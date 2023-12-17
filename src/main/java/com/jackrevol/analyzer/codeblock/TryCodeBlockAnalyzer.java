package com.jackrevol.analyzer.codeblock;

import com.google.common.collect.Lists;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TryStatement;

import java.util.List;

public class TryCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public TryCodeBlockAnalyzer(Function function) {
		super(function);
	}

	//To ensure connection with afterBlock, finalBlock, and catchBlock, the blocks are connected to the first block of tryBlock.
	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		TryStatement tryNode = (TryStatement) astNodes.remove(0);

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);


		// final
		if (tryNode.getFinally() != null) {
			CodeBlock finalBlock = this.createCodeBlockWithEmptyChecked((ASTNode) tryNode.getFinally(),
					afterBlock, loopExit);
			afterBlock = finalBlock;
		}

		// try
		CodeBlock tryBlock = this.createCodeBlockWithEmptyChecked((ASTNode) tryNode.getBody(), afterBlock,
				loopExit);
		if (!tryBlock.getSuccessors().contains(afterBlock)) {
			tryBlock.addSuccessor(afterBlock);
			afterBlock.addPredecessor(tryBlock);
		}

		// catch
		List<ASTNode> catchNodes = Lists.newArrayList(tryNode.catchClauses());
		for (ASTNode catchNode : catchNodes) {
			CodeBlock catchBlock = createCodeBlockWithEmptyChecked(catchNode, afterBlock, loopExit);
			tryBlock.addSuccessor(catchBlock);
			catchBlock.addPredecessor(tryBlock);
		}

		return tryBlock;
	}

}
