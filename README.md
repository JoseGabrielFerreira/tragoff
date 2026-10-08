# TRAGOFF

**TRAGOFF** (trago + off) é um sistema web para quem quer **reduzir ou parar de fumar**. Não é um sistema médico: é uma ferramenta de acompanhamento e controle do hábito.

O usuário registra o que consome, e o sistema transforma esses registros em informação útil: quanto consome por dia, em que horários, por qual motivo (gatilho), quanto dinheiro gasta e se está cumprindo uma meta de redução.

## Funcionalidades

- **Conta:** cadastro, login e edição de dados.
- **Perfil de fumante:** anos fumando, consumo diário inicial (em cigarros ou em tragadas, para quem usa vape) e motivo para parar.
- **Compras:** registro de um maço, vape ou pacote de tabaco (preço pago e quantidade). O sistema calcula o preço por unidade e controla o estoque: quando a compra acaba, é preciso registrar uma nova.
- **Registro de consumo:** produto, quantidade, data e hora e gatilho (opcional). O gasto é calculado automaticamente.
- **Histórico:** lista de consumos com o gasto de cada um e exclusão de registros.
- **Metas:** metas de redução por unidade (cigarros ou tragadas), com plano de redução e progresso.
- **Dashboard:** gastos (hoje, semana, mês e total), consumo e média por produto, últimos 7 dias, principais gatilhos, horário de maior consumo e progresso das metas.
- **Área administrativa:** cadastro e exclusão de produtos e gatilhos e lista de usuários.

## Tecnologias

| Parte | Tecnologias |
|---|---|
| Backend | Java 17, Spring Boot 4.1, Spring Web, Spring Data JPA, Hibernate, Lombok, Maven |
| Banco de dados | MySQL 8 |
| Frontend | HTML, CSS e JavaScript puro |
| API | REST, respostas em JSON |

## Arquitetura

O backend segue a divisão em camadas:

```
JavaScript  ->  Controller  ->  Service  ->  Repository  ->  JPA/Hibernate  ->  MySQL
  (tela)       (rotas REST)    (regras)      (acesso ao banco)
```

- `controller`: recebe as requisições REST e devolve o resultado dentro de um `Retorno` (`status`, `mensagemErro` e `object`).
- `service`: validações, regras de negócio e cálculos (gasto, estoque, metas, dashboard).
- `repository`: interfaces `JpaRepository`.
- `model`: as 15 entidades do sistema. `Usuario` e `Administrador` herdam de `Pessoa`.

O frontend fica dentro do próprio projeto Spring Boot, em `src/main/resources/static`, então a página e a API usam o mesmo endereço.

```
tragoff/
├── pom.xml
└── src/main/
    ├── java/com/tragoff/tragoff/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   ├── Retorno.java
    │   └── TragoffApplication.java
    └── resources/
        ├── application.properties
        ├── dados_iniciais.sql
        └── static/            (HTML, CSS e JavaScript)
```

## Como rodar

### 1. Pré-requisitos

- **JDK 17 ou superior**
- **MySQL 8** (de preferência com o **MySQL Workbench**). Anote a senha do usuário `root`.
- **IntelliJ IDEA** (ou outra IDE com suporte a Maven)
- **Git**

### 2. Baixar o projeto

```bash
git clone https://github.com/JoseGabrielFerreira/tragoff.git
```

Abra a pasta interna `tragoff` (a que contém o `pom.xml`) na IDE como projeto Maven e espere as dependências serem baixadas.

### 3. Configurar o banco

