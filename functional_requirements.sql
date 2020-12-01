/*
This is not an executable SQL file.
It contains the SQL statements for each functional requirement.
*/

-- 1. [Guest] sign up
INSERT INTO account (email, password, first_name, last_name, is_admin)
VALUES (?, ?, ?, ?, FALSE);

-- 2. [Guest] sign in
SELECT id, first_name, last_name
  FROM account
 WHERE email = ?,
   AND password = ?
   AND is_admin = FALSE;

-- 3. [Guest/Manager] view all rooms
SELECT id, room_num, room_floor, sqft, price
  FROM room;

-- 4. [Guest] view rooms by price
SELECT id, room_num, room_floor, sqft, price
  FROM room
 WHERE price >= ?
   AND price <= ?;

-- 5. [Guest] view rooms by sqft
SELECT id, room_num, room_floor, sqft, price
  FROM room
 WHERE sqft >= ?
   AND sqft <= ?;

-- 6. [Guest] view rooms by floor
SELECT id, room_num, room_floor, sqft, price
  FROM room
 WHERE room_floor >= ?
   AND room_floor <= ?;

-- 7. [Guest] view reservations
SELECT room_id, room_num, room_floor, sqft, price, reserve_date
  FROM reservation
       INNER JOIN room
       ON room_id = room.id
 WHERE account_id = ?;

-- 8. [Guest] create reservation
INSERT INTO reservation (account_id, room_id, reserve_date)
VALUES (?, ?, ?);

-- 9. [Guest] cancel reservation
DELETE FROM reservation
 WHERE account_id = ?
   AND room_id = ?
   AND reserve_date = ?;

-- 10. [Guest] update reservation
UPDATE reservation
   SET room_id = ?,
       reserve_date = ?
 WHERE account_id = ?
   AND room_id = ?
   AND reserve_date = ?;

-- 11. [Guest] view reservation requests
SELECT room_id, reserve_date, request
  FROM reservation_request
       NATURAL JOIN reservation
 WHERE account_id = ?;

-- 12. [Guest] create reservation request (only allow if requested by same guest)
SELECT *
  FROM reservation
 WHERE account_id = ?
   AND room_id = ?
   AND reserve_date = ?;
INSERT INTO reservation_request (room_id, reserve_date, request)
VALUES (?, ?, ?);

-- 13. [Guest] cancel reservation request (only allow if requested by same guest)
SELECT *
  FROM reservation
 WHERE account_id = ?
   AND room_id = ?
   AND reserve_date = ?;
DELETE FROM reservation_request
 WHERE room_id = ?
   AND reserve_date = ?
   AND request = ?;

-- 14. [Manager] sign in
SELECT id, first_name, last_name
  FROM account
 WHERE email = ?,
   AND password = ?
   AND is_admin = TRUE;

-- 15. [Manager] view all guests
SELECT id, first_name, last_name
  FROM account
 WHERE is_admin = FALSE;

-- 16. [Manager] view recurring guests (2 or more reservations)
SELECT id, first_name, last_name
  FROM account
 WHERE (SELECT COUNT(*)
          FROM reservation
         WHERE account_id = id) >= 2;

-- 17. [Manager] delete guest
DELETE FROM account
 WHERE id = ?
   AND is_admin = FALSE;

-- 18. [Manager] view reserved rooms
SELECT id, room_num, room_floor, sqft, price
  FROM room
 WHERE id IN
       (SELECT room_id
          FROM reservation);

-- 19. [Manager] view unpopular rooms (0 reservations)
SELECT id, room_num, room_floor, sqft, price
  FROM room
       LEFT OUTER JOIN reservation
       ON id = room_id
 WHERE reserve_date IS NULL;

-- 20. [Manager] create room
INSERT INTO room (room_num, room_floor, sqft, price)
VALUES (?, ?, ?, ?);

-- 21. [Manager] delete room
DELETE FROM room
 WHERE id = ?;

-- 22. [Manager] view all reservations
SELECT account_id, first_name, last_name, room_id, room_num, reserve_date, updated_at
  FROM reservation
       INNER JOIN account
       ON account_id = account.id
       INNER JOIN room
       ON room_id = room.id;

-- 23. [Manager] view number of reservations by date
SELECT COUNT(*)
  FROM reservation
 WHERE reserve_date = ?;

-- 24. [Manager] view number of reservations by room
SELECT COUNT(*)
  FROM reservation
 WHERE room_id = ?;

-- 25. [Manager] view canceled reservations
SELECT account_id, first_name, last_name, room_id, room_num, reserve_date, cancel_date
  FROM canceled_reservation
       INNER JOIN account
       ON account_id = account.id
       INNER JOIN room
       ON room_id = room.id
 WHERE (room_id, reserve_date) NOT IN
       (SELECT room_id, reserve_date
          FROM reservation_archive);

-- 26. [Manager] cancel reservation
DELETE FROM reservation
 WHERE room_id = ?
   AND reserve_date = ?;

-- 27. [Manager] view popular months (5 or more reservations)
SELECT MONTH(reserve_date)
  FROM reservation
 GROUP BY MONTH(reserve_date)
HAVING COUNT(*) >= 5;

-- 28. [Manager] view high-activity months (3 or more cancelations or reservations)
(SELECT MONTH(reserve_date)
   FROM canceled_reservation
  GROUP BY MONTH(reserve_date)
 HAVING COUNT(*) >= 3)
UNION
(SELECT MONTH(reserve_date)
   FROM reservation
  GROUP BY MONTH(reserve_date)
 HAVING COUNT(*) >= 3);

-- 29. [Manager] archive reservations
{CALL proc_archive_reservation(?)}

-- 30. [Manager] view reservation archive
SELECT account_id, first_name, last_name, room_id, room_num, reserve_date, updated_at
  FROM reservation_archive
       INNER JOIN account
       ON account_id = account.id
       INNER JOIN room
       ON room_id = room.id;
