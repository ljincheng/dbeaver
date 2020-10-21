package org.jkiss.dbeaver.model.sourcecode.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jkiss.dbeaver.model.sourcecode.utils.TemplateUtils;
import org.jkiss.utils.CommonUtils;

import cn.booktable.template.IContext;

public class TemplateContext {

	List<CodeTemplate> codeTemplates=null;
	private CodeTemplate activityTemplate;
	
	public TemplateContext() {
		super();
		loadTemplates();
	}
	
	
	public void loadTemplates() {
		if(codeTemplates==null)
		{
			codeTemplates=new ArrayList<CodeTemplate>();
			codeTemplates.add(new CodeTemplate("java_entity", "Entity(get/set)", "实体:Entity(get/set)"));
			codeTemplates.add(new CodeTemplate("java_lombokdata", "Entity(lombok)", "实体:Entity(lombok)"));
			codeTemplates.add(new CodeTemplate("java_dao", "Dao/Mapper", "Dao/Mapper"));
			codeTemplates.add(new CodeTemplate("mybatis", "Mybatis", "Mybatis XML文件"));
			codeTemplates.add(new CodeTemplate("lang_message", "Message", "Message 语言文件"));
			codeTemplates.add(new CodeTemplate("java_component", "Component", "Component"));
			codeTemplates.add(new CodeTemplate("java_component_impl", "ComponentImpl", "Component实现类"));
			codeTemplates.add(new CodeTemplate("java_service", "Service", "Service"));
			codeTemplates.add(new CodeTemplate("java_service_impl", "ServiceImpl", "Service实现类"));
			codeTemplates.add(new CodeTemplate("java_controller", "Controller", "Controller类"));
			codeTemplates.add(new CodeTemplate("html_thymeleaf_list", "Html-list(Thymeleaf)", "html(thymeleaf前端) list"));
			codeTemplates.add(new CodeTemplate("html_thymeleaf_list_table", "Html-list-table(Thymeleaf)", "html(thymeleaf前端) list_table"));
			codeTemplates.add(new CodeTemplate("html_thymeleaf_add", "Html-add(Thymeleaf)", "html(thymeleaf前端) add"));
		}
	}
	
	public List<CodeTemplate> getTemplates(){
		return this.codeTemplates;
	}
	
	public void fillFromCacheTemplate(CodeTemplate codeTemplate) {
		if(codeTemplate ==null || CommonUtils.isEmpty(codeTemplate.getId())) {
			return ;
		}
		String template= TemplateUtils.readCacheTemplate(codeTemplate.getId());
		codeTemplate.setTemplate(template); 
	}
	
	
	public void saveTemplateToCache(CodeTemplate codeTemplate)
	{
		if(codeTemplate ==null ||  CommonUtils.isEmpty(codeTemplate.getId()) || CommonUtils.isEmpty(codeTemplate.getTemplate())) {
			return ;
		}
		TemplateUtils.saveCacheTemplate(codeTemplate.getId(), codeTemplate.getTemplate());
	}
	
	
	public CodeTemplate getActivityTemplate() {
		if(this.activityTemplate==null) {
			activityTemplate=codeTemplates.get(0);
		}
		return activityTemplate;
	}
	
	public CodeTemplate setActivityTemplate(int index)
	{
		if(index<codeTemplates.size())
		{
			activityTemplate=codeTemplates.get(index);
		}
		if(CommonUtils.isEmpty( activityTemplate.getTemplate())) {
			fillFromCacheTemplate(activityTemplate);
		}
		return activityTemplate;
	}
	
	 
	
	public CodeTemplate get(int index) {
		return codeTemplates.get(index);
	}
	
	public int size() {
		return codeTemplates.size();
	}
	
	public  String convertActivityTemplate(IContext context) {
		CodeTemplate codeTemplate=getActivityTemplate();
		if(CommonUtils.isEmpty(codeTemplate.getTemplate()))
		{
			fillFromCacheTemplate(codeTemplate);
		} 
		return cn.booktable.template.TemplateEngine.process(codeTemplate.getTemplate(),context);
	}
}
