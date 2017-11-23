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

import net.sf.basheclipse.core.launching.BashLaunchDelegate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class BashDocumentProvider extends FileDocumentProvider implements IDocumentListener{
	Document document;
	public IFile file;
	protected IDocument createDocument(Object element) throws CoreException {
		document = (Document)super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new BashPartitionScanner(),BashPartitionScanner.PARTITION_TYPES);
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
			document.addDocumentListener(this);
		}
		return document;
	}
	public void changed(Object element){
		super.changed(element);
	}
	//IDocumentListener
	public void documentAboutToBeChanged(DocumentEvent event) {
		int line=-1;
		try {
			line=event.fDocument.getLineOfOffset(event.fOffset);
			int numLines=0;
			if(event.fLength>0){
				numLines=countLines(event.fDocument.get(event.fOffset, event.fLength));
				if(numLines!=0)
					BashLaunchDelegate.documentChanged(file, line, -1*numLines);
			}
			if(event.fText.length()>0){
				numLines=countLines(event.fText);
				if(numLines!=0)
					BashLaunchDelegate.documentChanged(file, line, numLines);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		};
		
	}
	int countLines(String text){
		int numLines=0;
		for (int i = 0; i < text.length(); i++) {
			if(text.charAt(i)=='\n')
				numLines++;
		}
		return numLines;
	}
	
	
	public void documentChanged(DocumentEvent event) {
	}
}