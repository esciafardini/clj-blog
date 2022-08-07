-- DOWN
ALTER TABLE blog_posts DROP COLUMN namespace;
--;;
ALTER TABLE blog_posts ALTER COLUMN date_created DROP NOT NULL;
--;;
ALTER TABLE blog_posts ALTER COLUMN date_created DROP DEFAULT;
--;;
