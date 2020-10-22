package org.jkiss.dbeaver.model.sourcecode.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.template.EngineContext;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.sourcecode.utils.TextUtils;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndex;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndexColumn;
import org.jkiss.utils.CommonUtils;
 

public final class JavaTemplateContext extends EngineContext{
	public static final String KEY_TABLE="table";
	public static final String KEY_SETTING="setting";
	public static final String KEY_TABLENAME="tableName";
	public static final String KEY_TABLEDESCIPTION="tableDesciption";
	public static final String KEY_TABLECODENAME="tableCodeName";
	public static final String KEY_TABLECODEPARAM="tableCodeParam";
	public static final String KEY_PRIMARYCOLUMN="primaryColumn";
	public static final String KEY_ENTITY="entity";
	public static final String KEY_DAO="dao";
	public static final String KEY_COMPONENT="component";
	public static final String KEY_COMPONENTIMPL="componentImpl";
	public static final String KEY_SERVICE="service";
	public static final String KEY_SERVICEIMPL="serviceImpl";
	public static final String KEY_CONTROLLER="controller";
	public static final String KEY_PAGEDO="pageDo";
	public static final String KEY_JSONVIEW="jsonView";
	public static final String KEY_BUSINESSEXCEPTION="businessException";
	public static final String KEY_ASSERTUTILS="assertUtils";
	public static final String KEY_BASECONTROLLER="baseController";
	public static final String KEY_COLUMNS="columns";
	public static final String KEY_COLUMNPACKAGES="columnsPackages";
	public static final String KEY_INPUTFORMS="inputForms";
	public static final String KEY_INPUTFORMPACKAGES="inputFormsPackages";
	public static final String KEY_TABLELISTCOLS="tableListCols";
	public static final String KEY_TABLELISTCOLSPACKAGES="tableListColsPackages";
	public static final String KEY_SEARCHFORMS="searchForms";
	public static final String KEY_SEARCHFORMPACKAGES="searchFormsPackages";
	public static final String KEY_MYBATISINSERTCOLUMNS="mybatisInsertColumns";
	public static final String KEY_MYBATISINSERTCOLUMNSPACKAGES="mybatisInsertColumnsPackages";
	
	private Map<String, Object> dataMap;
	public JavaTemplateContext() {
		super();
		dataMap=new HashMap<String, Object>();
	}
	
	public JavaTemplateContext(DBSTable table,SourceCodeSetting setting,DBRProgressMonitor monitor)
	{
		this();
		initData(table,setting,monitor);
	}
	
