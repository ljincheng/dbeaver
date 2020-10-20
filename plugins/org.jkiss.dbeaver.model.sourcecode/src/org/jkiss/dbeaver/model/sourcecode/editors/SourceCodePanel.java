package org.jkiss.dbeaver.model.sourcecode.editors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.widgets.WidgetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.model.exec.DBCExecutionContext;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeSetting;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.registry.SourceCodeGenerator;
import org.jkiss.dbeaver.model.sourcecode.ui.preferences.SourceCodePreferences;
import org.jkiss.dbeaver.model.sql.SQLDialect;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.ui.editors.StringEditorInput;
import org.jkiss.dbeaver.ui.editors.sql.SQLEditorBase;
import org.jkiss.dbeaver.ui.editors.sql.dialogs.BaseSQLDialog;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

public class SourceCodePanel {

	
	 private final SourceCodeGenerator<?> sqlGenerator;
	    private SourceCodeSetting sourceCodeSetting;
	    private Text directoryText;
	    private Text packageNameText;
	    private Text pageClassFullNameText;
//	    private Text fileRuleText;
	    private Text groupNameText;
	    
	    private Text tpl_jsonView;
	    private Text tpl_businessException;
	    private Text tpl_assertUtils;
	    private Text tpl_baseController;
	    
	    private DBPPreferenceStore store ;
//	    private StringEditorInput codeInput;
	    private JavaEditor codeViewer;
	    private String title;
	    private String code;
	    
	    public SourceCodePanel(String title,SourceCodeGenerator<?> sqlGenerator) {
	        this.title=title;
	        this.sqlGenerator = sqlGenerator;
	        store = DBWorkbench.getPlatform().getPreferenceStore();
//	        this.codeInput = new StringEditorInput(title, "", true, GeneralUtils.getDefaultFileEncoding());
	    }

	    
	 public Composite createPanel(Composite composite) {
		 sourceCodeSetting=new SourceCodeSetting();
		 
//		 GridLayout layout = new GridLayout(); 
//		 Composite composite = WidgetFactory.composite(SWT.NONE).layout(layout)
//					.layoutData(new GridData(GridData.FILL_BOTH)).create(parent);
//		 Composite composite = new Composite(parent, SWT.NONE);
	
//		 codeViewer= new Label(composite, SWT.NONE);
//		 codeViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
		 IWorkbenchPage activePage = UIUtils.getActiveWorkbenchWindow().getActivePage();
		 codeViewer=new JavaEditor(activePage.getActivePart().getSite());
		 codeViewer.createPartControl(composite);
		 
		// javaEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
 	      
		 Group settings = UIUtils.createControlGroup(composite, UIMessages.dbeaver_generate_sourcecode_settings, 2, GridData.FILL_HORIZONTAL, SWT.DEFAULT);
	        directoryText=DialogUtils.createOutputFolderChooser(settings, UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder,store.getString(SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER), e->{
	        	sqlGenerator.setRootPath(directoryText.getText());
	        	sourceCodeSetting.setOutPutDir(directoryText.getText());
	        });
	        packageNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_packageName, store.getString(SourceCodePreferences.SOURCECODE_PACKAGENAME));
	        pageClassFullNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_pageClassFullName, store.getString(SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME));
//	        fileRuleText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_entitySuffix, sqlGenerator.getFileRule());
	        groupNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_groupName, store.getString(SourceCodePreferences.SOURCECODE_GROUPNAME));
	        tpl_jsonView= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_jsonView, store.getString(SourceCodePreferences.SOURCECODE_JSONVIEW));
	        tpl_businessException= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_businessException, store.getString(SourceCodePreferences.SOURCECODE_BUSINESSEXCEPTION));
	        tpl_assertUtils= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_assertUtils, store.getString(SourceCodePreferences.SOURCECODE_ASSERTUTILS));
	        tpl_baseController= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_baseController, store.getString(SourceCodePreferences.SOURCECODE_BASECONTROLLER));
	        
	        sqlGenerator.setRootPath(directoryText.getText());
	        sourceCodeSetting.setOutPutDir(directoryText.getText());
	        sqlGenerator.setPackageName(packageNameText.getText());
	        sourceCodeSetting.setPackagePath(packageNameText.getText());
//	        sqlGenerator.setEntitySuffix(fileRuleText.getText());
	        sqlGenerator.setPageClassFullName(pageClassFullNameText.getText());
	        sourceCodeSetting.setClassPage(pageClassFullNameText.getText());
	        sqlGenerator.setAuthor(store.getString(SourceCodePreferences.SOURCECODE_AUTHOR));
	        sourceCodeSetting.setAuthor(store.getString(SourceCodePreferences.SOURCECODE_AUTHOR));
	        sqlGenerator.setGroupName(groupNameText.getText());
	        sourceCodeSetting.setGroupName(groupNameText.getText());
	        
	        sqlGenerator.setTpl_assertUtils(tpl_assertUtils.getText());
	        sourceCodeSetting.setClassAssertUtils(tpl_assertUtils.getText());
	        sqlGenerator.setTpl_baseController(tpl_baseController.getText());
	        sourceCodeSetting.setClassBaseController(tpl_baseController.getText());
	        sqlGenerator.setTpl_businessException(tpl_businessException.getText());
	        sourceCodeSetting.setClassBusinessException(tpl_businessException.getText());
	        sqlGenerator.setTpl_jsonView(tpl_jsonView.getText());
	        sourceCodeSetting.setClassJsonView(tpl_jsonView.getText());
	        
	        //设置文件规则
	        sqlGenerator.setRuleEntity(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY));
	        sourceCodeSetting.setRuleEntity(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY));
	        sqlGenerator.setRuleEntityLombokData(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY_LOMBOKDATA));
	        sqlGenerator.setRuleDao(store.getString(SourceCodePreferences.SOURCECODE_RULE_DAO));
	        sourceCodeSetting.setRuleDao(store.getString(SourceCodePreferences.SOURCECODE_RULE_DAO));
	        sqlGenerator.setRuleComponent(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT));
	        sourceCodeSetting.setRuleComponent(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT));
	        sqlGenerator.setRuleComponentImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL));
	        sourceCodeSetting.setRuleComponentImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL));
	        sqlGenerator.setRuleService(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE));
	        sourceCodeSetting.setRuleService(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE));
	        sqlGenerator.setRuleServiceImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL));
	        sourceCodeSetting.setRuleServiceImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL));
	        sqlGenerator.setRuleController(store.getString(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER));
	        sourceCodeSetting.setRuleController(store.getString(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER));
	        sqlGenerator.setSourceCodeSetting(sourceCodeSetting);
	        UIUtils.runInUI(sqlGenerator);
	        
	        Object sql = sqlGenerator.getResult();
	        if (sql != null) {
	        	setCode(CommonUtils.toString(sql));
	        	updateCode();
	        }
	       
	        
	        return composite;
	 }
	 
	 public void setCode(String code)
	 {
		 this.code=code;
	 }
	 
	 public void updateCode()
	    {
//	        try {
//	            this.codeInput.setText(code);
	           
	            codeViewer.setCode(code);
	           
//	            codeViewer.reloadSyntaxRules();
//	        } catch (PartInitException e) {
//	            DBWorkbench.getPlatformUI().showError(getShell().getText(), null, e);
//	        	e.printStackTrace();
//	        }
	    }
}
