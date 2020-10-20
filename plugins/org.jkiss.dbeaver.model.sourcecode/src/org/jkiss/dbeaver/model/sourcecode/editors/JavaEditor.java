package org.jkiss.dbeaver.model.sourcecode.editors;

import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.editors.StringEditorInput;
import org.jkiss.dbeaver.ui.editors.SubEditorSite;
import org.jkiss.dbeaver.ui.editors.text.BaseTextEditor;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class JavaEditor extends BaseTextEditor {
	
	 private StringEditorInput codeInput;
	 private ProjectionSupport fProjectionSupport;
	  private IEditorSite subSite;
	 
	 public JavaEditor(final IWorkbenchPartSite parentSite){
		 super();
		 this.subSite = new SubEditorSite(parentSite);
		 this.codeInput= new StringEditorInput("", "", true, GeneralUtils.getDefaultFileEncoding());
		 
	 }
	 
	 
	 public String getCode() {
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
	
	 
	
	 protected void updateCode()
	    {
	        try {
	            this.codeInput.setText(getCode());
	            if (this.getSite() != null) {
	                setInput(codeInput);
	            } else {
	                init(subSite, codeInput);
	            }
	            
	        } catch (PartInitException e) {
	            DBWorkbench.getPlatformUI().showError("", null, e);
	        }
	    }

}
