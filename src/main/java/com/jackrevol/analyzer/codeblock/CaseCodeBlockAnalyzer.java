package com.jackrevol.analyzer.codeblock;

import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SwitchCase;

import java.util.List;

public class CaseCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public CaseCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
									CodeBlock loopExit) {
		SwitchCase switchCase = (SwitchCase) astNodes.remove(0);


		// case
		CodeBlock caseBlock = new CodeBlock(function);
		caseBlock.addStatementNode(switchCase);

		// Body
		CodeBlock bodyBlock = null;
		if (astNodes.size() > 0) {
			bodyBlock = this.analyzeASTNodes(astNodes, exitBlock, loopExit);
		}
		bodyBlock = bodyBlock == null ? exitBlock : bodyBlock;

		caseBlock.addSuccessor(bodyBlock);
		bodyBlock.addPredecessor(caseBlock);

		function.addToCodeBlocks(caseBlock);
		return caseBlock;
	}

}
