package org.jkiss.dbeaver.model.sourcecode.entity;

import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;

public class GeneratorSourceCodeLangMessage extends GeneratorSourceCode{
	 
	private static final String TEMPLATE_FILE_ENTITY="lang_message";

	@Override
	protected String getTemplateFile() {
		
		return TEMPLATE_FILE_ENTITY;
	}
	    
	@Override
	protected boolean  writeCode(DBRProgressMonitor monitor,DBSTable table,StringBuilder code) {
		String fileName= "messages.properties";
		String filePath=sourceCodeSetting.getGroupName()+"/resources/i18n/";
		GeneratorSourceCodeExport codeExport=new GeneratorSourceCodeExport(sourceCodeSetting.getOutPutDir());
		boolean genResult=codeExport.saveFile(filePath,fileName, code.toString());
		return genResult;
	}


}