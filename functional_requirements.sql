-- 1. [Guest] create account
INSERT INTO account (email, password, first_name, last_name, is_admin)
VALUES (?, ?, ?, ?, FALSE);

-- 2. [Guest] log in to account
SELECT id, first_name, last_name
  FROM account
 WHERE email = ?,
   AND password = ?
   AND is_admin = FALSE;

-- 3. [Guest] create reservation
INSERT INTO reservation (account_id, room_id, reserve_date)
VALUES (?, ?, ?);

-- 4. [Guest] cancel reservation
DELETE FROM reservation
 WHERE account_id = ?
   AND room_id = ?
   AND reserve_date = ?;

-- 5. [Guest] update reservation
UPDATE reservation
   SET room_id = ?,
       reserve_date = ?
 WHERE account_id = ?
   AND room_id = ?
   AND reserve_date = ?;

-- 6. [Guest] view rooms in price range
SELECT id, room_floor, sqft, price
  FROM room
 WHERE price <= ?
   AND price >= ?;

-- 7. [Guest] view rooms in square footage range
SELECT id, room_floor, sqft, price
  FROM room
 WHERE sqft <= ?
   AND sqft >= ?;

-- 8. [Guest] view rooms in room floor range
SELECT id, room_floor, sqft, price
  FROM room
 WHERE room_floor <= ?
   AND room_floor >= ?;

-- 9. [Manager] log in to account
SELECT id, first_name, last_name
  FROM account
 WHERE email = ?,
   AND password = ?
   AND is_admin = TRUE;

-- 10. [Manager] view number of reservations for date
SELECT COUNT(*)
  FROM reservation
 WHERE reserve_date = ?;

-- 11. [Manager] view number of reservations for room
SELECT COUNT(*)
  FROM reservation
 WHERE room_id = ?;

-- 12. [Manager] view popular months (5 or more reservations)
SELECT MONTH(reserve_date)
  FROM reservation
 GROUP BY MONTH(reserve_date)
HAVING COUNT(*) >= 5;

-- 13. [Manager] view recurring guests (2 or more reservations)
SELECT id, first_name, last_name
  FROM account
 WHERE (SELECT COUNT(*)
          FROM reservation
         WHERE account_id = id) >= 2;

-- 14. [Manager] view high-activity months (3 or more cancelations or reservations)
(SELECT MONTH(reserve_date)
   FROM canceled_reservation
  GROUP BY MONTH(reserve_date)
 HAVING COUNT(*) >= 3)
UNION
(SELECT MONTH(reserve_date)
   FROM reservation
  GROUP BY MONTH(reserve_date)
 HAVING COUNT(*) >= 3);

-- 15. [Manager] view unpopular rooms (0 reservations)
SELECT id, room_num, room_floor, sqft, price
  FROM room
       LEFT OUTER JOIN reservation ON id = room_id
 WHERE reserve_date IS NULL;