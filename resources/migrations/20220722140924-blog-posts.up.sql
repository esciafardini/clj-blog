CREATE TABLE blog_posts
(id SERIAL PRIMARY KEY UNIQUE,
 title VARCHAR(500) UNIQUE,
 date_created DATE,
 component_function VARCHAR(200) UNIQUE,
 tags text[]);
