package org.jkiss.dbeaver.model.sourcecode.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIActionEvent;
import org.jkiss.dbeaver.model.sourcecode.ui.view.SourceCodeViewer;
import org.jkiss.dbeaver.runtime.DBWorkbench;

public class EntityCodeViewHandler extends AbstractHandler {

	 public static final String CODE_SAVE = "org.jkiss.dbeaver.sourcecode.saveCode";
	 public static final String CODE_EXPORT = "org.jkiss.dbeaver.sourcecode.exportCode";
	 public static final String CODE_COPY = "org.jkiss.dbeaver.sourcecode.copyCode";
	 public static final String CODE_REFRESH = "org.jkiss.dbeaver.sourcecode.refreshCode";
	 public static final String CODE_NEWADD = "org.jkiss.dbeaver.sourcecode.newAdd";
	 public static final String CODE_DELETE = "org.jkiss.dbeaver.sourcecode.delete";
	 
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		  IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
	        if (activePart == null) {
	            return null;
	        }
//	        IWorkbenchPartSite site = activePart.getSite();
	        String actionId = event.getCommand().getId();
	        System.out.println("Action:actionId="+actionId);
	        UIActionEvent actionEventObj=getActionEventObject(activePart);
	        if(actionEventObj!=null)
	        {
	        	actionEventObj.executeAction(event);
	        }

	        { 
		        SourceCodeViewer codeView=getSourceCodeViewer(activePart);
		        if (codeView!=null) {
		        	switch (actionId) {
						case CODE_REFRESH:{
							codeView.actionRefresh();
						}
						
						break;
	
					case CODE_COPY:{
						codeView.actionCopy();
	//					 UIUtils.setClipboardContents(site.getShell().getDisplay(), TextTransfer.getInstance(), codeView.getSourceCode());
						
					}
					break;
					case CODE_EXPORT:{
						codeView.actionExport();
					}break;
					default:
						break;
					}
		         
		        }
	        }
	       
		return null;
	}
	
	private SourceCodeViewer getSourceCodeViewer(IWorkbenchPart activePart) {
		  if (activePart != null) {
	            IWorkbenchPartSite site = activePart.getSite();
	            if (site != null && !DBWorkbench.getPlatform().isShuttingDown()) {
	                Shell shell = site.getShell();
	                if (shell != null) {
	                    for (Control focusControl = shell.getDisplay().getFocusControl(); 
	                    		focusControl != null; 
	                    		focusControl = focusControl.getParent()) {
	                    	SourceCodeViewer viewer = (SourceCodeViewer) focusControl.getData(SourceCodeViewer.CONTROL_ID);
	                        if (viewer != null) {
	                            return viewer;
	                        }
	                    }
	                }
	            }
	        }
		  return null;

	}
	
	private UIActionEvent getActionEventObject(IWorkbenchPart activePart) {
		 if (activePart != null) {
	            IWorkbenchPartSite site = activePart.getSite();
	            if (site != null && !DBWorkbench.getPlatform().isShuttingDown()) {
	                Shell shell = site.getShell();
	                if (shell != null) {
	                    for (Control focusControl = shell.getDisplay().getFocusControl(); 
	                    		focusControl != null; 
	                    		focusControl = focusControl.getParent()) {
	                    	UIActionEvent viewer = (UIActionEvent) focusControl.getData(UIActionEvent.COMMAND_OBJECT_ID);
	                        if (viewer != null && viewer instanceof UIActionEvent) {
	                            return viewer;
	                        }
	                    }
	                }
	            }
	        }
		  return null;
	}
	
	

}
