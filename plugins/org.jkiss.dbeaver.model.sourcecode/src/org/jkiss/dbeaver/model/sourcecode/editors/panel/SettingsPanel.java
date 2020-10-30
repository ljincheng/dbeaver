package org.jkiss.dbeaver.model.sourcecode.editors.panel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.sourcecode.core.CodeTemplate;
import org.jkiss.dbeaver.model.sourcecode.core.ContentListen;
import org.jkiss.dbeaver.model.sourcecode.core.SettingsContext;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeSetting;
import org.jkiss.dbeaver.model.sourcecode.editors.SourceCodeViewContext;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormConstants;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.dialog.SettingEditDialog;
import org.jkiss.dbeaver.model.sourcecode.ui.dialog.TemplateEditDialog;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIActionEvent;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifierContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent;
import org.jkiss.dbeaver.model.sourcecode.ui.handler.EntityCodeViewHandler;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.ActionUtils;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.CustomTableEditor;
import org.jkiss.dbeaver.ui.controls.TextWithOpen;
import org.jkiss.dbeaver.ui.css.CSSUtils;
import org.jkiss.dbeaver.ui.css.DBStyles;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.utils.RuntimeUtils;
import org.jkiss.utils.CommonUtils;

public class SettingsPanel implements UIEventNotifier,UIActionEvent,ContentListen{

	 private static final String DIALOG_FOLDER_PROPERTY = "dialog.default.folder";

	    public static String curDialogFolder;

	    static {
	        curDialogFolder = DBWorkbench.getPlatform().getPreferenceStore().getString(DIALOG_FOLDER_PROPERTY);
	        if (CommonUtils.isEmpty(curDialogFolder)) {
	            curDialogFolder = RuntimeUtils.getUserHomeDir().getAbsolutePath();
	        }
	    }
	    public static void setCurDialogFolder(String curDialogFolder)
	    {
	        DBWorkbench.getPlatform().getPreferenceStore().setValue(DIALOG_FOLDER_PROPERTY, curDialogFolder);
	        DialogUtils.curDialogFolder = curDialogFolder;
	    }
	    
	    
	private UINotifierContext mUINotifierContext;
	
	private SettingsContext mSettingsContext;
	private Table mEntityTable;
	private SourceCodeViewContext mSourceCodeViewContext;
	
