package com.example.success.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

public class StringUtil extends StringUtils {
    public static String concat(String src, String seperator, String appendStr) {
        String str = defaultString(src);
        if (isNotBlank(str)) {
            str += defaultString(seperator);
        }
        str += defaultString(appendStr);
        return str;
    }
    public static String escapeJSON(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String       t;

        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static String replaceXML(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }
        string = StringUtil.replace(string, "&", "&amp;");
        string = StringUtil.replace(string, "\"", "&quot;");
        string = StringUtil.replace(string, "\'", "&apos;");
        string = StringUtil.replace(string, "<", "&lt;");
        string = StringUtil.replace(string, ">", "&gt;");

        return string;
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    public static String formatBitRate(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1000.0;
        double m = ((size/1000.0)/1000.0);
        double g = (((size/1000.0)/1000.0)/1000.0);
        double t = ((((size/1000.0)/1000.0)/1000.0)/1000.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" Tbps");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" Gbps");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" Mbps");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" Kbps");
        } else {
            hrSize = dec.format(b).concat(" Bps");
        }

        return hrSize;
    }

    public static <T> String convertObjectToJSON(T obj) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(obj);
        return json;
    }
}

