create
EXTENSION IF NOT EXISTS "uuid-ossp";

insert into profile(id, name, username, password, status, visible, created_date)
values (1, 'ADMIN', 'admin@gmail.com',
        '$2a$10$6LyU.RZYLJWU78EdUiAH1urugCIJWy0f.8UEepYbp55967o.iQVYy', 'ACTIVE', true, now());

-- select setval('profile_id_seq', max(id)) from profile;

insert into profile_role(profile_id, roles, created_date)
values (1, 'ROLE_USER', now()),
       (1, 'ROLE_ADMIN', now())