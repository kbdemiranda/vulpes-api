# Vulpes API

## Descrição

Vulpes é uma API RESTful de gerenciamento de serviços por assinatura compartilhados. Desenvolvido com Spring Boot, o projeto permite que usuários registrem e gerenciem plataformas de assinatura como Netflix, HBO Max e Microsoft 365, bem como os membros com quem essas assinaturas são compartilhadas.

## Tecnologias Utilizadas

- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Validation

## Como Rodar o Projeto

1. Clone o repositório para sua máquina local.
2. Configure o banco de dados PostgreSQL e atualize o arquivo \`application.yml\`.
3. Execute o comando \`./mvnw spring-boot:run\` para iniciar a aplicação.
4. A API estará disponível em \`http://localhost:8080\`.

## Endpoints

- \`GET /plataformas\`: Lista todas as plataformas
- \`GET /plataformas/{id}\`: Busca uma plataforma pelo ID
- \`POST /plataformas\`: Cadastra uma nova plataforma
- \`PUT /plataformas/{id}\`: Atualiza uma plataforma existente pelo ID
- \`DELETE /plataformas/{id}\`: Exclui uma plataforma pelo ID

- \`GET /assinantes\`: Lista todos os assinantes
- \`GET /assinantes/{id}\`: Busca um assinante pelo ID
- \`POST /assinantes\`: Cadastra um novo assinante
- \`PUT /assinantes/{id}\`: Atualiza um assinante existente pelo ID
- \`DELETE /assinantes/{id}\`: Exclui um assinante pelo ID

## Contribuição

Sinta-se à vontade para contribuir com o projeto. Abra um PR e vamos discutir suas ideias!
