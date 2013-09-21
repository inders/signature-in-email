package com.enricher.extractor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Algo available at -> http://www.cs.cmu.edu/~vitor/codeAndData.html
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 02/09/13
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmailExtractor {

    static Pattern pattern = null;



    static  {
        BufferedReader reader = null;

        InputStream inputStream = ClassLoader.getSystemResourceAsStream("emailextractor-expression");
        reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String regEx = reader.readLine();
            pattern = Pattern.compile(regEx);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }



    }

    public List<String> getEmailAddress(String text) {
        List<String> emailList = new ArrayList<String>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String email = matcher.group(0);
            if (email.trim().endsWith("|"))  {
                email = email.substring(0, email.length() - 2);
            }
            emailList.add(email);
        }
        return emailList;
    }

}
