package org.jkiss.dbeaver.model.sourcecode.template;


public interface ITemplateEngine {
	
	
	public  String process(final String template,  IContext context);
	
	
	public String process(final TemplateSpec templateSpec,IContext context);

}
