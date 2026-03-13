package com.atlascv.atlascvbackend.util;

import java.security.SecureRandom;

public class VerificationCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generateCode() {
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
}