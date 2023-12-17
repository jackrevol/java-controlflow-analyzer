package com.jackrevol.analyzer.decisions;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;

import com.jackrevol.analyzer.source.SourceCodePicker;
import com.jackrevol.models.Decision;

public class DecisionProbeInfoCreator {
	private static final String EXPRESSION = "()";
	
	public Decision createDecisionProbeInfo(ASTNode decisionStatementNode, ASTNode expressionNode) {
		Decision decision = new Decision();

		CompilationUnit compilationUnit = (CompilationUnit) decisionStatementNode.getRoot();
		int startPosition = decisionStatementNode.getStartPosition();
		int startLine = compilationUnit.getLineNumber(startPosition);
		if(decisionStatementNode.getNodeType() == ASTNode.DO_STATEMENT) {
			DoStatement doStatement = (DoStatement) decisionStatementNode;
			ASTNode doBodyNode = doStatement.getBody();
			SourceCodePicker sourceCodePicker = SourceCodePicker.getInstance();
			int keywordPosition = sourceCodePicker.findForwardTokenPositionWithoutComment(
					doBodyNode.getStartPosition() + doBodyNode.getLength(), 
					"while", 
					expressionNode.getStartPosition());
			if(keywordPosition >= 0) {
				startLine = compilationUnit.getLineNumber(keywordPosition);
			}
		}

		decision.setConditionString(expressionNode.toString());
		decision.setExpressionNode(expressionNode);
		decision.setDecisionStatementNode(decisionStatementNode);

		return decision;
	}
}
