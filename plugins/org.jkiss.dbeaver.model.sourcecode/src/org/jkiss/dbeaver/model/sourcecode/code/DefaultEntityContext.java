package org.jkiss.dbeaver.model.sourcecode.code;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.core.CodeColumnAttribute;
import org.jkiss.dbeaver.model.sourcecode.core.CodeTemplate;
import org.jkiss.dbeaver.model.sourcecode.core.DBSTableCodeContext;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeSetting;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeTableColumn;
import org.jkiss.dbeaver.model.sourcecode.core.TemplateContext;
import org.jkiss.dbeaver.model.sourcecode.editors.JavaEditor;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.EntityContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormConstants;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.TabItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.CustomTableEditor;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;

public class DefaultEntityContext implements EntityContext{
	private List<TabItemContext> mTabs;
	private DBSTable mTable;
	private String mCode;
	private SourceCodeSetting mSourceCodeSetting;
	private Table mEntityTable;
	private Table mTemplateTable;
	private JavaTemplateContext mJavaTemplateContext;
	private ViewDataRunnableEvent mViewDataRunableEvent;
	private JavaEditor mTemplateEdit;
	private TemplateContext mTemplateContext;
	private FormContext mFormContext;
	private DBSTableCodeContext mDBSTableCodeContext;
	private int loadTableTime=0;
	private List<CodeColumnAttribute> mColumns;
	public DefaultEntityContext(DBSTable table) {
		super();
		this.mSourceCodeSetting=SourceCodeSettingContext.loadStoreContext();
		this.mTable=table;
		mTemplateContext=new TemplateContext();
		this.mDBSTableCodeContext=new DBSTableCodeContext(mTable);
		init_tabs();
	}
	 
