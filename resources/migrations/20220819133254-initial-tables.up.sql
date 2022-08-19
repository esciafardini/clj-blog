-- here we are now now

CREATE TABLE guestbook
(id SERIAL PRIMARY KEY,
  name VARCHAR(30),
  message VARCHAR(200),
  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
--;;

CREATE TABLE blog_posts
(id SERIAL PRIMARY KEY UNIQUE,
 title VARCHAR(500) UNIQUE,
 namespace VARCHAR (255) UNIQUE,
 date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 component_function VARCHAR(200) UNIQUE);
--;;

CREATE TABLE tags
(id SERIAL PRIMARY KEY UNIQUE,
 title VARCHAR(500) UNIQUE,
 date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 color VARCHAR(200));
--;;

-- tag table and tag_blog_post join table
CREATE TABLE tags_blog_posts
(tag_id int NOT NULL,
 blog_post_id int NOT NULL,
 -- `composite key` like a primary key for many-to-many:
 PRIMARY KEY (tag_id, blog_post_id),
 FOREIGN KEY (tag_id) REFERENCES tags(id) ON UPDATE CASCADE,
 FOREIGN KEY (blog_post_id) REFERENCES blog_posts(id) ON UPDATE CASCADE);
--;;

INSERT INTO blog_posts
 (title, date_created, component_function)
VALUES
 ('Hello, Cruel World!', '2022-08-19', 'blog_components_01/first-entry');
--;;

INSERT INTO tags
 (title, color)
VALUES
 ('Clojure', 'piss-green'),
 ('Web Development', 'piss-yellow');
--;;

--if this works I will urinate in my pants
WITH clojure_tag_id AS (SELECT id FROM tags WHERE title = 'Clojure'),
     web_dev_tag_id AS (SELECT id FROM tags WHERE title = 'Web Development'),
     blog_post_id AS (SELECT id FROM blog_posts WHERE title = 'Hello, Cruel World!')
INSERT INTO tags_blog_posts
 (tag_id, blog_post_id)
VALUES
 ((SELECT id from clojure_tag_id), (SELECT id from blog_post_id)),
 ((SELECT id from web_dev_tag_id), (SELECT id from blog_post_id));
--;;
--IT WORKED: COMMENCE URINATION MY FRIEND!
