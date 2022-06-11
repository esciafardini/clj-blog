-- :name get-authors :? :*
-- :doc returns all authors in db
SELECT * FROM authors;

-- :name get-author-by-id :? :1
-- :doc returns all authors in db
SELECT * FROM authors
WHERE id = :id;

-- :name create-author! :! :n
-- :doc creates a new author record
INSERT INTO authors (first_name, last_name, user_name, email)
VALUES (:first_name, :last_name, :user_name, :email);

-- :name update-author-by-id! :! :n
-- :doc updates an existing author record
UPDATE authors
SET first_name = :first_name, last_name = :last_name, user_name = :user_name, email = :email
WHERE id = :id;

-- :name delete-author-by-user-name! :! :n
-- :doc deletes an author
DELETE FROM authors 
WHERE user_name = :user_name;


-- question mark means query, * means variable result


--guestbook shit

-- :name save-message! :! :n
-- :doc creates a new message using the name and keys
INSERT INTO guestbook
(name, message)
VALUES (:name, :message)

-- :name get-messages :? :*
-- :doc selects all available messages
SELECT * FROM guestbook

