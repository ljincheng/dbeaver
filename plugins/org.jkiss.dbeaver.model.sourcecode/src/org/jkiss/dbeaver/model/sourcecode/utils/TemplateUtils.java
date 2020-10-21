package org.jkiss.dbeaver.model.sourcecode.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.utils.IOUtils;

import cn.booktable.template.IContext;

public class TemplateUtils {
	private static final String TEMPLATE_DIR = "templates/code/";
	private static final String TEMPLATE_CACHE_DIR = "codetemplate";
	private static final String TEMPLATE_EXT = ".txt";
	private static final String TEMPLATE_CACHE_EXT = ".template";

	
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
	
	
	public static String readCacheTemplate(String templateFile) {
		String result = null;
		try {
			File metadataFolder = DBWorkbench.getPlatform().getWorkspace().getMetadataFolder();
			File tplDir = new File(metadataFolder, TEMPLATE_CACHE_DIR);
			if (!tplDir.exists() && !tplDir.mkdirs()) {
				return null;
			}
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
		return result;
	}
	
	public static boolean saveCacheTemplate(String templateFile,String template) {
		try {
			File metadataFolder = DBWorkbench.getPlatform().getWorkspace().getMetadataFolder();
			File tplDir = new File(metadataFolder, TEMPLATE_CACHE_DIR);
			if (!tplDir.exists() && !tplDir.mkdirs()) {
				return false;
			}
			File tplFile = new File(tplDir, templateFile + TEMPLATE_CACHE_EXT);
			Files.write(tplFile.toPath(), template.getBytes(), StandardOpenOption.CREATE);
		  return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static String  convertCacheTemplate(String templateFile,IContext context) {
		String template=readCacheTemplate(templateFile);
		if(template==null)
		{
			return null;
		}
		return cn.booktable.template.TemplateEngine.process(template,context);
	}
}
