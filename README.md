# LucasVasquezSilva---CP2-Java

# Nome: Lucas Vasquez Silva
# RM: 555159

# Sobre:

## Nome:

- Gerenciador de Reuniões API

## Descrição:

- O projeto consiste em uma API REST em Java utilizando Spring Boot, com o objetivo de gerenciar salas de reunião e suas respectivas reservas.

- A aplicação permite o cadastro, consulta, atualização e remoção de salas, além da criação e gerenciamento de reservas associadas a essas salas. O sistema implementa regras de negócio para evitar conflitos de agendamento, garantindo que não existam reservas simultâneas para a mesma sala em um mesmo intervalo de tempo.

- A API segue o padrão de arquitetura em camadas (SOA), promovendo a separação de responsabilidades entre Controller, Service e Repository, além do uso de DTOs para o transporte de dados.

- A API também conta com autenticação e autorização via JWT, garantindo segurança no acesso aos endpoints protegidos. Para persistência de dados, é utilizado o banco H2 em memória, e a documentação da API é disponibilizada por meio do Swagger.


# Como Rodar o Projeto:
## Pré-requisitos:
- Java 17+
- Maven 3.8


## Passo a Passo
1. Baixar o ZIP no repositorio e abri-lo no 

2. Compile o projeto

   ```bash
   mvn clean install
   ```
3. Execute

   ```bash
   mvn spring-boot:run
   ```

# Decisões Técnicas:


## Tecnologias Utilizadas

- Java 17 
- Spring Boot 3.2.5
- Spring Security 6.x 
- JWT (jjwt) 0.11.5 
- H2 Database 
- SpringDoc OpenAPI  2.5.0
- Spring Cache
- Lombok 
- JUnit 5 + Mockito 
---
A aplicação estará disponível em: `http://localhost:8080`
---

### Configuração do H2 Console
- **JDBC URL:** `jdbc:h2:mem:meetingroom`
- **User:** `sa`
- **Password:** *(vazio)*

## URLs Disponíveis

| URL | Descrição |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Documentação interativa Swagger |
| `http://localhost:8080/h2-console` | Console do banco H2 |
| `http://localhost:8080/api-docs` | JSON do OpenAPI |

### Configuração do H2 Console
- **JDBC URL:** `jdbc:h2:mem:meetingroom`
- **User:** `sa`
- **Password:** *(vazio)*

---

## Autenticação

### Credenciais Padrão (seed do banco)

- username | `admin`
- password | `admin123`

### Fluxo de autenticação

1. Faça POST em `/api/auth/login` com as credenciais
2. Copie o `token` da resposta
3. Nos demais endpoints, adicione o header: `Authorization: Bearer <token>`


### Auth
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/login` | Login e geração de token JWT | ❌ |

### Salas
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/rooms` | Criar sala | ✅ |
| GET | `/api/rooms` | Listar salas (paginado + filtros) | ✅ |
| GET | `/api/rooms/{id}` | Buscar sala por ID | ✅ |
| PUT | `/api/rooms/{id}` | Atualizar sala | ✅ |
| DELETE | `/api/rooms/{id}` | Remover sala | ✅ |

**Parâmetros de filtro GET /api/rooms:**
- `nome` — filtro por nome (parcial)
- `localizacao` — filtro por localização (parcial)
- `capacidadeMin` — capacidade mínima
- `page`, `size`, `sortBy` — paginação

### Reservas
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | `/api/reservations` | Criar reserva | ✅ |
| GET | `/api/reservations` | Listar reservas (paginado + filtros) | ✅ |
| GET | `/api/reservations/{id}` | Buscar reserva por ID | ✅ |
| PATCH | `/api/reservations/{id}/cancel` | Cancelar reserva | ✅ |

**Parâmetros de filtro GET /api/reservations:**
- `salaId` — filtrar por sala
- `responsavel` — filtrar por responsável (parcial)
- `inicio` / `fim` — filtrar por intervalo de data (ISO 8601)
- `status` — `ATIVA` ou `CANCELADA`
- `page`, `size`, `sortBy` — paginação

---

## Exemplos de Requisições

### 1. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "type": "Bearer",
  "expiresIn": 86400000
}
```

---

### 2. Criar Sala
```http
POST /api/rooms
Authorization: Bearer <token>
Content-Type: application/json

{
  "nome": "Sala Gama",
  "capacidade": 15,
  "localizacao": "Andar 3 - Bloco D"
}
```

---

### 3. Listar Salas (com filtro e paginação)
```http
GET /api/rooms?nome=alpha&page=0&size=5
Authorization: Bearer <token>
```

---

### 4. Criar Reserva
```http
POST /api/reservations
Authorization: Bearer <token>
Content-Type: application/json

{
  "salaId": 1,
  "dataHoraInicio": "2025-12-10T09:00:00",
  "dataHoraFim": "2025-12-10T11:00:00",
  "responsavel": "Maria Oliveira"
}
```

---

### 5. Tentar criar reserva conflitante (retorna 409)
```http
POST /api/reservations
Authorization: Bearer <token>
Content-Type: application/json

{
  "salaId": 1,
  "dataHoraInicio": "2025-12-10T10:00:00",
  "dataHoraFim": "2025-12-10T12:00:00",
  "responsavel": "Carlos Souza"
}
```

**Resposta (409 Conflict):**
```json
{
  "status": 409,
  "message": "Conflito de horário! Já existe reserva para esta sala entre ...",
  "timestamp": "2025-..."
}
```

---

### 6. Cancelar Reserva
```http
PATCH /api/reservations/1/cancel
Authorization: Bearer <token>
```

---

### 7. Listar Reservas com filtros
```http
GET /api/reservations?salaId=1&status=ATIVA&page=0&size=10
Authorization: Bearer <token>
```
---

## Testes

```bash
# Executar todos os testes
mvn test
```

Testes implementados:
- `RoomServiceTest` — criação, busca, atualização, remoção de salas
- `ReservationServiceTest` — conflito de horário, datas inválidas, cancelamento
