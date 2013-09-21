package com.enricher.extractor;

import com.twitter.Extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 02/09/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLExtractor {
    Extractor extractor = new Extractor();

    /**
     * See http://www.codinghorror.com/blog/2008/10/the-problem-with-urls.html to understand why it's tough to parse URLS
     * @param text
     * @return
     */
    public List<String> getURLs(String text) {
        return extractor.extractURLs(text);
    }

}
