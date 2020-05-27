package com.fpt.base.net.interceptor;

import org.apache.commons.lang.exception.NestableRuntimeException;
import org.apache.commons.lang.text.StrBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <pre>
 *   @author  : fpt
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2018/12/14 09:25
 *   desc    : json格式化显示工具
 * </pre>
 */
public class JsonFormat {

    /**
     * 对json字符串格式化输出
     * @param jsonStr
     * @return
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char last;
        char current = '\0';
        int indent = 0;

        int j = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);

            switch (current) {
                case '{':
                case '[':
                    j = 0;
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                case '}':
                    if (j != 0){
                        handleContent(sb,jsonStr,i,j);
                        j = 0;
                    }
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                case ',':
                    if (j != 0){
                        handleContent(sb,jsonStr,i,j);
                        j = 0;
                    }
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                case ':':
                    sb.append(current);
                    j = i;
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }


    private static void handleContent(StringBuilder sb, String jsonStr, int i, int j) {
        String s0 = jsonStr.substring(j+1,i);
        String s1 = decodeUnicode(s0);
        String s2 = unescapeJava(s1);

        sb.replace(sb.length()-i+j+1,sb.length(),s2);
    }

    /**
     * 添加space
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    /**
     * unicode转中文
     * @param ascii
     * @return
     */
    private static String decodeUnicode (String ascii){
        String[] asciis = ascii.split ("\\\\u");
        String nativeValue = asciis[0];
        try {
            for ( int i = 1; i < asciis.length; i++ ) {
                String code = asciis[i];
                nativeValue += (char) Integer.parseInt (code.substring (0, 4), 16);
                if (code.length () > 4) {
                    nativeValue += code.substring (4);
                }
            }
        } catch (NumberFormatException e) {
            return ascii;
        }
        return nativeValue;
    }

    /**
     * 转义字符
     * @param str
     * @return
     */
    private static String unescapeJava(String str){
        StringWriter writer = new StringWriter(1024);
        try {
            unescapeJava(writer,str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * 转义字符
     * @param out
     * @param str
     * @throws IOException
     */
    private static void unescapeJava(Writer out, String str) throws IOException {
        if(out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if(str != null) {
            int sz = str.length();
            StrBuilder unicode = new StrBuilder(4);
            boolean hadSlash = false;
            boolean inUnicode = false;

            for(int i = 0; i < sz; ++i) {
                char ch = str.charAt(i);
                if(inUnicode) {
                    unicode.append(ch);
                    if(unicode.length() == 4) {
                        try {
                            int nfe = Integer.parseInt(unicode.toString(), 16);
                            out.write((char)nfe);
                            unicode.setLength(0);
                            inUnicode = false;
                            hadSlash = false;
                        } catch (NumberFormatException var9) {
                            throw new NestableRuntimeException("Unable to parse unicode value: " + unicode, var9);
                        }
                    }
                } else if(hadSlash) {
                    hadSlash = false;
                    switch(ch) {
                        case '\"':
                            out.write(34);
                            break;
                        case '\'':
                            out.write(39);
                            break;
                        case '\\':
                            out.write(92);
                            break;
                        case 'b':
                            out.write(8);
                            break;
                        case 'f':
                            out.write(12);
                            break;
                        case 'n':
                            out.write(10);
                            break;
                        case 'r':
                            out.write(13);
                            break;
                        case 't':
                            out.write(9);
                            break;
                        case 'u':
                            inUnicode = true;
                            break;
                        default:
                            out.write(ch);
                    }
                } else if(ch == 92) {
                    hadSlash = true;
                } else {
                    out.write(ch);
                }
            }

            if(hadSlash) {
                out.write(92);
            }

        }
    }

}
