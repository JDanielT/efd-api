ALTER TABLE evento
    ADD COLUMN declarante_id BIGINT;
ALTER TABLE evento
    ADD CONSTRAINT fk_declarante_id FOREIGN KEY (declarante_id) REFERENCES declarante (id);