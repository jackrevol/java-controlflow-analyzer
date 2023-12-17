package com.jackrevol.analyzer.clazz;

import java.util.List;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.collect.Lists;
import com.jackrevol.analyzer.method.InitializerAnalyzer;
import com.jackrevol.analyzer.method.MethodDeclarationAnalyzer;
import com.jackrevol.models.Function;
import com.jackrevol.models.SourceFile;

public class TypeDeclarationAnalyzer  {

	SourceFile sourceFile;

	public TypeDeclarationAnalyzer(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	public List<Function> analyze(List<AbstractTypeDeclaration> typeDeclarations) {
		List<Function> functions = Lists.newArrayList();
		for (AbstractTypeDeclaration typeDeclaration : typeDeclarations) {
			functions.addAll(analyze(typeDeclaration));
		}
		return functions;
	}

	public List<Function> analyze(AbstractTypeDeclaration abstractTypeDeclaration) {
		sourceFile.addClassAstNode(abstractTypeDeclaration);

		if (abstractTypeDeclaration instanceof EnumDeclaration) {

			EnumDeclaration enumDeclaration = (EnumDeclaration) abstractTypeDeclaration;
			List<BodyDeclaration> bodyDeclarations = Lists.newArrayList();
			bodyDeclarations.addAll(Lists.newArrayList(enumDeclaration.bodyDeclarations()));
			bodyDeclarations.addAll(Lists.newArrayList(enumDeclaration.enumConstants()));
			return analyzeBodyDeclaration(abstractTypeDeclaration, bodyDeclarations);

		} else if (abstractTypeDeclaration instanceof TypeDeclaration) {

			TypeDeclaration typeDeclaration = (TypeDeclaration) abstractTypeDeclaration;
			List<BodyDeclaration> bodyDeclarations = Lists.newArrayList(typeDeclaration.bodyDeclarations());
			return analyzeBodyDeclaration(abstractTypeDeclaration, bodyDeclarations);

		} else {

			return Lists.newArrayList();

		}
	}

	public List<Function> analyzeBodyDeclaration(AbstractTypeDeclaration abstractTypeDeclaration, List<BodyDeclaration> bodyDeclarations) {

		List<Function> functions = Lists.newArrayList();

		// ready analyzers
		InitializerAnalyzer initializerAnalyzer = new InitializerAnalyzer(abstractTypeDeclaration);

		// start analyze
		for (BodyDeclaration bodyDeclaration : bodyDeclarations) {
			if (bodyDeclaration instanceof AbstractTypeDeclaration) {
				// analyze inner classes
				AbstractTypeDeclaration innerTypeDeclaration = (AbstractTypeDeclaration) bodyDeclaration;
				List<Function> innerClassesFunctions = this.analyze(innerTypeDeclaration);
				functions.addAll(innerClassesFunctions);
			} else if (bodyDeclaration instanceof MethodDeclaration) {
				// analyze functions
				MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
				if (methodDeclaration.getBody() != null) {
					MethodDeclarationAnalyzer methodDeclarationAnalyzer = new MethodDeclarationAnalyzer();
					functions.add(methodDeclarationAnalyzer.analyze(methodDeclaration));
				}

			} else {
				// save initializer, field, enum
				initializerAnalyzer.addBodyDeclaration(bodyDeclaration);
			}
		}
		// Saved initializer batch processing
		if (initializerAnalyzer.getInitializers().size() > 0) {
			functions.add(initializerAnalyzer.analyze(false));
		}
		if (initializerAnalyzer.getStaticInitializers().size() > 0) {
			functions.add(initializerAnalyzer.analyze(true));
		}

		return functions;
	}
}
