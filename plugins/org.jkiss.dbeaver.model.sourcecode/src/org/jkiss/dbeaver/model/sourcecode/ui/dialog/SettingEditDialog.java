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
import org.jkiss.dbeaver.model.sourcecode.core.SettingsContext;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormConstants;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.dialogs.BaseDialog;
import org.jkiss.utils.CommonUtils;

public class SettingEditDialog  extends BaseDialog{

	private SettingsContext mSettingsContext;
	private Text mTextID;
	private Text mTextValue; 
	private Label mTextErrorTip;
	
	public SettingEditDialog(Shell parentShell, String title, DBPImage icon,SettingsContext context) {
		super(parentShell, title, icon);
		 this.mSettingsContext=context;
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
		mTextID=UIUtils.createLabelText(settings,UIMessages.dbeaver_generate_sourcecode_name, "");
		mTextValue=UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_value, "");
       
		mTextErrorTip=UIUtils.createLabel(settings, "");
		return composite;
	}
	
	
	@Override
    protected void buttonPressed(int buttonId)
    {
		String tplId=mTextID.getText();
		if(CommonUtils.isEmpty(tplId))
		{
			mTextErrorTip.setText("名称不能为空");
			return ;
		}
        if (buttonId == IDialogConstants.OK_ID) {
        	List<FormItemContext> templateList=this.mSettingsContext.getItems();
        	if(templateList!=null){
        		for(int i=0,k=templateList.size();i<k;i++) {
        			FormItemContext codeTemplate=templateList.get(i);
        			if(codeTemplate.getId().equals(tplId)) {
        				mTextErrorTip.setText("名称已存在，不能重复使用");
        				return ;
        			}
        		}
        			
        	}
        	FormItemContext newTemplate=new FormItemContext(tplId, tplId,mTextValue.getText(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_TEXT);
        	 
        	this.mSettingsContext.addItem(newTemplate);
        	super.buttonPressed(IDialogConstants.CANCEL_ID);
        }else {
        	super.buttonPressed(IDialogConstants.CANCEL_ID);
        }
    }
	        	
	        
}
