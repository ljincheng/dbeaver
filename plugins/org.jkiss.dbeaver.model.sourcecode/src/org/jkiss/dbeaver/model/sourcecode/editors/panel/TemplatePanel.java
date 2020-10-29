package org.jkiss.dbeaver.model.sourcecode.editors.panel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.dbeaver.model.sourcecode.core.CodeTemplate;
import org.jkiss.dbeaver.model.sourcecode.core.ContentListen;
import org.jkiss.dbeaver.model.sourcecode.core.TemplateContext;
import org.jkiss.dbeaver.model.sourcecode.editors.JavaEditor;
import org.jkiss.dbeaver.model.sourcecode.editors.SourceCodeViewContext;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.dialog.GeneratorSourceCodeDialog;
import org.jkiss.dbeaver.model.sourcecode.ui.dialog.TemplateEditDialog;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIActionEvent;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifierContext;
import org.jkiss.dbeaver.model.sourcecode.ui.handler.EntityCodeViewHandler;
import org.jkiss.dbeaver.ui.ActionUtils;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.CustomTableEditor;
import org.jkiss.dbeaver.ui.css.CSSUtils;
import org.jkiss.dbeaver.ui.css.DBStyles;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.ui.editors.sql.dialogs.ViewSQLDialog;
import org.jkiss.utils.CommonUtils;

import cn.booktable.template.IContext;
 

public class TemplatePanel implements UIEventNotifier,UIActionEvent,ContentListen{
	
	private UINotifierContext mUINotifierContext;
	private TemplateContext mTemplateContext;
	private JavaEditor mTemplateEdit;
	private Table mTemplateTable;
	private IContext mTableContext;
	public TemplatePanel(UINotifierContext context) {
		super();
		mUINotifierContext=context;
		mTemplateContext=new TemplateContext();
		mTemplateContext.addContentListen(this);
		mUINotifierContext.add(TYPE_VIEW_CREATE, this);
		mUINotifierContext.add(TYPE_VIEW_ACTION, this);
		
	}
	@Override
	public void action(int type,UINotifier notifier, Object context) {
		if(TYPE_VIEW_CREATE==type) {
			if(notifier.getCategory()==1 && notifier.getComposite() instanceof CTabFolder)
			{
			   CTabFolder panelFolder=(CTabFolder)notifier.getComposite();
			   CTabItem panelTab = new CTabItem(panelFolder, SWT.NONE);
		       panelTab.setText(UIMessages.dbeaver_generate_sourcecode_template);
		       panelTab.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
		       panelTab.setToolTipText(UIMessages.dbeaver_generate_sourcecode_template);
		       panelTab.setShowClose(false); 
	//	       panelTab.setControl(createTabContexts(panelFolder));
		       panelTab.setControl(createTemplatePanel(panelFolder,notifier.getSite()));
			}
		}else if(TYPE_VIEW_ACTION==type) {
			if(notifier.getCategory()==TYPE_VIEW_ACTION_CATEGORY_CODE_SETTING && context!=null && context instanceof SourceCodeViewContext && notifier.getComposite()!=null)
			{
					SourceCodeViewContext mSourceCodeViewContext=(SourceCodeViewContext)context;
					mSourceCodeViewContext.setTemplateContext(this.mTemplateContext);
					mTableContext=mSourceCodeViewContext.getDBSTableCodeContext();
					mUINotifierContext.send(TYPE_VIEW_ACTION,new UINotifier(TYPE_VIEW_ACTION_CATEGORY_CODE_TEMPLATE, this, null), mSourceCodeViewContext);
					reloadTableColumns();
			}else if(notifier.getCategory()==TYPE_VIEW_ACTION_CATEGORY_CODE_EXPORT) {
				exportSourceCode(notifier.getSite().getPart());
			}
		}
	}
	
