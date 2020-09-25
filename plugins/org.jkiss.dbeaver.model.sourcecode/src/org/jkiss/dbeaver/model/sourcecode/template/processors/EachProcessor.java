package org.jkiss.dbeaver.model.sourcecode.template.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jkiss.dbeaver.model.sourcecode.template.IEngineContext;
import org.jkiss.dbeaver.model.sourcecode.template.KeyValueReaderIterable;
import org.jkiss.dbeaver.model.sourcecode.template.TemplateData;

public class EachProcessor extends AbstractProcessor{
	
	private Map<String, String> expMap;
	
	
	public  EachProcessor(String expressionText, IEngineContext context) {
		super(expressionText, context);
		expMap=new HashMap<String, String>();
	}

	
	private String keyValueRead()
	{
		 
		int size=expressionText.length();
		if(size<7)
		{
			return "";
		}
			
	 StringBuilder result=new StringBuilder();
		char startTag=expressionText.charAt(5);
		char endTag=expressionText.charAt(size-1);
		if('{' == startTag || '}' == endTag )
		{
			String keyvalueStr=expressionText.substring(6, size-1);
		 System.out.println("keyvalueStr="+keyvalueStr);
			List<Map.Entry<String, String>> collect = KeyValueReaderIterable.stream(keyvalueStr,':').collect(Collectors.toList());
			for(int i=0,k=collect.size();i<k;i++)
			{
				Map.Entry<String, String> item=collect.get(i);
				expMap.put(item.getKey(), item.getValue());
			}
		}
		String list=expMap.get("list");
		String text=expMap.get("text");
		String item=expMap.get("item");
		
		if(list!=null && list.length()>0)
		{
			Object value=TemplateData.getVariable(list, getContext().getRoot());
			if(value!=null && text!=null)
			{
				String itemVarName=item+".";
				
				if(value instanceof List)
				{
					List<Object> objectList=(List<Object>)value;
					if(objectList!=null)
					{
						for(int i=0,k=objectList.size();i<k;i++)
						{
							Object object=objectList.get(i);
							StringBuffer sb=new StringBuffer();
							  Matcher m = Pattern.compile("\\$\\{(.+?)\\}", Pattern.DOTALL).matcher(text);
							    while (m.find()) {
//							    	try {
								    	String varKey=m.group();
								    	String varStr=varKey.substring(2,varKey.length()-1);
								    	if(varStr.startsWith(itemVarName))
								    	{
								    		String newVarStr=varStr.substring(itemVarName.length());
								    		Object varValue=TemplateData.getVariable(newVarStr, object);
								    		 m.appendReplacement(sb, varValue==null ? "" :m.quoteReplacement(varValue.toString()));
								    	}else {
								    		Object varValue=TemplateData.getVariable(varStr, getContext().getRoot());
								    		 m.appendReplacement(sb, varValue==null ? "" :m.quoteReplacement(varValue.toString()));
								    	}
//							    	}catch (Exception e) {
//							    		e.printStackTrace();
//									}
//							    	
							    }
							    m.appendTail(sb);
							    result.append(sb);
						}
						
					}
					
				}
				System.out.println("EACH VALUE="+result.toString());
				return result.toString();
				
			}
		}
		return null;
	}
	
	 
	
	
	@Override
	public String text() {
		String value= keyValueRead();
		System.out.println("EACHRESULT:"+value);
		return value;
	//	return "each1:"+getExpressionText();
	}

	 
	
}
