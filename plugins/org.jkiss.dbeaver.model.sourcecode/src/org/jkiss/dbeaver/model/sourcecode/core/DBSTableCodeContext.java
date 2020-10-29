package org.jkiss.dbeaver.model.sourcecode.core;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.security.Timestamp;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.runtime.DBRRunnableWithProgress;
import org.jkiss.dbeaver.model.sourcecode.template.EngineContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormContext;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.sourcecode.utils.TextUtils;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndex;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndexColumn;
import org.jkiss.utils.CommonUtils;

public class DBSTableCodeContext extends EngineContext{
	
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
	private DBSTable mTable;
	private Map<String, Object> dataMap;
	private List<CodeColumnAttribute> mColumns=new ArrayList<CodeColumnAttribute>();
	
	public DBSTableCodeContext() {
		super();
	}
//	public DBSTableCodeContext(DBSTable table) {
//		super();
//		this.mTable=table; 
//	}
	
	public void run(DBRProgressMonitor monitor,DBSTable table,List<FormItemContext> settings)
	{
		if(dataMap!=null)
		{
			dataMap.clear();
			dataMap=null;
		}
		dataMap=new HashMap<String, Object>();
		if(table!=null) {
			this.mTable=table;
			if(this.mTable!=null )
			{
					tableColumns(monitor);
			}
			pushColumnsAttribute(mColumns,settings);
		}
	} 
	 
	public void refersh(List<FormItemContext> settings)
	{
		if(this.mTable!=null )
		{
			if(dataMap!=null)
			{
				dataMap.clear();
				dataMap=null;
			}
			dataMap=new HashMap<String, Object>();
			pushColumnsAttribute(mColumns,settings);
		}
		
	}

	public List<CodeColumnAttribute> getColumns(){
		return this.mColumns;
	}
	
	public void pushColumnsAttribute(List<CodeColumnAttribute> columns,List<FormItemContext> settings)
	{
		if(columns!=null)
		{
			Object primaryKeyColumnObj=dataMap.get(KEY_PRIMARYCOLUMN);
			 CodeColumnAttribute primaryKeyColumn=null;
			 if(primaryKeyColumnObj!=null) {
				 primaryKeyColumn= (CodeColumnAttribute)primaryKeyColumnObj;
			 }
			 List<CodeColumnAttribute> mybatisInsertColumns=new ArrayList<CodeColumnAttribute>();
			 List<CodeColumnAttribute> tableListCols=new ArrayList<CodeColumnAttribute>();
			 List<CodeColumnAttribute> searchForms=new ArrayList<CodeColumnAttribute>();
			 List<CodeColumnAttribute> inputForms=new ArrayList<CodeColumnAttribute>();
			 
			for(int i=0,k=columns.size();i<k;i++) {
				CodeColumnAttribute column=columns.get(i);
				if(primaryKeyColumn!=null && column.getColumnName().equals(primaryKeyColumn.getColumnName()))
				{
					primaryKeyColumn=column;
				}
				if(column.getSqlInsertSelected()!=null && column.getSqlInsertSelected()){
					mybatisInsertColumns.add(column);
				}
				if(column.getHtmlTableListSelected()!=null && column.getHtmlTableListSelected()){
					tableListCols.add(column);
				}
				if(column.getHtmlSearchSelected()!=null && column.getHtmlSearchSelected()){
					searchForms.add(column);
				}
				if(column.getHtmlInputFormSelected()!=null && column.getHtmlInputFormSelected()) {
					inputForms.add(column);
				}
			}
			dataMap.put(KEY_PRIMARYCOLUMN,primaryKeyColumn);
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
			String tableName=mTable.getName();
			dataMap.put(KEY_TABLE, mTable);
			dataMap.put(KEY_TABLENAME,tableName);
			dataMap.put(KEY_TABLEDESCIPTION,CodeHelper.emptyString(mTable.getDescription(), false));
			dataMap.put(KEY_TABLECODENAME,CodeHelper.toUpperCamelCase(tableName));
			dataMap.put(KEY_TABLECODEPARAM,CodeHelper.toLowerCamelCase(tableName));
			Map<String,String> settingsData=new HashMap<String, String>(); 
			if(settings!=null)
			{
				for(int i=0,k=settings.size();i<k;i++)
				{
					FormItemContext formItem=settings.get(i);
					settingsData.put(formItem.getId(), formItem.getValue());
				}
				for(int i=0,k=settings.size();i<k;i++)
				{
					FormItemContext formItem=settings.get(i); 
					if("class".equals(formItem.getValueType()))
					{
						dataMap.put(formItem.getId(),fileRule2JavaClass(tableName,formItem.getValue(),settingsData));
					}
				}
			}
			dataMap.put(KEY_SETTING,settingsData); 
			setVariables(dataMap);
		}
	}
	
