insert into car_brand (id, name, country, deleted) values
    ('52adb2e9-5f79-4839-bb9b-1b66bdd693c2', 'Audi', 'Germany', 'false'),
    ('c1fc00da-de6c-4a42-89a5-792d462af560', 'BMW', 'Germany', 'false'),
    ('ecfdf192-b39d-49b9-9588-cba7667313a5', 'Volkswagen', 'Germany', 'false'),
    ('c17bad83-8a5f-4153-b7a3-e986fb24ecfb', 'Opel', 'Germany', 'false'),
    ('4fec0ab2-6cc4-42e3-bcfa-1d02eeb06063', 'Peugeot', 'France', 'false'),
    ('baae6743-61c9-4467-87f9-aaf0e0ce4605', 'Citroen', 'France', 'false'),
    ('0c86e027-57c8-403a-89ef-91fb9117d042', 'Ford', 'USA', 'false'),
    ('0b48f7ed-8b8f-418b-beda-0bcd6eafee14', 'Seat', 'Spain', 'false'),
    ('2078cfb3-9f17-4933-9d40-67a9136395b7', 'Toyota', 'Japan', 'false'),
    ('1c2a425e-89d6-43ae-b1a1-4ed2ea2a11dc', 'Zastava', 'Serbia', 'false'),
    ('75a482e5-3ad4-43a7-ac6d-74ebc53f8528', 'Fiat', 'Italy', 'false');

