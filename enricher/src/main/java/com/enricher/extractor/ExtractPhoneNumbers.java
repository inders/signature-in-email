package com.enricher.extractor;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Library Used -> https://code.google.com/p/libphonenumber/
 * Created with IntelliJ IDEA.
 * User: inderbir.singh
 * Date: 01/09/13
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtractPhoneNumbers {
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();


    public Iterable<PhoneNumberMatch> getPhoneNumbers(String message, int countryExtension) {
        Iterable<PhoneNumberMatch> phoneNumberMatches =  phoneNumberUtil.findNumbers(message, phoneNumberUtil.getRegionCodeForCountryCode(countryExtension), PhoneNumberUtil.Leniency.VALID, 3);
        return phoneNumberMatches;
    }
}
