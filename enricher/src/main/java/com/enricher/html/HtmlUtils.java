package com.enricher.html;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 07/09/13
 * Time: 4:54 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 07/09/13
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.swing.text.html.*;

/**
 * This pipe removes HTML from a CharSequence. The HTML is actually parsed here,
 * so we should have less HTML slipping through... but it is almost certainly
 * much slower than a regular expression, and could fail on broken HTML.
 *
 * @author Greg Druck <a href="mailto:gdruck@cs.umass.edu">gdruck@cs.umass.edu</a>
 */

public class HtmlUtils{

    public String skipHtml(String text) {

        // I take these out ahead of time because the
        // Java HTML parser seems to die here.
        text = text.replaceAll("\\<NOFRAMES\\>","");
        text = text.replaceAll("\\<\\/NOFRAMES\\>","");

        ParserGetter kit = new ParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        HTMLEditorKit.ParserCallback callback = new TagStripper();

        try {
            StringReader r = new StringReader(text);
            parser.parse(r, callback, true);
        } catch (IOException e) {
            System.err.println(e);
        }
        String result = ((TagStripper) callback).getText();
        return result;
    }

    private class TagStripper extends HTMLEditorKit.ParserCallback {
        private String text;

        public TagStripper() {
            text = "";
        }

        public void handleText(char[] txt, int position) {
            for (int index = 0; index < txt.length; index++) {
                text += txt[index];
            }
            text += "\n";
        }

        public String getText() {
            return text;
        }

    }

    private class ParserGetter extends HTMLEditorKit {
        // purely to make this method public
        public HTMLEditorKit.Parser getParser() {
            return super.getParser();
        }
    }



}
