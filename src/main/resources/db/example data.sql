
TRUNCATE cities CASCADE;
TRUNCATE addresses CASCADE;
TRUNCATE users CASCADE;
TRUNCATE clients CASCADE;
TRUNCATE companies CASCADE;
TRUNCATE distributors CASCADE;
TRUNCATE travels CASCADE;

--cities 
INSERT INTO cities
("name")
VALUES ('Варна')
,('Разград')
,('София')
,('Шумен')
,('Белослав')
,('Бургас')
,('Перник')
,('Плевен')
,('Видин')
,('Благоевград')
,('Русе');

--addresses
ALTER SEQUENCE addresses_id_seq RESTART;
INSERT INTO addresses
(city_id, address)
VALUES((SELECT id FROM cities WHERE name = 'Разград'), 'ул. Кръчмата 1')
,((SELECT id FROM cities WHERE name = 'София'), 'ул. Народно събрание 1')
,((SELECT id FROM cities WHERE name = 'Шумен'), 'ул. Дядо коледа 3')
,((SELECT id FROM cities WHERE name = 'Белослав'), 'ул. Пиринско 12')
,((SELECT id FROM cities WHERE name = 'Бургас'), 'ул. Малката улица 1')
,((SELECT id FROM cities WHERE name = 'Перник'), 'ул. Бяло вино 1')
,((SELECT id FROM cities WHERE name = 'Плевен'), 'ул. Старата кръчма 62')
,((SELECT id FROM cities WHERE name = 'Видин'), 'ул. Вълна 2')
,((SELECT id FROM cities WHERE name = 'Благоевград'), 'ул. Черно море 3')
,((SELECT id FROM cities WHERE name = 'Русе'), 'ул. Свобода 23');

-- users
INSERT INTO users
(email, username, "password", role_id)
VALUES
 ('admin@admin', 				'admin', 		'$2a$10$rtOMw3lPMlXO2gmPnZF2u.ZYw6.6V/aZTTXGMc9Wv.SX3clVkfQdW', (SELECT id FROM roles WHERE name = 'ADMIN'))
