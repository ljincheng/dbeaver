package org.jkiss.dbeaver.model.sourcecode.template;

import java.util.HashMap;
import java.util.Map;

import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.utils.GeneralUtils;

public class EngineContext implements IEngineContext{
	
	
	private HashMap<String,Object> maps;
	private Map<String, String> cacheTemplateFileMap;
	
	 

	
	public  EngineContext() {
		super();
		this.maps=new HashMap<String, Object>();
		this.cacheTemplateFileMap=new HashMap<String, String>();
		Map<String,String> systemMap=new HashMap<String, String>();
		systemMap.put("lf", GeneralUtils.getDefaultLineSeparator());
		setVariable("system", systemMap);
	} 
 

	
	@Override
	public Object getVariable(String name) {
		
		return this.maps.get(name);
	}

	@Override
	public void setVariable(String name, Object value) {
		this.maps.put(name, value);
		
	}



	@Override
	public Map<String, Object> getRoot() {
		return this.maps;
	}



	@Override
	public void setVariables(Map<String, Object> variables) {
		if(variables!=null)
		{
			this.maps.putAll(variables);
		}
	}
	

	@Override
	public String getTemplateFile(String tplFile)
	{
		String html=cacheTemplateFileMap.get(tplFile);
		if(html==null || html.length()==0)
		{
			 String tplHtml=CodeHelper.loadResource(tplFile);
			 cacheTemplateFileMap.put(tplFile, tplHtml);
			 return tplHtml;
		}
		return html;
	}
	
	

}
