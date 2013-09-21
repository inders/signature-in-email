package com.enricher.gmail;

/**
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 09/09/13
 * Time: 10:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RefreshTokenExtractor {
    org.scribe.model.Token extract(java.lang.String s);

}