insert into car_model (id, car_class, deleted, name, car_brand_id) values
    ('3f0ac840-66c8-4c23-b0ff-54cf8c5e760b', 'Small city', false, 'A3', '52adb2e9-5f79-4839-bb9b-1b66bdd693c2'),
    ('28cea716-dbe8-410b-ab5a-0f67c63f16f3', 'Full-size sedan', false, 'A6', '52adb2e9-5f79-4839-bb9b-1b66bdd693c2'),
    ('f41fd1fb-2444-4e3e-93b3-95c855df4453', 'Full-size sedan', false, '320', 'c1fc00da-de6c-4a42-89a5-792d462af560'),
    ('9d5bb205-c685-4ed0-a000-c9db919ccdc3', 'Full-size SUV', false, 'X5', 'c1fc00da-de6c-4a42-89a5-792d462af560'),
    ('2bd3953b-a5ff-4b8b-98c9-c00df0921c4c', 'Full-size SUV', false, 'X6', 'c1fc00da-de6c-4a42-89a5-792d462af560'),
    ('7c204c18-b3e2-41d3-9420-7dbf57cd1b4a', 'Hatchback', false, 'Golf', 'ecfdf192-b39d-49b9-9588-cba7667313a5'),
    ('4304edc8-36ea-4dbe-af07-5322ad40558f', 'Mid-size sedan', false, 'Tiguan', 'ecfdf192-b39d-49b9-9588-cba7667313a5'),
    ('3348429c-1df9-4049-95f4-7b08d931e28d', 'Full-size SUV', false, 'Touareg', 'ecfdf192-b39d-49b9-9588-cba7667313a5'),
    ('b1065642-c87b-4f3b-8c9e-eb2d53556ff9', 'Mid-size sedan', false, 'Astra', 'c17bad83-8a5f-4153-b7a3-e986fb24ecfb'),
    ('76738ec4-fe52-421c-8574-41b8536e7f8b', 'Full-size sedan', false, 'Insignia', 'c17bad83-8a5f-4153-b7a3-e986fb24ecfb'),
    ('c2b5a7fd-83bf-4092-b2c3-07550b7a1af1', 'MiniVan', false, 'Mondeo', '0c86e027-57c8-403a-89ef-91fb9117d042'),
    ('e3e63927-846f-4708-bdb8-1bfe6f3e1ef4', 'Full-size SUV', false, 'Kuga', '0c86e027-57c8-403a-89ef-91fb9117d042'),
    ('efc262b6-77c6-4b58-86a3-1ee0998f7bcf', 'Small city', false, 'Focus', '0c86e027-57c8-403a-89ef-91fb9117d042'),
    ('084dca71-801e-4ecf-9caa-dd34d5da87e1', 'Small city', false, 'Escort', '0c86e027-57c8-403a-89ef-91fb9117d042'),
    ('b564a269-a2c5-4730-9edc-affde5e0a40f', 'Hatchback', false, '206', '4fec0ab2-6cc4-42e3-bcfa-1d02eeb06063'),
    ('099f5939-82ca-490f-9937-b316f8c3aa6c', 'Mid-size sedan', false, '307', '4fec0ab2-6cc4-42e3-bcfa-1d02eeb06063'),
    ('d46a69de-c2cc-49d3-b3ac-fdb557249253', 'Full-size sedan', false, '5008', '4fec0ab2-6cc4-42e3-bcfa-1d02eeb06063'),
    ('e2b87db8-4663-4a32-99ae-ee5c4db50c1b', 'Mid-size sedan', false, 'C4', 'baae6743-61c9-4467-87f9-aaf0e0ce4605'),
    ('4ede1d4c-d180-42f4-9e48-d8085c86598f', 'Full-size sedan', false, 'Picasso', 'baae6743-61c9-4467-87f9-aaf0e0ce4605'),
    ('c3ade06b-004f-4541-a104-a9db87d192e6', 'Full-size sedan', false, 'Xsara', 'baae6743-61c9-4467-87f9-aaf0e0ce4605'),
    ('7ce1b45e-20a1-4946-bfe7-db86dbe55df0', 'Small city', false, 'Cactus', 'baae6743-61c9-4467-87f9-aaf0e0ce4605'),
    ('56be27fe-290a-4e6b-b32b-9187cb7e8930', 'Hatchback', false, 'Ibiza', '0b48f7ed-8b8f-418b-beda-0bcd6eafee14'),
    ('340fdab1-bf1a-45dd-aa87-7809fd351bf9', 'Mid-size sedan', false, 'Toledo', '0b48f7ed-8b8f-418b-beda-0bcd6eafee14'),
    ('fe83bff3-d767-4940-9d3d-3a0676cbae6e', 'Small city', false, 'Panda', '0b48f7ed-8b8f-418b-beda-0bcd6eafee14'),
    ('f6d3b30d-1f1a-4bbf-82fd-b86b848bef8e', 'Full-size SUV', false, 'Land Cruiser', '2078cfb3-9f17-4933-9d40-67a9136395b7'),
    ('6b1c96ca-0665-414a-8322-c9ae150e1ef5', 'Full-size sedan', false, 'Corolla', '2078cfb3-9f17-4933-9d40-67a9136395b7'),
    ('dc910e82-a576-43f3-998b-910bdaa999a4', 'Small city', false, 'Yaris', '2078cfb3-9f17-4933-9d40-67a9136395b7'),
    ('b7247a25-e470-49e4-b06b-d9a9fee20c8d', 'Mid-size sedan', false, 'Supra', '2078cfb3-9f17-4933-9d40-67a9136395b7'),
    ('e0553689-7cd3-4ad0-8acf-1c10fa75af12', 'Small city', false, '101', '1c2a425e-89d6-43ae-b1a1-4ed2ea2a11dc'),
    ('54b3a65f-d061-457f-a414-adc144f48a07', 'Mid-size sedan', false, '10', '1c2a425e-89d6-43ae-b1a1-4ed2ea2a11dc'),
    ('a49a8644-8d14-4dff-89a9-eacb30884664', 'Hatchback', false, 'Yugo', '1c2a425e-89d6-43ae-b1a1-4ed2ea2a11dc'),
    ('037d52b8-59da-4113-8f83-abe9c9e98a22', 'Mid-size sedan', false, '1300', '1c2a425e-89d6-43ae-b1a1-4ed2ea2a11dc'),
    ('56e37c1d-6ca2-4d26-814d-211c163c6ff8', 'Mid-size SUV', false, '500L', '75a482e5-3ad4-43a7-ac6d-74ebc53f8528'),
    ('9b6b6271-af04-4c01-9b68-7903a76a57db', 'Small city', false, 'Punto', '75a482e5-3ad4-43a7-ac6d-74ebc53f8528'),
    ('3e041156-4733-4de0-ab7b-e1259c86429f', 'Small city', false, 'Seicento', '75a482e5-3ad4-43a7-ac6d-74ebc53f8528'),
    ('e038b217-be04-4943-b7c9-65115b031ccd', 'Mid-size sedan', false, 'Multipla', '75a482e5-3ad4-43a7-ac6d-74ebc53f8528');

