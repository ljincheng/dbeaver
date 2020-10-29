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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jkiss.code.NotNull;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.model.data.json.JSONUtils;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.utils.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class CodeMetadata  {

		private static final String TEMPLATE_DIR = "templates/code/";
		private static final String TEMPLATE_EXT = ".txt";
		private static final String TEMPLATE_CACHE_EXT = ".template";
		private static final String PROPERTIES_CACHE_EXT = ".properties";
		private static final String WORKSPACE_DIR = ".sourcecode";
		private static final String METADATA_FOLDER = "metadata";
		private static final String TEMPLATE_FOLDER = "template";
	    private static final Log log = Log.getLog(CodeMetadata.class);

	    public static final String SETTINGS_STORAGE_FILE = "sourcecode-settings.json";
	 

	    private static Gson METADATA_GSON = new GsonBuilder()
	        .setLenient()
	        .serializeNulls()
	        .create();


	    private volatile Map<String, Object> properties;
	    private final Object metadataSync = new Object();

	    public CodeMetadata() {
 	    }  
	   
	    public File getAbsolutePath() {
	    	File metadataFolder = DBWorkbench.getPlatform().getWorkspace().getMetadataFolder();
			File tplDir = new File(metadataFolder, WORKSPACE_DIR);
			if (!tplDir.exists() && !tplDir.mkdirs()) {
				return null;
			}
			return tplDir;
	    }

	 
	    public File getMetadataFolder(boolean create) {
	        File metadataFolder = new File(getAbsolutePath(), METADATA_FOLDER);
	        if (create && !metadataFolder.exists()) {
	            if (!metadataFolder.mkdirs()) {
	                log.error("Error creating metadata folder");
	            }
	        }

	        return metadataFolder;
	    }
	    
	    public File getTemplateFolder(boolean create) {
	        File templateFolder = new File(getAbsolutePath(), TEMPLATE_FOLDER);
	        if (create && !templateFolder.exists()) {
	            if (!templateFolder.mkdirs()) {
	                log.error("Error creating metadata folder");
	            }
	        }

	        return templateFolder;
	    }

	    @NotNull
	    private File getMetadataPath() {
	        return new File(getAbsolutePath(), METADATA_FOLDER);
	    }
     


	    public Object getProjectProperty(String propName) {
	        synchronized (this) {
	            loadProperties();
	            return properties.get(propName);
	        }
	    }

	    public void setProjectProperty(String propName, Object propValue) {
	        synchronized (this) {
	            loadProperties();
	            if (propValue == null) {
	                properties.remove(propName);
	            } else {
	                properties.put(propName, propValue);
	            }
	            saveProperties();
	        }
	    }

	    private void loadProperties() {
	        if (properties != null) {
	            return;
	        }

	        synchronized (metadataSync) {
	            File settingsFile = new File(getMetadataPath(), SETTINGS_STORAGE_FILE);
	            if (settingsFile.exists() && settingsFile.length() > 0) {
	                // Parse metadata
	                try (Reader settingsReader = new InputStreamReader(new FileInputStream(settingsFile), StandardCharsets.UTF_8)) {
	                    properties = JSONUtils.parseMap(METADATA_GSON, settingsReader);
	                } catch (Throwable e) {
	                    log.error("Error reading code metadata from "  + settingsFile.getAbsolutePath(), e);
	                }
	            }
	            if (properties == null) {
	                properties = new LinkedHashMap<>();
	            }
	        }
	    }

	    private void saveProperties() {
	        File settingsFile = new File(getMetadataPath(), SETTINGS_STORAGE_FILE);
	        String settingsString = METADATA_GSON.toJson(properties);
	        try (Writer settingsWriter = new OutputStreamWriter(new FileOutputStream(settingsFile), StandardCharsets.UTF_8)) {
	            settingsWriter.write(settingsString);
	        } catch (Throwable e) {
	            log.error("Error writing code metadata to "  + settingsFile.getAbsolutePath(), e);
	        }
	    }

	    
	    public Map<String,Object> readTemplateProperties(String propertFile) {
	    	Map<String,Object> result=null;
	        synchronized (metadataSync) {
	            File settingsFile = new File(getTemplateFolder(true), propertFile+PROPERTIES_CACHE_EXT);
	            if (settingsFile.exists() && settingsFile.length() > 0) {
	                // Parse metadata
	                try (Reader settingsReader = new InputStreamReader(new FileInputStream(settingsFile), StandardCharsets.UTF_8)) {
	                	result = JSONUtils.parseMap(METADATA_GSON, settingsReader);
	                } catch (Throwable e) {
	                    log.error("Error reading code metadata from "  + settingsFile.getAbsolutePath(), e);
	                }
	            }
	            if (result == null) {
	            	result = new LinkedHashMap<>();
	            }
	        }
	        return result;
	    }
	    
	    public void saveTemplateProperties(String propertFile, Map<String,Object> properties) {
	    	File settingsFile = new File(getTemplateFolder(true),propertFile+ PROPERTIES_CACHE_EXT);
	        String settingsString = METADATA_GSON.toJson(properties);
	        try (Writer settingsWriter = new OutputStreamWriter(new FileOutputStream(settingsFile), StandardCharsets.UTF_8)) {
	            settingsWriter.write(settingsString);
	        } catch (Throwable e) {
	            log.error("Error writing code metadata to "  + settingsFile.getAbsolutePath(), e);
	        }
	    }
	    
	    
	    public  String readTemplate(String templateFile) {
			String result = null;
			 synchronized (metadataSync) {
				try {
					File tplDir =getTemplateFolder(true);
					File tplFile = new File(tplDir, templateFile + TEMPLATE_CACHE_EXT);
					if (tplFile.exists()) {
						result = new String(Files.readAllBytes(tplFile.toPath()));
					} else {
						result = readResource(templateFile);
						Files.write(tplFile.toPath(), result.getBytes(), StandardOpenOption.CREATE);
					}
	
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
			return result;
		}

	    public  boolean saveTemplate(String templateFile,String template) {
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
	    
	    public static String readResource(String templateName) {
			String resourceName = TEMPLATE_DIR + templateName +TEMPLATE_EXT;

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
		
	         
	}