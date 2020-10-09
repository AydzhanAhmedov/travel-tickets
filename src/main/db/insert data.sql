INSERT INTO travel_statuses(name)
	VALUES
	('incomming'),
	('ongoing'),
	('cancelled'),
	('ended');
	
INSERT INTO travel_types(name)
	VALUES
	('educational'),
	('adventure'),
	('business'),
	('medical');
	
INSERT INTO transport_types(name)
	VALUES 
	('buss'),
	('airplane'),
	('ship'),
	('car'),
	('train');

INSERT INTO request_statuses(name)
	VALUES 
	('pending'),
	('approved'),
	('rejected');

INSERT INTO roles(name)
	VALUES 
	('admin'),
	('client');

INSERT INTO client_types(name)
	VALUES
	('company'),
	('distributor'),
	('cashier');

INSERT INTO notification_types(name)
	VALUES 
	('new_travel'),
	('travel_status_changed');

INSERT INTO notification_statuses(name)
	VALUES
	('seen'),
	('not_seen');