,('company1@companies', 		'company1', 	'$2a$10$E8DiXRt15BiBsToF8xflOeUEprvw2vB11XPmf5bk/bZy713USX6Na', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('company2@companies', 		'company2', 	'$2a$10$w8LdjlK601LpijrJ0KVdxeDv3PkJQTgSzn6UwQ6XvFd4gGZUD.u36', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('company3@companies', 		'company3', 	'$2a$10$QrFojPQNaG5VxI9WsbBcReV2p2NpVae8XWSv7CI69roJWtDJWnBFC', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('distributor1@distributors', 	'distributor1', '$2a$10$KqDH4A66a/SCF6GB4K7rxOplKHG1vGpjA6W9XBEgHigFfuXeXRjN.', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('distributor2@distributors', 	'distributor2',	'$2a$10$JECZZKcAnjWU5aXpzGWfheA3V6Fd4vUGdtnuinTH0n4C4K3KWAEWG', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('distributor3@distributors', 	'distributor3',	'$2a$10$bYgQBkrYDKSR7Qca5/cqYO5e7laxBwoKSuROQd0NwdYIJB8f6HGka', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('cashier1@cashiers', 			'cashier1', 	'$2a$10$a3EIpUefI3lny9ojwpMjo.PwLIs0eKFkCUEVjxF4d1i6gOE2GLiBW', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('cashier2@cashiers', 			'cashier2', 	'$2a$10$1p3cbHdBg1pBhjRMzGIHEOxHKo1iL9XrGd9vGiQfuqdmT7QqPXYsi', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('cashier3@cashiers', 			'cashier3', 	'$2a$10$o/tTbzlPTew5/fcdmD8PauhsYmzL7YK2f3K556uMpx0NPtMx0ZUVK', (SELECT id FROM roles WHERE name = 'CLIENT'))
,('cashier4@cashiers', 			'cashier4', 	'$2a$10$EmsGo0iVYZOFyJ6mKTm2Cevptnxs0VLVpHJ6ONhzYcZlrtRm2NRoi', (SELECT id FROM roles WHERE name = 'CLIENT'));

--clients
INSERT INTO clients
(user_id, client_type_id, "name", phone, address_id)
VALUES
 ((SELECT id FROM users WHERE username = 'company1'), (SELECT id FROM client_types WHERE "name" = 'COMPANY'), 'Company1', '08811012345', 1)
,((SELECT id FROM users WHERE username = 'company2'), (SELECT id FROM client_types WHERE "name" = 'COMPANY'), 'Company2', '08812012345', 2)
,((SELECT id FROM users WHERE username = 'company3'), (SELECT id FROM client_types WHERE "name" = 'COMPANY'), 'Company3', '08813012345', 3)
,((SELECT id FROM users WHERE username = 'distributor1'), (SELECT id FROM client_types WHERE "name" = 'DISTRIBUTOR'), 'Distributor1', '08821012345', 4)
,((SELECT id FROM users WHERE username = 'distributor2'), (SELECT id FROM client_types WHERE "name" = 'DISTRIBUTOR'), 'Distributor2', '08822012345', 5)
,((SELECT id FROM users WHERE username = 'distributor3'), (SELECT id FROM client_types WHERE "name" = 'DISTRIBUTOR'), 'Distributor3', '08823012345', 6)
,((SELECT id FROM users WHERE username = 'cashier1'), (SELECT id FROM client_types WHERE "name" = 'CASHIER'), 'Cashier1', '08831012345', 7)
,((SELECT id FROM users WHERE username = 'cashier2'), (SELECT id FROM client_types WHERE "name" = 'CASHIER'), 'Cashier2', '08832012345', 8)
,((SELECT id FROM users WHERE username = 'cashier3'), (SELECT id FROM client_types WHERE "name" = 'CASHIER'), 'Cashier3', '08833012345', 9)
,((SELECT id FROM users WHERE username = 'cashier4'), (SELECT id FROM client_types WHERE "name" = 'CASHIER'), 'Cashier4', '08834012345', 10);

--companies
INSERT INTO companies
(client_id, logo_url, description)
VALUES
 ((SELECT id FROM users WHERE username = 'company1'), 'https://images-platform.99static.com//FGKbEryfX5ZrEwt-z766KwM-_kI=/262x150:795x683/fit-in/590x590/99designs-contests-attachments/42/42603/attachment_42603907', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi. Donec non lectus at nisi varius iaculis non non magna. Pellentesque nec elit et dui bibendum hendrerit.')
,((SELECT id FROM users WHERE username = 'company2'), 'https://i.pinimg.com/originals/1b/8e/62/1b8e628911c42bb354ff8c60e2cd2f97.jpg', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi. Donec non lectus at nisi varius iaculis non non magna. Pellentesque nec elit et dui bibendum hendrerit.')
,((SELECT id FROM users WHERE username = 'company3'), 'https://i.pinimg.com/736x/91/90/cd/9190cdd45b854dafbe5c0bddbb8e3aec.jpg', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi. Donec non lectus at nisi varius iaculis non non magna. Pellentesque nec elit et dui bibendum hendrerit.');

--distributors
INSERT INTO distributors
(client_id)
VALUES
 ((SELECT id FROM users WHERE username = 'distributor1'))
,((SELECT id FROM users WHERE username = 'distributor2'))
,((SELECT id FROM users WHERE username = 'distributor3'));

--cashiers
INSERT INTO public.cashiers
(client_id, created_by, created_at, honorarium)
VALUES
 ((SELECT id FROM users WHERE username = 'cashier1'), (SELECT id FROM users WHERE username = 'distributor1'), current_timestamp , 600)
,((SELECT id FROM users WHERE username = 'cashier2'), (SELECT id FROM users WHERE username = 'distributor1'), current_timestamp , 800)
,((SELECT id FROM users WHERE username = 'cashier3'), (SELECT id FROM users WHERE username = 'distributor2'), current_timestamp , 700)
,((SELECT id FROM users WHERE username = 'cashier4'), (SELECT id FROM users WHERE username = 'distributor3'), current_timestamp , 650);


--travels
ALTER SEQUENCE travels_id_seq RESTART;

INSERT INTO public.travels
("name", travel_type_id, travel_status_id, start_date, end_date, ticket_quantity, current_ticket_quantity, ticket_price, ticket_buy_limit, details, created_at, created_by)
VALUES
 ('Learn about hitler', (SELECT id FROM travel_types WHERE name = 'EDUCATIONAL'), (SELECT id FROM travel_statuses WHERE name = 'ENDED'), '2020-01-20', '2020-01-25', 30, 30, 15.25, 0, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi.', '2020-01-18', (SELECT id FROM users WHERE username = 'company1'))
,('Sofia landmarks', (SELECT id FROM travel_types WHERE name = 'ADVENTURE'), (SELECT id FROM travel_statuses WHERE name = 'ENDED'), '2020-03-20', '2020-03-25', 30, 30, 15.25, 0, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi.', '2020-03-18', (SELECT id FROM users WHERE username = 'company1'))
,('Sofia lunapark', (SELECT id FROM travel_types WHERE name = 'ADVENTURE'), (SELECT id FROM travel_statuses WHERE name = 'INCOMING'), '2020-03-25', '2020-04-10', 30, 30, 15.25, 0, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi.', '2020-03-18', (SELECT id FROM users WHERE username = 'company2'))
,('Visit razgrad antibiotic', (SELECT id FROM travel_types WHERE name = 'EDUCATIONAL'), (SELECT id FROM travel_statuses WHERE name = 'INCOMING'), '2020-03-25', '2020-04-10', 30, 30, 15.25, 0, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi.', '2020-03-18', (SELECT id FROM users WHERE username = 'company3'))
,('New year business event in Burgas', (SELECT id FROM travel_types WHERE name = 'BUSINESS'), (SELECT id FROM travel_statuses WHERE name = 'INCOMING'), '2020-12-20', '2020-12-25', 30, 30, 15.25, 0, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent ac vulputate nisi.', '2020-12-18', (SELECT id FROM users WHERE username = 'company3'));

INSERT INTO travels_routes
(travel_id, transport_type_id, city_id, arrival_date)
VALUES
 (1, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Плевен'), '2020-01-20')
,(1, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Видин'), '2020-01-21')
,(2, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Русе'), '2020-03-20')
,(3, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Варна'), '2020-03-25')
,(3, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Русе'), '2020-03-25')
,(4, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Бургас'), '2020-03-25')
,(4, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Видин'), '2020-03-25')
,(4, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Варна'), '2020-03-26')
,(5, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Благоевград'), '2020-12-19')
,(5, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Разград'), '2020-12-20')
,(5, (SELECT id FROM transport_types WHERE "name" = 'BUSS'), (SELECT id FROM cities WHERE name = 'Видин'), '2020-12-20');

-- requests
INSERT INTO travel_distributor_requests
(travel_id, distributor_id, request_status_id)
VALUES
 (1, (SELECT id FROM users WHERE username = 'distributor1'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(1, (SELECT id FROM users WHERE username = 'distributor2'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(2, (SELECT id FROM users WHERE username = 'distributor1'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(2, (SELECT id FROM users WHERE username = 'distributor3'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(3, (SELECT id FROM users WHERE username = 'distributor3'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(4, (SELECT id FROM users WHERE username = 'distributor2'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(4, (SELECT id FROM users WHERE username = 'distributor3'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(5, (SELECT id FROM users WHERE username = 'distributor1'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(5, (SELECT id FROM users WHERE username = 'distributor2'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'))
,(5, (SELECT id FROM users WHERE username = 'distributor3'), (SELECT id FROM request_statuses WHERE name = 'APPROVED'));


-- tickets t1
INSERT INTO tickets 
(travel_id, buyer_name, buyer_phone, buyer_email, created_by, created_at)
VALUES 
 (1, 'Magdalena Melendez', '+32 456 411 2205', 'ambersparanoid@yahoo.ca', (SELECT id FROM users WHERE username = 'cashier1'), current_date)
,(1, 'Ammarah Spence', '+32 456 094 1220', 'micksadisti@yahoo.com', (SELECT id FROM users WHERE username = 'cashier1'), current_date)
,(1, 'Genevieve Connelly', '+32 456 763 6092', 'bessiehorny@protonmail.com', (SELECT id FROM users WHERE username = 'cashier1'), current_date)
,(1, 'Mateo Bolton', '+32 456 778 0916', 'nikkihardasarock@icloud.com', (SELECT id FROM users WHERE username = 'cashier1'), current_date)
,(1, 'Aleeza Merrill', '+32 456 144 5444', 'receptiveebbe@icloud.com', (SELECT id FROM users WHERE username = 'cashier1'), current_date)
,(1, 'Abdur Jimenez', '+32 456 382 6343', 'eagerchuck@optonline.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Keelan Richmond', '+32 456 110 8042', 'insultedellie@aol.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Annabelle Haigh', '+32 456 327 0672', 'disillusionedmarc@yahoo.ca', (SELECT id FROM users WHERE username = 'cashier1'), current_date)
,(1, 'Rachel Finnegan', '+32 456 282 2615', 'spawnunderstated@live.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Chelsie Cervantes', '+32 456 944 5269', 'ebuildfalse@aol.commail', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Yasemin Hansen', '+32 456 963 9973', 'recoupuntidy@yahoo.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Alanah Mcdowell', '+32 456 243 4192', 'packcultured@mac.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Jordi Bush', '+32 456 806 1811', 'lynnepartygal/guy@outlook.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Hayley Nelson', '+32 456 286 2164', 'pepebitca@msn.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Korey Yoder', '+32 456 136 0470', 'jackieunpleasant@optonline.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Jeff Mcknight', '+32 456 894 4699', 'viclever@protonmail.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Taylan Cannon', '+32 456 852 2333', 'lonelygayle@msn.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Billy Vu', '+32 456 354 5903', 'disgracedrudy@verizon.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Shabaz Findlay', '+32 456 711 3210', 'dreadingrabbie@icloud.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Ishaq Partridge', '+32 456 588 8016', 'curiouschance@sbcglobal.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(1, 'Greg Barker', '+32 456 011 1918', 'curiouschance@sbcglobal.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date);
UPDATE  travels SET current_ticket_quantity = current_ticket_quantity - 21 WHERE id = 1;

--ticket t2
INSERT INTO tickets 
(travel_id, buyer_name, buyer_phone, buyer_email, created_by, created_at)
VALUES 
 (2, 'Magdalena Melendez', '+32 456 411 2205', 'ambersparanoid@yahoo.ca', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Rochelle Malone', '+49 30 578941737', 'benniesuperficial@aol.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Joel Wu', '+297 58 462 7007', 'christiestalker@sbcglobal.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Keane Gomez', '+297 58 647 5988', 'jounpleasant@optonline.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Cillian Swanson', '+297 58 052 4263', 'scornfullafe@me.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Noa Odling', '+297 58 445 1271', 'depressedwil@comcast.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Sadiyah Mackie', '+297 58 767 4989', 'anxiousamby@sbcglobal.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Abdullahi Lovell', '+297 58 849 6461', 'rallydeficient@verizon.net', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Olivia Simmons', '+297 58 326 4005', 'encompassfragrant@aol.com', (SELECT id FROM users WHERE username = 'cashier2'), current_date)
,(2, 'Kaitlin Devile', '+297 23 416 1205', 'marinasfragrant@oil.com', (SELECT id FROM users WHERE username = 'cashier4'), current_date)
,(2, 'Smurfy Dimanson', '+297 28 433 4555', 'lacompassant@hsl.com', (SELECT id FROM users WHERE username = 'cashier4'), current_date)
,(2, 'Katty Simmons', '+297 51 422 4005', 'comaprofragrant@aol.com', (SELECT id FROM users WHERE username = 'cashier4'), current_date);
UPDATE  travels SET current_ticket_quantity = current_ticket_quantity - 9 WHERE id = 2;













