package org.jkiss.dbeaver.model.sourcecode.editors;

import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.ActionUtils;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.editors.StringEditorInput;
import org.jkiss.dbeaver.ui.editors.SubEditorSite;
import org.jkiss.dbeaver.ui.editors.text.BaseTextEditor;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class JavaEditor extends BaseTextEditor {
	
	 private StringEditorInput codeInput;
	 private ProjectionSupport fProjectionSupport;
	  private IEditorSite subSite;
	 
	 public JavaEditor(final IWorkbenchPartSite parentSite){
		 this(parentSite,true);
	 }
	 public JavaEditor(final IWorkbenchPartSite parentSite,boolean readOnly){
		 super(); 
		 this.subSite = new SubEditorSite(parentSite);
		 this.codeInput= new StringEditorInput("Source Code View", "", readOnly, GeneralUtils.getDefaultFileEncoding());
		
		 if(!readOnly)
		 {
			 this.codeInput.setReadOnly(false);
		 }
		 
		 SourceViewer viewer = getViewer();
	        if (viewer != null) {
	            StyledText textWidget = viewer.getTextWidget();
	            if (textWidget != null) {
	                textWidget.addModifyListener(this::onTextChange);
	            }
	        }
	 }
	 
	 private void onTextChange(ModifyEvent e) {
		 
		 System.out.println("onTextChange Event:!!!!!");
	 }

	public String getCode() {
		 String newContent = getDocument().get();
		 return newContent;
	        //return codeInput.getBuffer().toString();
	 }
	
	protected String getCodeInputText() {
		return codeInput.getBuffer().toString();
	}
	 
	 public void setCode(String code)
	 {
		 this.codeInput.setText(code);
		 this.setInput(codeInput);
		  
	 }

	
	 @Override
	  public void createPartControl(Composite parent) {
		 
		 	Composite panel = UIUtils.createPlaceholder(parent, 1);
	        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
	        
	        Composite editorPH = new Composite(panel, SWT.BORDER);
	        GridData gd = new GridData(GridData.FILL_BOTH);
	        gd.verticalIndent = 3;
	        gd.horizontalSpan = 1;
	        gd.minimumHeight = 100;
	        gd.minimumWidth = 100;
	        editorPH.setLayoutData(gd);
	        editorPH.setLayout(new FillLayout());
	        
	        setRangeIndicator(new DefaultRangeIndicator());
	        
	        Composite control=new Composite(editorPH, SWT.NONE);
	        control.setLayout(new FillLayout());
	        
	        updateCode();
			super.createPartControl(control);
//			ProjectionViewer viewer= (ProjectionViewer) getSourceViewer();
//			fProjectionSupport= new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
//			fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
//			fProjectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
//			fProjectionSupport.install();
//			viewer.doOperation(ProjectionViewer.TOGGLE);
		}
	
	 
	
	 public void updateCode()
	    {
	        try {
	            this.codeInput.setText(getCodeInputText());
	            if (this.getSite() != null) {
	                setInput(codeInput);
	            } else {
	                init(subSite, codeInput);
	            }
	            
	        } catch (PartInitException e) {
	            DBWorkbench.getPlatformUI().showError("", null, e);
	        }
	    }
	 
	 	@Override
	    protected void editorContextMenuAboutToShow(IMenuManager menu) {
	        super.editorContextMenuAboutToShow(menu);
//	        addAction(menu, ITextEditorActionConstants.GROUP_EDIT, ITextEditorActionConstants.SHIFT_RIGHT);
//	        addAction(menu, ITextEditorActionConstants.GROUP_EDIT, ITextEditorActionConstants.SHIFT_LEFT);
	        //addAction(menu, ITextEditorActionConstants.GROUP_SAVE, "sourcecode.template.save");
//	        menu.add(ActionUtils.makeCommandContribution(getSite(), "sourcecode.template.save"));
	      if(saveMenuAction!=null && this.isEditable())
	      {
	    	 
	    	  IAction  saveAction= getAction(ITextEditorActionConstants.SAVE);
	    	  if(saveAction!=null )
	    	  {
	    		  if(!saveAction.equals(saveMenuAction)) {
	    		  setAction(ITextEditorActionConstants.SAVE, saveMenuAction);
	    		  }
	    	  }else {
	    		 menu.add(saveAction);
	    		 
	    		 
	    	  }
	      }
	       
	    }
	 	private IAction saveMenuAction;
	 	public void addSaveMenuAction(IAction action)
	 	{
	 		ICommandImageService imgService= this.getSite().getService(ICommandImageService.class);
	 		saveMenuAction=action; //addAction(this.action, ITextEditorActionConstants.SAVE);
	 		action.setImageDescriptor(imgService.getImageDescriptor(IWorkbenchCommandConstants.FILE_SAVE, ICommandImageService.TYPE_DEFAULT));
	 		
	 	}
	 	
	  
	 
	   
}
