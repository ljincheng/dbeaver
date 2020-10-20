package org.jkiss.dbeaver.model.sourcecode.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.DBIcon;
import org.jkiss.dbeaver.model.DBPDataSourceContainer;
import org.jkiss.dbeaver.model.DBPObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.code.DefaultEntityContext;
import org.jkiss.dbeaver.model.sourcecode.registry.GeneratorSourceCodeConfigurationRegistry;
import org.jkiss.dbeaver.model.sourcecode.registry.SourceCodeGenerator;
import org.jkiss.dbeaver.model.sourcecode.registry.SourceCodeGeneratorDescriptor;
import org.jkiss.dbeaver.model.sourcecode.ui.view.EntityCodeView;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.controls.folders.TabbedFolderComposite;
import org.jkiss.dbeaver.ui.controls.folders.TabbedFolderInfo;
import org.jkiss.dbeaver.ui.controls.folders.TabbedFolderPage;
import org.jkiss.dbeaver.ui.editors.IDatabaseEditorInput;

	
public class SourceCodeEditor extends EditorPart
{
	private static final Log log = Log.getLog(SourceCodeEditor.class);
	    
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
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
	       
		DefaultEntityContext context=new DefaultEntityContext(dbTable);
		new EntityCodeView(parent,getSite(),context); 
		
        
//		TabbedFolderInfo[] folders = collectFolders(this);
//        createFoldersPanel(parent,folders);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	 public TabbedFolderInfo[] collectFolders(IWorkbenchPart part)
	    {
	        List<TabbedFolderInfo> tabList = new ArrayList<>();
	        GeneratorSourceCodeConfigurationRegistry registryList=GeneratorSourceCodeConfigurationRegistry.getInstance();
	        List<SourceCodeGeneratorDescriptor> descriptorList= registryList.getAllGenerators();
	        for(int i=0,k=descriptorList.size();i<k;i++)
	        {
	        	SourceCodeGeneratorDescriptor descriptor=descriptorList.get(i);
	        	
	        			tabList.add(
                    new TabbedFolderInfo(
                    		descriptor.getId(),
                    		descriptor.getLabel(),
                    		DBIcon.TREE_FOLDER,
                    		descriptor.getDescription(),
                        false,loadFromDatabase(null,descriptor)));
	        }
	        return tabList.toArray(new TabbedFolderInfo[0]);
	    }
  
	
	 private Composite createFoldersPanel(Composite parent, TabbedFolderInfo[] folders) {
		 Composite foldersPlaceholder = UIUtils.createPlaceholder(parent, 1, 0);
	        foldersPlaceholder.setLayoutData(new GridData(GridData.FILL_BOTH));
	     TabbedFolderComposite  folderComposite = new TabbedFolderComposite(foldersPlaceholder, SWT.LEFT |  SWT.MULTI);
	        folderComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

	        // Load properties
	        {
	            String objectId = "PropertiesEditor.teee";
	            folderComposite.setFolders(objectId, folders);
	        }
		 return null;
	 }
	 
	 
	 private TabbedFolderPage loadFromDatabase(DBRProgressMonitor monitor,SourceCodeGeneratorDescriptor descriptor)
		        
    {
		
		 TabbedFolderPage tabbedFolderPage= null;
        DBSObject dbObject = getRootObject();
        if (dbObject == null) {
            log.error("Database object must be entity container to render ERD diagram");
            return null;
        }
       
        tabbedFolderPage=new TabbedFolderPage() {
            @Override
            public void createControl(Composite parent) {
            	 List<DBPObject> objects = new ArrayList<>();
                 objects.add(getRootObject());
                 SourceCodeGenerator generator;
                 SourceCodePanel sourceCodePanel=null;
                 try {
                 	
                     generator = descriptor.createGenerator(objects); 
//                     UIUtils.runInUI(generator);
                     sourceCodePanel=new SourceCodePanel(descriptor.getLabel(), generator);
                 } catch (DBException e) {
                     DBWorkbench.getPlatformUI().showError("Generator create", "Can't create code'" + dbObject.getName() + "'", e);
                   
                 } 
            	if(sourceCodePanel!=null)
            	{
            		sourceCodePanel.createPanel(parent);
            		
            	}else {
	            	Text text= new Text(parent, SWT.NONE);
	            	text.setText(dbObject.getName());
            	}
            }
        };
        

        return tabbedFolderPage;
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
    
    
}