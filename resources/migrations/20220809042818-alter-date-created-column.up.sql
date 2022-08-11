--UP
ALTER TABLE blog_posts DROP COLUMN date_created;
--;;
ALTER TABLE blog_posts ADD date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
--;;
UPDATE blog_posts set date_created = NOW() where id = 1;
--;;
