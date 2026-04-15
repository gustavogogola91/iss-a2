-- Tabela campeonato
CREATE TABLE IF NOT EXISTS tb_campeonato (
    id SERIAL PRIMARY KEY
);

-- Tabela time
CREATE TABLE IF NOT EXISTS tb_time (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

-- Tabela jogador
CREATE TABLE IF NOT EXISTS tb_jogador (
    id SERIAL PRIMARY KEY,
    id_time INT NOT NULL,
    nome VARCHAR(100) NOT NULL,
    CONSTRAINT fk_time_jogador FOREIGN KEY (id_time) REFERENCES tb_time (id)
);

-- Tabela inscrição
CREATE TABLE IF NOT EXISTS tb_inscricao (
    id SERIAL PRIMARY KEY,
    id_campeonato INT NOT NULL,
    id_time INT NOT NULL,
    CONSTRAINT fk_campeonato_inscricao FOREIGN KEY (id_campeonato) REFERENCES tb_campeonato (id),
    CONSTRAINT fk_time_inscricao FOREIGN KEY (id_time) REFERENCES tb_time (id)
);

-- Tabela partida
CREATE TABLE IF NOT EXISTS tb_partida (
    id SERIAL PRIMARY KEY,
    id_campeonato INT NOT NULL,
    id_time_a INT NOT NULL,
    id_time_b INT NOT NULL,
    data_partida DATE NOT NULL,
    resultado VARCHAR(200),
    CONSTRAINT fk_time_a_partida FOREIGN KEY (id_time_a) REFERENCES tb_time (id),
    CONSTRAINT fk_time_b_partida FOREIGN KEY (id_time_b) REFERENCES tb_time (id),
    CONSTRAINT fk_campeonato_partida FOREIGN KEY (id_campeonato) REFERENCES tb_campeonato (id)
);