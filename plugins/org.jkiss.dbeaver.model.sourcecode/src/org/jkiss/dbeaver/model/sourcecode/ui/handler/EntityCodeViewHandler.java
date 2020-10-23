package org.jkiss.dbeaver.model.sourcecode.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.dbeaver.model.sourcecode.ui.view.EntityCodeView;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.resultset.ResultSetViewer;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;

public class EntityCodeViewHandler extends AbstractHandler {

	 public static final String CODE_SAVE = "org.jkiss.dbeaver.sourcecode.saveCode";
	 public static final String CODE_COPY = "org.jkiss.dbeaver.sourcecode.copyCode";
	 public static final String CODE_REFRESH = "org.jkiss.dbeaver.sourcecode.refreshCode";
	 
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		  IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
	        if (activePart == null) {
	            return null;
	        }
	        IWorkbenchPartSite site = activePart.getSite();
	        String actionId = event.getCommand().getId();
	        EntityCodeView codeView=getEntityCodeView(activePart);
	        if (codeView!=null) {
	        	switch (actionId) {
					case CODE_REFRESH:{
						codeView.refreshActions();
					}
					
					break;

				case CODE_COPY:{
					 UIUtils.setClipboardContents(site.getShell().getDisplay(), TextTransfer.getInstance(), codeView.getSourceCode());
					
				}
				default:
					break;
				}
	         
	        }
	       
		return null;
	}
	
	private EntityCodeView getEntityCodeView(IWorkbenchPart activePart) {
		  if (activePart != null) {
	            IWorkbenchPartSite site = activePart.getSite();
	            if (site != null && !DBWorkbench.getPlatform().isShuttingDown()) {
	                Shell shell = site.getShell();
	                if (shell != null) {
	                    for (Control focusControl = shell.getDisplay().getFocusControl(); focusControl != null; focusControl = focusControl.getParent()) {
	                        EntityCodeView viewer = (EntityCodeView) focusControl.getData(EntityCodeView.CONTROL_ID);
	                        if (viewer != null) {
	                            return viewer;
	                        }
	                    }
	                }
	            }
	        }
		  return null;

	}
	
	

}
