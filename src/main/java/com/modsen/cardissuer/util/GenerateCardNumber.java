package com.modsen.cardissuer.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class GenerateCardNumber {

    public static final String[] MASTERCARD_PREFIX_LIST = new String[]{
            "51", "52", "53", "54", "55", "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228",
            "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720"};

    public static final String[] VISA_PREFIX_LIST = new String[]{"4539", "4556", "4916", "4532", "4929",
            "40240071", "4485", "4716", "4"};

    private Random random = new Random(System.currentTimeMillis());

    private String generate(String[] prefixes, int length) {
        final String prefix = prefixes[new Random().nextInt(prefixes.length)];

        int randomNumberLength = length - (prefix.length() + 1);

        StringBuilder builder = new StringBuilder(prefix);
        for (int i = 0; i < randomNumberLength; i++) {
            int digit = this.random.nextInt(10);
            builder.append(digit);
        }

        int checkDigit = this.getCheckDigit(builder.toString());
        builder.append(checkDigit);

        return builder.toString();
    }

    private int getCheckDigit(String number) {

        int sum = 0;
        for (int i = 0; i < number.length(); i++) {

            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }

        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
    }

    public Long generateMasterCard() {
        return Long.valueOf(generate(MASTERCARD_PREFIX_LIST, 16));
    }

    public Long generateVisa() {
        return Long.valueOf(generate(VISA_PREFIX_LIST, 16));
    }
}
