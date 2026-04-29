-- Usuário padrão para login: admin / admin123
-- (senha bcrypt de "admin123")
INSERT INTO users (id, username, password, role)
VALUES (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN');

-- Salas de exemplo
INSERT INTO rooms (id, nome, capacidade, localizacao)
VALUES (1, 'Sala Alpha', 10, 'Andar 1 - Bloco A');

INSERT INTO rooms (id, nome, capacidade, localizacao)
VALUES (2, 'Sala Beta', 20, 'Andar 2 - Bloco B');

INSERT INTO rooms (id, nome, capacidade, localizacao)
VALUES (3, 'Auditório Central', 100, 'Térreo - Bloco C');
