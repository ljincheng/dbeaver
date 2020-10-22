package org.jkiss.dbeaver.model.sourcecode.ui.context;

public class FormItemContext {
	private String id;
	private String name;
	private String value;
	private String type;
	private String valueType;
	
	public FormItemContext() {
		super();
	} 
	
	public FormItemContext(String id, String name, String value, String type) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.type = type;
	}
	
	public FormItemContext(String id, String name, String value, String type,String valueType) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.type = type;
		this.valueType=valueType;
	}



	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	
}
