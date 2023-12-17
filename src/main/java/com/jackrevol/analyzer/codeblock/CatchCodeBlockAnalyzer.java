package com.jackrevol.analyzer.codeblock;

import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CatchClause;

import java.util.List;

public class CatchCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public CatchCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		CatchClause catchNode = (CatchClause) astNodes.remove(0);


		CodeBlock catchBlock = this.createCodeBlockWithEmptyChecked(catchNode.getBody(), exitBlock, loopExit);

		return catchBlock;
	}

}
