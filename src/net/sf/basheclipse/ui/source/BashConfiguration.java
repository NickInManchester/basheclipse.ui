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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class BashConfiguration extends SourceViewerConfiguration {
	private BashScanner scanner;
	private BashColorManager colorManager;

	public BashConfiguration(BashColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			BashPartitionScanner.SINGLELINE_COMMENT,
			BashPartitionScanner.STRING};
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
			return null;
	}
	
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr;
		scanner = new BashScanner(colorManager);
		dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, BashPartitionScanner.SINGLELINE_COMMENT);
		reconciler.setRepairer(dr, BashPartitionScanner.SINGLELINE_COMMENT);
		dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, BashPartitionScanner.STRING);
		reconciler.setRepairer(dr, BashPartitionScanner.STRING);
		return reconciler;
	}

}