package org.jkiss.dbeaver.model.sourcecode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.eclipse.osgi.util.NLS;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.sourcecode.internal.UIMessages;
import org.jkiss.dbeaver.model.sourcecode.utils.CodeHelper;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;

public class GeneratorSourceCodeExport {
	
	private String outputFolder;
	private final String rootFolderName="src";
	
	private boolean useCamelCase=true;
	
	public GeneratorSourceCodeExport() {
		// TODO Auto-generated constructor stub
	}
	
	public GeneratorSourceCodeExport(String outputFolder)
	{
		this.outputFolder=outputFolder;
	}
	 
	public void setOutputFolder(String outputFolder) {
		this.outputFolder=outputFolder;
	}
	
	public String getOutputFolder()
	{
		return this.outputFolder;
	}
	

	public boolean getUseCamelCase()
	{
		return useCamelCase;
	}
	
	public void setUseCamelCase(boolean value)
	{
		this.useCamelCase=value;
	}
	

	public boolean saveFile(String filePath,String fileName,String content)
	{
		if(CodeHelper.isEmpty(outputFolder))
		{
			DBWorkbench.getPlatformUI().showMessageBox(UIMessages.dbeaver_generate_sourcecode_msg_selectOutPutFolder, UIMessages.dbeaver_generate_sourcecode_msg_selectOutPutFolder_detailTip, true);
			
			return false;
		}
		try {
			String rootPath=null;
			if(!outputFolder.endsWith("/") && !outputFolder.endsWith("\\"))
			{
				rootPath=outputFolder.replaceAll("/", File.separator)+File.separator;
			}else {
				rootPath=outputFolder.replaceAll("/", File.separator);
			}
			String fileDir=filePath.replace("/", File.separator);
			//String fileDir=systemFilePath.substring(0, filePath.lastIndexOf(File.separator));
			String systemFileDir=rootPath+ fileDir;
			String systemFile=rootPath+ fileDir+File.separator+fileName;
			File codeFileDir=new File(systemFileDir);
			if(!codeFileDir.exists())
			{
				codeFileDir.mkdirs();
			}
			File codeFile=new File(systemFile);
			if(codeFile.exists())
			{
				if(!DBWorkbench.getPlatformUI().confirmAction("", NLS.bind(UIMessages.dbeaver_generate_sourcecode_msg_confirmReplaceExistFile,fileName)))
				{ 
					return false;
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(codeFile));
			bw.write(content);
			bw.close();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			DBWorkbench.getPlatformUI().showError(UIMessages.dbeaver_generate_sourcecode_error,UIMessages.dbeaver_generate_sourcecode_generatorSourceCodeError,e);
			return false;
		}
		
	}
	
	 

}
