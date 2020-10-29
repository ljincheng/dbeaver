package org.jkiss.dbeaver.model.sourcecode.editors;

 

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithProgress;
import org.jkiss.dbeaver.model.sourcecode.core.DBSTableCodeContext;
import org.jkiss.dbeaver.model.sourcecode.core.TemplateContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.ui.UIUtils;


public class SourceCodeViewContext implements  DBRRunnableWithProgress{

	private DBSTableCodeContext mDBSTableCodeContext;
	private TemplateContext mTemplateContext;
	private DBSTable mDBSTable;
	private List<FormItemContext> mSettings;
	
	public SourceCodeViewContext() {
		super();
		mDBSTableCodeContext=new DBSTableCodeContext();
	}
	
	public DBSTableCodeContext getDBSTableCodeContext() {
		return mDBSTableCodeContext;
	}
	 
	public TemplateContext getTemplateContext() {
		return mTemplateContext;
	}
	public void setTemplateContext(TemplateContext mTemplateContext) {
		this.mTemplateContext = mTemplateContext;
	}
	
	public DBSTable getmDBSTable() {
		return mDBSTable;
	}
	
	public void setmDBSTable(DBSTable mDBSTable) {
		this.mDBSTable = mDBSTable;
		UIUtils.runInUI(this);
	} 
	
	@Override
	public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		
		if(this.mDBSTable!=null)
		 {
			this.mDBSTableCodeContext.run(monitor, this.mDBSTable, mSettings);
		 }
	}
	
	
	public void setContextSetting(List<FormItemContext> settings)
	{
		this.mSettings=settings;
		if(this.mDBSTable!=null)
		{
			this.mDBSTableCodeContext.refersh(this.mSettings);
		}
	}
	
	public String getSourceCode() {
		if(this.mTemplateContext!=null)
		{
			return mTemplateContext.convertActivityTemplate(this.mDBSTableCodeContext);
		}else {
			return "";
		}
	}
	
	
	
	
	
	
}
