package com.jackrevol.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class SourceFile {

	private String sourceCode;

	// many classes are can exist in one source code
	private Set<ASTNode> classAstNodes;

	private List<Function> functions;
	private boolean result;
	private String message;

	public SourceFile() {
		this.classAstNodes = Sets.newHashSet();
		this.functions = Lists.newArrayList();
	}

	public void addClassAstNode(ASTNode astNode) {
		this.classAstNodes.add(astNode);
	}

	public void addFunction(Function function) {
        this.functions.add(function);
    }

}
