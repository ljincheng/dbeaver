package org.jkiss.dbeaver.model.sourcecode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
import org.jkiss.dbeaver.model.sourcecode.registry.SourceCodeGenerator;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.runtime.DBWorkbench;

public abstract class GeneratorSourceCode  extends SourceCodeGenerator<Object> {
	
//	private GeneratorSourceCodeExport codeExport;

    @Override
    public void run(DBRProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        boolean allTables = true;
        List<DBSTable> tableList = new ArrayList<>();
        
        for (Object object : objects) {
            if (!(object instanceof DBSTable)) {
                allTables = false;
                break;
            } else {
                tableList.add((DBSTable) object);
            }
        }
        if (!allTables) {
//            super.run(monitor);
            return;
        }

        StringBuilder sql = new StringBuilder(100);
        Map<String, Object> options = new HashMap<>();
        addOptions(options);
        try {
        	monitor.beginTask("Generator source code ", 1000);
        	generateTableListCode(monitor, sql, tableList, options);
        } catch (DBException e) {
            throw new InvocationTargetException(e);
        }
        result = sql.toString().trim();
    }
    
     
    public  void generateTableListCode(@NotNull DBRProgressMonitor monitor, @NotNull StringBuilder sql, @NotNull List<DBSTable> tablesOrViews, Map<String, Object> options) throws DBException{
    	List<DBSTable> goodTableList = new ArrayList<>();
        List<DBSTable> cycleTableList = new ArrayList<>();
        List<DBSTable> viewList = new ArrayList<>();

        DBStructUtils.sortTableList(monitor, tablesOrViews, goodTableList, cycleTableList, viewList);

        // Good tables: generate full DDL
        if(getGeneratorType()==1)
        {
        	boolean runGenResult=true;
        	for (DBSTable table : goodTableList) { 
        		StringBuilder code=new StringBuilder(100);
        		generateOneTableSourceCode(code,monitor, table, options);
//	        	codeExport=new GeneratorSourceCodeExport(getRootPath());
//	        	String fileName=saveFileName(monitor,table);
//        		boolean genResult=codeExport.saveFile(fileName, code.toString());
//	        	boolean genResult=codeExport.exportHtml(javaFileName(table,"/add.html"), code.toString(),table.getDataSource().getName());
        		boolean genResult=writeCode(monitor,table,code);
	        	if(!genResult)
	        	{
	        		if(!DBWorkbench.getPlatformUI().confirmAction("错误", "生成出现错误，是否继续"))
	        		{
	        			runGenResult=false;
	        			setGeneratorResult(runGenResult);
	        			monitor.done();
	        			return ;
	        		}
	        		runGenResult=false;
	        	}
	        }
        	for (DBSTable table : cycleTableList) { 
        		StringBuilder code=new StringBuilder(100);
        		generateOneTableSourceCode(code,monitor, table, options);
        		boolean genResult=writeCode(monitor,table,code);
//	        	boolean genResult=codeExport.exportHtml(javaFileName(table,"/add.html"), code.toString(),table.getDataSource().getName());
	        	if(!genResult)
	        	{
	        		if(!DBWorkbench.getPlatformUI().confirmAction("错误", "生成出现错误，是否继续"))
	        		{
	        			runGenResult=false;
	        			setGeneratorResult(runGenResult);
	        			monitor.done();
	        			return ;
	        		}
	        		runGenResult=false;
	        	}
	        }
        	for (DBSTable table : viewList) { 
        		StringBuilder code=new StringBuilder(100);
        		generateOneTableSourceCode(code,monitor, table, options);
        		
//        		codeExport=new GeneratorSourceCodeExport(getRootPath());
        		boolean genResult=writeCode(monitor,table,code);
//        		boolean genResult=codeExport.saveFile(fileName, code.toString());
        		//boolean genResult=codeExport.exportHtml(javaFileName(table,"/add.html"), code.toString(),table.getDataSource().getName());
        		if(!genResult)
        		{
        			if(!DBWorkbench.getPlatformUI().confirmAction("错误", "生成出现错误，是否继续"))
        			{
        				runGenResult=false;
        				setGeneratorResult(runGenResult);
        				monitor.done();
        				return ;
        			}
        			runGenResult=false;
        		}
        	}
        	setGeneratorResult(runGenResult);
        }else {
        	for (DBSTable table : goodTableList) { 
        		generateOneTableSourceCode(sql,monitor, table, options);
	        }
        	for (DBSTable table : cycleTableList) { 
        		generateOneTableSourceCode(sql,monitor, table, options);
        	}
        	for (DBSTable table : viewList) { 
        		generateOneTableSourceCode(sql,monitor, table, options);
        	}
        }
        
        monitor.done();
    }
    
    protected abstract boolean writeCode(DBRProgressMonitor monitor,DBSTable table,StringBuilder code);
		
    protected abstract String getTemplateFile();
	 
    
    public  void  generateOneTableSourceCode(StringBuilder sql,DBRProgressMonitor monitor,DBSTable table, Map<String, Object> options) throws DBException{
    	
		String templateFile = getTemplateFile();
		try {
			if (templateFile != null && templateFile.length() > 0) {
				String tplHtml = CodeHelper.loadResource(getTemplateFile());
				JavaTemplateContext mContext = new JavaTemplateContext(table, this.sourceCodeSetting, monitor);
				String result =cn.booktable.template.TemplateEngine.process(tplHtml, mContext);
				//String result = TemplateEngine.process(tplHtml, mContext);
				sql.append(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
   
     
   
 
}