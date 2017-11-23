package net.sf.basheclipse.ui.model;

import net.sf.basheclipse.ui.source.BashDocumentProvider;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class BashOutlinePage extends ContentOutlinePage {

	protected Object fInput;
	protected BashDocumentProvider fDocumentProvider;
	protected BashEditor fTextEditor;
	
	
	public BashOutlinePage(IDocumentProvider provider, ITextEditor editor) {
		super();
		fDocumentProvider= (BashDocumentProvider)provider;
		fTextEditor= (BashEditor) editor;
		FileEditorInput input=(FileEditorInput) fTextEditor.getEditorInput();
		fDocumentProvider.file=input.getFile();
	}
	
	
	
	public void setInput(IEditorInput input) {
		// TODO Auto-generated method stub
		
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

}