	private void tableColumns(DBRProgressMonitor monitor)
	{
		 CodeColumnAttribute primaryKeyColumn=null;
		 List<CodeColumnAttribute> tableListCols=new ArrayList<CodeColumnAttribute>();
		 List<CodeColumnAttribute> inputForms=new ArrayList<CodeColumnAttribute>();
		 List<CodeColumnAttribute> mybatisInsertColumns=new ArrayList<CodeColumnAttribute>();
		 List<CodeColumnAttribute> searchForms=new ArrayList<CodeColumnAttribute>();
		try {
		
			 Collection<? extends DBSTableIndex> indexes = mTable.getIndexes(monitor);
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
			            			 
			            			 primaryKeyColumn=new CodeColumnAttribute();
			            			 primaryKeyColumn.setColumnName(primaryKey);
			            			 primaryKeyColumn.setDesciption(primaryColumn.getDescription());
			            			 primaryKeyColumn.setIsAutoGenerated(useGeneratedKeys);
			            			 primaryKeyColumn.setIsPrimary(true);
			            			 primaryKeyColumn.setIsRequired(primaryColumn.isRequired());
			            			 primaryKeyColumn.setCodeName(primaryKey_upperCamelCase);
			            			 primaryKeyColumn.setParamName(primaryKey_lowerCamelCase);
			            			 primaryKeyColumn.setJavaType(mPrimaryKey_typeName);
			            			 primaryKeyColumn.setJavaPackage(getJavaFullType(mPrimaryKey_typeName));
			            			
			            			 dataMap.put(KEY_PRIMARYCOLUMN,primaryKeyColumn);
		        			 }
		        		 }
		        		 
		        	 }
		         }
		         
		     }
		
		
		 List<DBSEntityAttribute> attrs= (List<DBSEntityAttribute>)mTable.getAttributes(monitor);
		 if(attrs!=null)
		 {
			 for(DBSEntityAttribute attr:attrs)
			 { 
				 
				 String columnName=attr.getName();
				 String codeName=CodeHelper.toLowerCamelCase(columnName);
				 String codeName_upperCamelCase=CodeHelper.toUpperCamelCase(columnName);
				  
				 CodeColumnAttribute codeColumn=new CodeColumnAttribute();
				  codeColumn.setIsRequired(attr.isRequired());
				  codeColumn.setColumnName(columnName);
				  codeColumn.setDesciption(attr.getDescription());
				  codeColumn.setCodeName(codeName_upperCamelCase);
				  codeColumn.setParamName(codeName);
				  codeColumn.setJavaType(CodeHelper.columnType2JavaType(attr));
				  codeColumn.setJavaPackage(getJavaFullType(codeColumn.getJavaType()));
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
            			 codeColumn.setIsAutoGenerated(primaryKeyColumn.getIsAutoGenerated());
					 }else {
						 codeColumn.setIsPrimary(false);
					 }
				  }
				  
				  if(isHtmlTableColumn(codeColumn))
				  {
					 codeColumn.setHtmlTableListSelected(true);
				  }
				  if(isHtmlInputForms(codeColumn))
				  {
					  codeColumn.setHtmlInputFormSelected(true);
				  }
				  if(isHtmlSearchForms(codeColumn))
				  {
					  codeColumn.setHtmlSearchSelected(true);
				  }
				  if(isMybatisInsertColumn(codeColumn))
				  {
					  codeColumn.setSqlInsertSelected(true);
				  }
				  mColumns.add(codeColumn);  
				 
			 }
		 }
		 
		}catch (DBException e) {
			e.printStackTrace();
		}
		dataMap.put(KEY_COLUMNS,mColumns);
		dataMap.put(KEY_COLUMNPACKAGES, tableColumnImportJavaPackageList(mColumns));
		dataMap.put(KEY_INPUTFORMS,inputForms);
		dataMap.put(KEY_INPUTFORMPACKAGES,tableColumnImportJavaPackageList(inputForms));
		dataMap.put(KEY_SEARCHFORMS,searchForms);
		dataMap.put(KEY_SEARCHFORMPACKAGES,tableColumnImportJavaPackageList(searchForms));
		dataMap.put(KEY_TABLELISTCOLS,tableListCols);
		dataMap.put(KEY_TABLELISTCOLSPACKAGES,tableColumnImportJavaPackageList(tableListCols));
		dataMap.put(KEY_MYBATISINSERTCOLUMNS,mybatisInsertColumns);
		dataMap.put(KEY_MYBATISINSERTCOLUMNSPACKAGES,tableColumnImportJavaPackageList(mybatisInsertColumns));
