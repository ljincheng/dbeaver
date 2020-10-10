package org.jkiss.dbeaver.model.sourcecode.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.model.DBPScriptObject;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
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

public class GeneratorSourceCodeController extends GeneratorSourceCode{
	 
	private static final String TEMPLATE_FILE_ENTITY = "java_controller";

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE_ENTITY;
	}

	@Override
	protected boolean writeCode(DBRProgressMonitor monitor, DBSTable table, StringBuilder code) {
		String tableName=table.getName();
		String fullName=JavaTemplateContext.fillRuleFullName(tableName,this.sourceCodeSetting.getRuleController(),this.sourceCodeSetting);
		String packageName=CodeHelper.getPackageName(fullName);
		String simpleName=CodeHelper.getClassSimpleName(fullName);
		String javaName= simpleName+".java";
		String mybatisFilePath=sourceCodeSetting.getGroupName()+"/"+packageName.replaceAll("\\.", "/")+"/";
		GeneratorSourceCodeExport codeExport=new GeneratorSourceCodeExport(sourceCodeSetting.getOutPutDir());
		boolean genResult=codeExport.saveFile(mybatisFilePath,javaName, code.toString());
		return genResult;
	}
	    

}