package org.jkiss.dbeaver.model.sourcecode.core;

public class SourceCodeTableColumn {

	private String columnName;
	private String desciption;
	private String codeName;
	private String paramName;
	private String javaType;
	private String javaPackage;
	private Boolean isAutoGenerated;
	private Boolean isRequired;
	private Boolean isPrimary;
	private String defaultValue;
	
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getDesciption() {
		return desciption;
	}
	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}
//	public String getUpperCamelCaseName() {
//		return upperCamelCaseName;
//	}
//	public void setUpperCamelCaseName(String upperCamelCaseName) {
//		this.upperCamelCaseName = upperCamelCaseName;
//	}
//	public String getLowerCamelCaseName() {
//		return lowerCamelCaseName;
//	}
//	public void setLowerCamelCaseName(String lowerCamelCaseName) {
//		this.lowerCamelCaseName = lowerCamelCaseName;
//	}
	
	public String getJavaType() {
		return javaType;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	
	public String getJavaPackage() {
		return javaPackage;
	}
	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}
	public Boolean getIsAutoGenerated() {
		return isAutoGenerated;
	}
	public void setIsAutoGenerated(Boolean isAutoGenerated) {
		this.isAutoGenerated = isAutoGenerated;
	}
	
	
	public Boolean getIsRequired() {
		return isRequired;
	}
	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}
	public Boolean getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
