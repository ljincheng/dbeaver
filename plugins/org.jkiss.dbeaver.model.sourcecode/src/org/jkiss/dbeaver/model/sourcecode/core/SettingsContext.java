package org.jkiss.dbeaver.model.sourcecode.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormConstants;
import org.jkiss.dbeaver.model.sourcecode.ui.context.FormItemContext;
import org.jkiss.dbeaver.model.sourcecode.ui.preferences.SourceCodePreferences;
import org.jkiss.dbeaver.model.sourcecode.utils.TemplateUtils;
import org.jkiss.dbeaver.runtime.DBWorkbench;

import com.google.gson.Gson;

public class SettingsContext extends AbstractContentBroadcas{
	
	private static final String SETTING_FILE="template_settings_var";
	private List<FormItemContext> mSettingItems;
	 
	
	public SettingsContext() {
		super();
		loadSettingsData();
	}
  	
	public List<FormItemContext> getItems(){
		return this.mSettingItems;
	}
	
	public boolean addItem(FormItemContext formItem) {
		boolean result=this.mSettingItems.add(formItem);
		if(result)
		{
			TemplateUtils.saveProperties(SETTING_FILE, mSettingItems);
			send(ContentListen.TYPE_DATA_CHANGE, ContentListen.SUBTYPE_DATA_CHANGE_ADD, formItem);
		}
		return result;
	}
	
	public boolean deleteItem(FormItemContext formItem) {
		boolean result=this.mSettingItems.remove(formItem);
		if(result)
		{
			TemplateUtils.saveProperties(SETTING_FILE, mSettingItems);
			send(ContentListen.TYPE_DATA_CHANGE, ContentListen.SUBTYPE_DATA_CHANGE_DELETE, formItem);
		}
		return result;
	}
	
	public boolean deleteItemAll(Collection<FormItemContext> mSettingItems) {
		boolean result=this.mSettingItems.removeAll(mSettingItems);
		if(result) {
			saveModify();
			send(ContentListen.TYPE_DATA_CHANGE, ContentListen.SUBTYPE_DATA_CHANGE_DELETE_MUL, mSettingItems);
		}
		return result;
	}
	
	
	public void saveModify() {
		TemplateUtils.saveProperties(SETTING_FILE, mSettingItems);
	}
	
	
	public void loadSettingsData() {
		List<FormItemContext> formItemList= readFromFile(SETTING_FILE);
		if(formItemList==null || formItemList.size()==0) {
			formItemList=new ArrayList<FormItemContext>();
			DBPPreferenceStore store= DBWorkbench.getPlatform().getPreferenceStore();
			formItemList.add( new FormItemContext("outPutDir",  UIMessages.dbeaver_generate_sourcecode_codeOutPutFolder, store.getString(SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER), FormConstants.FORM_FILE,FormConstants.FORM_VALUETYPE_TEXT));
			formItemList.add( new FormItemContext("author",  UIMessages.dbeaver_generate_sourcecode_preferences_author,store.getString(SourceCodePreferences.SOURCECODE_AUTHOR), FormConstants.FORM_FILE,FormConstants.FORM_VALUETYPE_TEXT));
			formItemList.add( new FormItemContext("packagePath",  UIMessages.dbeaver_generate_sourcecode_packageName, store.getString(SourceCodePreferences.SOURCECODE_PACKAGENAME), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_TEXT));
			formItemList.add( new FormItemContext("pageDo",  UIMessages.dbeaver_generate_sourcecode_pageClassFullName, store.getString(SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("groupName",  UIMessages.dbeaver_generate_sourcecode_groupName,store.getString(SourceCodePreferences.SOURCECODE_GROUPNAME), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("jsonView",  UIMessages.dbeaver_generate_sourcecode_jsonView, store.getString(SourceCodePreferences.SOURCECODE_JSONVIEW), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("businessException",  UIMessages.dbeaver_generate_sourcecode_businessException, store.getString(SourceCodePreferences.SOURCECODE_BUSINESSEXCEPTION), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("assertUtils",  UIMessages.dbeaver_generate_sourcecode_assertUtils, store.getString(SourceCodePreferences.SOURCECODE_ASSERTUTILS), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("baseController",  UIMessages.dbeaver_generate_sourcecode_baseController, store.getString(SourceCodePreferences.SOURCECODE_BASECONTROLLER), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			 
			formItemList.add( new FormItemContext("entity",  UIMessages.dbeaver_generate_sourcecode_entity_object, store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("dao",  "Dao", store.getString(SourceCodePreferences.SOURCECODE_RULE_DAO), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("component",  "Component", store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("componentImpl",  "componentImpl", store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("service",  "Service", store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("serviceImpl",  "serviceImpl", store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			formItemList.add( new FormItemContext("controller",  "controller", store.getString(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER), FormConstants.FORM_TEXT,FormConstants.FORM_VALUETYPE_CLASS));
			TemplateUtils.saveProperties(SETTING_FILE, formItemList);
		}
		this.mSettingItems=formItemList;
	}
	
	public static  List<FormItemContext> readFromFile(String propertFile) {
		List<FormItemContext> result = null;
			File settingsFile = new File(TemplateUtils.getMetadataFolder(true), propertFile + TemplateUtils.PROPERTIES_CACHE_EXT);
			if (settingsFile.exists() && settingsFile.length() > 0) {
				// Parse metadata
				try (Reader settingsReader = new InputStreamReader(new FileInputStream(settingsFile),
						StandardCharsets.UTF_8)) {
//					result = JSONUtils.parseMap(METADATA_GSON, settingsReader);
					Gson gson = new Gson();
					Type empMapType =	new com.google.gson.reflect.TypeToken<ArrayList<FormItemContext>>() {}.getType();
					result= gson.fromJson(settingsReader, empMapType);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			if (result == null) {
				result = new ArrayList<FormItemContext>();
			}
		return result;
	}
	
	
}
