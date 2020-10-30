package org.jkiss.dbeaver.model.sourcecode.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.model.DBPDataKind;
import org.jkiss.dbeaver.model.struct.DBSEntityAttribute;
import org.jkiss.dbeaver.model.struct.rdb.DBSTableColumn;
import org.jkiss.dbeaver.utils.ContentUtils;
import org.jkiss.dbeaver.utils.GeneralUtils;
import org.jkiss.utils.CommonUtils;
import org.jkiss.utils.IOUtils;

/**
 * 代码辅助方法
 * @author ljc
 *
 */
public class CodeHelper {
	
	private static final String TEMPLATE_DIR = "templates/code/";

	public static boolean isEmpty(@Nullable String value) {
        return value == null || value.length() == 0;
    }
	public static String emptyString(String value,boolean trim) {
		
        String str= (value == null || value.length() == 0)?"":value;
        if(trim)
        {
        	return str.trim();
        }else {
        	return str;
        }
    }
	 
	public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }

        final StringBuilder ret = new StringBuilder(str.length());

        boolean isWordStart = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                if (isWordStart) {
                    ret.append(Character.toUpperCase(ch));
                    isWordStart = false;
                } else {
                    ret.append(Character.toLowerCase(ch));
                }
            } else {
                ret.append(ch);
                isWordStart = true;
            }
        }

        return ret.toString();
    }
	
	public static String toUpperCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }

        final StringBuilder ret = new StringBuilder(str.length());

        boolean isWordStart = false;
        ret.append(Character.toUpperCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.compare('_', ch)!=0) {
                if (isWordStart) {
                    ret.append(Character.toUpperCase(ch));
                    isWordStart = false;
                } else {
                	 ret.append(ch);
                }
            } else {
               // ret.append(ch);
                isWordStart = true;
            }
        }

        return ret.toString();
    }
	
	
	public static String toLowerCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }

        final StringBuilder ret = new StringBuilder(str.length());

        boolean isWordStart = false;
        ret.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.compare('_', ch)!=0) {
                if (isWordStart) {
                    ret.append(Character.toUpperCase(ch));
                    isWordStart = false;
                } else {
                	 ret.append(ch);
                }
            } else {
               // ret.append(ch);
                isWordStart = true;
            }
        }

        return ret.toString();
    }
	
	public static void addCodeLine(StringBuilder sql, String ddl) {
        ddl = ddl.trim();
        if (!CommonUtils.isEmpty(ddl)) {
            sql.append(ddl);
            String lf = GeneralUtils.getDefaultLineSeparator();
            sql.append(lf);
        }
    }
	
	
	public static String joinStrings(String divider,String[] exclude, String ... array) {
        if (array == null) return "";
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
        	String value=array[i];
        	if(!hasIn(value,exclude))
        	{
        		 if (str.length() > 0) {
        			 str.append(divider);
        		 }
        		 str.append(value);
        	}
        }
        return str.toString();
    }
	
	 public static String joinStrings(String divider,String[] exclude, Collection<String> col) {
	        if (col == null) return "";
	        StringBuilder str = new StringBuilder();
	        for (String item : col) {
	        	if(!hasIn(item,exclude))
	        	{
	        		 if (str.length() > 0) {
	        			 str.append(divider);
	        		 }
	        		 str.append(item);
	        	}
	        }
	        return str.toString();
	    }
	 
	 public static boolean hasIn(String str,String ...strs)
	 {
		 if(strs!=null)
		 {
			 for(String s:strs)
			 {
				 if(s.equals(str))
				 {
					 return true;
				 }
			 }
		 }
		 return false;
	 }
	 
	 
	 public static String columnType2JavaType(DBSEntityAttribute attr)
	 {
		 String typeName=null;
		 DBPDataKind attrDataKind=attr.getDataKind();
		
		 if(DBPDataKind.NUMERIC.equals(attrDataKind))
		{
			switch (attr.getTypeID()) {
			
			case Types.FLOAT:
				typeName="Float";
				break;
			case Types.DECIMAL:
				typeName="BigDecimal";
				break;
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.TINYINT:
				typeName="Integer";
				break;
			case Types.DOUBLE:
				typeName="Double";
				break;
			case Types.BIGINT:
				typeName="Long";
				break;
			default:
				typeName="Long";
				break;
			}
			 
		}else if(DBPDataKind.DATETIME.equals(attrDataKind))
		{
			typeName="Date";
		}else {
			typeName= "String";
		}
 
		 return typeName;
	 }
	 
	 public static String columnType2JavaPackageName(DBSEntityAttribute attr)
	 {
		 String typeName=null;
		 DBPDataKind attrDataKind=attr.getDataKind();
		 if(DBPDataKind.NUMERIC.equals(attrDataKind))
		{
			if (attr.getTypeID()== Types.DECIMAL) {
				typeName="java.math.BigDecimal";
			}
			 
		}else if(DBPDataKind.DATETIME.equals(attrDataKind))
		{
			typeName="java.util.Date";
		} 
 
		 return typeName;
	 }
	 
	 
	 /**
	  * 连接package路径节点
	  * @param basePackage
	  * @param nodeName
	  * @return
	  */
	 public static String concatPackageNode(String basePackage,String nodeName)
	 {
		 if(isEmpty(basePackage))
		 {
			 return nodeName;
		 }else {
			 return basePackage+"."+nodeName;
		 }
		 
	 }
	 
	 /**
	  * 导入Java的package
	  * @param sql
	  * @param attrs
	  */
	 public static void importJavaPackage(StringBuilder sql,  Collection<DBSEntityAttribute> attrs) {
	       
		 	Map<String, String> packageMap=new HashMap<String, String>();
	        if (!CommonUtils.isEmpty(attrs)) {
	            for(DBSEntityAttribute attr:attrs)
	            {
	            	String packageName=columnType2JavaPackageName(attr);
	            	if(packageName!=null)
	            	{
	            		packageMap.put(packageName, packageName);
	            	}
	            }
	            for(Map.Entry<String, String> entry : packageMap.entrySet()){
	                String mapKey = entry.getKey();
	                CodeHelper.addCodeLine(sql, "import "+mapKey+";");
	            }
	            
	        }
	    }
	 
	 /**
	  * 获取类简称
	  * @param fullName
	  * @return
	  */
	 public static String getClassSimpleName(String fullName)
	 {
		 if(isEmpty(fullName))
		 {
			 return null;
		 }
		 String simpleName=fullName.substring(fullName.lastIndexOf(".")+1);
		 return simpleName;
	 }
	 
	 /**
	  * 获取类的包名
	  * @param value
	  * @return
	  */
	 public static String getPackageName(String value)
	 {
		 if(isEmpty(value))
		 {
			 return "";
		 }
		 String packagevalue="";
		 if(value.indexOf(".")>0)
		 {
			 packagevalue=value.substring(0,value.lastIndexOf("."));
		 }
		 return packagevalue;
	 }
	 
	 /**
	  * ${}变量替换
	  * @param template
	  * @param params
	  * @return
	  */
	 public static String processTemplate(String template, Map<String, Object> params){
		    StringBuffer sb = new StringBuffer();
		    Matcher m = Pattern.compile("\\$\\{[\\w,\\(,\\)]+\\}").matcher(template);
		    while (m.find()) {
		        String param = m.group();
		        String keyStr=param.substring(2, param.length() - 1);
		        Object value=null;
		        if(keyStr.startsWith("lower(") && keyStr.endsWith(")"))
		        {
		        	String key=keyStr.substring(6,keyStr.length()-1).trim();
		        	value=params.get(key);
		        	if(value!=null)
		        	{
		        		value=((String)value).toLowerCase();
		        	}
		        }else if(keyStr.startsWith("lcamel(") && keyStr.endsWith(")")) {
		        	String key=keyStr.substring(7,keyStr.length()-1).trim();
		        	value=params.get(key);
		        	if(value!=null)
		        	{
		        		value=CodeHelper.toLowerCamelCase((String)value);
		        	}
		        }else if(keyStr.startsWith("upper(") && keyStr.endsWith(")"))
			        {
			        	String key=keyStr.substring(6,keyStr.length()-1).trim();
			        	value=params.get(key);
			        	if(value!=null)
			        	{
			        		value=((String)value).toUpperCase();
			        	}
			        }else if(keyStr.startsWith("upcamel(") && keyStr.endsWith(")")) {
			        	String key=keyStr.substring(8,keyStr.length()-1).trim();
			        	value=params.get(key);
			        	if(value!=null)
			        	{
			        		value=CodeHelper.toUpperCamelCase((String)value);
			        	}
			        }else if(keyStr.startsWith("trim(") && keyStr.endsWith(")")) {
			        	String key=keyStr.substring(5,keyStr.length()-1).trim();
			        	value=params.get(key);
			        	if(value!=null)
			        	{
			        		value=((String)value).trim();
			        	}
			        }else {
			        	value = params.get(keyStr);
			        }
		       
		        m.appendReplacement(sb, value==null ? "" : value.toString());
		    }
		    m.appendTail(sb);
		   return sb.toString();
	 }
	 
	 
	 public static String matchCodeTemplate(String template, Map<String, String> params){
		    StringBuffer sb = new StringBuffer();
		    Matcher m = Pattern.compile("\\@\\@\\w+\\@\\@").matcher(template);
		    while (m.find()) {
		        String param = m.group();
		        
		        String value = params.get(param.substring(2, param.length() - 2));
		        m.appendReplacement(sb, value==null ? "" :m.quoteReplacement(value));
		    }
		    m.appendTail(sb);
		   return sb.toString();
	 }
	 
//	 public static String standardExpressionPreprocessor(String template, Map<String, String> params)
//	 {
//		 StringBuffer sb = new StringBuffer();
//		 Matcher m = Pattern.compile("\\@\\w+\\@").matcher(template);
//		 if(m.find())
//		 {
//			 sb.append(template.substring(0,m.start(0)))
//		 }
//		 
//	 }
	 
//	  public static String strPatternValuen( String pattern) {
//		  if(pattern==null)
//		  {
//			  return "";
//		  }
//	        final String pat = pattern.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\$","\\\\\$");
//	        return pat;
//	    }
	 
	 
		public static String loadResource(String templateName) {
			String resourceName = TEMPLATE_DIR + templateName + ".txt";

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
