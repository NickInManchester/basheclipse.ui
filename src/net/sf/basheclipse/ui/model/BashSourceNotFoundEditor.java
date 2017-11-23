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

import net.sf.basheclipse.core.launching.BashLaunchDelegate;
import net.sf.basheclipse.core.launching.BashSourceLookupParticipant;
import net.sf.basheclipse.core.model.BashStackFrame;
import net.sf.basheclipse.core.model.Debugger.UICommand;
import net.sf.basheclipse.ui.launching.BashMainTab;
import net.sf.basheclipse.ui.launching.BashMainTab.BashSelectionDialog;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.ui.sourcelookup.ISourceLookupResult;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.sourcelookup.CommonSourceNotFoundEditor;
import org.eclipse.debug.ui.sourcelookup.CommonSourceNotFoundEditorInput;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;

import org.eclipse.ui.part.FileEditorInput;

public class BashSourceNotFoundEditor extends CommonSourceNotFoundEditor {
	
	protected void selectBashSource(){
		BashMainTab.BashSelectionDialog dialog=new BashSelectionDialog(getEditorSite().getWorkbenchWindow().getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		CommonSourceNotFoundEditorInput editorInput=(CommonSourceNotFoundEditorInput)getEditorInput();
		BashStackFrame frame=(BashStackFrame)editorInput.getArtifact();
		dialog.setTitle("Source for: "+frame.getSourceName());
		if (dialog.open() == Window.OK) {
			Object[] files = dialog.getResult();
			IFile file = (IFile) files[0];
			BashSourceLookupParticipant.putLookupSourceItem(frame.getSourceName(), file);
			//SourceLookupManager.getDefault().displaySource(frame, getSite().getPage(), true);
			SourceLookupResult result=new SourceLookupResult(frame,file,"net.sourceforge.shelled.ui.editor",new FileEditorInput(file));
			DebugUITools.displaySource(result, getSite().getPage());
			BashLaunchDelegate.target.debugger.sendUICommand(UICommand.BreakpointToggled);
		}
	}
	
	protected void createButtons(Composite parent) {
		GridData data;
		Button button = new Button(parent, SWT.PUSH);
		data = new GridData();
		data.grabExcessHorizontalSpace = false;
		data.grabExcessVerticalSpace = false;
		button.setLayoutData(data);
		button.setText("Select Bash script"); 
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				selectBashSource();
			}
		});
	}

	public static class SourceLookupResult implements ISourceLookupResult {
	    private Object fArtifact;
	    private Object fSourceElement;
	    private String fEditorId;
	    private IEditorInput fEditorInput;
	    public SourceLookupResult(Object artifact, Object sourceElement, String editorId, IEditorInput editorInput) {
	        fArtifact = artifact;
	        fSourceElement=sourceElement;
	        fEditorId=editorId;
	        fEditorInput=editorInput;
	    }
	    public Object getArtifact() {
	        return fArtifact;
	    }
	    public Object getSourceElement() {
	        return fSourceElement;
	    }
	    public String getEditorId() {
	        return fEditorId;
	    }
	    public IEditorInput getEditorInput() {
	        return fEditorInput;
	    }
	}
	
	
	
}
