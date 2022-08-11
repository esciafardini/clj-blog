--UP
ALTER TABLE blog_posts DROP COLUMN date_created;
--;;
ALTER TABLE blog_posts ADD date_created DATE,
--;;
INSERT INTO blog_posts
(date_created)
VALUES
'2022-07-22';
--;;
