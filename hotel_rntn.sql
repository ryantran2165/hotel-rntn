DROP DATABASE IF EXISTS hotel_rntn;
CREATE DATABASE hotel_rntn;
USE hotel_rntn;

CREATE TABLE account (
  PRIMARY KEY (id),
  id         MEDIUMINT UNSIGNED AUTO_INCREMENT,
  email      VARCHAR(255) NOT NULL,
  password   VARCHAR(32)  NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name  VARCHAR(255) NOT NULL,
  is_admin   BOOLEAN      NOT NULL
);

CREATE TABLE room (
  PRIMARY KEY (id),
  id         SMALLINT UNSIGNED AUTO_INCREMENT,
  room_num   VARCHAR(16)        NOT NULL,
  room_floor TINYINT  UNSIGNED  NOT NULL,
  sqft       SMALLINT UNSIGNED  NOT NULL,
  price      DECIMAL(6, 2)      NOT NULL
);

CREATE TABLE reservation (
  PRIMARY KEY (account_id, room_id, reserve_date),
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
  PRIMARY KEY (account_id, room_id, reserve_date, request),
  account_id   MEDIUMINT UNSIGNED  NOT NULL,
  room_id      SMALLINT  UNSIGNED  NOT NULL,
  reserve_date DATE                NOT NULL,
               FOREIGN KEY (account_id, room_id, reserve_date)
               REFERENCES reservation(account_id, room_id, reserve_date)
               ON DELETE CASCADE,
  request      VARCHAR(255)        NOT NULL
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
  PRIMARY KEY (account_id, room_id, reserve_date, updated_at),
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
          (SELECT account_id, room_id, reserve_date, updated_at
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