insert into car (id, deleted, fuel_type, gearshift_type, kilometers_traveled, number_of_gears, car_model_id) values
    ('26e25ff4-4131-42ab-8b28-f41e154be7cb', false, 'DIESEL', 'MANUAL', 0, 'SIX', '3f0ac840-66c8-4c23-b0ff-54cf8c5e760b'),
    ('bc41b370-3ac0-42e3-8b86-724b9d674e6d', false, 'DIESEL', 'AUTOMATIC', 0, 'SEVEN', '28cea716-dbe8-410b-ab5a-0f67c63f16f3'),
    ('6c04fec6-4923-401a-baaa-6fe2c6958194', false, 'BENZINE', 'AUTOMATIC', 0, 'SEVEN', 'f41fd1fb-2444-4e3e-93b3-95c855df4453'),
    ('b844a9ec-5a0d-40a3-b7b5-2c3f532473c3', false, 'DIESEL', 'AUTOMATIC', 0, 'SIX', '9d5bb205-c685-4ed0-a000-c9db919ccdc3'),
    ('e889c690-157c-4e1a-b0b0-87df204eeaa4', false, 'DIESEL', 'MANUAL', 0, 'FIVE', '7c204c18-b3e2-41d3-9420-7dbf57cd1b4a'),
    ('0f182f59-76b4-4a4f-a409-208988b7d181', false, 'DIESEL', 'AUTOMATIC', 0, 'EIGHT', '3348429c-1df9-4049-95f4-7b08d931e28d'),
    ('cdc617dc-edc2-41a7-9110-8f83708b832d', false, 'BENZINE', 'MANUAL', 0, 'FIVE', 'b1065642-c87b-4f3b-8c9e-eb2d53556ff9'),
    ('e19727c1-7912-443b-abe7-ae515fd31a9b', false, 'BENZINE', 'AUTOMATIC', 0, 'SIX', '76738ec4-fe52-421c-8574-41b8536e7f8b'),
    ('60a428b0-b79e-43bf-a5d2-6e535f216130', false, 'DIESEL', 'AUTOMATIC', 0, 'EIGHT', 'e3e63927-846f-4708-bdb8-1bfe6f3e1ef4'),
    ('43de9aa1-20a0-43c1-8db0-ffb71b24cf4e', false, 'BENZINE', 'MANUAL', 0, 'FIVE', 'efc262b6-77c6-4b58-86a3-1ee0998f7bcf'),
    ('3e5f2f8e-de92-49ea-8370-101210fe8897', false, 'DIESEL', 'MANUAL', 0, 'FIVE', '099f5939-82ca-490f-9937-b316f8c3aa6c'),
    ('f8a90e76-36e9-4a01-bd2f-f12b97ee3c28', false, 'BENZINE', 'MANUAL', 0, 'SIX', 'e2b87db8-4663-4a32-99ae-ee5c4db50c1b'),
    ('cfa8abd2-2cfa-4bde-9e78-79ab629266ae', false, 'BENZINE', 'MANUAL', 0, 'FIVE', 'c3ade06b-004f-4541-a104-a9db87d192e6'),
    ('013b642b-9f62-482a-9afe-4c0c18dd2a27', false, 'DIESEL', 'AUTOMATIC', 0, 'SIX', '7ce1b45e-20a1-4946-bfe7-db86dbe55df0'),
    ('69cd21ef-94de-4204-ab42-1ff2ac651ff5', false, 'DIESEL', 'AUTOMATIC', 0, 'NINE', 'f6d3b30d-1f1a-4bbf-82fd-b86b848bef8e'),
    ('e99ea5f3-a0eb-4727-a596-52565bb57909', false, 'BENZINE', 'MANUAL', 0, 'FIVE', 'dc910e82-a576-43f3-998b-910bdaa999a4'),
    ('9bf6e260-e8ff-4e1b-9911-e06db4cbe351', false, 'BENZINE', 'MANUAL', 0, 'SIX', 'b7247a25-e470-49e4-b06b-d9a9fee20c8d'),
    ('a42659b7-550c-4fa0-bd43-17a4b18e01d8', false, 'BENZINE', 'MANUAL', 0, 'FIVE', 'e0553689-7cd3-4ad0-8acf-1c10fa75af12'),
    ('72d30e85-a9b1-4d86-863d-80a79399a231', false, 'DIESEL', 'MANUAL', 0, 'FOUR', 'a49a8644-8d14-4dff-89a9-eacb30884664'),
    ('c3ac231f-7ef9-464a-a6d3-f5fa811acddf', false, 'BENZINE', 'AUTOMATIC', 0, 'SEVEN', '56e37c1d-6ca2-4d26-814d-211c163c6ff8'),
    ('86b9ebc6-efab-47b8-b9c6-2234130b28bc', false, 'DIESEL', 'MANUAL', 0, 'FIVE', '3e041156-4733-4de0-ab7b-e1259c86429f'),
    ('ab104974-2e6b-4b7b-881b-f4502ad0a936', false, 'BENZINE', 'MANUAL', 0, 'FIVE', 'e038b217-be04-4943-b7c9-65115b031ccd');

