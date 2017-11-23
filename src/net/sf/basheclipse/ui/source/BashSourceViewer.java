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

import net.sf.basheclipse.ui.model.BashEditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.projection.ChildDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/* class extended in case we need to do override something later, johnr*/
public class BashSourceViewer extends SourceViewer {

	@SuppressWarnings("unused")
	private BashEditor bashEditor = null;
	
	public BashSourceViewer(Composite parent, IVerticalRuler ruler, int styles, BashEditor le) {
		super(parent, ruler, styles);
		bashEditor = le;
	}

	public BashSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles, BashEditor le) {
		super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles);
		bashEditor = le;
	}
	
	Color saveForeground=null;
	int saveStart;
	Color errorColor=null;
	public void setError(int start,int line){
		StyledText textWidget=getTextWidget();
		if (textWidget != null) {
			int length=10;
			length=textWidget.getLine(line).length();
			if(errorColor==null)
				errorColor=new Color(Display.getCurrent(), new RGB(255, 0, 0));
			setTextColor(errorColor, start, length, true);
		}
	}
}
