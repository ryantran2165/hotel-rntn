DROP DATABASE IF EXISTS hotel_rntn;
CREATE DATABASE hotel_rntn;
USE hotel_rntn;

CREATE TABLE account (
  PRIMARY KEY (id),
  id         MEDIUMINT UNSIGNED AUTO_INCREMENT,
  email      VARCHAR(255) NOT NULL UNIQUE CHECK (email <> ''),
  password   VARCHAR(32)  NOT NULL        CHECK (password <> ''),
  first_name VARCHAR(255) NOT NULL        CHECK (first_name <> ''),
  last_name  VARCHAR(255) NOT NULL        CHECK (last_name <> ''),
  is_admin   BOOLEAN      NOT NULL
);

CREATE TABLE room (
  PRIMARY KEY (id),
  id         SMALLINT UNSIGNED AUTO_INCREMENT,
  room_num   VARCHAR(16)        NOT NULL UNIQUE CHECK (room_num <> ''),
  room_floor TINYINT  UNSIGNED  NOT NULL,
  sqft       SMALLINT UNSIGNED  NOT NULL,
  price      DECIMAL(6, 2)      NOT NULL
);

CREATE TABLE reservation (
  PRIMARY KEY (room_id, reserve_date),
  account_id   MEDIUMINT UNSIGNED  NOT NULL,
               FOREIGN KEY (account_id)
               REFERENCES account(id)
               ON DELETE CASCADE,
  room_id      SMALLINT  UNSIGNED  NOT NULL,
               FOREIGN KEY (room_id)
               REFERENCES room(id)
               ON DELETE CASCADE,
  reserve_date DATE                NOT NULL,
  updated_at   TIMESTAMP           NOT NULL
               DEFAULT   CURRENT_TIMESTAMP
               ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE reservation_request (
  PRIMARY KEY (room_id, reserve_date, request),
  room_id      SMALLINT  UNSIGNED  NOT NULL,
  reserve_date DATE                NOT NULL,
               FOREIGN KEY (room_id, reserve_date)
               REFERENCES reservation(room_id, reserve_date)
               ON DELETE CASCADE,
  request      VARCHAR(255)        NOT NULL CHECK (request <> '')
);

CREATE TABLE canceled_reservation (
  PRIMARY KEY (account_id, room_id, reserve_date, cancel_date),
  account_id   MEDIUMINT UNSIGNED  NOT NULL,
               FOREIGN KEY (account_id)
               REFERENCES account(id)
               ON DELETE CASCADE,
  room_id      SMALLINT  UNSIGNED  NOT NULL,
               FOREIGN KEY (room_id)
               REFERENCES room(id)
               ON DELETE CASCADE,
  reserve_date DATE                NOT NULL,
  cancel_date  DATE                NOT NULL
);

CREATE TABLE reservation_archive (
  PRIMARY KEY (room_id, reserve_date),
  account_id   MEDIUMINT UNSIGNED  NOT NULL,
               FOREIGN KEY (account_id)
               REFERENCES account(id)
               ON DELETE CASCADE,
  room_id      SMALLINT  UNSIGNED  NOT NULL,
               FOREIGN KEY (room_id)
               REFERENCES room(id)
               ON DELETE CASCADE,
  reserve_date DATE                NOT NULL,
  updated_at   TIMESTAMP           NOT NULL
               DEFAULT   CURRENT_TIMESTAMP
               ON UPDATE CURRENT_TIMESTAMP
);

DELIMITER //
CREATE PROCEDURE proc_archive_reservation(IN cutoff DATE)
 BEGIN
   INSERT INTO reservation_archive
          (SELECT *
             FROM reservation
            WHERE DATE(updated_at) < cutoff);
   DELETE FROM reservation
    WHERE DATE(updated_at) < cutoff;
 END;//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trig_reservation_delete
 AFTER DELETE ON reservation
   FOR EACH ROW
 BEGIN
   IF (NOT EXISTS (SELECT *
                     FROM canceled_reservation
                    WHERE account_id = OLD.account_id
                      AND room_id = OLD.room_id
                      AND reserve_date = OLD.reserve_date)
      ) THEN
     INSERT INTO canceled_reservation (account_id, room_id, reserve_date, cancel_date)
     VALUES (OLD.account_id, OLD.room_id, OLD.reserve_date, CURRENT_DATE);
   END IF;
 END;//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trig_reservation_insert
BEFORE INSERT ON reservation
   FOR EACH ROW
 BEGIN
   IF (NEW.reserve_date < CURRENT_DATE) THEN
     SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'reservation date before current date';
   END IF;
 END;//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trig_reservation_update
BEFORE UPDATE ON reservation
   FOR EACH ROW
 BEGIN
   IF (NEW.reserve_date < CURRENT_DATE) THEN
     SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'reservation date before current date';
   END IF;
 END;//
DELIMITER ;

INSERT INTO account (email, password, first_name, last_name, is_admin)
VALUES ('admin@gmail.com', 'pass', 'Suneuy', 'Kim', TRUE),
       ('john@gmail.com', 'pass', 'John', 'Doe', FALSE),
       ('jane@gmail.com', 'pass', 'Jane', 'Doe', FALSE),
       ('joe@gmail.com', 'pass', 'Joe', 'Doe', FALSE);

INSERT INTO room (room_num, room_floor, sqft, price)
VALUES ('1a', 1, 300, 49.99),
       ('1b', 1, 300, 49.99),
       ('1c', 1, 300, 49.99),
       ('2a', 2, 400, 59.99),
       ('2b', 2, 400, 59.99),
       ('2c', 2, 400, 59.99),
       ('3a', 3, 500, 69.99),
       ('3b', 3, 500, 69.99),
       ('3c', 3, 500, 69.99),
       ('4a', 4, 1000, 99.99);

INSERT INTO reservation (account_id, room_id, reserve_date)
VALUES (2, 1, DATE('2021-01-01')),
       (2, 4, DATE('2021-02-15')),
       (3, 10, DATE('2021-10-30')),
       (4, 10, DATE('2021-01-01')),
       (4, 10, DATE('2021-01-02')),
       (4, 10, DATE('2021-01-03')),
       (4, 10, DATE('2021-01-04'));

INSERT INTO reservation_request (room_id, reserve_date, request)
VALUES (1, DATE('2021-01-01'), 'Please give extra towels');

INSERT INTO canceled_reservation (account_id, room_id, reserve_date, cancel_date)
VALUES (2, 5, DATE('2021-06-10'), DATE('2020-11-30')),
       (2, 5, DATE('2021-06-11'), DATE('2020-11-30')),
       (2, 5, DATE('2021-06-12'), DATE('2020-11-30'));
