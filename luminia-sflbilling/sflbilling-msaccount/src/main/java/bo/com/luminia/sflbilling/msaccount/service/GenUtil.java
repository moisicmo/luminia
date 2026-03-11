package bo.com.luminia.sflbilling.msaccount.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class GenUtil {

    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String businessCode() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
