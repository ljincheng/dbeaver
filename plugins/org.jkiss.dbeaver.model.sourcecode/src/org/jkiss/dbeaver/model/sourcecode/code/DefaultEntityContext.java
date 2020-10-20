package org.jkiss.dbeaver.model.sourcecode.code;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.core.CoreMessages;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithProgress;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeSetting;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeTableColumn;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.EntityContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.TabItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent;
import org.jkiss.dbeaver.model.sourcecode.ui.preferences.SourceCodePreferences;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.CustomTableEditor;
import org.jkiss.dbeaver.ui.dialogs.ConfirmationDialog;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.ui.editors.data.internal.DataEditorsMessages;
import org.jkiss.utils.CommonUtils;

public class DefaultEntityContext implements EntityContext{
	private List<TabItemContext> mTabs;
	private DBSTable mTable;
	private Map<String, String> mSelectedTemplate;
	private List<Map<String, String>> mTemplateList;
	private String mCode;
	private SourceCodeSetting mSourceCodeSetting;
	private Table mEntityTable;
	private Table mTemplateTable;
	private JavaTemplateContext mJavaTemplateContext;
	private ViewDataRunnableEvent mViewDataRunableEvent;
	
	public DefaultEntityContext(DBSTable table) {
		super();
		this.mSourceCodeSetting=SourceCodeSettingContext.loadStoreContext();
		this.mTable=table;
		init_tabs();
		
		mTemplateList=new ArrayList<Map<String, String>>();
		
		mTemplateList.add(new HashMap<String, String>(){{put("type","Entity");put("template","java_entity");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","LombokData");put("template","java_lombokdata");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Dao");put("template","java_dao");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Component");put("template","java_component");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","ComponentImpl");put("template","java_component_impl");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Service");put("template","java_service");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","ServiceImpl");put("template","java_service_impl");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Controller");put("template","java_controller");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Html-list(Thymeleaf)");put("template","html_thymeleaf_list");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Html-list-table(Thymeleaf)");put("template","html_thymeleaf_list_table");}});
		mTemplateList.add(new HashMap<String, String>(){{put("type","Html-add(Thymeleaf)");put("template","html_thymeleaf_add");}});
	}
	 
	
	 @Override
	 public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		 if(CommonUtils.isEmpty(mSelectedTemplate))
			{
				String templateFile=mTemplateList.get(0).get("template");
				String tplText = CodeHelper.loadResource(templateFile);
				mCode= templateCode(monitor,tplText);
			}else {
				String tplText = CodeHelper.loadResource(mSelectedTemplate.get("template"));
				mCode=templateCode(monitor,tplText);
			}
	}
	
	private void init_tabs()
	{
		if(this.mTabs==null)
		{
			this.mTabs=new ArrayList<TabItemContext>();
			String[] titles= {"Entity","Template","Setting"};
			for(int i=0,k=titles.length;i<k;i++)
			{
				TabItemContext item=new TabItemContext();
				item.setId(titles[i]);
				item.setName(titles[i]);
				item.setTip(titles[i]);
				this.mTabs.add(item);
			}
		}
	}

	private String templateCode(DBRProgressMonitor monitor,String template) {
		if(this.mTable!=null)
		{
			
			if(mJavaTemplateContext==null)
			{
				mJavaTemplateContext=new JavaTemplateContext(this.mTable, mSourceCodeSetting, monitor);
			}
			
			Object columns=mJavaTemplateContext.getVariable("columns");
			if(columns!=null) {
				createEntityTableItem((List<SourceCodeTableColumn>)columns);
			}
			String result =cn.booktable.template.TemplateEngine.process(template, mJavaTemplateContext);
			return result;
		}
		return null;
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
	public void createPanelTabs(CTabFolder panelFolder) {
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
	       panelTab.setControl(createTabContents(tabItem, panelFolder));
	       if(i==0)
	       {
	    	   		panelFolder.setSelection(panelTab);
	       }
	      
         }
      panelFolder.setRedraw(true);
	}
	
	@Override
	public Control createTabContents(TabItemContext context,  Composite parent) {
		if("Setting".equals(context.getId())){
			return createSettingContents(context, parent);	
		}else if("Entity".equals(context.getId())) {
			return createEntityContents(context,parent);
		}else if("Template".equals(context.getId())){	
			return createTemplateContents(context,parent);
		}else {
		  Text textView=new Text(parent, SWT.NONE);
	       textView.setText("HELLO WORLD ! NAME:"+context.getName());
	       return textView;
		}
	}
	
	private Control createSettingContents(TabItemContext context,  Composite parent) {
		 Group settings = UIUtils.createControlGroup(parent, UIMessages.dbeaver_generate_sourcecode_settings, 2, GridData.FILL_HORIZONTAL, SWT.DEFAULT);
		 Text  directoryText=DialogUtils.createOutputFolderChooser(settings, UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder,mSourceCodeSetting.getOutPutDir(), e->{});
		 Text packageNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_packageName, mSourceCodeSetting.getPackagePath());
		 Text pageClassFullNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_pageClassFullName,mSourceCodeSetting.getClassPage());
		 Text groupNameText= UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_groupName, mSourceCodeSetting.getGroupName());
		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_jsonView, mSourceCodeSetting.getClassJsonView());
		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_businessException, mSourceCodeSetting.getClassBusinessException());
		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_assertUtils, mSourceCodeSetting.getClassAssertUtils());
		  UIUtils.createLabelText(settings, UIMessages.dbeaver_generate_sourcecode_baseController,mSourceCodeSetting.getClassBaseController());
		  
		 return settings;
	}
	
	private Control createTemplateContents(TabItemContext context,  Composite parent) {
		mTemplateTable=new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		mTemplateTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mTemplateTable.setHeaderVisible(true);
		mTemplateTable.setLinesVisible(true);
		UIUtils.createTableColumn(mTemplateTable, SWT.LEFT,CoreMessages.pref_page_connection_types_label_table_column_name);
        UIUtils.createTableColumn(mTemplateTable, SWT.LEFT, CoreMessages.pref_page_query_manager_group_object_types);
        mTemplateTable.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if(e.item instanceof TableItem)
				{
					TableItem tableItem= (TableItem)e.item;
					Object tabelColumn=tableItem.getData("tabelColumn");
					if(tabelColumn!=null) {
						mSelectedTemplate=(Map)tabelColumn;
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
        for(int i=0,k=mTemplateList.size();i<k;i++)
		{
			Map<String, String> tableColumn=mTemplateList.get(i); 
				TableItem item=new TableItem(mTemplateTable, SWT.NONE);
				item.setData("tabelColumn", tableColumn);
				item.setText(0, tableColumn.get("type"));
				item.setText(1, tableColumn.get("template"));
		}
         
        UIUtils.asyncExec(() -> UIUtils.packColumns(mTemplateTable, true));
        return mTemplateTable;
	}
	
	private Control createEntityContents(TabItemContext context,  Composite parent) {
		mEntityTable=new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
		mEntityTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mEntityTable.setHeaderVisible(true);
		mEntityTable.setLinesVisible(true);
		UIUtils.createTableColumn(mEntityTable, SWT.LEFT,CoreMessages.pref_page_connection_types_label_table_column_name);
        UIUtils.createTableColumn(mEntityTable, SWT.LEFT, CoreMessages.pref_page_query_manager_group_object_types);
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
                editor.setItems(new String[]{"String","Date","Integer","Long","Double","BigDecimal","DateTime","Short","Time","Object"} );
                editor.setText(item.getText(1));
                return editor;
            }
            @Override
            protected void saveEditorValue(Control control, int index, TableItem item) {
            	String javaType=((CCombo) control).getText();
                item.setText(1,javaType);
                Object tabelColumn= item.getData("tabelColumn");
                if(tabelColumn!=null)
                {
                	((SourceCodeTableColumn)tabelColumn).setJavaType(javaType);
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
	 private void createEntityTableItem(List<SourceCodeTableColumn> columns )
	    {
			if (columns != null) {
				for(int i=0,k=columns.size();i<k;i++)
				{
					SourceCodeTableColumn tableColumn=columns.get(i);
					if(i<mEntityTable.getItemCount())
					{
						TableItem item=mEntityTable.getItem(i);
						item.setData("tabelColumn", tableColumn);
						item.setText(0, tableColumn.getColumnName());
						item.setText(1, tableColumn.getJavaType());
					}else { 
						TableItem item = new TableItem(mEntityTable, SWT.NONE);
						item.setData("id", tableColumn.getColumnName()); 
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
