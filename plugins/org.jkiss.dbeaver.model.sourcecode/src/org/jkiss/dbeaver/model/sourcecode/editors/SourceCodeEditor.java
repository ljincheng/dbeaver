package org.jkiss.dbeaver.model.sourcecode.editors;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.sourcecode.editors.panel.CodePanel;
import org.jkiss.dbeaver.model.sourcecode.editors.panel.ColumnPanel;
import org.jkiss.dbeaver.model.sourcecode.editors.panel.SettingsPanel;
import org.jkiss.dbeaver.model.sourcecode.editors.panel.TemplatePanel;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.event.UINotifier;
import org.jkiss.dbeaver.model.sourcecode.ui.view.SourceCodeViewer;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.ui.editors.IDatabaseEditorInput;

	
public class SourceCodeEditor extends EditorPart implements UIEventNotifier
{
	private static final Log log = Log.getLog(SourceCodeEditor.class);
	    
	private SourceCodeViewer mSourceCodeViewer;
	private SourceCodeViewContext mSourceCodeViewContext;
	private CodePanel mCodePanel;
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
//		System.out.print("SourceCodeEditor 执行保存");
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
//		System.out.print("SourceCodeEditor 执行保存AS");
	}

	
	@Override
    public IDatabaseEditorInput getEditorInput()
    {
        return (IDatabaseEditorInput)super.getEditorInput();
    }
  
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		 super.setSite(site);
	     super.setInput(input);
	     mSourceCodeViewContext=new SourceCodeViewContext();
	     mSourceCodeViewer= new SourceCodeViewer(getSite());
	     mSourceCodeViewer.add(UIEventNotifier.TYPE_VIEW_REFRESH, this);
	     
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		
//		SourceCodeViewer mainViewer=new SourceCodeViewer(parent, getSite());
		DBSTable dbTable=null;
		   DBSObject dbObject = getRootObject();
	        if (dbObject == null) {
	            log.error("Database object must be entity container to render ERD diagram");
	          
	        }else {
		        if(dbObject instanceof DBSTable) {
		        	dbTable=(DBSTable)dbObject;
		        }
	        }
	       
//		DefaultEntityContext context=new DefaultEntityContext(dbTable);
//		new EntityCodeView(parent,getSite(),context); 
	     
	       new TemplatePanel(mSourceCodeViewer);
	       new SettingsPanel(mSourceCodeViewer);
	       new ColumnPanel(mSourceCodeViewer);
	       mCodePanel= new CodePanel(mSourceCodeViewer); 
	        mSourceCodeViewer.createPartControl(parent);
	        if(dbTable!=null)
	        {
	        	mSourceCodeViewContext.setmDBSTable(dbTable);
	        	mSourceCodeViewer.send(UIEventNotifier.TYPE_VIEW_ACTION, new UINotifier(TYPE_VIEW_ACTION_CATEGORY_CODE, this, mCodePanel.getCodeViewer()), mSourceCodeViewContext);
	        }
        
//		TabbedFolderInfo[] folders = collectFolders(this);
//        createFoldersPanel(parent,folders);
	}
	
	 

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	  
	 
  private DBSObject getRootObject()
    {
        DBSObject object = getEditorInput().getDatabaseObject();
        if (object == null) {
            return null;
        }
        if (object instanceof DBPDataSourceContainer && object.getDataSource() != null) {
            object = object.getDataSource();
        }
        return object;
    }

@Override
public void action(int type, UINotifier notifier, Object context) {
	if(UIEventNotifier.TYPE_VIEW_REFRESH==type)
	{
//		if(UIEventNotifier.TYPE_VIEW_REFRESH_CATEGORY_SETTING== notifier.getCategory() || TYPE_VIEW_REFRESH_CATEGORY_TEMPLATE==notifier.getCategory())
//		{
			 mSourceCodeViewer.send(UIEventNotifier.TYPE_VIEW_ACTION, new UINotifier(TYPE_VIEW_ACTION_CATEGORY_CODE, this, mCodePanel.getCodeViewer()), mSourceCodeViewContext);
//		}
	}
	
}
    
  
    
}