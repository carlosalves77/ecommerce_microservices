# 🛒 E-commerce Microservices System

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-green?style=for-the-badge&logo=spring)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Enabled-black?style=for-the-badge&logo=apachekafka)
![Postgres](https://img.shields.io/badge/PostgreSQL-Database-336791?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Cache-red?style=for-the-badge&logo=redis)

Sistema robusto de microsserviços para e-commerce, desenvolvido com foco em escalabilidade, segurança e alta performance. O projeto orquestra serviços independentes para gestão de catálogo, autenticação segura, carrinho de compras de alta velocidade e processamento assíncrono de pagamentos.

---

## 🏗️ Arquitetura e Design

O sistema utiliza uma arquitetura baseada em microsserviços orquestrados por containers, seguindo o padrão MVC internamente em cada serviço. A comunicação ocorre via APIs REST (síncrona) utilizando **Feign Client** e via mensageria (assíncrona) com **Apache Kafka**.

### Diagrama de Arquitetura
```mermaid
graph TD
    Client[Cliente / Mobile / Web] -->|HTTP Requests| Gateway[API Gateway]
    
    subgraph "Cluster de Microsserviços"
        Gateway -->|Roteamento & Auth| AuthService[Auth Service]
        Gateway -->|Roteamento| ProductService[Product Service]
        Gateway -->|Roteamento| CartService[Cart Service]
        Gateway -->|Roteamento| PaymentService[Payment Service]
        
        AuthService -.->|Persistência| Postgres[(PostgreSQL)]
        ProductService -.->|Persistência| Postgres
        CartService -.->|Cache Rápido| Redis[(Redis)]
        
        PaymentService -->|Publica Evento| Kafka{{Apache Kafka}}
        Kafka -.->|Consome Evento| Notification[Notification/Order Service]
    end

```
### 🚀 Tecnologias e Ferramentas

• Core: Java 17, Spring Boot 3.5.7

• Arquitetura: Microservices, MVC, API Gateway

• Banco de Dados: * PostgreSQL: Para dados relacionais (Usuários, Produtos).

• Redis: Para dados voláteis e de acesso rápido (Carrinho de Compras).

• Mensageria: Apache Kafka (Processamento de eventos de pagamento).

• Segurança: Spring Security, OAuth2, JWT (JSON Web Token).

• Comunicação: Spring Cloud OpenFeign, Spring WebFlux.

• DevOps: Docker, Docker Compose.

• Utilitários: Lombok, MapStruct, Bean Validation, JPA.



### 🛠️ Instalação e Execução
Este projeto foi desenhado para rodar inteiramente via Docker, facilitando o setup do ambiente.

1. Pré-requisitos
Docker e Docker Compose instalados na máquina.

2. Configuração de Variáveis de Ambiente (.env)
Na raiz do projeto (onde está o docker-compose.yml), crie um arquivo chamado .env e preencha com suas credenciais:

#### --- Banco de Dados (PostgreSQL) ---

```

postgres_user=usuario_banco
postgres_password=sua_senha
postgres_database=seu_banco

```

#### --- Credenciais da Aplicação Spring ---
```

spring_user=user_app
spring_password=senha_app

```

#### --- Segurança (JWT) ---

Gere uma chave segura (ex: base64)

```

JWT_SECRET=sua_chave_secreta_jwt

```

3. Rodando a Aplicação
Execute o comando abaixo para compilar os projetos Java, construir as imagens Docker e subir todos os containers:

```

docker compose up --build

```
