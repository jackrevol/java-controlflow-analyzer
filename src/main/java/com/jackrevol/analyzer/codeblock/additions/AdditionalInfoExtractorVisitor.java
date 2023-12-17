package com.jackrevol.analyzer.codeblock.additions;

import com.google.common.collect.Lists;
import com.jackrevol.analyzer.codeblock.EntryCodeBlockAnalyzer;
import com.jackrevol.analyzer.decisions.DecisionProbeInfoCreator;
import com.jackrevol.analyzer.method.MethodDeclarationAnalyzer;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Decision;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AdditionalInfoExtractorVisitor extends ASTVisitor {
	private static Logger logger = LoggerFactory.getLogger(AdditionalInfoExtractorVisitor.class);
	private List<Function> lamdaFunctions;
	private List<Function> localClasses;
	private List<Decision> conditionalExpressions;
	private int functionCallCount;

	public AdditionalInfoExtractorVisitor() {
		super();
		lamdaFunctions = Lists.newArrayList();
		localClasses = Lists.newArrayList();
		conditionalExpressions = Lists.newArrayList();
		functionCallCount = 0;
	}

	public List<Function> getLamdaFunctions() {
		return lamdaFunctions;
	}

	public List<Function> getLocalClasses() {
		return localClasses;
	}

	public List<Decision> getConditionalExpressions() {
		return conditionalExpressions;
	}

	public int getFunctionCallCount() {
		return functionCallCount;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		functionCallCount += 1;
		return true;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		functionCallCount += 1;
		return true;
	}

	@Override
	public boolean visit(SuperConstructorInvocation node) {
		functionCallCount += 1;
		return true;
	}

	@Override
	public boolean visit(ConstructorInvocation node) {
		functionCallCount += 1;
		return true;
	}

	@Override
	public boolean visit(LambdaExpression node) {
		ASTNode bodyNode = node.getBody();
		if (bodyNode instanceof Block) {
			Function function = new Function();
			function.setFunctionAstNode(node);
			EntryCodeBlockAnalyzer codeBlockAnalyzer = new EntryCodeBlockAnalyzer(function);
			CodeBlock exitBlock = new CodeBlock(function);
			function.setExitCodeBlock(exitBlock);
			codeBlockAnalyzer.analyzeASTNode(Lists.newArrayList(bodyNode), exitBlock, null);
			// remove entryBlock from list
			function.getCodeBlocks().remove(0);
			this.lamdaFunctions.add(function);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(MethodDeclaration node) {

		if (node.getBody() != null) {
			MethodDeclarationAnalyzer methodDeclarationAnalyzer = new MethodDeclarationAnalyzer();
			Function function = methodDeclarationAnalyzer.analyze(node);

			//remove entryBlock, exitBlock from list
			function.getCodeBlocks().remove(0);
			function.getCodeBlocks().remove(0);

			this.localClasses.add(function);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean visit(ConditionalExpression node) {
		DecisionProbeInfoCreator decisionAnalyzer = new DecisionProbeInfoCreator();
		Expression expression = node.getExpression();
		if (!(node.getExpression() == null || AnalyzerUtils.hasPatternInstanceOfExpression(node.getExpression()))) {
			Decision decision = decisionAnalyzer.createDecisionProbeInfo(node, node.getExpression());
			this.conditionalExpressions.add(decision);
		}
		else{
			logger.warn("Unsupported pattern instanceof expression. (line %d: %s)", 
					AnalyzerUtils.getAstStartLine(node),
					expression == null ? node.toString() : expression.toString());
		}
		return true;
	}
}
