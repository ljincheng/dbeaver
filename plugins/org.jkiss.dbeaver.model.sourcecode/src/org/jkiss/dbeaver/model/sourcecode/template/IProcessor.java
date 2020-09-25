package org.jkiss.dbeaver.model.sourcecode.template;

public interface IProcessor {
	
	public void setExpressionText(String text);
	
	public String text();
	
	public void setContext(IEngineContext context);

}
