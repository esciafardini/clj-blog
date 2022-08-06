# clj-blog

generated using Luminus version "3.91"

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
    !!! ATTENTION: you must have the app open in browser for cljs-repl to work!!!!
