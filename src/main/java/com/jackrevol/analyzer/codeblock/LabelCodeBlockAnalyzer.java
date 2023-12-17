package com.jackrevol.analyzer.codeblock;

import com.google.common.collect.Lists;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.LabeledStatement;

import java.util.List;

public class LabelCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public LabelCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		LabeledStatement label = (LabeledStatement) astNodes.remove(0);		

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		List<ASTNode> labelContent = Lists.newArrayList((ASTNode) label.getBody());
		astNodes.addAll(0, labelContent);
		
		return analyzeASTNodes(astNodes, afterBlock, afterBlock);
	}

}
