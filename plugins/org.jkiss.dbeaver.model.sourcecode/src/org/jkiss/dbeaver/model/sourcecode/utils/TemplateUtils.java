package org.jkiss.dbeaver.model.sourcecode.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.data.json.JSONUtils;
import org.jkiss.dbeaver.model.sourcecode.core.CodeTemplate;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.utils.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.booktable.template.IContext;

public class TemplateUtils {
	private static final Log log = Log.getLog(TemplateUtils.class);

	private static final String TEMPLATE_DIR = "templates/code/";
	private static final String TEMPLATE_EXT = ".txt";
	private static final String TEMPLATE_CACHE_EXT = ".template";
	public static final String PROPERTIES_CACHE_EXT = ".properties";
	private static final String WORKSPACE_DIR = ".sourcecode";
	private static final String METADATA_FOLDER = "metadata";
	private static final String TEMPLATE_FOLDER = "template";
	public static final String SOURCECODE_TEMPLATE_SETTING = "sourcecode-template-settings.json";

	private static Gson METADATA_GSON = new GsonBuilder().setLenient().serializeNulls().create();

	private static final Object metadataSync = new Object();

	public static File getAbsolutePath() {
		File metadataFolder = DBWorkbench.getPlatform().getWorkspace().getMetadataFolder();
		File tplDir = new File(metadataFolder, WORKSPACE_DIR);
		if (!tplDir.exists() && !tplDir.mkdirs()) {
			return null;
		}
		return tplDir;
	}

	public static File getMetadataFolder(boolean create) {
		File metadataFolder = new File(getAbsolutePath(), METADATA_FOLDER);
		if (create && !metadataFolder.exists()) {
			if (!metadataFolder.mkdirs()) {
				log.error("Error creating metadata folder");
			}
		}

		return metadataFolder;
	}

	public static File getTemplateFolder(boolean create) {
		File templateFolder = new File(getAbsolutePath(), TEMPLATE_FOLDER);
		if (create && !templateFolder.exists()) {
			if (!templateFolder.mkdirs()) {
				log.error("Error creating metadata folder");
			}
		}

		return templateFolder;
	}

