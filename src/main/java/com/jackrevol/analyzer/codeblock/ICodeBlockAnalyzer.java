package com.jackrevol.analyzer.codeblock;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import com.jackrevol.models.CodeBlock;

public interface ICodeBlockAnalyzer {
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
                                    CodeBlock loopExit);
}
