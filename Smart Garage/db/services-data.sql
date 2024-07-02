insert into smart_garage.service_categories (category_id, category_name)
values  (1, 'Mechanical Services'),
        (2, 'Electrical Services'),
        (3, 'Body and Paint Services'),
        (4, 'Tire Services');

insert into smart_garage.services (service_id, service, price, currency, available, category_id)
values  (1, 'Oil Change', 60, 'BGN', 1, 1),
        (2, 'Brake Pad Replacement', 250, 'BGN', 1, 1),
        (3, 'Wheel Alignment', 100, 'BGN', 1, 1),
        (4, 'Spark Plug Replacement', 100, 'BGN', 1, 1),
        (5, 'Suspension Repair', 600, 'BGN', 1, 1),
        (6, 'Transmission Repair', 2000, 'BGN', 1, 1),
        (7, 'Battery Replacement', 200, 'BGN', 1, 2),
        (8, 'Alternator Replacement', 600, 'BGN', 1, 2),
        (9, 'Starter Replacement', 500, 'BGN', 1, 2),
        (10, 'Fuse Replacement', 40, 'BGN', 1, 2),
        (11, 'Dent Repair', 300, 'BGN', 1, 3),
        (12, 'Scratch Repair', 200, 'BGN', 1, 3),
        (13, 'Paint Job', 3000, 'BGN', 1, 3),
        (14, 'Windshield Replacement', 500, 'BGN', 1, 3),
        (17, 'Tire Rotation', 60, 'BGN', 1, 4),
        (18, 'Tire Balancing', 30, 'BGN', 1, 4),
        (19, 'Tire Mounting', 20, 'BGN', 1, 4),
        (20, 'Tire Repair', 50, 'BGN', 1, 4);