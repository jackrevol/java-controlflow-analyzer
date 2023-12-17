package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.analyzer.decisions.DecisionProbeInfoCreator;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WhileCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {
	private static Logger logger = LoggerFactory.getLogger(WhileCodeBlockAnalyzer.class);

	public WhileCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		WhileStatement whileNode = (WhileStatement) astNodes.remove(0);
		ASTNode expression = whileNode.getExpression();

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		// While
		CodeBlock whileBlock = new CodeBlock(function);

		if (whileNode.getExpression() == null || AnalyzerUtils.hasPatternInstanceOfExpression(whileNode.getExpression())) {
			logger.warn("Unsupported pattern instanceof expression. (line {})", AnalyzerUtils.getAstStartLine(whileNode));
		} else {
			DecisionProbeInfoCreator decisionProbeInfoCreator = new DecisionProbeInfoCreator();
			whileBlock.addToDecisions(decisionProbeInfoCreator.createDecisionProbeInfo(whileNode, expression));
		}

		whileBlock.addStatementNode(whileNode);
		AdditionalInfoExtractor additionalInfoExtractor = new AdditionalInfoExtractor(whileBlock);
		additionalInfoExtractor.extractAdditionalInfos(expression);
		whileBlock.addSuccessor(afterBlock);
		afterBlock.addPredecessor(whileBlock);

		// body
		CodeBlock bodyBlock = createCodeBlockWithEmptyChecked(whileNode.getBody(), whileBlock, afterBlock);
		whileBlock.addSuccessor(bodyBlock);
		bodyBlock.addPredecessor(whileBlock);

		function.addToCodeBlocks(whileBlock);
		return whileBlock;
	}

}
