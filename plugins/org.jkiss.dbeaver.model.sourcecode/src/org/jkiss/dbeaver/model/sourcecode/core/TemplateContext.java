package org.jkiss.dbeaver.model.sourcecode.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.model.sourcecode.ui.event.UIEventNotifier;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.sourcecode.utils.TemplateUtils;
import org.jkiss.utils.CommonUtils;

import cn.booktable.template.IContext;

public class TemplateContext extends AbstractContentBroadcas{

	List<CodeTemplate> mCodeTemplates=null;
	private CodeTemplate activityTemplate;
	private List<UIEventNotifier> mNotifierList;
	 
	
	public TemplateContext() {
		super();
		mNotifierList=new ArrayList<UIEventNotifier>();
		loadTemplates();
	}
	
	
	public boolean addNotifier(UIEventNotifier notifier)
	{
		return this.mNotifierList.add(notifier);
	}
	public boolean removeNotifier(UIEventNotifier notifier)
	{
		return this.mNotifierList.remove(notifier);
	}
	
	public void loadTemplates() {
		if(mCodeTemplates==null)
		{
			mCodeTemplates=TemplateUtils.loadCodeTemplateList();
		}
	}
	
	public List<CodeTemplate> getTemplates(){
		return this.mCodeTemplates;
	}
	
	public boolean addTemplate(CodeTemplate template) {
		if(template==null || CommonUtils.isEmpty(template.getId()))
		{
			return false;
		}
		for(int i=0,k=mCodeTemplates.size();i<k;i++) {
			CodeTemplate codeTemplate=mCodeTemplates.get(i);
			if(codeTemplate.getId().equals(template.getId())) {
				return false;
			}
		}
		boolean result=this.mCodeTemplates.add(template);
		if(result) {
			this.saveCodeTemplateList();
			send(ContentListen.TYPE_DATA_CHANGE, ContentListen.SUBTYPE_DATA_CHANGE_ADD, template);
		}
		return result;
	}
	
	
	public boolean deleteTemplate(CodeTemplate template) {
		boolean result=this.mCodeTemplates.remove(template);
		if(result) {
			this.saveCodeTemplateList();
			send(ContentListen.TYPE_DATA_CHANGE, ContentListen.SUBTYPE_DATA_CHANGE_DELETE, template);
		}
		return result;
	}
	
	public boolean deleteTemplateAll(Collection<CodeTemplate> templates) {
		boolean result=this.mCodeTemplates.removeAll(templates);
		if(result) {
			this.saveCodeTemplateList();
			send(ContentListen.TYPE_DATA_CHANGE, ContentListen.SUBTYPE_DATA_CHANGE_DELETE_MUL, templates);
		}
		return result;
	}
	
	
	public void fillFromCacheTemplate(CodeTemplate codeTemplate) {
		if(codeTemplate ==null || CommonUtils.isEmpty(codeTemplate.getId())) {
			return ;
		}
		String template= TemplateUtils.readTemplate(codeTemplate.getId());
		codeTemplate.setTemplate(template); 
	}
	
	
	public void saveTemplateToCache(CodeTemplate codeTemplate)
	{
		if(codeTemplate ==null ||  CommonUtils.isEmpty(codeTemplate.getId()) || CommonUtils.isEmpty(codeTemplate.getTemplate())) {
			return ;
		}
		TemplateUtils.saveTemplate(codeTemplate.getId(), codeTemplate.getTemplate());
	}
	
	public void saveCodeTemplateList() {
		TemplateUtils.saveTemplateSettingList(this.mCodeTemplates);
	}
	
	
	public CodeTemplate getActivityTemplate() {
		if(this.activityTemplate==null) {
			activityTemplate=mCodeTemplates.get(0);
		}
		return activityTemplate;
	}
	
	public CodeTemplate setActivityTemplate(int index)
	{
		if(index<mCodeTemplates.size())
		{
			activityTemplate=mCodeTemplates.get(index);
		}
		if(CommonUtils.isEmpty( activityTemplate.getTemplate())) {
			fillFromCacheTemplate(activityTemplate);
		}
		return activityTemplate;
	}
	
	public CodeTemplate setActivityTemplate(CodeTemplate codeTemplate)
	{
		 
		this.activityTemplate=codeTemplate;
		 
		if(CommonUtils.isEmpty( activityTemplate.getTemplate())) {
			fillFromCacheTemplate(activityTemplate);
		}
		return activityTemplate;
	}
	
	
	
	 
	
	public CodeTemplate get(int index) {
		return mCodeTemplates.get(index);
	}
	
	public int size() {
		return mCodeTemplates.size();
	}
	
	public  String convertActivityTemplate(IContext context) {
		CodeTemplate codeTemplate=getActivityTemplate();
		if(CommonUtils.isEmpty(codeTemplate.getTemplate()))
		{
			fillFromCacheTemplate(codeTemplate);
		} 
		return convertTemplate(codeTemplate,context);
	}
	
	public  String convertTemplate(CodeTemplate codeTemplate,IContext context) {
		return cn.booktable.template.TemplateEngine.process(codeTemplate.getTemplate(),context);
	}
	 
	
	public String exportFilePath(CodeTemplate codeTemplate,IContext context) { 
		String exportPath=codeTemplate.getExportPath();
		Map<String,String> root=new HashMap<String, String>();
		root.putAll((Map)context.getRoot().get(DBSTableCodeContext.KEY_SETTING));
		root.put("table_name", (String)context.getRoot().get(DBSTableCodeContext.KEY_TABLENAME));
		String exportFile=CodeHelper.processTemplate(exportPath, (Map)context.getRoot().get(DBSTableCodeContext.KEY_SETTING));
		return exportFile;
	}
	
	public boolean  exportFile(CodeTemplate codeTemplate,IContext context) {
	
		boolean result=true;
		try {
			String template=convertTemplate(codeTemplate,context);
			String exportPath=exportFilePath(codeTemplate, context);
			File exportFile=new File(exportPath);
			File parentDir= exportFile.getParentFile();
			if(!parentDir.exists() && !parentDir.mkdirs()) {
				return false;
			}
			Files.write(exportFile.toPath(), template.getBytes(), StandardOpenOption.CREATE);
		}catch (IOException e) {
			e.printStackTrace();
			result=false;
		}
		return result;
	}
}
