package com.jackrevol.analyzer.codeblock;

import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.analyzer.decisions.DecisionProbeInfoCreator;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IfCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {
	private static Logger logger = LoggerFactory.getLogger(IfCodeBlockAnalyzer.class);

	public IfCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		IfStatement ifNode = (IfStatement) astNodes.remove(0);
		ASTNode expression = ifNode.getExpression();

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		// if
		CodeBlock ifBlock = new CodeBlock(function);
		
		if (ifNode.getExpression() == null || AnalyzerUtils.hasPatternInstanceOfExpression(ifNode.getExpression())) {
			logger.warn("Unsupported pattern instanceof expression. (line {})", AnalyzerUtils.getAstStartLine(ifNode));
		} else {
			DecisionProbeInfoCreator decisionProbeInfoCreator = new DecisionProbeInfoCreator();
			ifBlock.addToDecisions(decisionProbeInfoCreator.createDecisionProbeInfo(ifNode, expression));
		}

		AdditionalInfoExtractor additionalInfoExtractor = new AdditionalInfoExtractor(ifBlock);
		ifBlock.addStatementNode(ifNode);
		additionalInfoExtractor.extractAdditionalInfos(expression);

		// then
		ASTNode thenNode = ifNode.getThenStatement();
		CodeBlock thenBlock = createCodeBlockWithEmptyChecked(thenNode, afterBlock, loopExit);
		ifBlock.addSuccessor(thenBlock);
		thenBlock.addPredecessor(ifBlock);

		// else
		ASTNode elseNode = ifNode.getElseStatement();
		if (elseNode != null) {
			CodeBlock elseBlock = createCodeBlockWithEmptyChecked(elseNode, afterBlock, loopExit);
			ifBlock.addSuccessor(elseBlock);
			elseBlock.addPredecessor(ifBlock);
		} else {
			ifBlock.addSuccessor(afterBlock);
			afterBlock.addPredecessor(ifBlock);
		}

		this.function.addToCodeBlocks(ifBlock);
		return ifBlock;
	}

}
