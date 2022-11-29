package com.modsen.cardissuer.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GenerateCardNumberTest {

    @InjectMocks
    GenerateCardNumber generateCardNumber;

    @Test
    void whenGenerateMasterCard_thenReturnValidNumber() {
        final Long masterCard = generateCardNumber.generateMasterCard();

        assertThat(masterCard.toString().length()).isEqualTo(16);
    }

    @Test
    void whenGenerateVisa_thenReturnValidNumber() {
        final Long masterCard = generateCardNumber.generateVisa();

        assertThat(masterCard.toString().length()).isEqualTo(16);
    }
}