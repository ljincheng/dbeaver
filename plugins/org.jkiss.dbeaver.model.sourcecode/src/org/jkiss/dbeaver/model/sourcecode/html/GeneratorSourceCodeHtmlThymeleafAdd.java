package org.jkiss.dbeaver.model.sourcecode.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeTable;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeTableAdapter;
import org.jkiss.dbeaver.model.sourcecode.core.SourceCodeTableColumn;
import org.jkiss.dbeaver.model.sourcecode.template.TemplateEngine;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndex;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndexColumn;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

/**
 * Html Add (Thymeleaf)
 * @author ljc
 *
 */
public class GeneratorSourceCodeHtmlThymeleafAdd extends GeneratorSourceCode{
	 
	private GeneratorSourceCodeExport codeExport;
	
	 
	public void generateTableListCode_back(DBRProgressMonitor monitor, StringBuilder sql, List<DBSTable> tablesOrViews,Map<String, Object> options) throws DBException {
		 
		 
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
		        	generateTableMybatisCode(code,monitor, table, options);
		        	codeExport=new GeneratorSourceCodeExport(getRootPath());
		        	boolean genResult=codeExport.exportHtml(javaFileName(table,"/add.html"), code.toString(),table.getDataSource().getName());
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
		        	generateTableMybatisCode(code,monitor, table, options);
		        	codeExport=new GeneratorSourceCodeExport(getRootPath());
		        	boolean genResult=codeExport.exportHtml(javaFileName(table,"/add.html"), code.toString(),table.getDataSource().getName());
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
	        		generateTableMybatisCode(code,monitor, table, options);
	        		codeExport=new GeneratorSourceCodeExport(getRootPath());
	        		boolean genResult=codeExport.exportHtml(javaFileName(table,"/add.html"), code.toString(),table.getDataSource().getName());
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
		        	generateTableMybatisCode(sql,monitor, table, options);
		        }
	        	for (DBSTable table : cycleTableList) { 
	        		generateTableMybatisCode(sql,monitor, table, options);
	        	}
	        	for (DBSTable table : viewList) { 
	        		generateTableMybatisCode(sql,monitor, table, options);
	        	}
	        }
	        
	        monitor.done();
		 
	 }
	 
	 private String javaFileName(DBSTable table,String extFileName)
	 {
		 String tableName=table.getName();
		 String mTableName_upperCamelCase=CodeHelper.toUpperCamelCase(tableName);//表名大写开头
		 String mClassFullName=getClassFullName(mTableName_upperCamelCase, getRuleController());//类全称
		 String mClassSimpleName=CodeHelper.getClassSimpleName(mClassFullName);//类简称
		 return mClassSimpleName+extFileName;
	 }
	 
	 private void generateTableMybatisCode( StringBuilder sql,DBRProgressMonitor monitor,DBSTable table, Map<String, Object> options)throws DBException
	 {
		 String tplHtml=CodeHelper.loadResource("html_thymeleaf_add");
		 sql.append(tplHtml);
		 if(tplHtml!=null)
		 {
			 return;
		 }
		 
		 String lf = GeneralUtils.getDefaultLineSeparator();
		 String tableName=table.getName();
		 String description=CodeHelper.emptyString(table.getDescription(),false);
		 String mTableName_upperCamelCase=CodeHelper.toUpperCamelCase(tableName);//表名大写开头
//		 String mTableName_lowerCamelCase=CodeHelper.toLowerCamelCase(tableName);//表名小写开头
		 String mEntity_fullName=getClassFullName(mTableName_upperCamelCase, getRuleEntity());//类全称
		 String mEntity_package=CodeHelper.getPackageName(mEntity_fullName);//类包名
		 String mEntity_typeName=CodeHelper.getClassSimpleName(mEntity_fullName);//类简称
		 String mEntity_paramName=CodeHelper.toLowerCamelCase(mEntity_typeName);//类简称
		 
		 String mDao_fullName=getClassFullName(mTableName_upperCamelCase, getRuleDao());//Dao类全称
		 String mDao_package=CodeHelper.getPackageName(mDao_fullName);//Dao类包名
		 String mDao_typeName=CodeHelper.getClassSimpleName(mDao_fullName);//Dao类型名称
		 String mDao_paramName=CodeHelper.toLowerCamelCase(mDao_typeName);//Dao类变量
		 
		 String mComponent_fullName=getClassFullName(mTableName_upperCamelCase, getRuleComponent());//ComponentImpl类全称
		 String mComponent_package=CodeHelper.getPackageName(mComponent_fullName);//ComponentImpl类包名
		 String mComponent_typeName=CodeHelper.getClassSimpleName(mComponent_fullName);//ComponentImpl类型名称
		 String mComponent_paramName=CodeHelper.toLowerCamelCase(mComponent_typeName);//ComponentImpl类变量
		 
		 String mComponentImpl_fullName=getClassFullName(mTableName_upperCamelCase, getRuleComponentImpl());//ComponentImpl类全称
		 String mComponentImpl_package=CodeHelper.getPackageName(mComponentImpl_fullName);//ComponentImpl类包名
		 String mComponentImpl_typeName=CodeHelper.getClassSimpleName(mComponentImpl_fullName);//ComponentImpl类型名称
		 String mComponentImpl_paramName=CodeHelper.toLowerCamelCase(mComponentImpl_typeName);//ComponentImpl类变量
		 
		 String mService_fullName=getClassFullName(mTableName_upperCamelCase, getRuleService());//ComponentImpl类全称
		 String mService_package=CodeHelper.getPackageName(mService_fullName);//ComponentImpl类包名
		 String mService_typeName=CodeHelper.getClassSimpleName(mService_fullName);//ComponentImpl类型名称
		 String mService_paramName=CodeHelper.toLowerCamelCase(mService_typeName);//ComponentImpl类变量
		 
		 String mController_fullName=getClassFullName(mTableName_upperCamelCase, getRuleController());//ComponentImpl类全称
		 String mController_package=CodeHelper.getPackageName(mController_fullName);//ComponentImpl类包名
		 String mController_typeName=CodeHelper.getClassSimpleName(mController_fullName);//ComponentImpl类型名称
		 
		 String mJsonView_fullName=getTpl_jsonView();
		 String mJsonView_typeName=CodeHelper.getClassSimpleName(mJsonView_fullName);//JsonView类型名称
		 String mBusinessException_fullName=getTpl_businessException(); 
		 String mBusinessException_typeName=CodeHelper.getClassSimpleName(mBusinessException_fullName);//businessException类型名称
		 String mAssertUtils_fullName=getTpl_assertUtils();
		 String mAssertUtils_typeName=CodeHelper.getClassSimpleName(mAssertUtils_fullName);//assertUtils类型名称
		 String mBaseController_fullName=getTpl_baseController();
		 String mBaseController_typeName=CodeHelper.getClassSimpleName(mBaseController_fullName);//BaseController类型名称
		 
		 String pageClassFullName=getPageClassFullName();
		 String pageClassSimpleName=null;
		 
		 if(!CodeHelper.isEmpty(pageClassFullName))
		 {
			 pageClassSimpleName=CodeHelper.getClassSimpleName(pageClassFullName);
		 }
		 String packageName=getPackageName();
		 String tableName_upperCamelCase=CodeHelper.toUpperCamelCase(table.getName());
		 String tableName_lowerCamelCase=CodeHelper.toLowerCamelCase(table.getName());
		 
		 String mGroupName=CodeHelper.emptyString(getGroupName(), true).trim();
		 
		 String primaryKey=null;//主键
		 DBSTableColumn primaryTableColumn=null;//主键
		 String primaryKey_upperCamelCase=null;
		 String primaryKey_lowerCamelCase=null;
		 String mPrimaryKey_typeName=null;
		 String mPrimaryKey_paramName=null;
		

		 List<DBSEntityAttribute> attrs= (List<DBSEntityAttribute>)table.getAttributes(monitor);
		 boolean useGeneratedKeys=false;
		 Collection<? extends DBSTableIndex> indexes = table.getIndexes(monitor);
	     if (!CommonUtils.isEmpty(indexes)) {
	         for (DBSTableIndex index : indexes) {
	        	 if( index.isPrimary())
	        	 {
	        		 List<? extends DBSTableIndexColumn>  tableColumn=index.getAttributeReferences(monitor);
	        		 for(DBSTableIndexColumn column:tableColumn)
	        		 {
	        			 DBSTableColumn primaryColumn= column.getTableColumn();
	        			 if(primaryColumn!=null)
	        			 {
	        				 primaryTableColumn=primaryColumn;
		            			 useGeneratedKeys=primaryColumn.isAutoGenerated();
		            			 primaryKey=primaryColumn.getName();
		            			 primaryKey_upperCamelCase=CodeHelper.toUpperCamelCase(primaryKey);
		            			 primaryKey_lowerCamelCase=CodeHelper.toLowerCamelCase(primaryKey);
		            			 
		            			 mPrimaryKey_typeName=CodeHelper.columnType2JavaType(primaryTableColumn);
		            			 mPrimaryKey_paramName= primaryKey_lowerCamelCase;
	        			 }
	        		 }
	        		 
	        	 }
	         }
	         
	     }
   
	     StringBuilder form_list=new StringBuilder();
	     StringBuilder btn_List=new StringBuilder();
	     
	     if(attrs!=null)
		 {
			 for(DBSEntityAttribute attr:attrs)
			 { 
				 String columnName=attr.getName();
				 String codeName=CodeHelper.toLowerCamelCase(columnName);
				 String codeName_upperCamelCase=CodeHelper.toUpperCamelCase(columnName);
				 String desc=attr.getDescription();
				 
				 CodeHelper.addCodeLine(form_list, String.format("<div class=\"form-group\">\n" + 
				 		"                <label class=\"col-sm-2 text-right required\" th:text=\"#{%s.%s.%s}+':'\">%s:</label>\n" + 
				 		"                <div class=\"col-md-6 col-sm-10\">\n" + 
				 		"                    <input type=\"text\"  name=\"%s\" placeholder=\"%s\" class=\"form-control\" th:value=\"${model?.%s}\" />\n" + 
				 		"                </div>\n" + 
				 		"            </div>\n", mGroupName,tableName_lowerCamelCase,codeName,desc,codeName,desc,codeName));
			 }
		 }
	     if(mPrimaryKey_paramName!=null && mPrimaryKey_paramName.length()>0)
	     {
	    	 CodeHelper.addCodeLine(btn_List, String.format("<div class=\"form-group\">\n" + 
	    	 		"                <div class=\"col-sm-offset-2  col-md-10 text-center\">\n" + 
	    	 		"                    <input type=\"hidden\" name=\"codeId\" th:value=\"${model?.codeId}\" />\n" + 
	    	 		"                    <div secure:hasPermission=\"%s:%s:add\" th:if=\"${model == null}\">\n" + 
	    	 		"                        <input type=\"button\" class=\"btn btn-primary\" onclick='submitFn(\"add\")' id=\"addBtn\" value=\"保存\" th:value=\"#{i18n.add('')}\" />\n" + 
	    	 		"                    </div>\n" + 
	    	 		"                    <div th:unless=\"${model == null }\" secure:hasPermission=\"%s:%s:edit\">\n" + 
	    	 		"                        <input type=\"button\" class=\"btn btn-primary\"  onclick='submitFn(\"update\")' id=\"updateBtn\" value=\"保存\" th:value=\"#{i18n.save('')}\" />\n" + 
	    	 		"                    </div>\n" + 
	    	 		"                </div>\n" + 
	    	 		"            </div>",mGroupName,tableName_lowerCamelCase,mGroupName,tableName_lowerCamelCase));
	     }
	    
		 String html= "<!DOCTYPE html>\n" + 
			 		"<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\" xmlns:secure=\"http://www.pollix.at/thymeleaf/shiro\">\n" + 
			 		"<head >\n" + 
			 		"    <meta charset=\"UTF-8\">\n" + 
			 		"    <title>%s</title>\n" + 
			 		"</head>\n" + 
			 		"<body>\n" + 
			 		"<div   id=\"promptMsg\"></div>\n" + 
			 		"<div class=\"form-panel\">\n" + 
		 		 "<form id=\"form_1\" name=\"form\"  method=\"post\"  class=\"form-horizontal\">\n" + 
		 		 "         %s\n" + 
		 		 "         %s\n" + 
		 		"</form></div>\n" + 
		 		"   <th:block th:replace=\"fragments/headTag ::copy \"></th:block>\n" + 
		 		"    <script type=\"text/javascript\" th:inline=\"javascript\">\n" + 
		 	 " function submitFn(url)\n" + 
		 	 "    {\n" + 
		 	 "        $(\"#form_1\").attr(\"action\",url);\n" + 
		 	 "        $(\"#form_1\").submit();\n" + 
		 	 "    }"+
		 		"    </script>\n" + 
		 		"</div></body></html>";
		 sql.append(String.format(html,description, form_list.toString(),btn_List.toString()));

	 }





	@Override
	protected void generateCode(DBRProgressMonitor monitor, StringBuilder sql, DBPScriptObject object)
			throws DBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String saveFileName(DBRProgressMonitor monitor, DBSTable table) {
		String mybatisFilePath=sourceCodeSetting.getOutPutDir()+"/"+sourceCodeSetting.getGroupName()+"/html/add.html";
		return mybatisFilePath;
	}

	@Override
	public void generateOneTableSourceCode(StringBuilder sql, DBRProgressMonitor monitor, DBSTable table,
			Map<String, Object> options) throws DBException {
		 String tplHtml=CodeHelper.loadResource("html_thymeleaf_add");
		 try {
			 	JavaTemplateContext context=new JavaTemplateContext(table, this.sourceCodeSetting, monitor);
			 	String result=TemplateEngine.process(tplHtml, context);
				System.out.println("result:"+result);
			}catch (Exception e) {
				e.printStackTrace();
			}
	String html=null;
		 if(tplHtml!=null)
		 {
			 SourceCodeTableAdapter tableAdapter=new SourceCodeTableAdapter();
			 SourceCodeTable codeTable= tableAdapter.adapterTable(table, sourceCodeSetting, monitor);
			 List<SourceCodeTableColumn> inputForms= codeTable.getInputForms();
			 StringBuilder addFormSB=new StringBuilder();
			 if(inputForms!=null && inputForms.size()>0)
			 {
				 String tplHtml_form=CodeHelper.loadResource("html_thymeleaf_add_form");
				
				 for(int i=0,k=inputForms.size();i<k;i++)
				 {
					 SourceCodeTableColumn tableColumn=inputForms.get(i);
					 Map<String,String> params=new HashMap<String, String>();
					 params.put("desciption",  tableColumn.getDesciption());
					 params.put("column_i18n",  sourceCodeSetting.getGroupName()+(".")+(codeTable.getTableNameLowerCamelCase())+(tableColumn.getLowerCamelCaseName()));
					 params.put("column_lowerCamelCaseName", tableColumn.getLowerCamelCaseName());
					 String addFormHtml=CodeHelper.matchCodeTemplate(tplHtml_form, params);
					addFormSB.append(addFormHtml);
				 }
			 }
			 Map<String,String>params=new HashMap<String,String>();
			 params.put("addForm", addFormSB.toString());
			 html=CodeHelper.matchCodeTemplate(tplHtml, params);
			// html=tplHtml.replaceAll("@addForm@", addFormSB.toString());
			 
		 }
		 sql.append(html);
		
	}
	    
	

}