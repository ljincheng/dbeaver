package org.jkiss.dbeaver.model.sourcecode.core;

import java.util.List;

public class SourceCodeTable {

	private String tableName;
	private String tableDesciption;
	private String tableNameUpperCamelCase;
	private String tableNameLowerCamelCase;
	
	private SourceCodeJavaClass javaEntity;
	private SourceCodeJavaClass javaDao;
	private SourceCodeJavaClass javaComponent;
	private SourceCodeJavaClass javaComponentImpl;
	private SourceCodeJavaClass javaService;
	private SourceCodeJavaClass javaServiceImpl;
	private SourceCodeJavaClass javaController;
	private SourceCodeJavaClass javaJsonView;
	private SourceCodeJavaClass javaBusinessException;
	private SourceCodeJavaClass javaAssertUtils;
	private SourceCodeJavaClass javaBaseController;
	private SourceCodeJavaClass javaPage;
	      
	
	private List<SourceCodeTableColumn> columns;
	
	private SourceCodeTableColumn primaryColumn;
	
	private List<SourceCodeTableColumn> inputForms;//录入数据表单
	private List<SourceCodeTableColumn> tableListCols;//查询表格列表
	private List<SourceCodeTableColumn> searchForms;//搜索过滤列表
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableDesciption() {
		return tableDesciption;
	}
	public void setTableDesciption(String tableDesciption) {
		this.tableDesciption = tableDesciption;
	}
	public String getTableNameUpperCamelCase() {
		return tableNameUpperCamelCase;
	}
	public void setTableNameUpperCamelCase(String tableNameUpperCamelCase) {
		this.tableNameUpperCamelCase = tableNameUpperCamelCase;
	}
	public String getTableNameLowerCamelCase() {
		return tableNameLowerCamelCase;
	}
	public void setTableNameLowerCamelCase(String tableNameLowerCamelCase) {
		this.tableNameLowerCamelCase = tableNameLowerCamelCase;
	}
	public SourceCodeJavaClass getJavaEntity() {
		return javaEntity;
	}
	public void setJavaEntity(SourceCodeJavaClass javaEntity) {
		this.javaEntity = javaEntity;
	}
	public SourceCodeJavaClass getJavaDao() {
		return javaDao;
	}
	public void setJavaDao(SourceCodeJavaClass javaDao) {
		this.javaDao = javaDao;
	}
	public SourceCodeJavaClass getJavaComponent() {
		return javaComponent;
	}
	public void setJavaComponent(SourceCodeJavaClass javaComponent) {
		this.javaComponent = javaComponent;
	}
	public SourceCodeJavaClass getJavaComponentImpl() {
		return javaComponentImpl;
	}
	public void setJavaComponentImpl(SourceCodeJavaClass javaComponentImpl) {
		this.javaComponentImpl = javaComponentImpl;
	}
	public SourceCodeJavaClass getJavaService() {
		return javaService;
	}
	public void setJavaService(SourceCodeJavaClass javaService) {
		this.javaService = javaService;
	}
	public SourceCodeJavaClass getJavaServiceImpl() {
		return javaServiceImpl;
	}
	public void setJavaServiceImpl(SourceCodeJavaClass javaServiceImpl) {
		this.javaServiceImpl = javaServiceImpl;
	}
	public SourceCodeJavaClass getJavaController() {
		return javaController;
	}
	public void setJavaController(SourceCodeJavaClass javaController) {
		this.javaController = javaController;
	}
	public SourceCodeJavaClass getJavaJsonView() {
		return javaJsonView;
	}
	public void setJavaJsonView(SourceCodeJavaClass javaJsonView) {
		this.javaJsonView = javaJsonView;
	}
	
	public SourceCodeJavaClass getJavaBusinessException() {
		return javaBusinessException;
	}
	public void setJavaBusinessException(SourceCodeJavaClass javaBusinessException) {
		this.javaBusinessException = javaBusinessException;
	}
	public SourceCodeJavaClass getJavaAssertUtils() {
		return javaAssertUtils;
	}
	public void setJavaAssertUtils(SourceCodeJavaClass javaAssertUtils) {
		this.javaAssertUtils = javaAssertUtils;
	}
	public SourceCodeJavaClass getJavaBaseController() {
		return javaBaseController;
	}
	public void setJavaBaseController(SourceCodeJavaClass javaBaseController) {
		this.javaBaseController = javaBaseController;
	}
	public SourceCodeJavaClass getJavaPage() {
		return javaPage;
	}
	public void setJavaPage(SourceCodeJavaClass javaPage) {
		this.javaPage = javaPage;
	}
	public List<SourceCodeTableColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<SourceCodeTableColumn> columns) {
		this.columns = columns;
	}
	public SourceCodeTableColumn getPrimaryColumn() {
		return primaryColumn;
	}
	public void setPrimaryColumn(SourceCodeTableColumn primaryColumn) {
		this.primaryColumn = primaryColumn;
	}
	public List<SourceCodeTableColumn> getInputForms() {
		return inputForms;
	}
	public void setInputForms(List<SourceCodeTableColumn> inputForms) {
		this.inputForms = inputForms;
	}
	public List<SourceCodeTableColumn> getTableListCols() {
		return tableListCols;
	}
	public void setTableListCols(List<SourceCodeTableColumn> tableListCols) {
		this.tableListCols = tableListCols;
	}
	public List<SourceCodeTableColumn> getSearchForms() {
		return searchForms;
	}
	public void setSearchForms(List<SourceCodeTableColumn> searchForms) {
		this.searchForms = searchForms;
	}
	 
	
	
}
