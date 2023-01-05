package com.modsen.cardissuer.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Messages {

    @Value("${exception.user.not.found}")
    public String userNotFound;
    @Value("${exception.cards.not.found}")
    public String cardsNotFound;
    @Value("${exception.cards.personal}")
    public String cardIsPersonal;
    @Value("${exception.company.not.found}")
    public String companyNotFound;
    @Value("${exception.role.not.found}")
    public String roleNotFound;
}
