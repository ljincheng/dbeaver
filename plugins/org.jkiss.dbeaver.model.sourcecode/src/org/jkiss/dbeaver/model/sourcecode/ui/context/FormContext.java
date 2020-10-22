package org.jkiss.dbeaver.model.sourcecode.ui.context;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.event.ViewDataRunnableEvent;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.CustomTableEditor;
import org.jkiss.dbeaver.ui.controls.TextWithOpen;
import org.jkiss.dbeaver.ui.dialogs.DialogUtils;
import org.jkiss.dbeaver.utils.RuntimeUtils;
import org.jkiss.utils.CommonUtils;

public class FormContext {

	private List<FormItemContext> mForms;

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

	
	public FormContext(List<FormItemContext> forms) {
		super();
		if(forms!=null) {
			this.mForms=forms;
		}else {
			mForms=new ArrayList<FormItemContext>();
		}
	}
	
	public void addFormItem(FormItemContext formItem) {
		this.mForms.add(formItem);
	}
	
	public List<FormItemContext> getForms(){
		return mForms;
	}
	
	
	public Control createControls(Composite parent,ViewDataRunnableEvent event) {
		Table mEntityTable=new Table(parent, SWT.BORDER | SWT.FULL_SELECTION);
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
                if(event!=null)
                {
                	event.refreshActions();
                }

            }
        };
        UIUtils.asyncExec(() -> UIUtils.packColumns(mEntityTable, true));
        
		 
		 for(int i=0,k=mForms.size();i<k;i++)
		 {
			 FormItemContext formItem=mForms.get(i);
				TableItem item= new TableItem(mEntityTable, SWT.NONE);
				item.setData("data", formItem);
				item.setText(0, formItem.getId()+"=");
				item.setText(1, formItem.getValue());
				item.setText(2, formItem.getName());
		 }
		 return mEntityTable;
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
	
//	public class TextModifyListener implements ModifyListener
//	{
//		private Text mText;
//		private FormItemContext mFormItem;
//		public TextModifyListener(Text text,FormItemContext formItem)
//		{
//			this.mText=text;
//			mFormItem=formItem;
//		}
//		
//		 @Override
//	     public void modifyText(ModifyEvent arg0) {
//			 mFormItem.setValue(mText.getText());
//		 }
//	}
}
