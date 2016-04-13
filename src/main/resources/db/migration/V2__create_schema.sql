CREATE TABLE "trolls"(
    "id" BIGINT NOT NULL,
    "name" VARCHAR NOT NULL,
    "equipment" VARCHAR,
    PRIMARY KEY("id")
);
CREATE UNIQUE INDEX "trolls_name" ON "trolls"("name");
