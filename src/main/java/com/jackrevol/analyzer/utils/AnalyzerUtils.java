package com.jackrevol.analyzer.utils;

import java.util.List;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;

import com.google.common.collect.Lists;

public class AnalyzerUtils {

	public static int getAstStartLine(ASTNode astNode) {
		CompilationUnit compilationUnit = (CompilationUnit) astNode.getRoot();
		int functionStartPosition = astNode.getStartPosition();
		int functionStartLine = compilationUnit.getLineNumber(functionStartPosition);
		
		return functionStartLine;
	}


	public static boolean hasPatternInstanceOfExpression(ASTNode expression) {
		switch (expression.getNodeType()) {
		case ASTNode.PATTERN_INSTANCEOF_EXPRESSION:
			return true;
		case ASTNode.INFIX_EXPRESSION:
			InfixExpression infixExpression = (InfixExpression) expression;
			return hasPatternInstanceOfExpression(infixExpression.getLeftOperand())
					|| hasPatternInstanceOfExpression(infixExpression.getRightOperand());
		case ASTNode.PREFIX_EXPRESSION:
			PrefixExpression prefixExpression = (PrefixExpression) expression;
			return hasPatternInstanceOfExpression(prefixExpression.getOperand());
		case ASTNode.PARENTHESIZED_EXPRESSION:
			ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression;
			return hasPatternInstanceOfExpression(parenthesizedExpression.getExpression());
		default:
			return false;
		}
	}

}
