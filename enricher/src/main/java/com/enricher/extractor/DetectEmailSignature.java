package com.enricher.extractor;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 01/09/13
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import jangada.*;
import edu.cmu.minorthird.util.LineProcessingUtil;

/**
 * How to use the Signature file detector.
 *
 * Usage: Demo2 filename1 filename2 filename3
 * Usage: Demo2 directoryname\*
 *
 * (only detects if a msg has or not a sig file; does not predict where the
 * sig file is)
 * Please use 1 email message per file. Please use .eml format.
 *
 *  */
public class DetectEmailSignature {


    public static void main(String[] args) {


        if(args.length<1){
            System.out.println("Usage: \n");
            System.out.println("Demo2 filename1 filename2 filename3 \n");
            System.out.println("Demo2 directoryWithFiles\\*\n");
            return;
        }

        /**
         * example to see the usage of SigDetector
         * (detecting if an email message has a signature or not)
         */
        SigFileDetector det = new SigFileDetector();
        String wholeMessage = "";
        for(int i=0; i< args.length; i++){
            try {
                //reads the input file
                wholeMessage = LineProcessingUtil.readFile(args[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            boolean isSig = det.hasSig(wholeMessage);
            if(isSig){
                System.out.println(args[i]+" has Signature");
            }
            else{
                System.out.println(args[i]+" has NOT Signature");
            }
        }


    }
}

