package com.jackrevol.analyzer.codeblock;

import com.google.common.collect.Lists;
import com.jackrevol.models.CodeBlock;
import com.jackrevol.models.Function;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.List;

public abstract class AbstractCodeBlockAnalyzer implements ICodeBlockAnalyzer {

	protected Function function;

	public AbstractCodeBlockAnalyzer(Function function) {
		this.function = function;
	}

	public CodeBlock analyzeASTNodes(List<ASTNode> astNodes, CodeBlock exitBlock,
									 CodeBlock loopExit) {
		List<ASTNode> normalStatements = Lists.newArrayList();
		// if no more node exist to analyze then return exitBlock
		if (astNodes.size() == 0)
			return exitBlock;

		// Run the analyzer appropriate for the syntax type to be analyzed
		ICodeBlockAnalyzer analyzer = null;
		do {
			ASTNode statement = astNodes.get(0);
			switch (statement.getNodeType()) {
			case ASTNode.IF_STATEMENT:
				analyzer = new IfCodeBlockAnalyzer(function);
				break;
			case ASTNode.FOR_STATEMENT:
				analyzer = new ForCodeBlockAnalyzer(function);
				break;
			case ASTNode.ENHANCED_FOR_STATEMENT:
				analyzer = new EnhancedForCodeBlockAnalyzer(function);
				break;
			case ASTNode.WHILE_STATEMENT:
				analyzer = new WhileCodeBlockAnalyzer(function);
				break;
			case ASTNode.DO_STATEMENT:
				analyzer = new DoCodeBlockAnalyzer(function);
				break;
			case ASTNode.SWITCH_STATEMENT:
				analyzer = new SwitchCodeBlockAnalyzer(function);
				break;
			case ASTNode.SWITCH_CASE:
				analyzer = new CaseCodeBlockAnalyzer(function);
				break;
			case ASTNode.TRY_STATEMENT:
				analyzer = new TryCodeBlockAnalyzer(function);
				break;
			case ASTNode.CATCH_CLAUSE:
				analyzer = new CatchCodeBlockAnalyzer(function);
				break;
			case ASTNode.BREAK_STATEMENT:
				analyzer = new BreakCodeBlockAnalyzer(function);
				break;
			case ASTNode.CONTINUE_STATEMENT:
				analyzer = new ContinueCodeBlockAnalyzer(function);
				break;
			case ASTNode.RETURN_STATEMENT:
				analyzer = new ReturnCodeBlockAnalyzer(function);
				break;
			case ASTNode.LABELED_STATEMENT:
				analyzer = new LabelCodeBlockAnalyzer(function);
				break;
			case ASTNode.SYNCHRONIZED_STATEMENT:
				analyzer = new SynchronizedCodeBlockAnalyzer(function);
				break;
			case ASTNode.BLOCK:
				analyzer = new BlockCodeBlockAnalyzer(function);
				break;
			default:
				normalStatements.add(statement);
				astNodes.remove(0);
				break;
			}
		} while (astNodes.size() > 0 && analyzer == null);

		CodeBlock afterCodeBlock;
		if (analyzer != null) {
			afterCodeBlock = analyzer.analyzeASTNode(astNodes, exitBlock, loopExit);
		} else {
			afterCodeBlock = exitBlock;
		}

		if (normalStatements.size() > 0) {
			analyzer = new NormalCodeBlockAnalyzer(function);
			return analyzer.analyzeASTNode(normalStatements, afterCodeBlock, loopExit);
		}
		return afterCodeBlock;
	}

	@Override
	public abstract CodeBlock analyzeASTNode(List<ASTNode> astNodes, CodeBlock exitBlock,
											 CodeBlock loopExit);

	protected CodeBlock createAfterBlock(List<ASTNode> astNodes, CodeBlock exitBlock,
										 CodeBlock loopExit) {

		CodeBlock afterBlock = null;
		if (astNodes.size() > 0) {
			afterBlock = this.analyzeASTNodes(astNodes, exitBlock, loopExit);

		}
		afterBlock = afterBlock == null ? exitBlock : afterBlock;

		return afterBlock;
	}

	public CodeBlock createCodeBlockWithEmptyChecked(ASTNode node, CodeBlock exitBlock,
													 CodeBlock loopExit) {
		CodeBlock codeBlock = null;
		if (node instanceof Block && ((Block) node).statements().size() == 0) {
			codeBlock = new CodeBlock(function);
			codeBlock.addStatementNode(node);
			codeBlock.addSuccessor(exitBlock);
			exitBlock.addPredecessor(codeBlock);
			function.addToCodeBlocks(codeBlock);
		} else {
			codeBlock = this.analyzeASTNodes(Lists.newArrayList(node), exitBlock, loopExit);
		}

		return codeBlock;
	}

	protected int getStartLine(ASTNode astNode) {
		CompilationUnit compilationUnit = (CompilationUnit) astNode.getRoot();
		int startPosition = astNode.getStartPosition();
		int startLine = compilationUnit.getLineNumber(startPosition);
		return startLine;
	}
}
