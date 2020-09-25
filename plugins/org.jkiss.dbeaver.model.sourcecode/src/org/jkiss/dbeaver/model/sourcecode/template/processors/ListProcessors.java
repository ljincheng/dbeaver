package org.jkiss.dbeaver.model.sourcecode.template.processors;

import org.jkiss.dbeaver.model.sourcecode.template.IEngineContext;

public class ListProcessors extends AbstractProcessor{

	public  ListProcessors(String expressionText, IEngineContext context) {
		super(expressionText, context);
	}
	
	
	@Override
	public String text() {
		return "list:"+getExpressionText();
	}

	
}
