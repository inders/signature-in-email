package com.enricher;

import com.enricher.extractor.*;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import edu.cmu.minorthird.util.LineProcessingUtil;
import jangada.SigFilePredictor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 01/09/13
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Test {
    public static void main(String[] args) {

        ExtractEmailSignature extractEmailSignature = new ExtractEmailSignature();
        ExtractPhoneNumbers extractPhoneNumbers = new ExtractPhoneNumbers();
        ExtractNamedEntities extractNamedEntities = new ExtractNamedEntities();
        EmailExtractor emailExtractor = new EmailExtractor();
        URLExtractor urlExtractor = new URLExtractor();
        TwitterHandleExtractor twitterHandleExtractor = new TwitterHandleExtractor();
        SigFilePredictor sigpred = new SigFilePredictor();

        if (args.length < 1) {
            System.out.println("Usage: \n");
            System.out.println("Demo5 filename1 filename2 filename3 \n");
            System.out.println("Demo5 directoryWithFiles\\*\n");
            return;
        }

        try {
            for (int i = 0; i < args.length; i++) {
                //read the msg file
                String message = LineProcessingUtil.readFile(args[i]);

                //extract, and print the reply-to lines
                System.out.println("\n######### Signature Lines of " + args[i]+ " #######");
                String signatureLine = extractEmailSignature.getSignature(message);
                System.out.print(signatureLine);

                String[] signatureLines = signatureLine.split("\n");

                System.out.println("####Finding Information in Signature....");
                //find country for this user
                int countryExtension = 91;
                for (int j=0; j < signatureLines.length; j++) {

                    Iterable<PhoneNumberMatch> phoneNumberMatches = extractPhoneNumbers.getPhoneNumbers(signatureLines[j], countryExtension);
                    for (PhoneNumberMatch phoneNumberMatch : phoneNumberMatches) {
                        System.out.println("Matched phone numbers " + phoneNumberMatch.rawString());
                    }
                    //Try to get email address
                    List<String> emailList = emailExtractor.getEmailAddress(signatureLines[j]);
                    for(String email: emailList) {
                        System.out.println("Email id found :" + email);
                    }

                }
                List<String> urls = urlExtractor.getURLs(signatureLine);
                for (String url : urls) {
                    System.out.println("url :" + url);
                }

                List<String> twitterHandles = twitterHandleExtractor.getTwitterHandles(signatureLine);
                for(String twitterHandle : twitterHandles) {
                    System.out.println("TwitterHandle :" + twitterHandle);
                }

                System.out.println("\n#####Finding named entities in#######" + args[i] + "####");
                extractNamedEntities.getNamedEntities(signatureLine, null);

                //let's try to find quote
                String lastLine = signatureLines[signatureLines.length - 1];
                extractNamedEntities.getPosTag(lastLine);


                //in case you want to print the message with the
                //reply-lines removed, just uncommend the code below

                //String msg = sigpred.getMsgWithoutSignatureLines(message);
                //System.out.println("\n######### Msg After Removing the Reply Lines  #######");
                //System.out.print(msg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
