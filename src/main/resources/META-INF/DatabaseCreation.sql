CREATE TABLE "Users" (
    "id" INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "name" VARCHAR(128) NOT NULL
);

CREATE TABLE "Articles" (
    "id" INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "title" VARCHAR(128) NOT NULL,
    "description" VARCHAR(1024),
    "added_by" INT NOT NULL,
    "date_added" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "file" BLOB NOT NULL,
    FOREIGN KEY ("added_by") REFERENCES "Users" ("id")
);

CREATE TABLE "Revisions" (
    "article_id" INT NOT NULL,
    "id" INT GENERATED ALWAYS AS IDENTITY,
    "user" INT NOT NULL,
    "date_changed" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY ("article_id", "id"),
    FOREIGN KEY ("article_id") REFERENCES "Articles" ("id"),
    FOREIGN KEY ("user") REFERENCES "Users" ("id")
);
