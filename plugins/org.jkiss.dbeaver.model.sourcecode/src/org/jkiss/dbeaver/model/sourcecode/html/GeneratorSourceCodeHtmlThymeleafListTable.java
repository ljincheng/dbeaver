package org.jkiss.dbeaver.model.sourcecode.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.DBStructUtils;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndex;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableIndexColumn;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

/**
 * Html list_table
 * @author ljc
 *
 */
public class GeneratorSourceCodeHtmlThymeleafListTable extends GeneratorSourceCode{
	 
	private static final String TEMPLATE_FILE_ENTITY="html_thymeleaf_list_table";

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE_ENTITY;
	}
	    
	@Override
	protected boolean  writeCode(DBRProgressMonitor monitor,DBSTable table,StringBuilder code) {
		String tableName=table.getName();
		String javaName= "list_table.html";
		String mybatisFilePath=sourceCodeSetting.getGroupName()+"/resources/templates/"+sourceCodeSetting.getGroupName()+"/"+CodeHelper.toLowerCamelCase(tableName)+"/";
		GeneratorSourceCodeExport codeExport=new GeneratorSourceCodeExport(sourceCodeSetting.getOutPutDir());
		boolean genResult=codeExport.saveFile(mybatisFilePath,javaName, code.toString());
		return genResult;
	}
	    

}