package org.jkiss.dbeaver.model.sourcecode.template;

import java.io.Writer;

public class TemplateEngine {

	public static String process(String template, IContext context) {
	
		return process(new TemplateSpec(template), context);
	}

	public static String process(TemplateSpec templateSpec, IContext context) {
		Writer stringWriter = new FastStringWriter(100);
		TemplateManager templateManager=new TemplateManager();
		if(context instanceof IEngineContext)
		{
			templateManager.process((IEngineContext)context,templateSpec, stringWriter);
		}else {
			IEngineContext engineContext=new EngineContext();
			if(context!=null)
			{
				engineContext.setVariables(context.getRoot());
			}
			templateManager.process(engineContext,templateSpec, stringWriter);
		}
		return stringWriter.toString();
	} 
	
	

}
