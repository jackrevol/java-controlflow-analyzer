package com.jackrevol.analyzer.codeblock;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;

import com.google.common.collect.Lists;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;

public class BlockCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public BlockCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		Block block = (Block) astNodes.remove(0);

		List<ASTNode> statements = Lists.newArrayList(block.statements());
		astNodes.addAll(0, statements);

		return this.analyzeASTNodes(astNodes, exitBlock, loopExit);
	}

}