	public SettingsPanel(UINotifierContext context) {
		super();
		this.mUINotifierContext = context;
		mSettingsContext=new SettingsContext();
		mSettingsContext.addContentListen(this);
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
		       panelTab.setControl(createPanel(panelFolder,notifier.getSite()));
			}
		}else if(TYPE_VIEW_ACTION == type)
		{
			if(notifier.getCategory()==TYPE_VIEW_ACTION_CATEGORY_CODE && context!=null && context instanceof SourceCodeViewContext && mSettingsContext!=null)
			{
				mSourceCodeViewContext=(SourceCodeViewContext)context;
				mSourceCodeViewContext.setContextSetting(mSettingsContext.getItems());
//				refreshSetting();
				mUINotifierContext.send(TYPE_VIEW_ACTION, new UINotifier(TYPE_VIEW_ACTION_CATEGORY_CODE_SETTING, this, notifier.getComposite()), context);
			}
		}
	}
	
	public Control createControls(Composite parent) {
	    mEntityTable=new Table(parent, SWT.BORDER |SWT.CHECK |SWT.SINGLE |  SWT.FULL_SELECTION);
		mEntityTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mEntityTable.setHeaderVisible(true);
		mEntityTable.setLinesVisible(true); 
		UIUtils.createTableColumn(mEntityTable, SWT.RIGHT,UIMessages.dbeaver_generate_sourcecode_param).setWidth(100);
        UIUtils.createTableColumn(mEntityTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_value);
        UIUtils.createTableColumn(mEntityTable, SWT.LEFT,UIMessages.dbeaver_generate_sourcecode_description);
        final CustomTableEditor tableEditor = new CustomTableEditor(mEntityTable) {
            {
                firstTraverseIndex = 1;
                lastTraverseIndex = 1;
                editOnEnter = true;
            }
            @Override
            protected Control createEditor(Table table, int index, TableItem item) {
                if (index != 1) {
                    return null;
                }
                FormItemContext formItem=(FormItemContext)item.getData("data");
                String type=formItem.getType();
                Control control=null;
   			 if("file".equalsIgnoreCase(type)) {
   				control=createOutputFolderChooser(table, formItem.getValue(),item,1,formItem);
   			 }else {
   				 control=UIUtils.createLabelText(table,  null,formItem.getValue()); 
   			 }
                return control;
            }
            @Override
            protected void saveEditorValue(Control control, int index, TableItem item) {
            	String value=null;
            	if(control instanceof TextWithOpen)
            	{
            		 value=((TextWithOpen) control).getText();
            	}else {
            		
            	  value=((Text) control).getText();
            	}
            	 FormItemContext formItem=(FormItemContext)item.getData("data");
                item.setText(1,value); 
                formItem.setValue(value);
                refreshSetting();

            }
        };
        UIUtils.asyncExec(() -> UIUtils.packColumns(mEntityTable, true));
        
		 
		 for(int i=0,k=mSettingsContext.getItems().size();i<k;i++)
		 {
			 FormItemContext formItem=mSettingsContext.getItems().get(i);
				TableItem item= new TableItem(mEntityTable, SWT.NONE);
				item.setData("data", formItem);
				item.setText(0, formItem.getId()+"=");
				item.setText(1, formItem.getValue());
				item.setText(2, formItem.getName());
		 }
		 return mEntityTable;
	}
	
	private Control createPanel(CTabFolder parent,IWorkbenchPartSite site) {
		Composite mainPanel = UIUtils.createPlaceholder(parent, 1);
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setData(UIActionEvent.COMMAND_OBJECT_ID,this);
		create_topbar(mainPanel,site);
		
		createControls(mainPanel);
		
		  return mainPanel;
	}
	
	private void create_topbar(Composite parent,IWorkbenchPartSite site) {
		Composite statusComposite = UIUtils.createPlaceholder(parent, 3);
        statusComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite statusBar = new Composite(statusComposite, SWT.NONE);
        statusBar.setBackgroundMode(SWT.INHERIT_FORCE);
        statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        CSSUtils.setCSSClass(statusBar, DBStyles.COLORED_BY_CONNECTION_TYPE);
        RowLayout toolbarsLayout = new RowLayout(SWT.HORIZONTAL);
        toolbarsLayout.marginTop = 0;
        toolbarsLayout.marginBottom = 0;
        toolbarsLayout.center = true;
        toolbarsLayout.wrap = true;
        toolbarsLayout.pack = true;
        //toolbarsLayout.fill = true;
        statusBar.setLayout(toolbarsLayout);
        {
       	 	ToolBarManager editToolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
       	 	editToolBarManager.add(new Separator());
       	 	editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_NEWADD,"New add",null,"Add new Template",false));
       	 	editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_DELETE,"Delete",null,"Delete Template",false));
   			ToolBar editorToolBar = editToolBarManager.createControl(statusBar);
	        CSSUtils.setCSSClass(editorToolBar, DBStyles.COLORED_BY_CONNECTION_TYPE);
       }
	}
	
	@Override
	public Object executeAction(ExecutionEvent event) {
		String comId=event.getCommand().getId();
		if(EntityCodeViewHandler.CODE_NEWADD.equals(comId))
		{
			IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
			SettingEditDialog dialog=new SettingEditDialog(activePart.getSite().getShell(), "add",  UIIcon.ADD,this.mSettingsContext);
			dialog.open();
		}else if(EntityCodeViewHandler.CODE_DELETE.equals(comId)) {
			int count=this.mEntityTable.getItemCount();
			List<FormItemContext> removeTemplates=new ArrayList<FormItemContext>();
			for(int i=0,k=count;i<k;i++)
			{
				TableItem tableItem=this.mEntityTable.getItem(i);
				if(tableItem.getChecked())
				{
					removeTemplates.add((FormItemContext)tableItem.getData("data"));
				}
			}
			if(removeTemplates.size()>0)
			{ 
				this.mSettingsContext.deleteItemAll(removeTemplates); 
			}
		}
		return null;
	}
	
	 public void refreshSetting() { 
		 mSourceCodeViewContext.setContextSetting(mSettingsContext.getItems());
		 mSettingsContext.saveModify();
			mUINotifierContext.send(TYPE_VIEW_REFRESH, new UINotifier(TYPE_VIEW_REFRESH_CATEGORY_SETTING, this, null), mSourceCodeViewContext);
			 
	 }
	 
	 @Override
	public void contentAction(int type, int subtype, Object data) {
			 if(TYPE_DATA_CHANGE==type ) {
				 if(subtype==SUBTYPE_DATA_CHANGE_ADD && data !=null && data instanceof FormItemContext) {
					 FormItemContext formItem=(FormItemContext)data;
						TableItem item= new TableItem(mEntityTable, SWT.NONE);
						item.setData("data", formItem);
						item.setText(0, formItem.getId()+"=");
						item.setText(1, formItem.getValue());
						item.setText(2, formItem.getName()); 
						refreshSetting();
				 }else if(subtype==SUBTYPE_DATA_CHANGE_DELETE_MUL && data!=null) {
					 List<FormItemContext> templates=(List<FormItemContext>)data;
					 if(templates!=null) {
						 for(FormItemContext template:templates) {
							 
							 int count=this.mEntityTable.getItemCount();
							 for(int i=0,k=count;i<k;i++)
							 {
								 TableItem item= this.mEntityTable.getItem(i);
								 Object tableItemData=item.getData("data");
								 if(template.equals(tableItemData)) {
									 this.mEntityTable.remove(i);
									 
									 break;
								 }
							 }
						 }
						  
					 }
					 refreshSetting();
				 }
			 }
	 }
	 
	 
	
	  public static TextWithOpen createOutputFolderChooser(final Composite parent,  @Nullable String value,TableItem tableItem,int textIndex,FormItemContext formItem)
	    {
	         TextWithOpen directoryText = new TextWithOpen(parent) {
	            @Override
	            protected void openBrowser() {
	                DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.NONE);
	                dialog.setMessage("Choose target directory");
	                String directory = getText();
	                if (CommonUtils.isEmpty(directory)) {
	                    directory = curDialogFolder;
	                }
	                if (!CommonUtils.isEmpty(directory)) {
	                    dialog.setFilterPath(directory);
	                }
	                directory = dialog.open();
	                if (directory != null) {
	                    setText(directory);
	                    tableItem.setText(textIndex, directory);
	                    formItem.setValue(directory);
	                    setCurDialogFolder(directory);
	                }
	            }
	        };
	        directoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        if (value != null) {
	            directoryText.getTextControl().setText(value);
	        }

	        return directoryText;
	    }

}