	public static String readResource(String templateName) {
		String resourceName = TEMPLATE_DIR + templateName + TEMPLATE_EXT;

		InputStream in = CodeHelper.class.getClassLoader().getResourceAsStream(resourceName);

		if (in == null) {
			return null;
		}

		try {
			try (InputStreamReader isr = new InputStreamReader(in)) {
				String viewTemplate = IOUtils.readToString(isr);
				return viewTemplate;
			} finally {
				ContentUtils.close(in);
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
		return null;
	}

	public static String readTemplate(String templateFile) {
		String result = null;
		synchronized (metadataSync) {
			try {
				File tplDir = getTemplateFolder(true);
				File tplFile = new File(tplDir, templateFile + TEMPLATE_CACHE_EXT);
				if (tplFile.exists()) {
					result = new String(Files.readAllBytes(tplFile.toPath()));
				} else {
					result = readResource(templateFile);
					if(result==null)
					{
						return "";
					}else {
						Files.write(tplFile.toPath(), result.getBytes(), StandardOpenOption.CREATE);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean saveTemplate(String templateFile, String template) {
		try {
			File tplDir = getTemplateFolder(true);
			File tplFile = new File(tplDir, templateFile + TEMPLATE_CACHE_EXT);
			Files.write(tplFile.toPath(), template.getBytes(), StandardOpenOption.CREATE);
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String convertCacheTemplate(String templateFile, IContext context) {
		String template = readTemplate(templateFile);
		if (template == null) {
			return null;
		}
		return cn.booktable.template.TemplateEngine.process(template, context);
	}

	public static <T> List<T> readProperties(String propertFile) {
		List<T> result = null;
		synchronized (metadataSync) {
			File settingsFile = new File(getMetadataFolder(true), propertFile + PROPERTIES_CACHE_EXT);
			if (settingsFile.exists() && settingsFile.length() > 0) {
				// Parse metadata
				try (Reader settingsReader = new InputStreamReader(new FileInputStream(settingsFile),
						StandardCharsets.UTF_8)) {
//					result = JSONUtils.parseMap(METADATA_GSON, settingsReader);
					Gson gson = new Gson();
					Type empMapType =	new com.google.gson.reflect.TypeToken<ArrayList<T>>() {}.getType();
					result= gson.fromJson(settingsReader, empMapType);
				} catch (Throwable e) {
					log.error("Error reading code metadata from " + settingsFile.getAbsolutePath(), e);
				}
			}
			if (result == null) {
				result = new ArrayList<T>();
			}
		}
		return result;
	}

	public static void saveProperties(String propertFile,Object properties) {
		File settingsFile = new File(getMetadataFolder(true), propertFile + PROPERTIES_CACHE_EXT);
		String settingsString = METADATA_GSON.toJson(properties);
		try (Writer settingsWriter = new OutputStreamWriter(new FileOutputStream(settingsFile),
				StandardCharsets.UTF_8)) {
			settingsWriter.write(settingsString);
		} catch (Throwable e) {
			log.error("Error writing code metadata to " + settingsFile.getAbsolutePath(), e);
		}
	}

	public static List<CodeTemplate> loadCodeTemplateList() {
		Map<String, CodeTemplate> result = null;
		List<CodeTemplate> codeTemplates=null;;
		synchronized (metadataSync) {
			File settingsFile = new File(getMetadataFolder(true), SOURCECODE_TEMPLATE_SETTING);
			if (settingsFile.exists() && settingsFile.length() > 0) {
				// Parse metadata
				try (Reader settingsReader = new InputStreamReader(new FileInputStream(settingsFile),
						StandardCharsets.UTF_8)) {
					Gson gson = new Gson();
					Type empMapType = new com.google.gson.reflect.TypeToken<LinkedHashMap<String, CodeTemplate>>() {}.getType();
					result= gson.fromJson(settingsReader, empMapType);
					//result = JSONUtils.parseMap(METADATA_GSON, settingsReader); 
					 
				} catch (Throwable e) {
					log.error("Error reading code metadata from " + settingsFile.getAbsolutePath(), e);
				}
			}
			if (result == null) {
				result = new LinkedHashMap<>();
			}
		}
		
		if(result.size()==0)
		{
			String projectPath="/workspace/temp/code/src";
			result.put("java_entity", new CodeTemplate("java_entity", "Entity(get/set)", "实体:Entity(get/set)",projectPath+"/${projectName}/${entityClass}.java"));
			result.put("java_lombokdata", new CodeTemplate("java_lombokdata", "Entity(lombok)", "实体:Entity(lombok)",projectPath+"/${projectName}/${entityClass}.java"));
			result.put("java_dao",new CodeTemplate("java_dao", "Dao/Mapper", "Dao/Mapper",projectPath+"/${projectName}/${daoClass}.java") );
			result.put("mybatis",new CodeTemplate("mybatis", "Mybatis", "Mybatis XML文件",projectPath+"/${projectName}/${mybatisTable}.xml") );
			result.put("lang_message",new CodeTemplate("lang_message", "Message", "Message 语言文件",projectPath+"/${projectName}/${messageTable}.properties") );
			result.put("java_component", new CodeTemplate("java_component", "Component", "Component",projectPath+"/${projectName}/${componentClass}.java"));
			result.put("java_component_impl",new CodeTemplate("java_component_impl", "ComponentImpl", "Component实现类",projectPath+"/${projectName}/${componentImplClass}.java") );
			result.put("java_service", new CodeTemplate("java_service", "Service", "Service",projectPath+"/${projectName}/${serviceClass}.java"));
			result.put("java_service_impl", new CodeTemplate("java_service_impl", "ServiceImpl", "Service实现类",projectPath+"/${projectName}/${serviceImplClass}.java"));
			result.put("java_controller", new CodeTemplate("java_controller", "Controller", "Controller类",projectPath+"/${projectName}/${controllerClass}.java"));
			result.put("html_thymeleaf_list", new CodeTemplate("html_thymeleaf_list", "Html-list(Thymeleaf)", "html(thymeleaf前端) list",projectPath+"/${projectName}/template/${groupName}/${table_name}_list.html"));
			result.put("html_thymeleaf_list_table",new CodeTemplate("html_thymeleaf_list_table", "Html-list-table(Thymeleaf)", "html(thymeleaf前端) list_table",projectPath+"/${projectName}/${groupName}/${table_name}_list_table.html") );
			result.put("html_thymeleaf_add", new CodeTemplate("html_thymeleaf_add", "Html-add(Thymeleaf)", "html(thymeleaf前端) add",projectPath+"/${projectName}/${groupName}/${table_name}_add.html"));
			saveTemplateSettingMap(result);
		}
		{
			codeTemplates=new ArrayList();
			for(String key:result.keySet())
			{
				Object value=result.get(key);
				if(value!=null) {
					codeTemplates.add((CodeTemplate)result.get(key));
				}
			}
		
		}
		return codeTemplates;
	}
	
	public static void saveTemplateSettingList(List<CodeTemplate> codeTemplates) {
		Map<String,CodeTemplate> properties=new LinkedHashMap<String, CodeTemplate>();
		if(codeTemplates!=null)
		{
			for(int i=0,k=codeTemplates.size();i<k;i++)
			{
				CodeTemplate item=codeTemplates.get(i);
				properties.put(item.getId(), item);
			}
		}
		File settingsFile = new File(getMetadataFolder(true), SOURCECODE_TEMPLATE_SETTING);
		String settingsString = METADATA_GSON.toJson(properties);
		try (Writer settingsWriter = new OutputStreamWriter(new FileOutputStream(settingsFile),
				StandardCharsets.UTF_8)) {
			settingsWriter.write(settingsString);
		} catch (Throwable e) {
			log.error("Error writing code metadata to " + settingsFile.getAbsolutePath(), e);
		}
	}
	
	public static void saveTemplateSettingMap(Map<String, CodeTemplate> properties) {
		File settingsFile = new File(getMetadataFolder(true), SOURCECODE_TEMPLATE_SETTING);
		String settingsString = METADATA_GSON.toJson(properties);
		try (Writer settingsWriter = new OutputStreamWriter(new FileOutputStream(settingsFile),
				StandardCharsets.UTF_8)) {
			settingsWriter.write(settingsString);
		} catch (Throwable e) {
			log.error("Error writing code metadata to " + settingsFile.getAbsolutePath(), e);
		}
	}
}