Abra `tragoff/src/main/resources/application.properties` e coloque a senha do seu MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tragoff?createDatabaseIfNotExist=true&serverTimezone=America/Sao_Paulo
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
```

Se o seu MySQL não usa a porta `3306`, troque o número na `url`. **Não é preciso criar o banco nem as tabelas:** o banco `tragoff` e as 15 tabelas são criados automaticamente na primeira execução.

### 4. Rodar a aplicação (primeira vez)

Execute a classe `TragoffApplication` pela IDE ou, pelo terminal, dentro da pasta `tragoff`:

```bash
./mvnw spring-boot:run        # Linux e Mac
mvnw.cmd spring-boot:run      # Windows
```

Espere aparecer `Started TragoffApplication` no console. Neste momento o Hibernate criou as tabelas. **Deixe a aplicação rodando.**

### 5. Cadastrar os dados iniciais (uma vez só)

Os produtos, os gatilhos e o administrador precisam existir no banco. No MySQL Workbench, abra o arquivo `tragoff/src/main/resources/dados_iniciais.sql` (**File > Open SQL Script**) e execute tudo com o botão do raio. O conteúdo é este:

```sql
USE tragoff;

-- Produtos. A unidade define em que o consumo é contado.
-- Preço 0.00 = o usuário precisa registrar a compra antes de consumir (caso do vape).
INSERT INTO tipo_cigarro (nome, unidade, preco_unitario) VALUES
('Cigarro', 'cigarros', 0.00),
('Cigarro de palha', 'cigarros', 0.00),
('Tabaco', 'cigarros', 0.00),
('Vape/Cigarro eletrônico', 'tragadas', 0.00);

INSERT INTO gatilho (nome) VALUES
('Café'),
('Ansiedade'),
('Estresse'),
('Álcool'),
('Socialização'),
('Outro');

INSERT INTO pessoa (nome, email, senha) VALUES ('Administrador', 'admin@tragoff.com', 'admin');
INSERT INTO administrador (id, nivel_acesso) VALUES (LAST_INSERT_ID(), 'TOTAL');
```

> Este script só deve ser executado **uma vez**. Se rodar de novo, o MySQL avisa que os dados já existem (nomes únicos), o que é normal. Ele precisa rodar **depois** da primeira execução da aplicação, porque as tabelas ainda não existem antes dela.

### 6. Usar o sistema

Abra no navegador: **http://localhost:8080**

| Acesso | Como entrar |
|---|---|
| Usuário comum | Clique em **Criar conta** na página inicial e cadastre-se |
| Administrador | Link **Área administrativa** no rodapé da página inicial. E-mail `admin@tragoff.com`, senha `admin` |

Fluxo sugerido para testar: criar conta, preencher o perfil, registrar uma **compra**, registrar um **consumo**, criar uma **meta** e abrir o **dashboard**.

### Recomeçar do zero

Pare a aplicação e, no Workbench, execute:

```sql
DROP DATABASE IF EXISTS tragoff;
```

Depois repita os passos 4 e 5.

## Principais rotas da API

Todas começam com `/api` e respondem `{ "status": "OK" | "Erro", "mensagemErro": ..., "object": ... }`.

| Recurso | Rotas |
|---|---|
| Conta | `POST /usuario`, `POST /login`, `GET /usuario`, `GET` e `PUT /usuario/{id}`, `POST /administrador/login` |
| Perfil | `GET` e `POST /perfil/{usuarioId}` |
| Produtos | `GET` e `POST /tipocigarro`, `PUT` e `DELETE /tipocigarro/{id}` |
| Gatilhos | `GET` e `POST /gatilho`, `PUT` e `DELETE /gatilho/{id}` |
| Compras | `POST /compra`, `GET /compra/usuario/{usuarioId}` |
| Consumo | `POST /registro`, `GET /registro/usuario/{usuarioId}`, `DELETE /registro/{id}` |
| Metas | `POST /meta`, `GET /meta/usuario/{usuarioId}`, `PUT /meta/{id}/status/{novoStatus}` |
| Dashboard | `GET /dashboard/{usuarioId}` |

## Observações

- As senhas ficam em texto puro no banco. É uma simplificação intencional para o escopo do trabalho acadêmico. Em um sistema real seria usada criptografia (BCrypt) e autenticação com Spring Security.
- O projeto não usa DTO: as entidades são enviadas diretamente como JSON.