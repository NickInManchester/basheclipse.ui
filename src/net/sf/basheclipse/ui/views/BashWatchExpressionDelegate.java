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
package net.sf.basheclipse.ui.views;

import net.sf.basheclipse.core.launching.BashLaunchDelegate;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionDelegate;
import org.eclipse.debug.core.model.IWatchExpressionListener;
import org.eclipse.debug.core.model.IWatchExpressionResult;

public class BashWatchExpressionDelegate implements IWatchExpressionDelegate {

	public void evaluateExpression(String expression, IDebugElement context,
			IWatchExpressionListener listener) {
		listener.watchEvaluationFinished(new BashWatchExpressionResult(expression,context));
	}
	class BashWatchExpressionResult implements IWatchExpressionResult{
	
		String expression;
		IValue value;
		String error;
		Exception ex;
		
		BashWatchExpressionResult(String expression, IDebugElement context){
			this.expression=expression;
			if(BashLaunchDelegate.target!=null && !BashLaunchDelegate.target.isTerminated()){
				try {
					value=BashLaunchDelegate.target.debugger.getValue(expression, context);
					if(value==null)
						error="Not found";
				} catch (Exception e) {
					error=e.getMessage();
					ex=e;
				}
			}
			
		}
		public IValue getValue() {
			return value;
		}
	
		public boolean hasErrors() {
			return error!=null;
		}
	
		public String[] getErrorMessages() {
			return new String[]{error};
		}
	
		public String getExpressionText() {
			return expression;
		}
	
		public DebugException getException() {
			if(ex!=null && ex instanceof DebugException)
				return (DebugException) ex;
			return null;
		}
	}
}
