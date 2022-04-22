create table user_permissions
(
    user_id   int
        constraint user_permissions_users_id_fk
            references users,
    access_id int
        constraint user_permissions_access_id_fk
            references access,
    constraint user_permissions_pk
        primary key (user_id, access_id)
);

INSERT INTO user_permissions(user_id, access_id) VALUES (1, 1)