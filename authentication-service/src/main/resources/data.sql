insert into permission (name) values
    ('CREATE_AD'), ('VIEW_AD'), ('UPDATE_AD'), ('DELETE_AD'), ('UPLOAD_PHOTO'),     -- 1, 2, 3, 4, 5
    ('POST_COMMENT'), ('VIEW_COMMENT'),  ('DENY_COMMENT'), ('APPROVE_COMMENT'),     -- 6, 7, 8, 9
    ('POST_RATE'), ('VIEW_RATE'), ('UPDATE_RATE'),      -- 10, 11, 12
    ('LOGIN'), ('REGISTER'), ('SEARCH'),    -- 13, 14, 15
    ('CREATE_AGENT'), ('CHANGE_PERMISSION'),    -- 16, 17
    ('RENT_A_CAR'), ('CREATE_REQUEST'), ('APPROVE_REQUEST'),    -- 18, 19, 20
    ('APPROVE_USER_REQUEST'), ('DENY_USER_REQUEST'),    -- 21, 22
    ('VIEW_USER_REQUESTS'), ('VIEW_AGENT_REQUESTS'),    -- 23, 24
    ('PAY_REQUEST'), ('DROP_REQUEST'), ('DENY_REQUEST');    -- 25, 26, 27

insert into authority (name) values ('ROLE_ADMIN'), ('ROLE_AGENT'), ('ROLE_SIMPLE_USER'),
    ('ROLE_REVIEWER_USER'), ('ROLE_RENT_USER'), ('ROLE_COMMENT_USER'),
    ('ROLE_REQUEST_USER'), ('ROLE_AD_USER');

insert into authorities_permissions (authority_id, permission_id) values
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16),
    (1, 17), (1, 18), (1, 19), (1, 20), (1, 21), (1, 22),
    (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6), (2, 7), (2, 11), (2, 20), (2, 24), (2, 27),
    (3, 13), (3, 14), (3, 15),
    (4, 10), (4, 11), (4, 12),
    (5, 18),
    (6, 6), (6, 7),
    (7, 19), (7, 23), (7, 25), (7, 26),
    (8, 1), (8, 2), (8, 3), (8, 4), (8, 5);

-- admin@gmail.com -> Admin123!!!
-- agent@gmail.com -> Agent123!!!
-- customer@gmail.com -> Customer123!!!
insert into user_entity (id, deleted, first_name, last_name, last_password_reset_date, password, username, user_role) values
    ('e47ca3f0-4906-495f-b508-4d9af7013575', false, 'Fake', 'Admin', '2020-06-12 21:58:58.508-07', 'admin', 'fakeadmin@gmail.com', 'ADMIN'),
    ('e153d906-ba0f-4ac9-88bb-0d9b5817cb78', false, 'User', 'User', '2020-06-12 21:58:58.508-07', 'password', 'user@gmail.com', 'SIMPLE_USER'),
    ('924c26a6-d96b-4ffb-ab8b-250602c03f75', false, 'Ed', 'Snowden', '2020-06-12 21:58:58.508-07', '$2y$10$UFTyoDVYFFUqlb0lnKfoKe7H/EbQOqZH.ZYHf6sOYiOWSRCmpcJ5K', 'admin@gmail.com', 'ADMIN'),
    ('602399f4-183a-4174-95ea-1b42940fa0a9', false, 'Glenn', 'Greenwald', '2020-06-12 21:58:58.508-07', '$2a$10$zQU7XEdDSMvxt13Xkjs3X.CCY64edvCS0ZXcgqPtU8FhSYVUhtnau', 'agent@gmail.com', 'AGENT'),
    ('f1aed848-878f-4d4a-b198-e707b5dd220c', false, 'Julian', 'Assange', '2020-06-12 21:58:58.508-07', '$2a$10$UJEbOrAMWN/bh8tEPHt.Z.fD2RX.T0e0MXNuZEFCEFTNAjHkdAVju', 'customer@gmail.com', 'SIMPLE_USER');

insert into user_authority (user_id, authority_id) values
    ('924c26a6-d96b-4ffb-ab8b-250602c03f75', 1),
    ('602399f4-183a-4174-95ea-1b42940fa0a9', 2),
    ('602399f4-183a-4174-95ea-1b42940fa0a9', 3),
    ('602399f4-183a-4174-95ea-1b42940fa0a9', 8),
    ('f1aed848-878f-4d4a-b198-e707b5dd220c', 3),
    ('f1aed848-878f-4d4a-b198-e707b5dd220c', 5),
    ('f1aed848-878f-4d4a-b198-e707b5dd220c', 7),
    ('e47ca3f0-4906-495f-b508-4d9af7013575', 1),
    ('e153d906-ba0f-4ac9-88bb-0d9b5817cb78', 3),
    ('e153d906-ba0f-4ac9-88bb-0d9b5817cb78', 5),
    ('e153d906-ba0f-4ac9-88bb-0d9b5817cb78', 7);

-- Goolmangar Primary School
insert into security_question (id, answer, question, deleted) values
    ('2b25f4ca-d167-4a8f-8071-5ffd177ff29b', '$2y$12$LXlQgMdb5s0oJ6G3xBXRRuCCLMS7pHy8xxKfxhh9Y0KsZjj5mEEea', 'Name of your first school?', false);

insert into admin (id) values ('924c26a6-d96b-4ffb-ab8b-250602c03f75');

insert into agent (address, date_founded, id) values ('Rio Street, Novi Sad, Serbia', '2020-07-20T06:30:00', '602399f4-183a-4174-95ea-1b42940fa0a9');

insert into simple_user (address, confirmation_time, ssn, user_status, id, security_question_id) values
    ('Townsville', '2020-07-20T06:30:00', '121206780062', 'APPROVED', 'f1aed848-878f-4d4a-b198-e707b5dd220c', '2b25f4ca-d167-4a8f-8071-5ffd177ff29b');