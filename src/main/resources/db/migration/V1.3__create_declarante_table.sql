CREATE TABLE declarante
(
    id                       SERIAL PRIMARY KEY,
    tipo_inscricao           INTEGER,
    numero_inscricao         VARCHAR(255),
    classificacao_tributaria VARCHAR(255),
    cert_alias               VARCHAR(255),
    keystore_path            VARCHAR(255),
    keystore_password        VARCHAR(255)
);