package com.jackrevol.analyzer.codeblock;

import com.google.common.collect.Lists;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.SynchronizedStatement;

import java.util.List;

public class SynchronizedCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public SynchronizedCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		SynchronizedStatement synchronizedStatement = (SynchronizedStatement) astNodes.remove(0);
		Block block = synchronizedStatement.getBody();

		List<ASTNode> statements = Lists.newArrayList(block.statements());
		astNodes.addAll(0, statements);
		return this.analyzeASTNodes(astNodes, exitBlock, loopExit);
	}

}
