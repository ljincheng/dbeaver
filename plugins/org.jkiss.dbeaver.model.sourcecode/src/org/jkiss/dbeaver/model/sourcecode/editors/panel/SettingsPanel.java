package org.jkiss.dbeaver.model.sourcecode.editors.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;
import org.jkiss.dbeaver.model.sourcecode.core.SettingsContext;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeSetting;
import org.jkiss.dbeaver.model.sourcecode.editors.SourceCodeViewContext;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormConstants;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifierContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;

public class SettingsPanel implements UIEventNotifier{

	private UINotifierContext mUINotifierContext;
	
	private SourceCodeSetting mSourceCodeSetting;
	private FormContext mFormContext;
	
	public SettingsPanel(UINotifierContext context) {
		super();
		this.mUINotifierContext = context;
		this.mSourceCodeSetting=SettingsContext.loadStoreContext();
		mUINotifierContext.add(TYPE_VIEW_CREATE, this);
		mUINotifierContext.add(TYPE_VIEW_ACTION, this);
	}

	 
	@Override
	public void action(int type,UINotifier notifier, Object context) {
		if(TYPE_VIEW_CREATE== type) {
			if(notifier.getCategory()==1 && notifier.getComposite() instanceof CTabFolder)
			{
			   CTabFolder panelFolder=(CTabFolder)notifier.getComposite();
			   CTabItem panelTab = new CTabItem(panelFolder, SWT.NONE);
		       panelTab.setText(UIMessages.dbeaver_generate_sourcecode_settings);
		       panelTab.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
		       panelTab.setToolTipText(UIMessages.dbeaver_generate_sourcecode_settings);
		       panelTab.setShowClose(false); 
	//	       panelTab.setControl(createTabContexts(panelFolder));
		       panelTab.setControl(createPanel(panelFolder));
			}
		}else if(TYPE_VIEW_ACTION == type)
		{
			if(notifier.getCategory()==TYPE_VIEW_ACTION_CATEGORY_CODE && context!=null && context instanceof SourceCodeViewContext && mFormContext!=null)
			{
				SourceCodeViewContext sourceCodeViewContext=(SourceCodeViewContext)context;
				sourceCodeViewContext.setContextSetting(mFormContext.getForms());
				mUINotifierContext.send(TYPE_VIEW_ACTION, new UINotifier(TYPE_VIEW_ACTION_CATEGORY_CODE_SETTING, this, notifier.getComposite()), context);
			}
		}
	}
	
	private Control createPanel(CTabFolder parent) {
		mFormContext=new FormContext(null);
		  mFormContext.addFormItem(new FormItemContext("outPutDir",  UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder, mSourceCodeSetting.getOutPutDir(), FormConstants.FORM_FILE,FormConstants.FORM_VALUETYPE_TEXT));
		  mFormContext.addFormItem(new FormItemContext("author",  UIMessages.dbeaver_generate_sourcecode_preferences_author, mSourceCodeSetting.getAuthor(), FormConstants.FORM_FILE,FormConstants.FORM_VALUETYPE_TEXT));
		  mFormContext.addFormItem(new FormItemContext("packagePath",  UIMessages.dbeaver_generate_sourcecode_packageName, mSourceCodeSetting.getPackagePath(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_TEXT));
		  mFormContext.addFormItem(new FormItemContext("pageDo",  UIMessages.dbeaver_generate_sourcecode_pageClassFullName, mSourceCodeSetting.getClassPage(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("groupName",  UIMessages.dbeaver_generate_sourcecode_groupName, mSourceCodeSetting.getGroupName(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("jsonView",  UIMessages.dbeaver_generate_sourcecode_jsonView, mSourceCodeSetting.getClassJsonView(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("businessException",  UIMessages.dbeaver_generate_sourcecode_businessException, mSourceCodeSetting.getClassBusinessException(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("assertUtils",  UIMessages.dbeaver_generate_sourcecode_assertUtils, mSourceCodeSetting.getClassAssertUtils(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("baseController",  UIMessages.dbeaver_generate_sourcecode_baseController, mSourceCodeSetting.getClassBaseController(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		 
		  mFormContext.addFormItem(new FormItemContext("entity",  UIMessages.dbeaver_generate_sourcecode_entity_object, mSourceCodeSetting.getRuleEntity(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("dao",  "Dao", mSourceCodeSetting.getRuleDao(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("component",  "Component", mSourceCodeSetting.getRuleComponent(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("componentImpl",  "componentImpl", mSourceCodeSetting.getRuleComponentImpl(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("service",  "Service", mSourceCodeSetting.getRuleService(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("serviceImpl",  "serviceImpl", mSourceCodeSetting.getRuleServiceImpl(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  mFormContext.addFormItem(new FormItemContext("controller",  "controller", mSourceCodeSetting.getRuleController(), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
		  ViewDataRunnableEvent mViewDataRunnableEvent=new ViewDataRunnableEvent() {
			
			@Override
			public boolean refreshActions() {
				mUINotifierContext.send(TYPE_VIEW_REFRESH, new UINotifier(TYPE_VIEW_REFRESH_CATEGORY_SETTING, mFormContext, null), null);
				return false;
			}
			
			@Override
			public int exportActions() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		  return mFormContext.createControls(parent,mViewDataRunnableEvent);
	}
	
	 

}
