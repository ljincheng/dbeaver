package org.jkiss.dbeaver.model.sourcecode.entity;

import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCode;
import org.jkiss.dbeaver.model.sourcecode.GeneratorSourceCodeExport;
import org.jkiss.dbeaver.model.sourcecode.core.JavaTemplateContext;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.model.struct.rdb.DBSTable;

/**
 * 生成entity
 * @author ljc
 *
 */
public final class GeneratorSourceCodeEntity extends GeneratorSourceCode{
	 
	private static final String TEMPLATE_FILE_ENTITY="java_entity";

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE_ENTITY;
	}
	    
	@Override
	protected boolean  writeCode(DBRProgressMonitor monitor,DBSTable table,StringBuilder code) {
		String tableName=table.getName();
		String fullName=JavaTemplateContext.fillRuleFullName(tableName,this.sourceCodeSetting.getRuleEntity(),this.sourceCodeSetting);
		String packageName=CodeHelper.getPackageName(fullName);
		String simpleName=CodeHelper.getClassSimpleName(fullName);
		String javaName= simpleName+".java";
		String mybatisFilePath=sourceCodeSetting.getGroupName()+"/"+packageName.replaceAll("\\.", "/")+"/";
		GeneratorSourceCodeExport codeExport=new GeneratorSourceCodeExport(sourceCodeSetting.getOutPutDir());
		boolean genResult=codeExport.saveFile(mybatisFilePath,javaName, code.toString());
		return genResult;
	}


}