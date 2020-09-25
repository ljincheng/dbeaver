package org.jkiss.dbeaver.model.sourcecode.template;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jkiss.dbeaver.model.sourcecode.template.processors.EachProcessor;
import org.jkiss.dbeaver.model.sourcecode.template.processors.ListProcessors;
import org.jkiss.dbeaver.model.sourcecode.template.processors.TextProcessors;

public class StandardDialect{
	
	private HashSet<String> tplExpSet;
	private List<IProcessor> mProcessors;
	private IEngineContext mContext;
	
	public StandardDialect() {
        this.tplExpSet=new HashSet<String>();
        this.mProcessors=new ArrayList<IProcessor>();
    }
	
	
	public void execTemplate(IEngineContext context,TemplateSpec templateSpec,Writer writer)
	{
		mContext=context;
		parse(templateSpec,writer);
		// TEST 测试部分
		if(tplExpSet.size()>0)
		{
			Object[] expArray= tplExpSet.toArray();
			System.out.println("EXP:===========");
			for(int i=0,k=expArray.length;i<k;i++)
			{
				String exp=expArray[i].toString();
				System.out.println("EXP: "+i+"  "+exp);
			}
		}
		if(mProcessors.size()>0)
		{ 
			for(int i=0,k=mProcessors.size();i<k;i++)
			{
				IProcessor processor=mProcessors.get(i);
				if(processor!=null)
				{
				   System.out.println("processor: "+processor.text());
				}else {
					System.out.println("processor: 空");
				}
			}
		}
	}
	
	private IProcessor matcherExp(String expStr)
	{
		IProcessor processor=null;
		if(expStr!=null && expStr.length()>1)
		{
			int size=expStr.length();
			int i=0;
			char type= expStr.charAt(0);
			if(type=='$')//VariableExpression
			{ 
				TextProcessors textProcessors=new TextProcessors(expStr,mContext);
				processor=textProcessors;
			}else if(type =='#')//Utility Object
			{
				StringBuilder key=new StringBuilder();
			
				boolean gonext=true;
				while(size-- != 0)
				{
					char c=expStr.charAt(i++);
					if(c==':' || c=='.' ||  c == '{')
					{
						gonext=false;
					}
					if(gonext)
					{
						if(i>1)
						{
						key.append(c);
						}
					}
				}
				if(key.length()>0)
				{
					System.out.println("KEY:"+key);
					if("each".equals(key.toString()))
					{
						EachProcessor eachProcessor=new EachProcessor(expStr,mContext);
						processor=eachProcessor;
					}else {
						ListProcessors listProcessors=new ListProcessors(expStr,mContext);
						processor=listProcessors;
					}
				}
			}
			
			tplExpSet.add(expStr);
		}
		mProcessors.add(processor);
		return processor;
	}
	
	private void parse(TemplateSpec templateSpec,Writer writer)
	{
		StringBuffer sb=new StringBuffer();
		  Matcher m = Pattern.compile("\\@\\@(.+?)\\@\\@", Pattern.DOTALL).matcher(templateSpec.getTemplate());
		    while (m.find()) {
		    	try {
		        String param = m.group();
		        System.out.println("param:"+param);
		        String variableExp=param.substring(2, param.length() - 2);
		        IProcessor processor=  matcherExp(variableExp);
			       String value=null;
			       if(processor!=null)
			       {
			    	  value=processor.text();
			       } 
		         m.appendReplacement(sb, value==null ? "" :m.quoteReplacement(value));
		    	}catch (Exception e) {
		    		e.printStackTrace();
				}
		    }
		    m.appendTail(sb);
		    try {
		    writer.write(sb.toString());
		    }catch (Exception e) {
				e.printStackTrace();
			}
	}

	
	
}
