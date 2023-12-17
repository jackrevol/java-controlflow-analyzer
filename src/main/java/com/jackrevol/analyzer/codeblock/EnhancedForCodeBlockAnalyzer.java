package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnhancedForStatement;

import java.util.List;

public class EnhancedForCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public EnhancedForCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		EnhancedForStatement enhancedForNode = (EnhancedForStatement) astNodes.remove(0);

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		// for
		CodeBlock enhancedForBlock = new CodeBlock(function);
		enhancedForBlock.addStatementNode(enhancedForNode);
		AdditionalInfoExtractor additionalInfoExtractor = new AdditionalInfoExtractor(enhancedForBlock);
		ASTNode expression = enhancedForNode.getExpression();
		additionalInfoExtractor.extractAdditionalInfos(expression);
		enhancedForBlock.addSuccessor(afterBlock);
		afterBlock.addPredecessor(enhancedForBlock);

		// body
		CodeBlock bodyBlock = createCodeBlockWithEmptyChecked(enhancedForNode.getBody(), enhancedForBlock,
				afterBlock);
		bodyBlock.addPredecessor(enhancedForBlock);
		enhancedForBlock.addSuccessor(bodyBlock);

		function.addToCodeBlocks(enhancedForBlock);
		return enhancedForBlock;
	}

}
