package org.jkiss.dbeaver.model.sourcecode.template;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class TemplateSpec {
	
	 private  String template;

	 private  Set<String> templateSelectors;
	 private Map<String,Object> templateVariable;
	 
	  
	 public TemplateSpec()
	 {
		 super();
		 templateVariable=new HashMap<String, Object>();
	 }
	 
	public TemplateSpec(String template) {
		super();
		this.template = template;
		
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public Map<String, Object> getTemplateVariable() {
		return templateVariable;
	}

	public void setTemplateVariable(Map<String, Object> templateVariable) {
		this.templateVariable = templateVariable;
	}
	 
	 

}
