package org.jkiss.dbeaver.model.sourcecode.core;


public class CodeTemplate {

	private String id;
	private String name;
	private String desc;
	private String template; 
	private String exportPath; 
	
	public CodeTemplate(String id, String name, String desc) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
	}
	
	public CodeTemplate(String id, String name, String desc,String exportPath) {
		super();
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.exportPath=exportPath;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getExportPath() {
		return exportPath;
	}

	public void setExportPath(String exportPath) {
		this.exportPath = exportPath;
	}

	 
	
	
	
}
