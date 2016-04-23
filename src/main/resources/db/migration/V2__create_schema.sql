CREATE TABLE "orders"(
    "id" BIGINT NOT NULL,
    "name" VARCHAR NOT NULL,
    "equipment" VARCHAR,
    PRIMARY KEY("id")
);
CREATE UNIQUE INDEX "orders_name" ON "orders"("name");