//		
	}
	
	private static SourceCodeJavaClass fileRule2JavaClass(String tableName,String rule,Map setting) {
		
		String fullName=fillRuleFullName(tableName,rule,setting);
		SourceCodeJavaClass javaClass=new SourceCodeJavaClass();
		javaClass.setName(fullName);
		javaClass.setPackageName(CodeHelper.getPackageName(fullName));
		javaClass.setSimpleName(CodeHelper.getClassSimpleName(fullName));
		javaClass.setParamName(CodeHelper.toLowerCamelCase(javaClass.getSimpleName()));
		return javaClass;
	}
	
	public static String fillRuleFullName(String tableName,String rule,Map params)
	{
		if(CodeHelper.isEmpty(rule))
		{
			return tableName;
		}
//		Map<String, Object> params=new HashMap<String, Object>();
//		params.put("package_name", setting.getPackagePath());
//		params.put("group_name", setting.getGroupName());
		params.put("table_name",CodeHelper.toUpperCamelCase(tableName));
		return CodeHelper.processTemplate(rule, params);
	} 
	
	public static String getJavaFullType(String javaType)
	{
		if(CommonUtils.isNotEmpty(javaType))
		{
			if(javaType.equals("BigDecimal"))
			{
				return BigDecimal.class.getName();
			}else if(javaType.equals("Date") || javaType.equals("DateTime")) {
				return Date.class.getName();
			}else if(javaType.equals("Timestamp")) {
				return Timestamp.class.getName();
			}else if(javaType.equals("Time")) {
				return Time.class.getName();
			} 
			return null;
		}
		return null;
	}
	private List<String> tableColumnImportJavaPackageList(List<CodeColumnAttribute> tableColumns)
	{
		List<String> result=new ArrayList<String>();
		Map<String, String> packageMap=new HashMap<String, String>();
        if (!CommonUtils.isEmpty(tableColumns)) {
            for(CodeColumnAttribute column:tableColumns)
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
	
	private static boolean isHtmlTableColumn(CodeColumnAttribute column)
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
	private static boolean isHtmlInputForms(CodeColumnAttribute column)
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
	private static boolean isMybatisInsertColumn(CodeColumnAttribute column)
	{
		if(column!=null && column.getIsPrimary()!=null &&  column.getIsPrimary()  && column.getIsAutoGenerated()!=null && column.getIsAutoGenerated())
		{
			return false;
		}
		return true;
	}
	private static boolean isHtmlSearchForms(CodeColumnAttribute column)
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

}
