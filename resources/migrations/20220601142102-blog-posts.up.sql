CREATE TABLE blog_posts (
  fk_author_id uuid references authors(id),
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(200) UNIQUE,
  component_fn VARCHAR(200) UNIQUE,
  tags jsonb,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
