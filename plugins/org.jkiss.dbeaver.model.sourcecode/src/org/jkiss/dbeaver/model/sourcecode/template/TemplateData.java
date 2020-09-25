package org.jkiss.dbeaver.model.sourcecode.template;


import ognl.OgnlContext;
import ognl.OgnlException;

public class TemplateData {

	
	private static OgnlContext context;
	private static final String EXPRESSION_ROOT="#root.";
	
	static{
		context = new OgnlContext(null, null, new OgnlDefaultMemberAccess(true));
	}
	
	public static Object getVariable(String exp,Object data) {
		try {
			String expression=EXPRESSION_ROOT+exp;
		 	Object value = ognl.Ognl.getValue(expression,context,data);
		 	if (value != null) {
				return value; 
			}
		}catch (OgnlException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
