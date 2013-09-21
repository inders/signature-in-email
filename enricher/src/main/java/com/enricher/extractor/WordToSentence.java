package com.enricher.extractor;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 11/09/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordToSentence {

    private final String word;
    private final String sentence;

    public WordToSentence(String word, String sentence) {
        this.word = word;
        this.sentence = sentence;
    }

    public String getWord() {
        return word;
    }

    public String getSentence() {
        return sentence;
    }
}
