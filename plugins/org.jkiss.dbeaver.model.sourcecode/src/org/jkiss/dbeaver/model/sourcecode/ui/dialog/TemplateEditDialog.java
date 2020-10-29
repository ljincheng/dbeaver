package org.jkiss.dbeaver.model.sourcecode.ui.dialog;


import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.model.DBPImage;
import org.jkiss.dbeaver.model.sourcecode.core.CodeTemplate;
import org.jkiss.dbeaver.model.sourcecode.core.TemplateContext;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.dialogs.BaseDialog;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.utils.CommonUtils;

public class TemplateEditDialog extends BaseDialog{

	private TemplateContext mTemplateContext;
	private Text mTextID;
	private Text mTextDesc;
	private Text mTextExportPath;
	private Label mTextErrorTip;
	
	public TemplateEditDialog(Shell parentShell, String title, DBPImage icon,TemplateContext context) {
		super(parentShell, title, icon);
		 this.mTemplateContext=context;
	}

	@Override
	protected Composite createDialogArea(Composite parent) {
		Composite composite= super.createDialogArea(parent);
		/*
		 * private String id;
	private String name;
	private String desc;
	private String template; 
	private String exportPath; 
		 */
		Group settings = UIUtils.createControlGroup(composite, UIMessages.dbeaver_generate_sourcecode_settings, 2, GridData.FILL_HORIZONTAL, SWT.DEFAULT);
		mTextID=UIUtils.createLabelText(settings,UIMessages.dbeaver_generate_sourcecode_name, "TPL-1000");
		mTextDesc=UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_description, "我的新加模板1000");
		mTextExportPath=DialogUtils.createOutputFolderChooser(settings,UIMessages.dbeaver_generate_sourcecode_savePosition,"/workspace/temp/code/src/", e->{
        });
		mTextErrorTip=UIUtils.createLabel(settings, "");
		return composite;
	}
	
	
	@Override
    protected void buttonPressed(int buttonId)
    {
		String tplId=mTextID.getText();
		if(CommonUtils.isEmpty(tplId))
		{
			mTextErrorTip.setText("模板名称不能为空");
			return ;
		}
        if (buttonId == IDialogConstants.OK_ID) {
        	List<CodeTemplate> templateList=this.mTemplateContext.getTemplates();
        	if(templateList!=null){
        		for(int i=0,k=templateList.size();i<k;i++) {
        			CodeTemplate codeTemplate=templateList.get(i);
        			if(codeTemplate.getId().equals(tplId)) {
        				mTextErrorTip.setText("模板名称已存在，不能重复使用");
        				return ;
        			}
        		}
        			
        	}
        	CodeTemplate newTemplate=new CodeTemplate(tplId, tplId, mTextDesc.getText());
        	newTemplate.setExportPath(mTextExportPath.getText());
        	this.mTemplateContext.addTemplate(newTemplate);
        	super.buttonPressed(IDialogConstants.CANCEL_ID);
        }else {
        	System.out.println("NOT OK btn!!!!,buttonId="+buttonId);
        	super.buttonPressed(IDialogConstants.CANCEL_ID);
        }
    }
	        	
	        
	
	

	
}
