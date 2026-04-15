CREATE TABLE IF NOT EXISTS "Usuario" (
  "id" int PRIMARY KEY,
  "nome" varchar NOT NULL,
  "senha" varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS  "Categoria" (
  "id" int PRIMARY KEY,
  "descricao" varchar NOT NULL,
  "meta" numeric(19),
  "ativo" boolean NOT NULL,
  "usuario_id" int NOT NULL
);

CREATE TABLE IF NOT EXISTS  "FormaPagamento" (
  "id" int PRIMARY KEY,
  "descricao" varchar NOT NULL,
  "ativo" boolean NOT NULL,
  "usuario_id" int NOT NULL
);

CREATE TABLE IF NOT EXISTS  "Lancamento" (
  "id" int PRIMARY KEY,
  "descricao" varchar NOT NULL,
  "valorTotal" numeric(19) NOT NULL,
  "data" date NOT NULL,
  "tipo" varchar NOT NULL,
  "recorrencia" varchar NOT NULL,
  "usuario_id" int NOT NULL,
  "categoria_id" int NOT NULL,
  "forma_pagamento_id" int NOT NULL
);

CREATE TABLE IF NOT EXISTS  "Parcela" (
  "id" int PRIMARY KEY,
  "numParcela" int NOT NULL,
  "valor" numeric(19) NOT NULL,
  "dataVencimento" date NOT NULL,
  "lancamento_id" int NOT NULL
);

CREATE TABLE IF NOT EXISTS  "Alerta" (
  "id" int PRIMARY KEY,
  "diasAntes" int NOT NULL,
  "ativo" boolean NOT NULL,
  "email" varchar NOT NULL,
  "ultimaExecucao" date,
  "usuario_id" int NOT NULL,
  "lancamento_id" int NOT NULL
);

ALTER TABLE "Categoria" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id");

ALTER TABLE "FormaPagamento" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id");

ALTER TABLE "Lancamento" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id");

ALTER TABLE "Lancamento" ADD FOREIGN KEY ("categoria_id") REFERENCES "Categoria" ("id");

ALTER TABLE "Lancamento" ADD FOREIGN KEY ("forma_pagamento_id") REFERENCES "FormaPagamento" ("id");

ALTER TABLE "Parcela" ADD FOREIGN KEY ("lancamento_id") REFERENCES "Lancamento" ("id");

ALTER TABLE "Alerta" ADD FOREIGN KEY ("usuario_id") REFERENCES "Usuario" ("id");

ALTER TABLE "Alerta" ADD FOREIGN KEY ("lancamento_id") REFERENCES "Lancamento" ("id");
