package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.analyzer.decisions.DecisionProbeInfoCreator;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ForStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ForCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {
	private static Logger logger = LoggerFactory.getLogger(ForCodeBlockAnalyzer.class);

	public ForCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		ForStatement forNode = (ForStatement) astNodes.remove(0);

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		// for
		CodeBlock forBlock = new CodeBlock(function);
		AdditionalInfoExtractor additionalInfoExtractor = new AdditionalInfoExtractor(forBlock);
		DecisionProbeInfoCreator decisionProbeInfoCreator = new DecisionProbeInfoCreator();

		forBlock.addStatementNode(forNode);

		List<ASTNode> initializers = forNode.initializers();
		for (ASTNode initializer : initializers) {
			additionalInfoExtractor.extractAdditionalInfos(initializer);
		}

		if (forNode.getExpression() == null) {
			logger.warn("Unsupported pattern expression is null. (line {})", AnalyzerUtils.getAstStartLine(forNode));
		} else if (AnalyzerUtils.hasPatternInstanceOfExpression(forNode.getExpression())) {
			logger.warn("Unsupported pattern instanceof expression. (line {})", AnalyzerUtils.getAstStartLine(forNode));
		} else {
			ASTNode expression = forNode.getExpression();
			additionalInfoExtractor.extractAdditionalInfos(expression);
			forBlock.addToDecisions(decisionProbeInfoCreator.createDecisionProbeInfo(forNode, expression));
		}
		List<ASTNode> updaters = forNode.updaters();
		for (ASTNode updater : updaters) {
			additionalInfoExtractor.extractAdditionalInfos(updater);
		}

		forBlock.addSuccessor(afterBlock);
		afterBlock.addPredecessor(forBlock);

		// body
		CodeBlock bodyBlock = null;
		ASTNode bodyNode = forNode.getBody();
		bodyBlock = createCodeBlockWithEmptyChecked(bodyNode, forBlock, afterBlock);
		forBlock.addSuccessor(bodyBlock);
		bodyBlock.addPredecessor(forBlock);

		function.addToCodeBlocks(forBlock);
		return forBlock;
	}

}