	private void initData(DBSTable table,SourceCodeSetting setting,DBRProgressMonitor monitor)
	{
		if(table==null || setting==null)
		{
			return ;
		}
		String tableName=table.getName();
		
		dataMap.put(KEY_TABLE, table);
		dataMap.put(KEY_SETTING, setting);
		dataMap.put(KEY_TABLENAME,tableName);
		dataMap.put(KEY_TABLEDESCIPTION,CodeHelper.emptyString(table.getDescription(), false));
		dataMap.put(KEY_TABLECODENAME,CodeHelper.toUpperCamelCase(tableName));
		dataMap.put(KEY_TABLECODEPARAM,CodeHelper.toLowerCamelCase(tableName));
		dataMap.put(KEY_ENTITY,fileRule2JavaClass(tableName,setting.getRuleEntity(),setting));
		
		dataMap.put(KEY_DAO,fileRule2JavaClass(tableName,setting.getRuleDao(),setting));
		dataMap.put(KEY_COMPONENT,fileRule2JavaClass(tableName,setting.getRuleComponent(),setting));
		dataMap.put(KEY_COMPONENTIMPL,fileRule2JavaClass(tableName,setting.getRuleComponentImpl(),setting));
		dataMap.put(KEY_SERVICE,fileRule2JavaClass(tableName,setting.getRuleService(),setting));
		dataMap.put(KEY_SERVICEIMPL,fileRule2JavaClass(tableName,setting.getRuleServiceImpl(),setting));
		dataMap.put(KEY_CONTROLLER,fileRule2JavaClass(tableName,setting.getRuleController(),setting));
		dataMap.put(KEY_PAGEDO,fileRule2JavaClass(tableName,setting.getClassPage(),setting));
		dataMap.put(KEY_JSONVIEW,fileRule2JavaClass(tableName,setting.getClassJsonView(),setting));
		dataMap.put(KEY_BUSINESSEXCEPTION,fileRule2JavaClass(tableName,setting.getClassBusinessException(),setting));
		dataMap.put(KEY_ASSERTUTILS,fileRule2JavaClass(tableName,setting.getClassAssertUtils(),setting));
		dataMap.put(KEY_BASECONTROLLER,fileRule2JavaClass(tableName,setting.getClassBaseController(),setting));
		
		  
		
		
		// primaryKey
		 SourceCodeTableColumn primaryKeyColumn=null;
		 List<SourceCodeTableColumn> columns=new ArrayList<SourceCodeTableColumn>();
		 List<SourceCodeTableColumn> tableListCols=new ArrayList<SourceCodeTableColumn>();
		 List<SourceCodeTableColumn> inputForms=new ArrayList<SourceCodeTableColumn>();
		 List<SourceCodeTableColumn> mybatisInsertColumns=new ArrayList<SourceCodeTableColumn>();
		 List<SourceCodeTableColumn> searchForms=new ArrayList<SourceCodeTableColumn>();
		try {
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
			            			boolean useGeneratedKeys=primaryColumn.isAutoGenerated();
			            			 String primaryKey=primaryColumn.getName();
			            			 String primaryKey_upperCamelCase=CodeHelper.toUpperCamelCase(primaryKey);
			            			 String primaryKey_lowerCamelCase=CodeHelper.toLowerCamelCase(primaryKey);
			            			 
			            			 String mPrimaryKey_typeName=CodeHelper.columnType2JavaType(primaryColumn);
			            			 String mPrimaryKey_paramName= primaryKey_lowerCamelCase;
			            			 
			            			 primaryKeyColumn=new SourceCodeTableColumn();
			            			 primaryKeyColumn.setColumnName(primaryKey);
			            			 primaryKeyColumn.setDesciption(primaryColumn.getDescription());
			            			 primaryKeyColumn.setIsAutoGenerated(useGeneratedKeys);
			            			 primaryKeyColumn.setIsPrimary(true);
			            			 primaryKeyColumn.setIsRequired(primaryColumn.isRequired());
			            			 primaryKeyColumn.setCodeName(primaryKey_upperCamelCase);
			            			 primaryKeyColumn.setParamName(primaryKey_lowerCamelCase);
			            			 primaryKeyColumn.setJavaType(mPrimaryKey_typeName);
			            			 dataMap.put(KEY_PRIMARYCOLUMN,primaryKeyColumn);
		        			 }
		        		 }
		        		 
		        	 }
		         }
		         
		     }
		
		
		
		
		 List<DBSEntityAttribute> attrs= (List<DBSEntityAttribute>)table.getAttributes(monitor);
		 if(attrs!=null)
		 {
			 for(DBSEntityAttribute attr:attrs)
			 { 
				 
				 String columnName=attr.getName();
				 String codeName=CodeHelper.toLowerCamelCase(columnName);
				 String codeName_upperCamelCase=CodeHelper.toUpperCamelCase(columnName);
				  
				  SourceCodeTableColumn codeColumn=new SourceCodeTableColumn();
				  codeColumn.setIsRequired(attr.isRequired());
				  codeColumn.setColumnName(columnName);
				  codeColumn.setDesciption(attr.getDescription());
				  codeColumn.setCodeName(codeName_upperCamelCase);
				  codeColumn.setParamName(codeName);
				  codeColumn.setJavaType(CodeHelper.columnType2JavaType(attr));
				  codeColumn.setJavaPackage(CodeHelper.columnType2JavaPackageName(attr));
				  codeColumn.setDefaultValue(attr.getDefaultValue());
				  
				  boolean isPrimary=false;
				  boolean isAutoGenerated=attr.isAutoGenerated();
				  if(isAutoGenerated)
				  {
					  codeColumn.setIsPrimary(true);
					  codeColumn.setIsAutoGenerated(isAutoGenerated);
				  }else {
					 if(primaryKeyColumn!=null && columnName.equals(primaryKeyColumn.getColumnName())){
						 codeColumn.setIsPrimary(true);
					 }else {
						 codeColumn.setIsPrimary(false);
					 }
				  }
				  columns.add(codeColumn);
				  
				  if(isHtmlTableColumn(codeColumn))
				  {
					  tableListCols.add(codeColumn);
				  }
				  if(isHtmlInputForms(codeColumn))
				  {
					  inputForms.add(codeColumn);
				  }
				  if(isHtmlSearchForms(codeColumn))
				  {
					  searchForms.add(codeColumn);
				  }
				  if(isMybatisInsertColumn(codeColumn))
				  {
					  mybatisInsertColumns.add(codeColumn);
				  }
				 
			 }
		 }
		 
		}catch (DBException e) {
			e.printStackTrace();
		}
		dataMap.put(KEY_COLUMNS,columns);
		dataMap.put(KEY_COLUMNPACKAGES, tableColumnImportJavaPackageList(columns));
		dataMap.put(KEY_INPUTFORMS,inputForms);
		dataMap.put(KEY_INPUTFORMPACKAGES,tableColumnImportJavaPackageList(inputForms));
		dataMap.put(KEY_SEARCHFORMS,searchForms);
		dataMap.put(KEY_SEARCHFORMPACKAGES,tableColumnImportJavaPackageList(searchForms));
		dataMap.put(KEY_TABLELISTCOLS,tableListCols);
		dataMap.put(KEY_TABLELISTCOLSPACKAGES,tableColumnImportJavaPackageList(tableListCols));
		dataMap.put(KEY_MYBATISINSERTCOLUMNS,mybatisInsertColumns);
		dataMap.put(KEY_MYBATISINSERTCOLUMNSPACKAGES,tableColumnImportJavaPackageList(mybatisInsertColumns));
		setVariables(dataMap);
		 
	}
	
	
	private List<String> tableColumnImportJavaPackageList(List<SourceCodeTableColumn> tableColumns)
	{
		List<String> result=new ArrayList<String>();
		Map<String, String> packageMap=new HashMap<String, String>();
        if (!CommonUtils.isEmpty(tableColumns)) {
            for(SourceCodeTableColumn column:tableColumns)
            {
            	String packageName=column.getJavaPackage();
            	if(packageName!=null && packageName.length()>0)
            	{
            		packageMap.put(packageName, packageName);
            	}
            }
            if(!packageMap.isEmpty())
            {
	            for(Map.Entry<String, String> entry : packageMap.entrySet()){
	                String mapKey = entry.getKey();
	                result.add(mapKey);
	            }
            }
            
        }
        return result;
	}

	private static SourceCodeJavaClass fileRule2JavaClass(String tableName,String rule,SourceCodeSetting setting) {
		
		String fullName=fillRuleFullName(tableName,rule,setting);
		SourceCodeJavaClass javaClass=new SourceCodeJavaClass();
		javaClass.setName(fullName);
		javaClass.setPackageName(CodeHelper.getPackageName(fullName));
		javaClass.setSimpleName(CodeHelper.getClassSimpleName(fullName));
		javaClass.setParamName(CodeHelper.toLowerCamelCase(javaClass.getSimpleName()));
		return javaClass;
	}
	
	private static boolean isHtmlTableColumn(SourceCodeTableColumn column)
	{
		if(column.getIsPrimary())
		{
			return false;
		}
		if(TextUtils.hasIn(column.getParamName().toLowerCase(), "createtime","id","parentid","pid","userid") )
		{
			return false;
		}
		return true;
	}
	private static boolean isHtmlInputForms(SourceCodeTableColumn column)
	{
		if(column.getIsPrimary())
		{
			return false;
		}
		if(TextUtils.hasIn(column.getParamName().toLowerCase(), "createtime","updatetime","lasttime","id","parentid","pid","userid") )
		{
			return false;
		}
		return true;
	}
	private static boolean isMybatisInsertColumn(SourceCodeTableColumn column)
	{
		if(column!=null && column.getIsPrimary()!=null &&  column.getIsPrimary()  && column.getIsAutoGenerated()!=null && column.getIsAutoGenerated())
		{
			return false;
		}
		return true;
	}
	private static boolean isHtmlSearchForms(SourceCodeTableColumn column)
	{
		if(column.getIsPrimary())
		{
			return false;
		}
		if(TextUtils.hasIn(column.getParamName().toLowerCase(), "createtime","updatetime","lasttime","id","parentid","pid","userid","remark") )
		{
			return false;
		}
		return true;
	}
	
	public static String fillRuleFullName(String tableName,String rule,SourceCodeSetting setting)
	{
		if(CodeHelper.isEmpty(rule))
		{
			return tableName;
		}
		Map<String, Object> params=new HashMap<String, Object>();
		params.put("packagePath", setting.getPackagePath());
		params.put("groupName", setting.getGroupName());
		params.put("table_name",CodeHelper.toUpperCamelCase(tableName));
		return CodeHelper.processTemplate(rule, params);
	} 
	

}
