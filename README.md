# clj-blog

generated using Luminus version "3.91"

SEE IT LIVE: <br>
[FPBLOGG Website](https://fpblogg.com)

## Deployment

To deploy latest to heroku:
git push heroku master

To run migrations in heroku:
heroku run java -cp target/uberjar/clj-blog.jar clojure.main -m clj-blog.core migrate

## Migrations

create a new migration:
lein repl
(create-migration "migration-name")

migrate
lein run migrate
or [from lein repl]:
(migrate)

rollback
lein run rollback

## Docker

To build the database container:

    docker-compose -f blog.yaml up -d                                                                                                                                   ~/Projects/clj-blog

To open interactive terminal env for PSQL:

    docker exec -it full-stack-blogdb-1 psql -U postgres

## Running

To start a web server for the application, run:

    lein run

To start build the front end with shadow-cljs, run:

    npx shadow-cljs watch app

To debug routes with swagger:

    http://localhost:3010/api/swagger-ui

## Shadow CLJS Dependency Issues

According to shadow-cljs docs, many libraries don't specify dependencies in a way that is meaningful to shadow-cljs and so the specific versions required by each library used must be installed at command line via "npm install..."

- This requires some hopping around in Github to find which specific versions need to be installed.
- Another shadow issue is that the nrepl port won't die sometimes..... DO THIS - KILL IT:
  kill -9 $(lsof -ti:7772)
