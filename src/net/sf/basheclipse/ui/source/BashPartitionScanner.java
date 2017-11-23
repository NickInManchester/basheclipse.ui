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

public class BashPartitionScanner extends RuleBasedPartitionScanner {
	public final static String SINGLELINE_COMMENT = "__SINGLELINE_COMMENT";
	public final static String STRING = "__STRING";
	public final static String[] PARTITION_TYPES = new String[] {
		SINGLELINE_COMMENT, STRING };

	public BashPartitionScanner() {
		IPredicateRule[] rules = new IPredicateRule[]{
			new EndOfLineRule("#", new Token(SINGLELINE_COMMENT)),
			new SingleLineRule("\"", "\"", new Token(STRING), '\\'),
			new SingleLineRule("'", "'", new Token(STRING), '\\')
		};
		setPredicateRules(rules);
	}
	
	
}
