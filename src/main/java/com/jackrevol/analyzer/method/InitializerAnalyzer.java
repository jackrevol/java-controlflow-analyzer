package com.jackrevol.analyzer.method;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.*;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jackrevol.analyzer.codeblock.EntryCodeBlockAnalyzer;
import com.jackrevol.analyzer.utils.AnalyzerUtils;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;

public class InitializerAnalyzer {
	private Map<BodyDeclaration, List<Block>> initializers;
	private Map<BodyDeclaration, List<Block>> staticInitializers;
	private AbstractTypeDeclaration typeDeclaration;
	public static final String INITIALIZER = "<initializer>";
	public static final String STATIC_INITIALIZER = "<static initializer>";
	public static final String POINT_SEPERATOR = ".";

	public InitializerAnalyzer(AbstractTypeDeclaration typeDeclaration) {
		initializers = Maps.newLinkedHashMap();
		staticInitializers = Maps.newLinkedHashMap();
		this.typeDeclaration = typeDeclaration;
	}

	public Function analyze(boolean isStatic) {
		Function function = new Function();
		List<ASTNode> bodyNodes = getFunctionBody(isStatic);
		function.setFunctionAstNode(bodyNodes.get(0));


		// create exitBlock
		CodeBlock exitBlock = new CodeBlock(function);
		function.addToCodeBlocks(exitBlock);
		function.setExitCodeBlock(exitBlock);

		// create entryBlock
		CodeBlock entryBlock = new CodeBlock(function);
		function.addToCodeBlocks(entryBlock);
		function.setEntryCodeBlock(entryBlock);

		// start
		EntryCodeBlockAnalyzer entryCodeBlockAnalyzer = new EntryCodeBlockAnalyzer(function);
		for (ASTNode bodyNode : bodyNodes) {
			CodeBlock codeBlock = entryCodeBlockAnalyzer.createCodeBlockWithEmptyChecked(bodyNode,
					exitBlock, null);
			codeBlock.addPredecessor(entryBlock);
			entryBlock.addSuccessor(codeBlock);
		}

		// create function name
		setFunctionName(function, isStatic);

		return function;
	}

	public Map<BodyDeclaration, List<Block>> getInitializers() {
		return initializers;
	}

	public Map<BodyDeclaration, List<Block>> getStaticInitializers() {
		return staticInitializers;
	}

	private void setFunctionName(Function function, boolean isStatic) {

		String simpleName = isStatic ? STATIC_INITIALIZER : INITIALIZER;

		List<String> classNames = Lists.newArrayList();
		ASTNode parent = typeDeclaration;
		while (parent != null) {
			if(parent instanceof AbstractTypeDeclaration) {
				classNames.add(((AbstractTypeDeclaration) parent).getName().getIdentifier());
			}
			parent = parent.getParent();
		}
		Collections.reverse(classNames);
		String className = Joiner.on(POINT_SEPERATOR).skipNulls().join(classNames);
		CompilationUnit cu = (CompilationUnit) typeDeclaration.getRoot();
		String packageName = cu.getPackage() != null ? cu.getPackage().getName().getFullyQualifiedName() : null;
		String functionName = Joiner.on(POINT_SEPERATOR).skipNulls().join(packageName, className, simpleName);

		function.setName(functionName);

	}

	private List<ASTNode> getFunctionBody(boolean isStatic) {
		List<ASTNode> bodyNodes = Lists.newArrayList();
		if (isStatic) {
			for (List<Block> staticInitializer : staticInitializers.values()) {
				bodyNodes.addAll(staticInitializer);
			}
		} else {
			for (List<Block> initializer : initializers.values()) {
				bodyNodes.addAll(initializer);
			}
		}
		return bodyNodes;
	}

	private boolean isStatic(BodyDeclaration initializer) {
		List<ASTNode> modifiers = initializer.modifiers();
		for (ASTNode astNode : modifiers) {
			if (astNode instanceof Modifier) {
				Modifier modifier = (Modifier) astNode;
				if (modifier.isStatic()) {
					return true;
				}
			}

		}
		return false;
	}

	public void addBodyDeclaration(BodyDeclaration bodyDeclaration) {
		BodyDeclarationVisitor bodyDeclarationVisitor = new BodyDeclarationVisitor();
		bodyDeclaration.accept(bodyDeclarationVisitor);

		List<Block> bodyBlocks = bodyDeclarationVisitor.getBodyBlocks();
		if (bodyBlocks.size() > 0) {
			if (isStatic(bodyDeclaration)) {
				staticInitializers.put(bodyDeclaration, bodyBlocks);
			} else {
				initializers.put(bodyDeclaration, bodyBlocks);
			}
		}
	}

	private class BodyDeclarationVisitor extends ASTVisitor {

		private List<Block> bodyBlocks;

		public List<Block> getBodyBlocks() {
			return bodyBlocks;
		}

		public BodyDeclarationVisitor() {
			super();
			bodyBlocks = Lists.newArrayList();
		}

		@Override
		public boolean visit(Block node) {
			if (node.statements().size() > 0) {
				bodyBlocks.add(node);
			}
			return false;
		}
	}
}
