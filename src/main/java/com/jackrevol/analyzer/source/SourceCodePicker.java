package com.jackrevol.analyzer.source;

import org.eclipse.jdt.core.dom.CompilationUnit;

import com.google.common.base.Preconditions;

public class SourceCodePicker {
	private static SourceCodePicker instance;
	private String sourceCode;
	private CompilationUnit compilationUnit;

	private SourceCodePicker(CompilationUnit compilationUnit, String sourceCode) {
		this.compilationUnit = compilationUnit;
		this.sourceCode = sourceCode;
	}

	private void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	private void setCompilationUnit(CompilationUnit compilationUnit) {
		this.compilationUnit = compilationUnit;
	}

	public static SourceCodePicker getInstance(CompilationUnit compilationUnit, String sourceCode) {
		if (instance == null) {
			instance = new SourceCodePicker(compilationUnit, sourceCode);
		} else {
			instance.setCompilationUnit(compilationUnit);
			instance.setSourceCode(sourceCode);
		}
		return instance;
	}

	public static SourceCodePicker getInstance() {
		Preconditions.checkArgument(instance != null, "SourceCodePicker is not initialized.");
		return instance;
	}

	/**
	 * searche the source code from top to bottom, finds the input token, and returns the position.
	 * 
	 * @param cursor                search start position
	 * @param token                 search token (keyword)
	 * @param limitBackwardPosition (last Position)
	 * @return
	 */
	public int findForwardTokenPositionWithoutComment(int cursor, String token, int limitForwardPosition) {
		int tokenPosition = -1;
		boolean isBlockComment = false;
		int lineCommentNumber = -1;
		for (int cursorPos = cursor; cursorPos < limitForwardPosition; cursorPos++) {
			String sourcePiece = sourceCode.substring(cursor, cursorPos);
			if (lineCommentNumber != -1) {
				int currLine = compilationUnit.getLineNumber(cursorPos);
				if (currLine != lineCommentNumber) {
					lineCommentNumber = -1;
					// Set -1 to respond when the token comes immediately after a line break.
					cursor = cursorPos-1;
				}
			} else if (isBlockComment) {
				if (sourcePiece.endsWith("*/")) {
					isBlockComment = false;
					cursor = cursorPos;
				}
			} else if (sourcePiece.endsWith("//")) {
				lineCommentNumber = compilationUnit.getLineNumber(cursorPos);
			} else if (sourcePiece.endsWith("/*")) {
				isBlockComment = true;
				/*/
				  Update the cursor to respond to comments of the same type as this comment.
				 */  
				cursor = cursorPos;
			} else if (sourcePiece.endsWith("while")) {
				tokenPosition = cursorPos;
				break;
			}
		}
		return tokenPosition;
	}
}
