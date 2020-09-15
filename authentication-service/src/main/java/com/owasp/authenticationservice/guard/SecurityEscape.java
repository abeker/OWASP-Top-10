package com.owasp.authenticationservice.guard;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class SecurityEscape {

    public static String cleanIt(String content) {
        return Jsoup.clean(
                StringEscapeUtils.escapeHtml(
                        StringEscapeUtils.escapeJavaScript(
                                StringEscapeUtils.escapeSql(
                                        content))), Whitelist.basic());
    }


}