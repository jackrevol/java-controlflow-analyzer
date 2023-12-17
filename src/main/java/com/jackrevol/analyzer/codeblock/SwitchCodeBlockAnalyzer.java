package com.jackrevol.analyzer.codeblock;

import com.google.common.collect.Lists;
import com.jackrevol.analyzer.codeblock.additions.AdditionalInfoExtractor;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;

import java.util.List;

public class SwitchCodeBlockAnalyzer extends AbstractCodeBlockAnalyzer {

	public SwitchCodeBlockAnalyzer(Function function) {
		super(function);
	}

	@SuppressWarnings("unchecked")
	@Override
	public CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
			CodeBlock loopExit) {
		SwitchStatement switchNode = (SwitchStatement) astNodes.remove(0);

		// after
		CodeBlock afterBlock = createAfterBlock(astNodes, exitBlock, loopExit);

		// swtichBlock
		CodeBlock switchBlock = new CodeBlock(function);
		ASTNode expression = switchNode.getExpression();
		switchBlock.addStatementNode(switchNode);
		AdditionalInfoExtractor additionalInfoExtractor = new AdditionalInfoExtractor(switchBlock);
		additionalInfoExtractor.extractAdditionalInfos(expression);

		boolean hasDefault = false;
		List<ASTNode> switchBodyNodes = Lists.newArrayList(switchNode.statements());
		List<ASTNode> switchStatements = Lists.newArrayList();
		while (switchBodyNodes.size() > 0) {
			ASTNode astNode = switchBodyNodes.remove(0);
			if (astNode instanceof SwitchCase) {
				if(((SwitchCase)astNode).isSwitchLabeledRule()) {
					return new NormalCodeBlockAnalyzer(function).analyzeASTNode(Lists.newArrayList(switchNode), afterBlock, loopExit);
				}
				switchStatements.add(astNode);
				SwitchCase switchCase = (SwitchCase) astNode;
				if (switchCase.isDefault()) {
					hasDefault = true;
				}
			} else {
				switchStatements.add(astNode);
			}

			if (switchBodyNodes.size() == 0 || switchBodyNodes.get(0) instanceof SwitchCase) {
				CodeBlock caseBlock = analyzeASTNodes(switchStatements, afterBlock, afterBlock);
				switchBlock.addSuccessor(caseBlock);
				caseBlock.addPredecessor(switchBlock);
				switchStatements.clear();
			} else {
				continue;
			}
		}

		if (hasDefault == false) {
			CodeBlock emptyDefaultBlock = new CodeBlock(function);

			switchBlock.addSuccessor(emptyDefaultBlock);
			emptyDefaultBlock.addPredecessor(switchBlock);
			
			emptyDefaultBlock.addSuccessor(afterBlock);
			afterBlock.addPredecessor(emptyDefaultBlock);
			function.addToCodeBlocks(emptyDefaultBlock);
		}

		function.addToCodeBlocks(switchBlock);
		return switchBlock;
	}

}
