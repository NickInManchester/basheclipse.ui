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
package net.sf.basheclipse.ui.launching;


import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sf.basheclipse.core.DebugBashPlugin;
import net.sf.basheclipse.core.launching.IBashConstants;
import net.sf.basheclipse.core.launching.BashLaunchDelegate;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

public class BashMainTab extends AbstractLaunchConfigurationTab {
	
	private Text fBashProgramText;
	private Button fBashProgramButton;
	private Text fSocketPortText;
	private Label fSocketPortLabel;
	private Button fStopOnStartup;
	String configurationName="";

	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 0;
		topLayout.numColumns = 3;
		comp.setLayout(topLayout);
		comp.setFont(font);
		
		//createVerticalSpacer(comp, 3);
		Label programLabel = new Label(comp, SWT.NONE);
		programLabel.setText("&Bash script:");
		GridData gd = new GridData(GridData.BEGINNING);
		programLabel.setLayoutData(gd);
		programLabel.setFont(font);
		
		fBashProgramText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fBashProgramText.setLayoutData(gd);
		fBashProgramText.setFont(font);
		fBashProgramText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		fBashProgramButton = createPushButton(comp, "&Browse...", null); //$NON-NLS-1$
		fBashProgramButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseBashFiles();
			}
		});
		// TODO fDebuggingType

		
		fSocketPortLabel = new Label(comp, SWT.NONE);
		fSocketPortLabel.setText("Debugger port:");
		gd = new GridData(GridData.BEGINNING);
		fSocketPortLabel.setLayoutData(gd);
		fSocketPortLabel.setFont(font);
		fSocketPortLabel.setEnabled(true);
		
		fSocketPortText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fSocketPortText.setLayoutData(gd);
		fSocketPortText.setFont(font);
		fSocketPortText.setEditable(true);
		fSocketPortText.setEnabled(true);
		fSocketPortText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NONE);
		createSeparator(comp,3);
		fStopOnStartup = new Button(comp, SWT.CHECK);
		fStopOnStartup.setText("Stop on startup");
		gd = new GridData(GridData.FILL);
		fStopOnStartup.setLayoutData(gd);
		fStopOnStartup.setFont(font);
		fStopOnStartup.setSelection(false);
		fStopOnStartup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		new Label(comp, SWT.NONE);
		new Label(comp, SWT.NONE);
		getLaunchConfigurationDialog().setActiveTab(0);
	}

	public static class BashSelectionDialog extends ResourceListSelectionDialog{
	    public BashSelectionDialog(Shell parentShell, IContainer container,
	            int typeMask) {
	        super(parentShell,container,typeMask);
	    }
	    protected Control createDialogArea(Composite parent) {
	    	Control ret=super.createDialogArea(parent);
	    	refresh(true);	    	
	    	return ret;
	    }
	    protected String adjustPattern() {
	    	String s=super.adjustPattern();
	    	if(!s.equals(""))
	    		return s;
	    	return "*.sh";
	    }
		
	}

	protected void browseBashFiles() {
		BashSelectionDialog dialog = new BashSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
		dialog.setTitle("Bash script");
		dialog.setMessage("Select Bash script");
		if (dialog.open() == Window.OK) {
			Object[] files = dialog.getResult();
			IFile file = (IFile) files[0];
			fBashProgramText.setText(file.getFullPath().toString());
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			configurationName=configuration.getName();
			String program = configuration.getAttribute(IBashConstants.ATTR_BASH_PROGRAM, (String)null);
			if (program == null)
				program ="";
			fBashProgramText.setText(program);
			String port = configuration.getAttribute(IBashConstants.ATTR_SOCKET_PORT, "33333");
			if (port == null)
				port="";
			fSocketPortText.setText(port);
			
			fStopOnStartup.setSelection(configuration.getAttribute(IBashConstants.ATTR_STOP_ON_STARTUP, true));
			
			setMessage(null);
			setErrorMessage(null);
			updateLaunchConfigurationDialog();
			// TODO initializeFrom
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if(fBashProgramText.getText()==null)
			fBashProgramText.setText("");
		String program = fBashProgramText.getText().trim();
		if (program.length() == 0) {
			program = null;
		}
		configuration.setAttribute(IBashConstants.ATTR_BASH_PROGRAM, program);

		String port = fSocketPortText.getText().trim();
		if (port.length() == 0) {
			port = null;
		}
		configuration.setAttribute(IBashConstants.ATTR_SOCKET_PORT, port);

		configuration.setAttribute(IBashConstants.ATTR_STOP_ON_STARTUP,fStopOnStartup.getSelection());
		
		//
		
		// TODO performApply

	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Main";
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		String text = fBashProgramText.getText();
		if (text.length() > 0) {
			IPath path = new Path(text);
			if (ResourcesPlugin.getWorkspace().getRoot().findMember(path) == null) {
				setErrorMessage("Specified program does not exist");
				return false;
			}
		} else {
			setMessage("Specify a bash script");
			return false;
		}
		text = fSocketPortText.getText();
		try{
			int port=Integer.parseInt(text);
			if(port>0xFFFF || port<1 ){
				fSocketPortText.setText("33333");
				return false;
			}
		}catch(Exception ex){
			fSocketPortText.setText("33333");
			return false;
		}
		setMessage(null);
		setErrorMessage(null);
		return super.isValid(launchConfig);
	}
}
