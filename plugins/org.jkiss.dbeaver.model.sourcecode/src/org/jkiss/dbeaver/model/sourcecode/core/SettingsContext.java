package org.jkiss.dbeaver.model.sourcecode.core;

import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.model.sourcecode.ui.preferences.SourceCodePreferences;
import org.jkiss.dbeaver.runtime.DBWorkbench;

public class SettingsContext {

	public static SourceCodeSetting loadStoreContext() {
		DBPPreferenceStore store= DBWorkbench.getPlatform().getPreferenceStore();
		SourceCodeSetting setting=new SourceCodeSetting();
		setting.setOutPutDir(store.getString(SourceCodePreferences.SOURCECODE_CODEOUTPUTFOLDER));
		setting.setPackagePath(store.getString(SourceCodePreferences.SOURCECODE_PACKAGENAME));
		setting.setAuthor(store.getString(SourceCodePreferences.SOURCECODE_AUTHOR));
		setting.setGroupName(store.getString(SourceCodePreferences.SOURCECODE_GROUPNAME));
		setting.setClassPage(store.getString(SourceCodePreferences.SOURCECODE_PAGECLASSFULLNAME));
		setting.setClassAssertUtils( store.getString(SourceCodePreferences.SOURCECODE_ASSERTUTILS));
		setting.setClassBaseController(store.getString(SourceCodePreferences.SOURCECODE_BASECONTROLLER));
		setting.setClassBusinessException(store.getString(SourceCodePreferences.SOURCECODE_BUSINESSEXCEPTION));
		setting.setClassJsonView(store.getString(SourceCodePreferences.SOURCECODE_JSONVIEW));
		setting.setRuleEntity(store.getString(SourceCodePreferences.SOURCECODE_RULE_ENTITY));
		setting.setRuleDao(store.getString(SourceCodePreferences.SOURCECODE_RULE_DAO));
		setting.setRuleComponent(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT));
		setting.setRuleComponentImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_COMPONENT_IMPL));
		setting.setRuleService(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE));
		setting.setRuleServiceImpl(store.getString(SourceCodePreferences.SOURCECODE_RULE_SERVICE_IMPL));
		setting.setRuleController(store.getString(SourceCodePreferences.SOURCECODE_RULE_CONTROLLER));
		setting.setAuthor(store.getString(SourceCodePreferences.SOURCECODE_AUTHOR));
		return setting;
	}
}
