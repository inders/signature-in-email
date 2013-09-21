package com.enricher.gmail;


import com.google.common.base.Preconditions;
import com.sun.mail.imap.IMAPFolder;
import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthException;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 05/09/13
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class GmailClient
{
    private static final String NETWORK_NAME = "Google";
    private static final Logger LOG = LoggerFactory.getLogger(GmailClient.class);
    private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
    private static final String SCOPE = "https://mail.google.com";
    private static final Token EMPTY_TOKEN = null;
    private final String email;
    Google2Api api = new Google2Api();

    private final OAuthService service = new ServiceBuilder()
            .provider(Google2Api.class)
            .apiKey("429404759614-9d2glr77jb3h0bt9ti20lk32fe0t9qfr.apps.googleusercontent.com")
            .apiSecret("067gdtHSiyL0M8dxSPNH41yH")
            .scope(SCOPE)
                    //.callback("https://whistle-app.com/callback")
            .build();


    /**
     * Installs the OAuth2 SASL provider. This must be called exactly once before
     * calling other methods on this class.
     */
    public GmailClient(String email) {
        this.email = email;
    }


    public Token getOAuthToken() {
        boolean refresh = true;
        Scanner in = new Scanner(System.in);

        System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        //  Token requestToken = service.getRequestToken();
        String authorizationURL = service.getAuthorizationUrl(EMPTY_TOKEN);

        System.out.println("Got the Authorization URL!");
        System.out.println("Now go and authorize Whistle here:");
        System.out.println(authorizationURL);
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");

        System.out.println();
        return accessToken;
    }


    public IMAPFolder connectToStore(String accessToken) {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        IMAPFolder folder = null;

        try {
            OAuth2Authenticator.initialize();
            Store store = null;
            store = OAuth2Authenticator.connectToImap(
                    "imap.gmail.com",
                    993,
                    email,
                    accessToken,
                    false);

            folder = (IMAPFolder) store.getFolder("[Gmail]/All Mail");

            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return folder;
    }


    public void closeIMapAccount(Folder folder) {
        try {
            if (folder != null && folder.isOpen()) {
                folder.close(true);
            }
            Store store = folder.getStore();
            if (store != null) {
                store.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Fetches and saves all the interesting body parts. Currently pdf and html attachments are retrieved
     * and saved.
     *
     * @return UID of the last message that was downloaded. This needs to be stored so that next time we
     *         start retrieving from the next message onwards.
     * @throws java.io.IOException
     */
    public Message[] fetch(int startUID, int numMessages, Folder folder) throws Exception {
        Message[] messages = null;
        messages = folder.getMessages(startUID, startUID + numMessages);
        return messages;
    }

    public int getMessageCount(Folder folder) throws Exception{
        return folder.getMessageCount();
    }

    public static void main(String[] args) throws Exception
    {
        String email = "inder.pall@gmail.com";
        GmailClient gmailClient = new GmailClient(email);
        //Get the token
        String accessToken = gmailClient.getOAuthToken().getToken();
        //connect to imap store
        Folder imapFolder = gmailClient.connectToStore(accessToken);

        //fetch some messages
        try {
            Message[] messages = gmailClient.fetch(10, 20, imapFolder);
            for (int i=0; i < messages.length; i++) {
                System.out.println("Message :" + messages[i].getMessageNumber());
            }
            messages = gmailClient.fetch(30, 20, imapFolder);
            for (int i=0; i < messages.length; i++) {
                System.out.println("Message :" + messages[i].getMessageNumber());
            }

        } catch (Exception e) {

        }
        //close the imap account
        gmailClient.closeIMapAccount(imapFolder);
    }
}