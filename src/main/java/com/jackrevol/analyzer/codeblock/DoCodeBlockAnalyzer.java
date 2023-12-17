package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.analyzer.decisions.DecisionProbeInfoCreator;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.DoStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DoCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {
	private static Logger logger = LoggerFactory.getLogger(DoCodeBlockAnalyzer.class);

	public DoCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		DoStatement doNode = (DoStatement) astNodes.remove(0);
		ASTNode expression = doNode.getExpression();

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		// while
		CodeBlock whileBlock = new CodeBlock(function);

		if (doNode.getExpression() == null || AnalyzerUtils.hasPatternInstanceOfExpression(doNode.getExpression())) {
			logger.warn("Unsupported pattern instanceof expression. (line {})", AnalyzerUtils.getAstStartLine(doNode));
		} else {
			DecisionProbeInfoCreator decisionProbeInfoCreator = new DecisionProbeInfoCreator();
			whileBlock.addToDecisions(decisionProbeInfoCreator.createDecisionProbeInfo(doNode, expression));
		}

		AdditionalInfoExtractor additionalInfoExtractor = new AdditionalInfoExtractor(whileBlock);
		whileBlock.addStatementNode(doNode);
		additionalInfoExtractor.extractAdditionalInfos(expression);
		whileBlock.addSuccessor(afterBlock);
		afterBlock.addPredecessor(whileBlock);

		// do
		CodeBlock doBlock = createCodeBlockWithEmptyChecked(doNode.getBody(), whileBlock, afterBlock);
		whileBlock.addSuccessor(doBlock);
		doBlock.addPredecessor(whileBlock);
		function.addToCodeBlocks(whileBlock);
		return doBlock;
	}

}
