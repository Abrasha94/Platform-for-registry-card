create table users_cards
(
    user_id   bigint
        constraint users_cards_users_id_fk
            references users,
    cards_number bigint
        constraint users_cards_cards_number_fk
            references cards,
    constraint users_cards_pk
        primary key (user_id, cards_number)
);
