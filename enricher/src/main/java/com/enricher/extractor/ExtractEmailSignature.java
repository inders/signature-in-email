package com.enricher.extractor;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 01/09/13
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
import edu.cmu.minorthird.text.CharAnnotation;
import edu.cmu.minorthird.util.LineProcessingUtil;
import jangada.*;

/**
 * An example of how to extract the signature blocks of an email message.
 * Or extracting the message with the signature-blocks removed.
 * Created on Jun 12, 2005
 * @author Vitor R. Carvalho
 *
 * The simplest way, pass an email msg as a String, and have returned a String
 * with the signature lines only (or a String with the original msg without the
 * signature lines)
 *
 * Please use 1 email message per file. Please use .eml format.
 *
 */
public class ExtractEmailSignature {
    SigFilePredictor sigpred = new SigFilePredictor();


    public String getSignature(String message) {
        return sigpred.getSignatureLines(message);
    }



}