	private Composite createTemplatePanel(CTabFolder panelFolder,IWorkbenchPartSite site) {
//		panelFolder.setRedraw(false);
		mTemplateEdit=new JavaEditor(site,false);
		Composite mainPanel = UIUtils.createPlaceholder(panelFolder, 1);
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setData(UIActionEvent.COMMAND_OBJECT_ID,this);
		create_topbar(mainPanel,site);
		
		SashForm viewerSash= UIUtils.createPartDivider(null, mainPanel, SWT.VERTICAL | SWT.SMOOTH);
		viewerSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		mTemplateTable=new Table(viewerSash, SWT.BORDER | SWT.CHECK | SWT.SINGLE | SWT.FULL_SELECTION);
		mTemplateTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mTemplateTable.setHeaderVisible(true);
		mTemplateTable.setLinesVisible(true);
		UIUtils.createTableColumn(mTemplateTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_name).setWidth(150);
        UIUtils.createTableColumn(mTemplateTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_description);
        UIUtils.createTableColumn(mTemplateTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_savePosition);
 
        mTemplateTable.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(e.item instanceof TableItem)
				{
					TableItem tableItem= (TableItem)e.item;
					
					Object tabelColumn=tableItem.getData("data");
					if(tabelColumn!=null && tabelColumn instanceof CodeTemplate) { 
						CodeTemplate codeTemplate=(CodeTemplate)tabelColumn;
						mTemplateEdit.setCode(mTemplateContext.setActivityTemplate(codeTemplate).getTemplate());
						mUINotifierContext.send(TYPE_VIEW_REFRESH, new UINotifier(TYPE_VIEW_REFRESH_CATEGORY_TEMPLATE, this, mTemplateTable), null);
					}
					
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
       
        final CustomTableEditor tableEditor = new CustomTableEditor(mTemplateTable) {
            {
                firstTraverseIndex = 2;
                lastTraverseIndex = 2;
                editOnEnter = false;
            }
            @Override
            protected Control createEditor(Table table, int index, TableItem item) {
                if (index != 2) {
                    return null;
                }
                Object itemData= item.getData("data");
                String value=item.getText(2);
                if(itemData!=null) {
	                CodeTemplate codeTemplate=(CodeTemplate)itemData;
	                value=codeTemplate.getExportPath();
                }
                 
               Text control=UIUtils.createLabelText(table,  null,value==null?"":value); 
                return control;
            }
            @Override
            protected void saveEditorValue(Control control, int index, TableItem item) {
            	String value=((Text) control).getText();
                item.setText(2,value);
                Object itemData= item.getData("data");
                if(itemData!=null) {
	                CodeTemplate codeTemplate=(CodeTemplate)itemData;
	                codeTemplate.setExportPath(value);
	                mTemplateContext.saveCodeTemplateList();
	                reloadTableColumns();
                }

            }
        };
        
       
        mTemplateEdit.createPartControl(viewerSash);
        mTemplateEdit.addSaveMenuAction(new SaveTefmplateAction());
        
        for(int i=0,k=mTemplateContext.size();i<k;i++)
		{
			  	CodeTemplate codeTemplate=mTemplateContext.get(i); 
				TableItem item=new TableItem(mTemplateTable, SWT.NONE);
				item.setData("data", codeTemplate);
				item.setText(0, codeTemplate.getName());
				item.setText(1, codeTemplate.getDesc());
				item.setText(2,"");
				//item.setText(2,(mDBSTableCodeContext==null ||loadTableTime<1)?(codeTemplate.getExportPath()==null?"":codeTemplate.getExportPath()):mTemplateContext.exportFilePath(codeTemplate,mDBSTableCodeContext));
		}
        
        UIUtils.asyncExec(() -> UIUtils.packColumns(mTemplateTable, true)); 
//        panelFolder.setRedraw(true);
        return mainPanel;
	}
	
	private void reloadTableColumns( ) {
		if(this.mTableContext!=null) {
		int count=mTemplateTable.getItemCount();
		for(int i=0;i<count;i++)
		{
			TableItem item=mTemplateTable.getItem(i);
			CodeTemplate codeTemplate=(CodeTemplate)item.getData("data");
			if(codeTemplate!=null)
			{
				item.setText(2,this.mTemplateContext.exportFilePath(codeTemplate, mTableContext));
			}
			
		}
		}
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
       	 	editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_EXPORT,"Export",null,"Export source code from template",false));
       	 	editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_NEWADD,"New add",null,"Add new Template",false));
       	 	editToolBarManager.add(ActionUtils.makeCommandContribution(site, EntityCodeViewHandler.CODE_DELETE,"Delete",null,"Delete Template",false));
   			ToolBar editorToolBar = editToolBarManager.createControl(statusBar);
	        CSSUtils.setCSSClass(editorToolBar, DBStyles.COLORED_BY_CONNECTION_TYPE);
       }
	}
	
	public int exportSourceCode(IWorkbenchPart part) {
		 
		try {
			int checkNum=0;
			int count=this.mTemplateTable.getItemCount();
			List<CodeTemplate> exportCodeTplList=new ArrayList<CodeTemplate>();
			for(int i=0;i<count;i++) {
				TableItem item=this.mTemplateTable.getItem(i);
				if(item.getChecked())
				{
					checkNum+=1;
					CodeTemplate codeTpl=(CodeTemplate)item.getData("data");
					if(CommonUtils.isEmpty(codeTpl.getExportPath()))
					{
						UIUtils.showMessageBox(part.getSite().getShell(), "导出文件路径不能为空", "请先设置文件导出系统保存的位置，在模板项"+codeTpl.getName() +"中设置", SWT.OK);
						continue;
					}else {
						String exportPath=this.mTemplateContext.exportFilePath(codeTpl, this.mTableContext);
						File exportFile=new File(exportPath);
						if(exportFile.exists()) {
							//UIUtils.showMessageBox(part.getSite().getShell(), "导出文件已", "请先设置文件导出系统保存的位置，在模板项"+codeTpl.getName() +"中设置", SWT.NO);
							if(!UIUtils.confirmAction("导出的文件已存在，确认要覆盖？", "要导出位置系统文件存在相同的名字，确定是否要覆盖已存在的文件？(文件:"+exportPath+")"))
							{
								continue;
							}
						}
					}
					exportCodeTplList.add(codeTpl);
				}
			}
			for(int i=0,k=exportCodeTplList.size();i<k;i++) {
				CodeTemplate codeTemplate=exportCodeTplList.get(i);
				mTemplateContext.exportFile(codeTemplate,mTableContext);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
			return 0;
	}
	
	 public class SaveTefmplateAction extends Action{
		 
		 public  SaveTefmplateAction() {
			setText(UIMessages.dbeaver_generate_sourcecode_save);
		}
		 
			@Override
			public void run() {
				CodeTemplate codeTemplate=mTemplateContext.getActivityTemplate();
				String code=mTemplateEdit.getCode();
				codeTemplate.setTemplate(code);
				  mTemplateContext.saveCodeTemplateList();
			}
	    	
	    }

	@Override
	public Object executeAction(ExecutionEvent event) {
		String comId=event.getCommand().getId();
		if(EntityCodeViewHandler.CODE_NEWADD.equals(comId))
		{
			IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
			TemplateEditDialog dialog = new TemplateEditDialog(activePart.getSite().getShell(), "TEST", UIIcon.ADD,this.mTemplateContext);
                 dialog.open();
		}else if(EntityCodeViewHandler.CODE_DELETE.equals(comId)) {
			int count=this.mTemplateTable.getItemCount();
			List<CodeTemplate> removeTemplates=new ArrayList<CodeTemplate>();
			for(int i=0,k=count;i<k;i++)
			{
				TableItem tableItem=this.mTemplateTable.getItem(i);
				if(tableItem.getChecked())
				{
					removeTemplates.add((CodeTemplate)tableItem.getData("data"));
				}
			}
			if(removeTemplates.size()>0)
			{ 
				this.mTemplateContext.deleteTemplateAll(removeTemplates);
			}
		}
		return null;
	}
	
	@Override
	public void contentAction(int type, int subtype, Object data) {
		 if(TYPE_DATA_CHANGE==type ) {
			 if(subtype==SUBTYPE_DATA_CHANGE_ADD && data !=null && data instanceof CodeTemplate) {
				 CodeTemplate codeTemplate=(CodeTemplate)data;
				 TableItem item=new TableItem(mTemplateTable, SWT.NONE);
					item.setData("data", codeTemplate);
					item.setText(0, codeTemplate.getName());
					item.setText(1, codeTemplate.getDesc());
					item.setText(2,this.mTemplateContext.exportFilePath(codeTemplate, mTableContext));
			 }else if(subtype==SUBTYPE_DATA_CHANGE_DELETE && data !=null && data instanceof CodeTemplate) {
				 int count=this.mTemplateTable.getItemCount();
				 for(int i=0,k=count;i<k;i++)
				 {
					 TableItem item= this.mTemplateTable.getItem(i);
					 Object tableItemData=item.getData("data");
					 if(data.equals(tableItemData)) {
						 this.mTemplateTable.remove(i);
						 break;
					 }
				 }
			 }else if(subtype==SUBTYPE_DATA_CHANGE_DELETE_MUL && data!=null) {
				 List<CodeTemplate> templates=(List<CodeTemplate>)data;
				 if(templates!=null) {
					 for(CodeTemplate template:templates) {
						 
						 int count=this.mTemplateTable.getItemCount();
						 for(int i=0,k=count;i<k;i++)
						 {
							 TableItem item= this.mTemplateTable.getItem(i);
							 Object tableItemData=item.getData("data");
							 if(template.equals(tableItemData)) {
								 this.mTemplateTable.remove(i);
								 
								 break;
							 }
						 }
					 }
					 this.mTemplateTable.redraw();
				 }
			 }
		 }
		
	}
	 
	
	 
}
