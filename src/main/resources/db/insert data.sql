INSERT INTO travel_statuses(name)
	VALUES
	('INCOMING'),
	('OINGOIN'),
	('CANCELLED'),
	('ENDED');
	
INSERT INTO travel_types(name)
	VALUES
	('EDUCATIONAL'),
	('ADVENTURE'),
	('BUSINESS'),
	('MEDICAL');
	
INSERT INTO transport_types(name)
	VALUES 
	('BUSS'),
	('AIRPLANE'),
	('SHIP'),
	('CAR'),
	('TRAIN');

INSERT INTO request_statuses(name)
	VALUES 
	('PENDING'),
	('APPROVED'),
	('REJECTED');

INSERT INTO roles(name)
	VALUES 
	('ADMIN'),
	('CLIENT');

INSERT INTO client_types(name)
	VALUES
	('COMPANY'),
	('DISTRIBUTOR'),
	('CASHIER');

INSERT INTO notification_types(name)
	VALUES 
	('NEW_TRAVEL'),
	('TRAVEL_STATUS_CHANGED');

INSERT INTO notification_statuses(name)
	VALUES
	('SEEN'),
	('NOT_SEEN');
