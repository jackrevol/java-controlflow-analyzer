package com.jackrevol.models;

import org.eclipse.jdt.core.dom.ASTNode;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Decision {

	private ASTNode decisionStatementNode;
	private ASTNode expressionNode;
	private String conditionString;


}
