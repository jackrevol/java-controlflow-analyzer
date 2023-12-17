package com.jackrevol.analyzer.codeblock;

import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class EntryCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public EntryCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		CodeBlock entryBlock = new CodeBlock(function);
		function.addToCodeBlocks(entryBlock);
		function.setEntryCodeBlock(entryBlock);

		CodeBlock codeBlock = analyzeASTNodes(astNodes, exitBlock, loopExit);
		codeBlock.addPredecessor(entryBlock);
		entryBlock.addSuccessor(codeBlock);
		return entryBlock;
	}
}
