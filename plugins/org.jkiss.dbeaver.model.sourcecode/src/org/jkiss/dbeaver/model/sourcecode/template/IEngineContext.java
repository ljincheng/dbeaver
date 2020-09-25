package org.jkiss.dbeaver.model.sourcecode.template;

import java.util.Map;

public interface IEngineContext extends IContext{

	public Object getVariable(final String name);
	public void setVariable(final String name, final Object value);
	public void setVariables(final Map<String, Object> variables);
	public String getTemplateFile(String tplFile);
	 
}
