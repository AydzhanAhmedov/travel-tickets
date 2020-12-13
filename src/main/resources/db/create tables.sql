DROP TABLE IF EXISTS notifications_recipients;
CREATE TABLE notifications_recipients(
	notification_id int8 NOT NULL,
	recipient_id int8 NOT NULL,
	notification_status_id int4 NOT NULL
);

DROP TABLE IF EXISTS notifications;
CREATE TABLE notifications(
	id bigserial NOT NULL PRIMARY KEY,
	type_id int4 NOT NULL,
	message varchar(200) NOT NULL,
	created_at timestamp NOT NULL,
	created_by int8 NOT NULL
);

DROP TABLE IF EXISTS notification_types;
CREATE TABLE notification_types(
	id serial NOT NULL PRIMARY KEY,
	name varchar(30) NOT NULL UNIQUE 
);

DROP TABLE IF EXISTS notification_statuses;
CREATE TABLE notification_statuses(
	id serial NOT NULL PRIMARY KEY,
	name varchar(30) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS travel_distributor_requests;
CREATE TABLE travel_distributor_requests(
	travel_id int8 NOT NULL,
	distributor_id int8 NOT NULL,
	request_status_id int4 NOT NULL
);

DROP TABLE IF EXISTS request_statuses;
	CREATE TABLE request_statuses(
	id serial NOT NULL PRIMARY KEY,
	name varchar(20) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS tickets;
CREATE TABLE tickets(
	id bigserial NOT NULL PRIMARY KEY,
	travel_id int8 NOT NULL,
	buyer_name varchar(30) NOT NULL,
	buyer_phone varchar(20) NOT NULL,
	buyer_email varchar(50),
	created_by int8 NOT NULL,
	created_at timestamp NOT null
);

DROP TABLE IF EXISTS travels_routes;
CREATE TABLE travels_routes(
	travel_id int8 NOT NULL,
	transport_type_id int4 NOT NULL,
	city_id int8 NOT NULL,
	arrival_date timestamp NOT NULL
);

DROP TABLE IF EXISTS transport_types;
CREATE TABLE transport_types(
	id serial NOT NULL PRIMARY KEY,
	name varchar(30) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS travels;
CREATE TABLE travels(
	id bigserial NOT NULL PRIMARY KEY,
	name varchar(100) NOT NULL,
	travel_type_id int4 NOT NULL,
	travel_status_id int4 NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL CHECK (end_date >= start_date),
	ticket_quantity int2 NOT NULL CHECK (ticket_quantity > 0),
	current_ticket_quantity int2 NOT NULL CHECK (current_ticket_quantity >= 0 AND current_ticket_quantity <= ticket_quantity),
	ticket_price numeric(15,2) NOT NULL,
	ticket_buy_limit int2 NOT NULL,
	details varchar(500),
	created_at timestamp NOT NULL,
	created_by int8 NOT NULL
);

DROP TABLE IF EXISTS companies;
CREATE TABLE companies(
	client_id int8 NOT NULL PRIMARY KEY,
	logo_url varchar(200) NOT NULL,
	description varchar(500)
);

DROP TABLE IF EXISTS distributors;
CREATE TABLE distributors(
	client_id int8 NOT NULL PRIMARY KEY
);

DROP TABLE IF EXISTS cashiers;
CREATE TABLE cashiers(
	client_id int8 NOT NULL PRIMARY KEY,
	created_by int8 NOT NULL,
	created_at timestamp NOT NULL,
	honorarium numeric(15,2) NOT NULL
);

DROP TABLE IF EXISTS clients;
CREATE TABLE clients(
user_id int8 NOT NULL PRIMARY KEY,
client_type_id int4 NOT NULL,
name varchar(30) NOT NULL,
phone varchar(20) NOT NULL,
address_id int8 NOT NULL
);

DROP TABLE IF EXISTS client_types;
CREATE TABLE client_types(
id serial NOT NULL PRIMARY KEY,
name varchar(20) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS users;
CREATE TABLE users(
id bigserial NOT NULL PRIMARY KEY,
email varchar(30) NOT NULL UNIQUE,
username varchar(30) UNIQUE NOT NULL,
password varchar(100) NOT NULL,
role_id int4 NOT NULL
);

DROP TABLE IF EXISTS roles;
CREATE TABLE roles(
id serial NOT NULL PRIMARY KEY,
name varchar(20) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS travel_statuses;
CREATE TABLE travel_statuses(
	id serial NOT NULL PRIMARY KEY,
	name varchar(15) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS travel_types;
CREATE TABLE travel_types(
	id serial NOT NULL PRIMARY KEY,
	name varchar(15) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS addresses;
CREATE TABLE addresses(
	id bigserial NOT NULL PRIMARY KEY,
	city_id int8 NOT NULL,
	address varchar(50) NOT NULL
);

DROP TABLE IF EXISTS cities;
CREATE TABLE cities(
	id bigserial NOT NULL PRIMARY KEY,
	name varchar(50) NOT NULL UNIQUE
);

ALTER TABLE notifications_recipients
	ADD CONSTRAINT fk_notifications_recipients_notification_id FOREIGN KEY
(notification_id) REFERENCES notifications(id),
	ADD CONSTRAINT fk_notifications_recipients_recipient_id FOREIGN KEY
(recipient_id) REFERENCES users(id),
	ADD CONSTRAINT fk_notifications_recipients_notification_status_id FOREIGN KEY
(notification_status_id) REFERENCES notification_statuses(id);

ALTER TABLE notifications
	ADD CONSTRAINT fk_notifications_type_id FOREIGN KEY
(type_id) REFERENCES notification_types(id),
	ADD CONSTRAINT fk_notifications_created_by FOREIGN KEY
(created_by) REFERENCES users(id);

ALTER TABLE travel_distributor_requests
	ADD CONSTRAINT fk_travel_distributor_requests_travel_id FOREIGN KEY 
(travel_id) REFERENCES travels(id),
	ADD CONSTRAINT fk_travel_distributor_requests_distributor_id FOREIGN KEY
(distributor_id) REFERENCES users(id),
	ADD CONSTRAINT fk_travel_distributor_requests_request_status_id FOREIGN KEY
(request_status_id) REFERENCES request_statuses(id);

ALTER TABLE tickets
	ADD CONSTRAINT fk_tickets_travel_id FOREIGN KEY
(travel_id) REFERENCES travels(id),
	ADD CONSTRAINT fk_tickets_created_by FOREIGN KEY
(created_by) REFERENCES users(id);

ALTER TABLE travels_routes
	ADD CONSTRAINT fk_travels_routes_travel_id FOREIGN KEY
(travel_id) REFERENCES travels(id),
	ADD CONSTRAINT fk_travels_routes_transport_type_id FOREIGN KEY
(transport_type_id) REFERENCES transport_types(id),
	ADD CONSTRAINT fk_travels_routes_city_id FOREIGN KEY
(city_id) REFERENCES cities(id);

ALTER TABLE travels 
	ADD CONSTRAINT fk_travels_travel_type_id FOREIGN KEY 
(travel_type_id) REFERENCES travel_types(id),
	ADD CONSTRAINT fk_travels_travel_status_id FOREIGN KEY 
(travel_status_id) REFERENCES travel_statuses(id),
	ADD CONSTRAINT fk_travels_created_by_id FOREIGN KEY 
(created_by) REFERENCES users(id);

ALTER TABLE companies 
	ADD CONSTRAINT fk_companies_client_id FOREIGN KEY
(client_id) REFERENCES clients(user_id);

ALTER TABLE distributors 
	ADD CONSTRAINT fk_distributors_client_id FOREIGN KEY
(client_id) REFERENCES clients(user_id);

ALTER TABLE cashiers 
	ADD CONSTRAINT fk_cashiers_client_id FOREIGN KEY
(client_id) REFERENCES clients(user_id),
	ADD CONSTRAINT fk_cashiers_created_by FOREIGN KEY
(created_by) REFERENCES users(id);

ALTER TABLE clients 
	ADD CONSTRAINT fk_clients_user_id FOREIGN KEY
(user_id) REFERENCES users(id),
	ADD CONSTRAINT fk_clients_client_type_id FOREIGN KEY
(client_type_id) REFERENCES client_types(id),
	ADD CONSTRAINT fk_clients_address_id FOREIGN KEY
(address_id) REFERENCES addresses(id);

ALTER TABLE users
	ADD CONSTRAINT fk_users_role_id FOREIGN KEY 
(role_id) REFERENCES roles(id);

ALTER TABLE addresses
	ADD CONSTRAINT fk_addresses_city_id FOREIGN KEY
(city_id) REFERENCES cities(id);