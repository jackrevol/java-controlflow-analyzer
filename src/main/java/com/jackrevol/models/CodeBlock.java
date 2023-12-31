package com.jackrevol.models;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("serial")
@Getter
@Setter
public class CodeBlock {

	private UUID id;

	// block's statements
	private List<ASTNode> statementNodes;

	// entry
	private List<CodeBlock> predecessors;
	// exit
	private List<CodeBlock> successors;

	// Anonymous Class function, Local Class function, lambda function
	private List<Function> functions;

	private Function parentFunction;

	private List<Decision> decisions;


	public CodeBlock(Function parentFunction) {
		this.id = UUID.randomUUID();
		this.statementNodes = Lists.newArrayList();
		this.predecessors = Lists.newArrayList();
		this.successors = Lists.newArrayList();
		this.functions = Lists.newArrayList();
		this.decisions =  Lists.newArrayList();
		this.parentFunction = parentFunction;
		parentFunction.getFunctionControlFlowGraph().addNode(this);
	}

	public boolean addStatementNode(ASTNode astNode) {
		return this.statementNodes.add(astNode);
	}

	public boolean addPredecessor(CodeBlock codeBlock) {
		return this.predecessors.add(codeBlock);
	}

	public boolean addSuccessor(CodeBlock codeBlock) {
		parentFunction.getFunctionControlFlowGraph().putEdge(this,codeBlock);
		return this.successors.add(codeBlock);



	}


	public boolean addToDecisions(Decision decision) {
		return  this.decisions.add(decision);
	}

	public boolean addAnonymousFunctionInfo(Function anonymousFunctionInfo) {
		return this.functions.add(anonymousFunctionInfo);
	}

	public boolean addAllAnonymousFunctionInfo(List<Function> anonymousFunctionInfos) {
		return this.functions.addAll(anonymousFunctionInfos);
	}

	public String getStatementsString(){
		StringBuilder stringBuilder = new StringBuilder();
		if(this.getPredecessors().isEmpty()){
			return "entry";
		}else if(this.getSuccessors().isEmpty()){
			return "exit";
		}
		for(ASTNode statementNode : statementNodes){
			stringBuilder.append(statementNode.toString());
		}

		return stringBuilder.toString();
	}

}
