package org.jkiss.dbeaver.model.sourcecode.utils;


public final class UnicodeUtils {
	public final static String UNICODE_PREFFIX = "\\u";
	public final static char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private UnicodeUtils() {

	}

	public static String encode(String str) {
		StringBuilder sb = new StringBuilder();
		char[] ch = str.toCharArray();
		for (char c : ch) {
			if (c == '\r' || c == '\n' || c == '\t') {
				sb.append(c);
			} else if (c < 0x20 || c > 0x7e) {
				sb.append(UNICODE_PREFFIX);
				sb.append(HEX_CHAR[(c >>> 12) & 0xF]);
				sb.append(HEX_CHAR[(c >>> 8) & 0xF]);
				sb.append(HEX_CHAR[(c >>> 4) & 0xF]);
				sb.append(HEX_CHAR[(c) & 0xF]);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String decode(String unicode) {
		char[] ch = unicode.toCharArray();
		StringBuilder sb = new StringBuilder(ch.length);
		for (int i = 0; i < ch.length; i++) {
			int j = i + 1;
			if (ch[i] == '\\' && j < ch.length && ch[j] == 'u') {
				int code = 0;
				int i1 = 0;
				if (j + 1 < ch.length && (i1 = getHexCharValue(ch[j + 1])) >= 0) {
					code += i1 << 12;
					int i2 = 0;
					if (j + 2 < ch.length
							&& (i2 = getHexCharValue(ch[j + 2])) >= 0) {
						code += i2 << 8;
						int i3 = 0;
						if (j + 3 < ch.length
								&& (i3 = getHexCharValue(ch[j + 3])) >= 0) {
							code += i3 << 4;
							int i4 = 0;
							if (j + 4 < ch.length
									&& (i4 = getHexCharValue(ch[j + 4])) >= 0) {
								code += i4;
								sb.append((char) code);
								i += 5;
							} else {
								sb.append(UNICODE_PREFFIX);
								sb.append(ch[j + 1]);
								sb.append(ch[j + 2]);
								sb.append(ch[j + 3]);
								i += 4;
							}
						} else {
							sb.append(UNICODE_PREFFIX);
							sb.append(ch[j + 1]);
							sb.append(ch[j + 2]);
							i += 3;
						}
					} else {
						sb.append(UNICODE_PREFFIX);
						sb.append(ch[j + 1]);
						i += 2;
					}
				} else {
					sb.append(UNICODE_PREFFIX);
					sb.append(ch[j + 1]);
					i += 1;
				}
			} else {
				sb.append(ch[i]);
			}
		}
		return sb.toString();
	}

	private static int getHexCharValue(char c) {
		if (c >= '0' && c <= '9') {
			return c - '0';
		} else if (c >= 'a' && c <= 'f') {
			return c - 87;
		} else if (c >= 'A' && c <= 'F') {
			return c - 55;
		}
		return -1;
	}

	public static void main(String[] args) {
		System.out.println(encode("元数据"));
	}
}
