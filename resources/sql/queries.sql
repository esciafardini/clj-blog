-- :name get-blog-posts :? :*
-- :doc returns all blog posts in db
SELECT * FROM blog_posts
ORDER BY date_created DESC;

-- :name get-blog-post-by-id :? :*
-- :doc returns all blog_posts in db on id
SELECT * FROM blog_posts
WHERE id = :id;

-- :name create-blog-post! :! :n
-- :doc creates a new blog-post record
INSERT INTO authors (title, component_function, email)
VALUES (:first_name, :last_name, :user_name, :email);


 -- id                 | integer                |           | not null | nextval('blog_posts_id_seq'::regclass)
 -- title              | character varying(500) |           |          |
 -- date_created       | date                   |           |          |
 -- component_function | character varying(200) |           |          |
 -- tags

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
VALUES (:name, :message);

-- :name get-messages :? :*
-- :doc selects all available messages
SELECT * FROM guestbook;