insert into ad (id, agent, available_kilometers_per_rent, creation_date, deleted, limited_distance, seats, car_id) values
    ('310e525c-2b1f-4712-a7e6-4349464cc117', '602399f4-183a-4174-95ea-1b42940fa0a9', 5000, '2020-08-08', false, true, 2, '26e25ff4-4131-42ab-8b28-f41e154be7cb'),
    ('d51bf9ce-d975-46da-aa84-8e8359d6ab16', '602399f4-183a-4174-95ea-1b42940fa0a9', 2000, '2020-10-10', false, true, 0, '6c04fec6-4923-401a-baaa-6fe2c6958194'),
    ('1e998c60-bb94-48cb-a205-bdcbf9ec5e7f', '602399f4-183a-4174-95ea-1b42940fa0a9', 5000, '2020-08-08', false, true, 2, 'e889c690-157c-4e1a-b0b0-87df204eeaa4'),
    ('35d3ebf7-ef12-4118-a02e-e3587ec86de6', '602399f4-183a-4174-95ea-1b42940fa0a9', 7000, '2020-08-07', false, true, 0, '0f182f59-76b4-4a4f-a409-208988b7d181'),
    ('ebd5dda5-8e7e-4543-89ad-c62c061697e7', '602399f4-183a-4174-95ea-1b42940fa0a9', 4500, '2020-08-10', false, true, 0, 'e19727c1-7912-443b-abe7-ae515fd31a9b'),
    ('4e00fbfe-e625-4dfd-a48b-a70e23879b05', '602399f4-183a-4174-95ea-1b42940fa0a9', 3000, '2020-07-28', false, true, 3, '3e5f2f8e-de92-49ea-8370-101210fe8897'),
    ('b84a746f-09f5-4d55-9b0a-746241ec01f9', '602399f4-183a-4174-95ea-1b42940fa0a9', 500, '2020-07-29', false, true, 1, 'cfa8abd2-2cfa-4bde-9e78-79ab629266ae'),
    ('51d3e961-23bb-4ba4-a96f-f844c189ddac', '602399f4-183a-4174-95ea-1b42940fa0a9', 700, '2020-08-07', false, true, 1, '69cd21ef-94de-4204-ab42-1ff2ac651ff5'),
    ('d7c866eb-8a25-4fc1-8286-4bb2ea9ae299', '602399f4-183a-4174-95ea-1b42940fa0a9', 1000, '2020-07-26', false, true, 1, 'e99ea5f3-a0eb-4727-a596-52565bb57909'),
    ('389bc1a1-c537-48e4-b4c6-2796d0641227', '602399f4-183a-4174-95ea-1b42940fa0a9', 2000, '2020-08-09', false, true, 1, 'a42659b7-550c-4fa0-bd43-17a4b18e01d8'),
    ('4ba0d99c-c3f4-4556-954e-bec125542008', '602399f4-183a-4174-95ea-1b42940fa0a9', 1500, '2020-08-10', false, true, 0, '72d30e85-a9b1-4d86-863d-80a79399a231'),
    ('5af9c5be-467f-4bc6-9509-dc59de51d567', '602399f4-183a-4174-95ea-1b42940fa0a9', 800, '2020-08-12', false, true, 2, '86b9ebc6-efab-47b8-b9c6-2234130b28bc'),
    ('2017a5de-4a19-4351-82f6-2188c0e24044', '602399f4-183a-4174-95ea-1b42940fa0a9', 500, '2020-08-12', false, true, 1, '013b642b-9f62-482a-9afe-4c0c18dd2a27'),
    ('42841722-fb45-4360-a8a2-bae5da58c2b1', '602399f4-183a-4174-95ea-1b42940fa0a9', 5000, '2020-08-07', false, true, 2, 'ab104974-2e6b-4b7b-881b-f4502ad0a936'),
    ('b81b3bdb-85b7-4b49-bb23-3a1399f0d0a2', '602399f4-183a-4174-95ea-1b42940fa0a9', 2000, '2020-08-10', false, true, 2, 'c3ac231f-7ef9-464a-a6d3-f5fa811acddf'),
    ('f9549085-c571-465b-ab1f-60002e41793d', '602399f4-183a-4174-95ea-1b42940fa0a9', 3000, '2020-07-21', false, true, 0, 'f8a90e76-36e9-4a01-bd2f-f12b97ee3c28'),
    ('cfbb7add-fb17-4ce1-9639-476bb1eb21ce', '602399f4-183a-4174-95ea-1b42940fa0a9', 1500, '2020-07-18', false, true, 0, '69cd21ef-94de-4204-ab42-1ff2ac651ff5'),
    ('9899c72c-666b-49b8-8e1e-91135e94ba1b', '602399f4-183a-4174-95ea-1b42940fa0a9', 1000, '2020-07-26', false, true, 0, '0f182f59-76b4-4a4f-a409-208988b7d181'),
    ('b4ba7e3f-447a-4444-979b-5521e47d0b8a', '602399f4-183a-4174-95ea-1b42940fa0a9', 1000, '2020-07-10', false, true, 0, 'a42659b7-550c-4fa0-bd43-17a4b18e01d8'),
    ('ec2cbee2-11ee-4a62-acf1-af029efd0c82', '602399f4-183a-4174-95ea-1b42940fa0a9', 2000, '2020-08-02', false, true, 1, 'f8a90e76-36e9-4a01-bd2f-f12b97ee3c28');