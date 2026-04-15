CREATE TABLE "Usuario" (
  "id" long PRIMARY KEY,
  "nome" varchar NOT NULL,
  "senha" varchar NOT NULL
);

CREATE TABLE "Categoria" (
  "id" long PRIMARY KEY,
  "descricao" varchar NOT NULL,
  "meta" double,
  "ativo" boolean NOT NULL,
  "usuario_id" long NOT NULL
);

CREATE TABLE "FormaPagamento" (
  "id" long PRIMARY KEY,
  "descricao" varchar NOT NULL,
  "ativo" boolean NOT NULL,
  "usuario_id" long NOT NULL
);

CREATE TABLE "Lancamento" (
  "id" long PRIMARY KEY,
  "descricao" varchar NOT NULL,
  "valorTotal" double NOT NULL,
  "data" date NOT NULL,
  "tipo" varchar NOT NULL,
  "recorrencia" varchar NOT NULL,
  "usuario_id" long NOT NULL,
  "categoria_id" long NOT NULL,
  "forma_pagamento_id" long NOT NULL
);

CREATE TABLE "Parcela" (
  "id" int PRIMARY KEY,
  "numParcela" int NOT NULL,
  "valor" double NOT NULL,
  "dataVencimento" date NOT NULL,
  "lancamento_id" long NOT NULL
);

CREATE TABLE "Alerta" (
  "id" long PRIMARY KEY,
  "diasAntes" int NOT NULL,
  "ativo" boolean NOT NULL,
  "email" varchar NOT NULL,
  "ultimaExecucao" date,
  "usuario_id" long NOT NULL,
  "lancamento_id" long NOT NULL
);

ALTER TABLE "Categoria" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "FormaPagamento" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Lancamento" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Lancamento" ADD FOREIGN KEY ("categoria_id") REFERENCES "Categoria" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Lancamento" ADD FOREIGN KEY ("forma_pagamento_id") REFERENCES "FormaPagamento" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Parcela" ADD FOREIGN KEY ("lancamento_id") REFERENCES "Lancamento" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Alerta" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id") DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "Alerta" ADD FOREIGN KEY ("lancamento_id") REFERENCES "Lancamento" ("id") DEFERRABLE INITIALLY IMMEDIATE;
