package com.jackrevol.models;

import com.google.common.collect.Lists;
import org.eclipse.jdt.core.dom.ASTNode;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Function {

	private CodeBlock entryCodeBlock;

	private CodeBlock exitCodeBlock;

	private ASTNode functionAstNode;

	private List<CodeBlock> codeBlocks;

	private String name;

	public Function(){
		codeBlocks= Lists.newArrayList();
	}

	public void addToCodeBlocks(CodeBlock codeBlock){
		codeBlocks.add(codeBlock);
	}

}