	private String loadTemplateFile(String templateFile)
	{
		String result=null;
		try {
		// TEST Start
		File metadataFolder= DBWorkbench.getPlatform().getWorkspace().getAbsolutePath();
		System.out.println("1,metadataFolder="+metadataFolder);
		metadataFolder= DBWorkbench.getPlatform().getWorkspace().getActiveProject().getWorkspace().getAbsolutePath();
		System.out.println("2,metadataFolder="+metadataFolder);
		metadataFolder= DBWorkbench.getPlatform().getWorkspace().getMetadataFolder();
		System.out.println("3,metadataFolder="+metadataFolder);
		
		File tplDir=new File(metadataFolder, "codetemplate");
		if(!tplDir.exists() && !tplDir.mkdirs()) {
			return null;
		}
		File tplFile=new File(tplDir,templateFile+".template");
		if(tplFile.exists())
		{
			result=new String(Files.readAllBytes(tplFile.toPath()));
		}else {
			 result = CodeHelper.loadResource(templateFile);
			Files.write(tplFile.toPath(), result.getBytes(),StandardOpenOption.CREATE);
		}
		
		// result = CodeHelper.loadResource(templateFile);
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	 @Override
	 public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		 
		if(this.mTable!=null)
		{  
			if(loadTableTime==0) {
				this.mDBSTableCodeContext.run(monitor, mFormContext.getForms());
				mColumns=this.mDBSTableCodeContext.getColumns();
				if(mColumns!=null)
				{
					createEntityTableItem(mColumns);
				}
				loadTableTime++;
			}else {
				mDBSTableCodeContext.pushColumnsAttribute(mColumns, mFormContext.getForms());
			}
			mCode=mTemplateContext.convertActivityTemplate(mDBSTableCodeContext); 
		}
	}
	
	private void init_tabs()
	{
		if(this.mTabs==null)
		{
			this.mTabs=new ArrayList<TabItemContext>();
			String[] ids= {"Template","Entity","Setting"};
			String[] titles= {UIMessages.dbeaver_generate_sourcecode_template,UIMessages.dbeaver_generate_sourcecode_entity_object,UIMessages.dbeaver_generate_sourcecode_settings};
			for(int i=0,k=titles.length;i<k;i++)
			{
				TabItemContext item=new TabItemContext();
				item.setId(ids[i]);
				item.setName(titles[i]);
				item.setTip(titles[i]);
				this.mTabs.add(item);
			}
		}
	}

	 
	@Override
	public String generateCode() {
		 return mCode;
	}


	@Override
	public List<TabItemContext> panelTabs() {
		
		return mTabs;
	}
	
	
	
	@Override
	public void setViewDataRunnableEvent(ViewDataRunnableEvent event) {
		this.mViewDataRunableEvent=event;
	}


	@Override
	public void createPanelTabs(IWorkbenchPart part,CTabFolder panelFolder) {
		 panelFolder.setRedraw(false);
	        
         List<TabItemContext> tabItems=panelTabs();
      for(int i=0,k=tabItems.size();i<k;i++)
         { 
    	  TabItemContext tabItem=tabItems.get(i);
    	   		 
	       CTabItem panelTab = new CTabItem(panelFolder, SWT.NONE);
	       panelTab.setData(tabItem.getId());
	       panelTab.setText(tabItem.getName());
	       panelTab.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
	       panelTab.setToolTipText(tabItem.getTip());
	       panelTab.setShowClose(false); 
	       panelTab.setControl(createTabContents(part,tabItem, panelFolder));
	       if(i==0)
	       {
	    	   		panelFolder.setSelection(panelTab);
	       }
	      
         }
      panelFolder.setRedraw(true);
	}
	
	 
	public Control createTabContents(IWorkbenchPart part,TabItemContext context,  Composite parent) {
		if("Setting".equals(context.getId())){
			return createSettingContents(context, parent);	
		}else if("Entity".equals(context.getId())) {
			return createEntityContents(context,parent);
		}else if("Template".equals(context.getId())){	
			return createTemplateContents(part,context,parent);
		}else {
		  Text textView=new Text(parent, SWT.NONE);
	       textView.setText("HELLO WORLD ! NAME:"+context.getName());
	       return textView;
		}
	}
	
	private Control createSettingContents(TabItemContext context,  Composite parent) {
//		 Group settings = UIUtils.createControlGroup(parent, UIMessages.dbeaver_generate_sourcecode_settings, 2, GridData.FILL_HORIZONTAL, SWT.DEFAULT);
//		 Text  directoryText=DialogUtils.createOutputFolderChooser(settings, UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder,mSourceCodeSetting.getOutPutDir(), e->{});
//		 Text packageNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_packageName, mSourceCodeSetting.getPackagePath());
//		 Text pageClassFullNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_pageClassFullName,mSourceCodeSetting.getClassPage());
//		 Text groupNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_groupName, mSourceCodeSetting.getGroupName());
//		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_jsonView, mSourceCodeSetting.getClassJsonView());
//		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_businessException, mSourceCodeSetting.getClassBusinessException());
//		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_assertUtils, mSourceCodeSetting.getClassAssertUtils());
//		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_baseController,mSourceCodeSetting.getClassBaseController());
		  
		  mFormContext=new FormContext(null);
		  mFormContext.addFormItem(new FormItemContext("outPutDir",  UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder, mSourceCodeSetting.getOutPutDir(), FormConstants.FORM_FILE,FormConstants.FORM_VALUETYPE_TEXT));
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
		  return mFormContext.createControls(parent,this.mViewDataRunableEvent);
	}
	
	private Control createTemplateContents(IWorkbenchPart part,TabItemContext context,  Composite parent) {
		
		SashForm viewerSash= UIUtils.createPartDivider(part, parent, SWT.VERTICAL | SWT.SMOOTH);
		viewerSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mTemplateTable=new Table(viewerSash, SWT.BORDER | SWT.FULL_SELECTION);
		mTemplateTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mTemplateTable.setHeaderVisible(true);
		mTemplateTable.setLinesVisible(true);
		UIUtils.createTableColumn(mTemplateTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_name).setWidth(150);
        UIUtils.createTableColumn(mTemplateTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_description);
        mTemplateTable.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(e.item instanceof TableItem)
				{
					TableItem tableItem= (TableItem)e.item;
					
					Object tabelColumn=tableItem.getData("index");
					if(tabelColumn!=null) {
						
						int index=(int)tabelColumn;
						mTemplateEdit.setCode(mTemplateContext.setActivityTemplate(index).getTemplate());
						if(mViewDataRunableEvent!=null)
						{
							mViewDataRunableEvent.refreshActions();
						}
					}
					
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        for(int i=0,k=mTemplateContext.size();i<k;i++)
		{
			  CodeTemplate codeTemplate=mTemplateContext.get(i); 
				TableItem item=new TableItem(mTemplateTable, SWT.NONE);
				item.setData("index", i);
				item.setText(0, codeTemplate.getName());
				item.setText(1, codeTemplate.getDesc());
		}
        

        mTemplateEdit=new JavaEditor( part.getSite(),false);
        mTemplateEdit.createPartControl(viewerSash);
        mTemplateEdit.addSaveMenuAction(new SaveTefmplateAction());
		
        UIUtils.asyncExec(() -> UIUtils.packColumns(mTemplateTable, true));
        
        
        return viewerSash;
	}
	
	 public class SaveTefmplateAction extends Action{
 
		 public  SaveTefmplateAction() {
			setText(UIMessages.dbeaver_generate_sourcecode_save);
		}
		 
			@Override
			public void run() {
				CodeTemplate codeTemplate=mTemplateContext.getActivityTemplate();
//				mTemplateEdit.updateCode();
				String code=mTemplateEdit.getCode();
				System.out.println("TEMPLATE:"+code);
				codeTemplate.setTemplate(code);
				mTemplateContext.saveTemplateToCache(codeTemplate);
				 System.out.println("保存操作");
			}
	    	
	    }

	
	private Control createEntityContents(TabItemContext context,  Composite parent) {
		mEntityTable=new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		mEntityTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mEntityTable.setHeaderVisible(true);
		mEntityTable.setLinesVisible(true);
		UIUtils.createTableColumn(mEntityTable, SWT.LEFT,UIMessages.dbeaver_generate_sourcecode_name).setWidth(300);
        UIUtils.createTableColumn(mEntityTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_object_types).setWidth(300);
        final CustomTableEditor tableEditor = new CustomTableEditor(mEntityTable) {
            {
                firstTraverseIndex = 1;
                lastTraverseIndex = 1;
                editOnEnter = false;
            }
            @Override
            protected Control createEditor(Table table, int index, TableItem item) {
                if (index != 1) {
                    return null;
                }
                CCombo editor = new CCombo(table, SWT.DROP_DOWN | SWT.READ_ONLY);
                editor.setItems(new String[]{"BigDecimal","Date","DateTime","Double","Integer","Long","Object","Short","String","Time"} );
                editor.setText(item.getText(1));
                return editor;
            }
            @Override
            protected void saveEditorValue(Control control, int index, TableItem item) {
            	String javaType=((CCombo) control).getText();
                item.setText(1,javaType);
                Object tableColumn= item.getData("data");
                if(tableColumn!=null && tableColumn instanceof CodeColumnAttribute)
                {
                	CodeColumnAttribute codeColumn=(CodeColumnAttribute)tableColumn;
                	codeColumn.setJavaType(javaType);
                	codeColumn.setJavaPackage(DBSTableCodeContext.getJavaFullType(javaType));
                	if(mViewDataRunableEvent!=null)
                	{
                		mViewDataRunableEvent.refreshActions();
                	}
                }

            }
        };
        UIUtils.asyncExec(() -> UIUtils.packColumns(mEntityTable, true));
        return mEntityTable;
	}
	 private void createEntityTableItem(List<CodeColumnAttribute> columns )
	    {
			if (columns != null) {
				for(int i=0,k=columns.size();i<k;i++)
				{
					CodeColumnAttribute tableColumn=columns.get(i);
					if(i<mEntityTable.getItemCount())
					{
						TableItem item=mEntityTable.getItem(i);
						item.setData("data", tableColumn);
						item.setText(0, tableColumn.getColumnName());
						item.setText(1, tableColumn.getJavaType());
					}else { 
						TableItem item = new TableItem(mEntityTable, SWT.NONE);
						item.setData("data",tableColumn); 
						item.setText(0, tableColumn.getColumnName());
						item.setText(1, tableColumn.getJavaType());
					}
				}
				if(this.mEntityTable.getItemCount()>columns.size())
				{
					for(int i=columns.size(),k=this.mEntityTable.getItemCount();i<k;i++)
					{
						this.mEntityTable.remove(i);
					}
				}
				
			}else {
				if(this.mEntityTable.getItemCount()>0)
				{
					for(int i=0,k=this.mEntityTable.getItemCount();i<k;i++)
					{
						this.mEntityTable.remove(i);
					}
				}
			}
	    }

}

