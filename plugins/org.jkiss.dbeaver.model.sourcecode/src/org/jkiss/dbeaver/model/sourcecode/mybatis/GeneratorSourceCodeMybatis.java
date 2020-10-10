package org.jkiss.dbeaver.model.sourcecode.mybatis;

import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;


/**
 * 生成Mybatis文件
 * @author ljc
 *
 */
public class GeneratorSourceCodeMybatis extends GeneratorSourceCode{
	 
	private static final String TEMPLATE_FILE_ENTITY = "mybatis";

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE_ENTITY;
	}

	@Override
	protected boolean writeCode(DBRProgressMonitor monitor, DBSTable table, StringBuilder code) {
		String tableName=table.getName();
		String fullName=JavaTemplateContext.fillRuleFullName(tableName,this.sourceCodeSetting.getRuleDao(),this.sourceCodeSetting);
		String simpleName=CodeHelper.getClassSimpleName(fullName);
		String javaName= simpleName+".xml";
		String mybatisFilePath=sourceCodeSetting.getGroupName()+"/resources/mapper/"+sourceCodeSetting.getGroupName()+"/";
		GeneratorSourceCodeExport codeExport=new GeneratorSourceCodeExport(sourceCodeSetting.getOutPutDir());
		boolean genResult=codeExport.saveFile(mybatisFilePath,javaName, code.toString());
		return genResult;
	}
	

}
