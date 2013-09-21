package com.enricher;

import com.enricher.extractor.*;
import com.enricher.gmail.GmailClient;
import com.enricher.html.HtmlUtils;
import com.google.common.collect.Multimap;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.sun.mail.imap.IMAPFolder;
import jangada.SigFilePredictor;
import org.scribe.model.Token;

import javax.mail.Address;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 06/09/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Worker {

    //initialize all extractors
    ExtractEmailSignature extractEmailSignature = new ExtractEmailSignature();
    ExtractPhoneNumbers extractPhoneNumbers = new ExtractPhoneNumbers();
    ExtractNamedEntities extractNamedEntities = new ExtractNamedEntities();
    EmailExtractor emailExtractor = new EmailExtractor();
    URLExtractor urlExtractor = new URLExtractor();
    TwitterHandleExtractor twitterHandleExtractor = new TwitterHandleExtractor();
    SigFilePredictor sigpred = new SigFilePredictor();
    HtmlUtils htmlUtils = new HtmlUtils();

    public void doWork(String[] args) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("/tmp/signatures"));

        }catch (Exception e) {
            System.out.println(e);
            return;
        }

        Map<String, String> userToSignatures = new HashMap<String, String>();

        if (args.length != 1) {
            System.out.println("Please pass emailID to work on");
            return;
        }

        String email = args[0];
        if (email == null) {
            System.out.println("Please pass emailID to work on");
            return;
        }

        GmailClient gmailClient = new GmailClient(email);
        //Get oAuth Token for this Email ID
        Token accessToken = gmailClient.getOAuthToken();
        //connect to IMAP store
        IMAPFolder folder = gmailClient.connectToStore(accessToken.getToken());
        //Now work on the folder

        int startMessageNum = getStartMessageNum(email);
        int numMessages = 100;
        int cursorMessageNum = startMessageNum;
        try {
            int totalMessages = gmailClient.getMessageCount(folder);
            //Work on all messages in a folder
            while (startMessageNum < totalMessages) {
                try {
                    Message[] messages = gmailClient.fetch(startMessageNum, numMessages, folder);
                    //Extract Signature for each message
                    for (Message message : messages) {
                        try {
                            if (message.getContentType() != null && (message.getContentType().contains("TEXT/PLAIN") ||
                                    message.getContentType().contains("TEXT/HTML"))) {

                                Address[] addresses = message.getFrom();
                                String address = addresses[0].toString();
                                //validate that address is not a bot
                                String signature = null;
                                try {
                                    String text = null;
                                    if (message.getContentType().contains("TEXT/HTML"))
                                        text = htmlUtils.skipHtml((String) message.getContent());
                                    else
                                        text = (String) message.getContent();
                                    signature = extractEmailSignature.getSignature(text);
                                } catch (IOException e) {
                                    //java.io.IOException: Unknown encoding: 8BITS
                                    //skip such unknown encoding
                                    cursorMessageNum++;
                                    continue;

                                }
                                userToSignatures.put(address, signature);

                            }
                        } catch (MessagingException e) {
                            /**
                             * Bugs in IMAP server can cause this
                             * http://www.oracle.com/technetwork/java/faq-135477.html
                             */
                            try {
                                MimeMessage msg = (MimeMessage)folder.getMessage(cursorMessageNum);
                                // Use the MimeMessage copy constructor to make a copy
                                // of the entire message, which will fetch the entire
                                // message from the server and parse it on the client:
                                MimeMessage mimeMessage = new MimeMessage(msg);
                                if (mimeMessage.getContentType() != null && (mimeMessage.getContentType().contains("TEXT/PLAIN") ||
                                        mimeMessage.getContentType().contains("TEXT/HTML"))) {

                                    Address[] addresses = mimeMessage.getFrom();
                                    String address = addresses[0].toString();
                                    //validate that address is not a bot
                                    String signature = null;
                                    try {
                                        String text = null;
                                        if (message.getContentType().contains("TEXT/HTML"))
                                            text = htmlUtils.skipHtml((String) message.getContent());
                                        else
                                            text = (String) message.getContent();
                                        signature = extractEmailSignature.getSignature(text);
                                    } catch (IOException ioe) {
                                        //java.io.IOException: Unknown encoding: 8BITS
                                        //skip such unknown encoding
                                        cursorMessageNum++;
                                        continue;
                                    }
                                    userToSignatures.put(address, signature);
                                }

                            } catch (FolderClosedException fe) {
                                System.out.println(fe);
                                //reconnect to the folder store to get messages
                                folder = gmailClient.connectToStore(accessToken.getToken());
                                //start back from current message num till eternity
                                startMessageNum = cursorMessageNum;
                                continue;
                            }
                        }
                        cursorMessageNum++; //incremented for each message processed.
                    }
                }catch (FolderClosedException e) {
                    System.out.println(e);
                    e.printStackTrace();
                    //reconnect to the folder store to get messages
                    folder = gmailClient.connectToStore(accessToken.getToken());
                    //start back from current message num till eternity
                    startMessageNum = cursorMessageNum;
                    continue;
                }
                //Dump processed signatures till now
                System.out.println("#####Now dumping signatures####");
                for (Map.Entry<String, String> entry : userToSignatures.entrySet()) {
                    writer.write("#################\n");
                    writer.write("\n ****From**** :" + entry.getKey());
                    writer.write("\n*****Signature**** : \n" + entry.getValue());
                    String signature = entry.getValue();
                    getInformationInSignature(signature, writer);
                    writer.write("#################\n");
                }
                userToSignatures.clear();
                startMessageNum += numMessages;

            }
            writer.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {

        Worker worker = new Worker();
        worker.doWork(args);



    }

    private void getInformationInSignature(String signatureLine, BufferedWriter writer)  {
        try {
        String[] signatureLines = signatureLine.split("\n");

        System.out.println("####Finding Information in Signature....");
        //find country for this user
        int countryExtension = 91;
        for (int j=0; j < signatureLines.length; j++) {

            Iterable<PhoneNumberMatch> phoneNumberMatches = extractPhoneNumbers.getPhoneNumbers(signatureLines[j], countryExtension);
            for (PhoneNumberMatch phoneNumberMatch : phoneNumberMatches) {
              writer.write("Matched phone numbers " + phoneNumberMatch.rawString());
                writer.newLine();
            }
            //Try to get email address
            List<String> emailList = emailExtractor.getEmailAddress(signatureLines[j]);
            for(String email: emailList) {
                writer.write("Email id found :" + email);
                writer.newLine();
            }

        }
        List<String> urls = urlExtractor.getURLs(signatureLine);
        for (String url : urls) {
            writer.write("url :" + url);
            writer.newLine();
        }

        List<String> twitterHandles = twitterHandleExtractor.getTwitterHandles(signatureLine);
        for(String twitterHandle : twitterHandles) {
            writer.write("TwitterHandle :" + twitterHandle);
            writer.newLine();
        }

      //  extractNamedEntities.getNamedEntities(signatureLine, writer);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static int getStartMessageNum(String email) {
        //currently start from starting
        //TODO: store pre-processed count and start from there on.
        return 1;
    }
}
