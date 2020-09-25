package org.jkiss.dbeaver.model.sourcecode.template.processors;

import org.jkiss.dbeaver.model.sourcecode.template.IEngineContext;
import org.jkiss.dbeaver.model.sourcecode.template.IProcessor;

public abstract class AbstractProcessor implements IProcessor{
	
	protected String expressionText;
	protected IEngineContext context; 
	
	public AbstractProcessor(String expressionText, IEngineContext context) {
		super();
		this.expressionText = expressionText;
		this.context = context;
	}


	@Override
	public void setExpressionText(String text) {
		this.expressionText=text;
	}
	
	
	public String getExpressionText()
	{
		return this.expressionText;
	}

	public void setContext(IEngineContext context) {
		this.context=context;	
	}
	
	public IEngineContext getContext()
	{
		return this.context;
	}
	
}
