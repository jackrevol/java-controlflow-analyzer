package com.jackrevol.models;

import com.google.common.collect.Lists;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

@Getter
@Setter
public class Function {

	private MutableGraph<CodeBlock> functionControlFlowGraph;

	private CodeBlock entryCodeBlock;

	private CodeBlock exitCodeBlock;

	private ASTNode functionAstNode;

	private List<CodeBlock> codeBlocks;

	private String name;

	public Function(){
		codeBlocks= Lists.newArrayList();
		functionControlFlowGraph = GraphBuilder.directed().allowsSelfLoops(true).build();
	}

	public void addToCodeBlocks(CodeBlock codeBlock){
		codeBlocks.add(codeBlock);
	}

}
