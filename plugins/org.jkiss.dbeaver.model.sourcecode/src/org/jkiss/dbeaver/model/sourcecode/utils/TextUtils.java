package org.jkiss.dbeaver.model.sourcecode.utils;

import java.lang.Character.UnicodeBlock;

public class TextUtils {
	private static final char DECIMAL_POINT='.';
	
	public static boolean isBlank(String str)
    {
        return (str==null || str.length()==0) ? true:false;
    }

	/**
	 * utf-8 转换成 unicode
	 * 
	 * @param inStr
	 * @return
	 */
	public static String utf8ToUnicode(String inStr) {
		return UnicodeUtils.encode(inStr);
	}

	/**
	 * unicode 转换成 utf-8
	 * 
	 * @param theString
	 * @return
	 */
	public static String unicodeToUtf8(String theString) {
		return UnicodeUtils.decode(theString);
	}
	
	public static boolean hasIn(String value,String ...strs)
	{
		if(strs!=null){
			for(String s:strs){
				if(s.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isNumber(final String text) {
       if(text==null)
       {
    	   return false;
       }
       String input=text.trim();
        boolean decimalFound = false;
        final int inputLen = input.length();
        for (int i = 0; i < inputLen; i++) {
            final char c = input.charAt(i);
            if (Character.isDigit(c)) {
                continue;
            } else if (c == DECIMAL_POINT) {
                if (decimalFound) {
                    return false;
                }
                decimalFound = true;
                continue;
            } else {
                return false;
            }
        }
       return true;
    }

}
