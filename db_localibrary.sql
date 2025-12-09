-- ===========================================================
-- 📘 Banco de Dados: db_localibrary
-- Versão: Revisada (MVP)
-- Compatível com: MySQL 8.0+
-- ===========================================================

CREATE DATABASE IF NOT EXISTS db_localibrary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_localibrary;

-- ===========================================================
-- 1. Tabela: tbl_admin — Administradores / Moderadores
-- ===========================================================
CREATE TABLE tbl_admin (
    id_admin BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    sobrenome VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    role_admin ENUM('ADMIN','MODERADOR') NOT NULL,
    status ENUM('ATIVO','INATIVO') DEFAULT 'ATIVO' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ===========================================================
-- 2. Tabela: tbl_endereco — Endereços das Bibliotecas
-- ===========================================================
CREATE TABLE tbl_endereco (
    id_endereco BIGINT AUTO_INCREMENT PRIMARY KEY,
    cep VARCHAR(10) NOT NULL,
    logradouro VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    complemento VARCHAR(50),
    bairro VARCHAR(50) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ===========================================================
-- 3. Tabela: tbl_biblioteca — Dados das Bibliotecas
-- ===========================================================
CREATE TABLE tbl_biblioteca (
    id_biblioteca BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_fantasia VARCHAR(100) NOT NULL,
    razao_social VARCHAR(100) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    categoria ENUM('Pública','Privada','Universitária','Escolar') NOT NULL,
    site VARCHAR(100),
    status ENUM('ATIVO','INATIVO','PENDENTE') DEFAULT 'PENDENTE' NOT NULL,
    id_endereco BIGINT NOT NULL UNIQUE,
    foto_biblioteca VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_biblioteca_endereco FOREIGN KEY (id_endereco)
        REFERENCES tbl_endereco(id_endereco)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ===========================================================
-- 4. Tabela: tbl_credenciais_biblioteca — Credenciais de Login
-- ===========================================================
CREATE TABLE tbl_credenciais_biblioteca (
    id_credencial BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_biblioteca BIGINT NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_credenciais_biblioteca FOREIGN KEY (id_biblioteca)
        REFERENCES tbl_biblioteca(id_biblioteca)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ===========================================================
-- 5. Tabela: tbl_livro — Catálogo Global de Livros
-- ===========================================================
CREATE TABLE tbl_livro (
    id_livro BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    editora VARCHAR(100),
    ano_publicacao INT,
    capa VARCHAR(255),
    resumo TEXT,
    foto_autor VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ===========================================================
-- 6. Tabela: tbl_biblioteca_livro — Relacionamento Biblioteca ↔ Livro
-- ===========================================================
CREATE TABLE tbl_biblioteca_livro (
    id_biblioteca BIGINT NOT NULL,
    id_livro BIGINT NOT NULL,
    quantidade INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_biblioteca, id_livro),
    CONSTRAINT fk_bl_biblioteca FOREIGN KEY (id_biblioteca)
        REFERENCES tbl_biblioteca(id_biblioteca)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_bl_livro FOREIGN KEY (id_livro)
        REFERENCES tbl_livro(id_livro)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ===========================================================
-- 7. Tabela: tbl_genero — Gêneros Literários
-- ===========================================================
CREATE TABLE tbl_genero (
    id_genero BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_genero VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ===========================================================
-- 8. Tabela: tbl_livro_genero — Relacionamento Livro ↔ Gênero
-- ===========================================================
CREATE TABLE tbl_livro_genero (
    id_livro BIGINT NOT NULL,
    id_genero BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_livro, id_genero),
    CONSTRAINT fk_lg_livro FOREIGN KEY (id_livro)
        REFERENCES tbl_livro(id_livro)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_lg_genero FOREIGN KEY (id_genero)
        REFERENCES tbl_genero(id_genero)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ===========================================================
-- 9. Tabela: tbl_horario_funcionamento — Horários por Dia
-- ===========================================================
CREATE TABLE tbl_horario_funcionamento (
    id_horario BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_biblioteca BIGINT NOT NULL,
    dia_semana ENUM('DOMINGO','SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO') NOT NULL,
    horario_abertura TIME,
    horario_fechamento TIME,
    fechado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_horario_biblioteca FOREIGN KEY (id_biblioteca)
        REFERENCES tbl_biblioteca(id_biblioteca)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE KEY unique_biblioteca_dia (id_biblioteca, dia_semana)
) ENGINE=InnoDB;

-- ===========================
-- Inserir os gêneros
-- ===========================
INSERT IGNORE INTO tbl_genero (nome_genero) VALUES
('Romance'),
('Conto'),
('Novela (formato literário)'),
('Fábula'),
('Ficção Científica (Sci-fi)'),
('Fantasia'),
('Distopia'),
('Utopia'),
('Terror/Horror'),
('Suspense/Thriller'),
('Mistério/Policial'),
('Romance Policial Noir'),
('Aventura'),
('Drama'),
('Humor/Comédia'),
('Ficção Histórica'),
('Realismo Mágico'),
('Cyberpunk'),
('Steampunk'),
('LitRPG (ficção baseada em jogos)'),
('Fantasia Urbana'),
('Space Opera'),
('Ficção Apocalíptica/Pós-apocalíptica'),
('Biografia'),
('Autobiografia'),
('Memórias'),
('Ensaio'),
('Crônica'),
('Reportagem Literária'),
('Autoajuda'),
('Desenvolvimento Pessoal'),
('Filosofia'),
('Espiritualidade/Religião'),
('Política'),
('Sociologia'),
('Psicologia'),
('História'),
('Economia'),
('Administração e Negócios'),
('Jornalismo Investigativo'),
('Viagem e Turismo'),
('Gastronomia (culinária)'),
('Educação'),
('Ciência Popular (Divulgação científica)'),
('Saúde e Bem-estar'),
('Literatura Infantil'),
('Literatura Juvenil'),
('Contos de Fadas'),
('Fábulas'),
('Livros Ilustrados'),
('HQs e Mangás'),
('Fantasia Infantojuvenil'),
('Aventura Juvenil'),
('Young Adult (YA)'),
('New Adult'),
('Tragédia'),
('Comédia'),
('Tragicomédia'),
('Farsa'),
('Monólogo'),
('Musical'),
('Metaficção'),
('Literatura Interativa'),
('Romance Epistolar (em cartas)'),
('Romance Gráfico (graphic novel)'),
('Flash Fiction (microcontos)'),
('Poesia Visual/Concreta');
