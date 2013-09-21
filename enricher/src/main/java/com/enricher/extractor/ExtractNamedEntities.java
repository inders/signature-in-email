package com.enricher.extractor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.tokensregex.MultiCoreMapNodePattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.Writer;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 02/09/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtractNamedEntities {

    static Properties props = new Properties();
    static {
        //  props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse");

    }
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


    public void getPosTag(String text) {
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            System.out.println("Sentence :" + sentence);

            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.println("word [" + word + "] pos tag ["+ pos + "]");
            }


        }
    }

    public Multimap<String, String> getNamedEntities(String text, Writer writer) {
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        Multimap<String, String> entityToWord = LinkedListMultimap.create();

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {

            // traversing the words in the current sentence
            // System.out.println("Sentence :" + sentence.toString());
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                entityToWord.put(ne, word);
                //  System.out.println("Word [" + word + "] postag [" + pos +"] named entity [" + ne + "]");
            }
            //print words grouped by entity Name
            for (String entityName : entityToWord.keySet()) {
                try {
                    if (writer != null) {
                    writer.write("Entity Name [" + entityName + "] + words [" + entityToWord.get(entityName) + "]");
                    writer.write("\n");
                    } else  {
                        System.out.println("Entity Name [" + entityName + "] + words [" + entityToWord.get(entityName) + "]");

                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        }
        return entityToWord;
    }

}
