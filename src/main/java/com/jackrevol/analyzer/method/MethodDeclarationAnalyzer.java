package com.jackrevol.analyzer.method;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.dom.*;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jackrevol.analyzer.codeblock.EntryCodeBlockAnalyzer;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;

public class MethodDeclarationAnalyzer {
	private static final String OPRN_BRACKETS = "(";
	private static final String CLOSE_BRACKETS = ")";
	public static final String POINT_SEPERATOR = ".";
	public static final String COMMA_SEPERATOR = ",";

	public Function analyze(BodyDeclaration methodDeclaration) {
		Function function = new Function();
		function.setFunctionAstNode(methodDeclaration);
		List<ASTNode> astNodes = getFunctionBody(methodDeclaration);

		// generate function name
		setFunctionName(function, methodDeclaration);

		// bodyHash
		StringBuilder bodyString = new StringBuilder();
		for (ASTNode astNode : astNodes) {
			bodyString.append(astNode.toString());
		}
		// ready Anayzler ,create exitblock
		CodeBlock exitBlock = new CodeBlock(function);
		function.addToCodeBlocks(exitBlock);
		function.setExitCodeBlock(exitBlock);

		// start codeblockanalyzer
		EntryCodeBlockAnalyzer entryCodeBlockAnalyzer = new EntryCodeBlockAnalyzer(function);
		entryCodeBlockAnalyzer.analyzeASTNode(astNodes, exitBlock, null);

		return function;
	}

	private void setFunctionName(Function function, BodyDeclaration bodyDeclaration) {
		MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
		// extract function name
		String simpleName = methodDeclaration.getName().toString();

		List<String> classNames = Lists.newArrayList();
		ASTNode parentNode = methodDeclaration.getParent();
		while(parentNode != null) {
			if(parentNode instanceof AbstractTypeDeclaration) {
				classNames.add(((AbstractTypeDeclaration) parentNode).getName().toString());
			}
			parentNode = parentNode.getParent();
		}
		Collections.reverse(classNames);
		String className = Joiner.on(POINT_SEPERATOR).skipNulls().join(classNames);

		// extract package name
		CompilationUnit cu = (CompilationUnit) methodDeclaration.getRoot();
		String pakageName = cu.getPackage() != null ? cu.getPackage().getName().getFullyQualifiedName() : null;

		// extract parameter type
		List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
		List<String> parameterTypes = Lists.newArrayList();
		for (SingleVariableDeclaration parameter : parameters) {
			parameterTypes.add((parameter).getType().toString());
		}

		// combine name
		StringBuilder functionName = new StringBuilder();
		functionName.append(simpleName);
		functionName.append(OPRN_BRACKETS);
		functionName.append(Joiner.on(COMMA_SEPERATOR).skipNulls().join(parameterTypes));
		functionName.append(CLOSE_BRACKETS);

		function.setName(Joiner.on(POINT_SEPERATOR).skipNulls().join(pakageName, className, functionName));
	}

	private List<ASTNode> getFunctionBody(BodyDeclaration bodyDeclaration) {
		MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
		return Lists.newArrayList((ASTNode) methodDeclaration.getBody());
	}

}
