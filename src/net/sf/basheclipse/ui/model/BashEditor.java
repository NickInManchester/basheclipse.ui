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
package net.sf.basheclipse.ui.model;

import java.util.ResourceBundle;


import net.sf.basheclipse.core.launching.BashLaunchDelegate;
import net.sf.basheclipse.core.launching.BashSourceLookupParticipant;
import net.sf.basheclipse.core.model.BashStackFrame;
import net.sf.basheclipse.core.model.Debugger.UICommand;
import net.sf.basheclipse.ui.model.BashSourceNotFoundEditor.SourceLookupResult;
import net.sf.basheclipse.ui.source.BashColorManager;
import net.sf.basheclipse.ui.source.BashConfiguration;
import net.sf.basheclipse.ui.source.BashDocumentProvider;
import net.sf.basheclipse.ui.source.BashSourceViewer;

import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.manipulation.ConvertLineDelimitersOperation;
import org.eclipse.core.filebuffers.manipulation.FileBufferOperationRunner;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.sourcelookup.SourceLookupFacility;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class BashEditor extends TextEditor{
    
	private class MapToSelectedBashStackFrameAction extends TextEditorAction {

		public MapToSelectedBashStackFrameAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
			super(bundle, prefix, editor);
		}
		public void run() {
			ITextEditor editor= getTextEditor();
			IAdaptable context=DebugUITools.getDebugContext();
			if(!(context instanceof BashStackFrame) || !(editor instanceof BashEditor))
				return;
			IFile file=((FileEditorInput)editor.getEditorInput()).getFile();
			BashStackFrame frame=(BashStackFrame)context;
			BashSourceLookupParticipant.putLookupSourceItem(frame.getSourceName(), file);
			SourceLookupResult result=new SourceLookupResult(frame,file,"bash.editor",new FileEditorInput(file));
			DebugUITools.displaySource(result, getSite().getPage());
			BashLaunchDelegate.target.debugger.sendUICommand(UICommand.BreakpointToggled);
			
		}
	}
	
	private class RapidModeAction extends TextEditorAction {
		public RapidModeAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
			super(bundle, prefix, editor);
			setChecked(BashLaunchDelegate.rapidMode);
		}
		public void run() {
			if(BashLaunchDelegate.rapidMode)
				BashLaunchDelegate.rapidMode=false;
			else
				BashLaunchDelegate.rapidMode=true;
		}
		public void update(){
			setChecked(BashLaunchDelegate.rapidMode);
		}
	}
	
	private BashOutlinePage outlinePage; 
	public BashSourceViewer viewer;
    @SuppressWarnings("restriction")
	public BashEditor() {
		super();
		try {
			DebugUIPlugin.getDefault().getPreferenceStore().setValue(IDebugUIConstants.PREF_REUSE_EDITOR,false);
		} catch (Exception e) {	}
		setSourceViewerConfiguration(new BashConfiguration(new BashColorManager()));
		setDocumentProvider(new BashDocumentProvider());

		// added for making hover work
        setRulerContextMenuId("bash.editor.rulerMenu");
        setEditorContextMenuId("bash.editor.editorMenu");
    }
    
    @SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
    { 
    	
        if (adapter.equals(IContentOutlinePage.class))
        {
            IEditorInput input = getEditorInput();

            if (input!=null && input instanceof IFileEditorInput)
            {
                if (outlinePage == null)
                {
                	outlinePage = new BashOutlinePage(getDocumentProvider(), this);
			    				if (input != null)
			    					outlinePage.setInput(input);
	                }
                	return outlinePage;
                
            }
        }     	
    	
    	
        return super.getAdapter(adapter); 
    }

    
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions() {
        super.createActions();
        ResourceBundle bundle = ResourceBundle.getBundle("net.sf.basheclipse.ui.model.BashEditorMessages"); //$NON-NLS-1$
		IAction a= new MapToSelectedBashStackFrameAction(bundle, "MapToSelectedBashStackFrame.", this); //$NON-NLS-1$
		setAction("MapToSelectedBashStackFrame", a);
		a= new RapidModeAction(bundle, "RapidMode.", this); //$NON-NLS-1$
		setAction("RapidMode", a); 		
        
    }

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		addAction(menu, "MapToSelectedBashStackFrame");  //$NON-NLS-1$
		addAction(menu, "RapidMode");  //$NON-NLS-1$
		super.editorContextMenuAboutToShow(menu);
	}
    
	public void doSaveAs() {
		super.doSaveAs();
		convertLineDelimiters();
		refresh();
	}
	
	void refresh(){
		if (outlinePage != null){
			outlinePage.update();
		}
	}
	
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		convertLineDelimiters();
		refresh();
	}

	protected void convertLineDelimiters(){
		IFile file=((FileEditorInput)getEditorInput()).getFile();
		FileBufferOperationRunner runner= new FileBufferOperationRunner(FileBuffers.getTextFileBufferManager(), null);
		try {
			runner.execute(new IPath[]{file.getFullPath()}, new ConvertLineDelimitersOperation("\n"), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		viewer = new BashSourceViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles, this);

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}
	
	
	
}
