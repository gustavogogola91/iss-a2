# Sistema de Gerenciamento de Campeonatos de Futebol

API RESTful desenvolvida em Java puro para gerenciamento de times, jogadores, campeonatos, inscrições e partidas de futebol.

## 👨‍💻 Alunos

| Nome | RGM |
|---|---|
| Gustavo Luiz Gogola | 33032319 |
| João Paulo Class | 34610286 |
| João Pedro Durau | 32809352 |
| Vinicios de Jesus Moraes | 34535624 |

---

## 🛠️ Tecnologias Utilizadas

- **Java 26**
- **Maven** — gerenciamento de dependências
- **PostgreSQL** — banco de dados relacional
- **Jackson Databind** — serialização/deserialização JSON
- **`com.sun.net.httpserver`** — servidor HTTP nativo do Java

---

## ⚙️ Funcionamento do Sistema

O sistema expõe uma API HTTP que roda na porta **4321**. Cada recurso possui seu próprio contexto (rota base) e handler responsável por interpretar o método HTTP e executar a operação correspondente no banco de dados via JDBC.

O banco de dados utilizado é o **PostgreSQL**, configurado localmente em `localhost:5432` com o banco `db_sistema_futebol`.

### Arquitetura

```
Server.java
├── /jogador   → JogadorHandler  → JogadorRepository
├── /time      → TimeHandler     → TimeRepository
├── /campeonato→ CampeonatoHandler → CampeonatoRepository
├── /inscricao → InscricaoHandler → InscricaoRepository
└── /partida   → PartidaHandler  → PartidaRepository
```

Cada camada tem responsabilidade bem definida:
- **Handler**: recebe a requisição HTTP, identifica o método e o path, delega para o repositório e retorna a resposta.
- **Repository**: executa as queries SQL usando `PreparedStatement`.
- **Model**: representa as entidades do domínio.

---

## 🗄️ Modelo de Dados

```sql
tb_time        (id, nome)
tb_jogador     (id, id_time FK, nome)
tb_campeonato  (id, nome, prizePool)
tb_inscricao   (id, id_campeonato FK, id_time FK)
tb_partida     (id, id_campeonato FK, id_time_a FK, id_time_b FK, data_partida, resultado)
```

---

## 📋 Endpoints

### `/jogador`

| Método | Path | Descrição | Body |
|--------|------|-----------|------|
| `GET` | `/jogador` | Lista todos os jogadores | — |
| `GET` | `/jogador/{id}` | Busca jogador por ID | — |
| `POST` | `/jogador` | Cadastra um novo jogador | `{ "nome": "string", "timeId": int }` |
| `PUT` | `/jogador/{id}` | Atualiza dados de um jogador | `{ "nome": "string", "timeId": int }` |
| `DELETE` | `/jogador/{id}` | Remove um jogador | — |

---

### `/time`

| Método | Path | Descrição | Body |
|--------|------|-----------|------|
| `GET` | `/time` | Lista todos os times | — |
| `GET` | `/time/{id}` | Busca time por ID (retorna com lista de jogadores) | — |
| `POST` | `/time` | Cadastra um novo time | `{ "nome": "string" }` |
| `PUT` | `/time/{id}` | Atualiza nome de um time | `{ "nome": "string" }` |
| `DELETE` | `/time/{id}` | Remove um time | — |

---

### `/campeonato`

| Método | Path | Descrição | Body |
|--------|------|-----------|------|
| `GET` | `/campeonato` | Lista todos os campeonatos | — |
| `GET` | `/campeonato/{id}` | Busca campeonato por ID (retorna com times e partidas) | — |
| `POST` | `/campeonato` | Cadastra um novo campeonato | `{ "nome": "string", "prizepool": float }` |
| `PUT` | `/campeonato/{id}` | Atualiza dados de um campeonato | `{ "nome": "string", "prizepool": float }` |
| `DELETE` | `/campeonato/{id}` | Remove um campeonato | — |

---

### `/inscricao`

| Método | Path | Descrição | Body |
|--------|------|-----------|------|
| `GET` | `/inscricao` | Lista todas as inscrições | — |
| `POST` | `/inscricao` | Inscreve um time em um campeonato | `{ "idCampeonato": int, "idTime": int }` |
| `DELETE` | `/inscricao/{id}` | Remove uma inscrição | — |

> ⚠️ **Regra de Negócio aplicada neste endpoint** — ver seção abaixo.

---

### `/partida`

| Método | Path | Descrição | Body |
|--------|------|-----------|------|
| `GET` | `/partida` | Lista todas as partidas | — |
| `GET` | `/partida/{id}` | Busca partida por ID | — |
| `GET` | `/partida/por_campeonato/{id}` | Lista partidas de um campeonato específico | — |
| `POST` | `/partida` | Cadastra uma nova partida | `{ "idCampeonato": int, "idTimeA": int, "idTimeB": int, "data": "YYYY-MM-DD", "resultado": "string" }` |
| `PUT` | `/partida/{id}` | Atualiza dados de uma partida | `{ "idCampeonato": int, "idTimeA": int, "idTimeB": int, "data": "YYYY-MM-DD", "resultado": "string" }` |
| `DELETE` | `/partida/{id}` | Remove uma partida | — |

---

## 📐 Regras de Negócio

### RN01 — Mínimo de jogadores para inscrição

> Um time só pode ser inscrito em um campeonato se possuir **no mínimo 5 jogadores** cadastrados.

Ao tentar realizar uma inscrição (`POST /inscricao`), o sistema busca o time informado e verifica o tamanho de sua lista de jogadores. Caso o time tenha menos de 5 jogadores, a requisição é rejeitada com status `400 Bad Request` e a seguinte mensagem:

```
Regra de Negocio: O time precisa ter pelo menos 5 jogadores cadastrados para se inscrever.
```

---

## 🔁 Respostas HTTP

| Código | Significado |
|--------|-------------|
| `200` | Operação realizada com sucesso |
| `201` | Recurso criado com sucesso (inscrição) |
| `400` | Dados inválidos ou regra de negócio violada |
| `404` | Recurso não encontrado |
| `405` | Método HTTP não permitido |
| `500` | Erro interno no servidor |

---

## 🚀 Como Executar

**Pré-requisitos:** Java 26, Maven e PostgreSQL instalados.

1. Clone o repositório:
   ```bash
   git clone https://github.com/gustavogogola91/iss-a2.git
   ```

2. Crie o banco de dados e execute o script SQL:
   ```bash
   psql -U postgres -c "CREATE DATABASE db_sistema_futebol;"
   psql -U postgres -d db_sistema_futebol -f src/sql/sql.sql
   ```

3. Compile e execute:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="Server"
   ```

4. A API estará disponível em `http://localhost:4321`.
