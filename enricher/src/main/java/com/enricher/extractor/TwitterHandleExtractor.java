package com.enricher.extractor;

import com.twitter.Extractor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 03/09/13
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterHandleExtractor {

    Extractor extractor = new Extractor();

    public List<String> getTwitterHandles(String text){
        return extractor.extractMentionedScreennames(text);

    }
}
