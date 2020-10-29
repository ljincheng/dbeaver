package org.jkiss.dbeaver.model.sourcecode.editors.panel;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jkiss.dbeaver.model.sourcecode.core.CodeColumnAttribute;
import org.jkiss.dbeaver.model.sourcecode.core.CodeTemplate;
import org.jkiss.dbeaver.model.sourcecode.core.DBSTableCodeContext;
import org.jkiss.dbeaver.model.sourcecode.editors.SourceCodeViewContext;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifierContext;
import org.jkiss.dbeaver.ui.DBeaverIcons;
import org.jkiss.dbeaver.ui.UIIcon;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.CustomTableEditor;
import org.jkiss.dbeaver.ui.controls.TextWithOpen;

public class ColumnPanel implements UIEventNotifier{

	private UINotifierContext mUINotifierContext;
	private Table mColumnTable;
	private SourceCodeViewContext mSourceCodeViewContext;
	
	public ColumnPanel(UINotifierContext context) {
		super();
		this.mUINotifierContext=context;
		mUINotifierContext.add(TYPE_VIEW_CREATE, this);
		mUINotifierContext.add(TYPE_VIEW_ACTION, this);
		
	}
	
	@Override
	public void action(int type, UINotifier notifier, Object context) {
		if(TYPE_VIEW_CREATE== type) {
			if(notifier.getCategory()==1 && notifier.getComposite() instanceof CTabFolder)
			{
			   CTabFolder panelFolder=(CTabFolder)notifier.getComposite();
			   CTabItem panelTab = new CTabItem(panelFolder, SWT.NONE);
		       panelTab.setText(UIMessages.dbeaver_generate_sourcecode_entity_object);
		       panelTab.setImage(DBeaverIcons.getImage(UIIcon.PANEL_CUSTOMIZE));
		       panelTab.setToolTipText(UIMessages.dbeaver_generate_sourcecode_settings);
		       panelTab.setShowClose(false); 
		       panelTab.setControl(createPanel(panelFolder));
			}
		}else if(TYPE_VIEW_ACTION==type) {
			if(notifier.getCategory()==TYPE_VIEW_ACTION_CATEGORY_CODE && context!=null && context instanceof SourceCodeViewContext && notifier.getComposite()!=null)
			{
					SourceCodeViewContext sourceCodeViewContext=(SourceCodeViewContext)context;
					this.mSourceCodeViewContext=sourceCodeViewContext;
//					mDBSTableCodeContext=mSourceCodeViewContext.getDBSTableCodeContext();
					
					reloadTableColumns();
			}
		}
		
	}
	
	private Control createPanel(CTabFolder parent) {
		mColumnTable=new Table(parent, SWT.BORDER |  SWT.SINGLE | SWT.FULL_SELECTION);
		mColumnTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		mColumnTable.setHeaderVisible(true);
		mColumnTable.setLinesVisible(true);
		UIUtils.createTableColumn(mColumnTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_name).setWidth(150);
        UIUtils.createTableColumn(mColumnTable, SWT.LEFT, UIMessages.dbeaver_generate_sourcecode_object_types);
        final CustomTableEditor tableEditor = new CustomTableEditor(mColumnTable) {
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
                	mUINotifierContext.send(TYPE_VIEW_REFRESH, new UINotifier(TYPE_VIEW_REFRESH_CATEGORY_TEMPLATE, this, null), mSourceCodeViewContext);
                }

            }
        };
        return mColumnTable;
	}
	
	
	private void reloadTableColumns( ) {
		if(this.mSourceCodeViewContext!=null && this.mSourceCodeViewContext.getDBSTableCodeContext()!=null) {
			mColumnTable.removeAll();
			List<CodeColumnAttribute> columns= this.mSourceCodeViewContext.getDBSTableCodeContext().getColumns();
		int count=columns.size();
		for(int i=0;i<count;i++)
		{
			CodeColumnAttribute columnItem=columns.get(i);
			TableItem item=new TableItem(mColumnTable, SWT.NONE);;
			item.setData("data",columnItem);
			item.setText(0,columnItem.getColumnName());
			item.setText(1,columnItem.getJavaType());
			
		}
		 UIUtils.asyncExec(() -> UIUtils.packColumns(mColumnTable, true)); 
		}
	}
	

}
