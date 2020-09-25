package org.jkiss.dbeaver.model.sourcecode.template.processors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jkiss.dbeaver.model.sourcecode.template.IEngineContext;
import org.jkiss.dbeaver.model.sourcecode.template.TemplateData;

public class TextProcessors extends AbstractProcessor{
	
	
	public  TextProcessors(String expressionText, IEngineContext context) {
		super(expressionText, context);
	}
	
	public String getVariableName()
	{
		String name=null;
		String text=getExpressionText();
		 Matcher m = Pattern.compile("^\\$\\{(.+?)\\}$", Pattern.DOTALL).matcher(text);
		 if(m.find())
		 {
			String mvalue=m.group();
			name=mvalue.substring(2,mvalue.length()-1);
		 }
		 return name;
	}

	@Override
	public String text() {
	 
		String name=getVariableName();
		System.out.println("text: "+name);
		if(name!=null)
		{
			Object value=TemplateData.getVariable(name, getContext().getRoot());
			if(value!=null)
			{
				if(value instanceof String)
				{
					return (String)value;
				}else {
					return value.toString();
				}
			}
		}
		return null;
	}
	
	
	

}
