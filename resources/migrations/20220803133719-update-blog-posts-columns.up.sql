--UP
ALTER TABLE blog_posts ALTER COLUMN date_created SET DEFAULT CURRENT_DATE;
--;;
ALTER TABLE blog_posts ALTER COLUMN date_created SET NOT NULL;
--;;
ALTER TABLE blog_posts ADD namespace VARCHAR (255) UNIQUE;
--;;
UPDATE blog_posts set namespace = 'blog_components_01' where id = 1;
--;;
ALTER TABLE blog_posts ALTER COLUMN namespace SET NOT NULL;
--;;
