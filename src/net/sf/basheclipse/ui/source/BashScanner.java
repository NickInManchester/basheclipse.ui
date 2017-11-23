/*******************************************************************************
 * Copyright (c) 2011 Alex Kosinsky.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Alex Kosinsky - initial API and implementation
 *******************************************************************************/
package net.sf.basheclipse.ui.source;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.SWT;

public class BashScanner extends RuleBasedScanner {
	private static String[] fgKeywords = {
		"!","[[","]]","case","do","done","elif","else","esac","fi","for","function","if","in","select","then","time","until","while"};
	private static String[] fgBuiltin  = {
		"alias","bg","bind","break","builtin","caller","cd","command","compgen","complete","compopt","continue","declare","dirs","disown","echo","enable","eval","exec","exit",
		"export","fc","fg","getopts","hash","help","history","jobs","kill","let","local","logout","mapfile","popd","printf","pushd","pwd","read","readarray","readonly",
		"return","set","shift","shopt","source","suspend","test","times","trap","type","typeset","ulimit","umask","unalias","unset","wait"
		};
	public BashScanner(BashColorManager manager) {
		IToken keyword = new Token(new TextAttribute(manager.getColor(IBashColorConstants.KEYWORD),null,SWT.BOLD));
		IToken specialParameters = new Token(new TextAttribute(manager.getColor(IBashColorConstants.BUILTIN),null,SWT.BOLD));
		WordRule wordRule = new WordRule(new BashWordDetector(), new Token(new TextAttribute(manager.getColor(IBashColorConstants.DEFAULT))));
		
		for (int i = 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], keyword);
		for (int i = 0; i < fgBuiltin .length; i++)
			wordRule.addWord(fgBuiltin [i], specialParameters);
		IRule[] rules = new IRule[]{
			wordRule,
			new SingleLineRule("\"", "\"", new Token(
					new TextAttribute(manager.getColor(IBashColorConstants.STRING))), '\\'),
			new SingleLineRule("'", "'", new Token(
					new TextAttribute(manager.getColor(IBashColorConstants.STRING))), '\\'),
			new EndOfLineRule("#", new Token(
					new TextAttribute(manager.getColor(IBashColorConstants.SINGLE_LINE_COMMENT)))),
			new WhitespaceRule(new BashWhitespaceDetector()),
		};
		setRules(rules);
	}

	class BashWordDetector implements IWordDetector {
	    public boolean isWordStart(char c) {
	        return Character.isJavaIdentifierStart(c);
	    }
	    public boolean isWordPart(char c) {
	        return Character.isJavaIdentifierPart(c);
	    }
	}	
	class BashWhitespaceDetector implements IWhitespaceDetector {

		public boolean isWhitespace(char c) {
			return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
		}
	}
	
	
	
}
