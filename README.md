# Vulpes - Gerenciador de Assinaturas

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Entidades e Relações](#entidades-e-relações)
- [Endpoints](#endpoints)
- [Segurança](#segurança)
- [Como Rodar o Projeto](#como-rodar-o-projeto)
- [Testes](#testes)
- [Documentação da API](#documentação-da-api)
- [Contribuição](#contribuição)

## Sobre o Projeto

Vulpes é um gerenciador de assinaturas que permite aos usuários controlar suas diversas assinaturas de serviços como streaming, software, entre outros. O projeto foi desenvolvido usando Spring Boot no backend.

## Tecnologias Utilizadas

- Java 11
- Spring Boot
- Spring Security
- JWT para autenticação
- PostgreSQL
- JPA/Hibernate
- Maven
- JUnit para testes
- Swagger para documentação da API

## Entidades e Relações

- `Assinante`: Informações sobre o assinante.
- `AssinantePlataforma`: Relação entre assinantes e plataformas.
- `Pagamento`: Informações sobre os pagamentos.
- `Perfil`: Perfis de usuário para controle de acesso.
- `Plataforma`: Informações sobre as plataformas de assinatura.
- `StatusPagamentoMensal`: Status mensal dos pagamentos.
- `Usuario`: Informações sobre os usuários do sistema.

## Endpoints

### Assinantes

- `GET /assinantes`: Listar todos os assinantes.
- `POST /assinantes`: Cadastrar um novo assinante.
- `PUT /assinantes/{id}`: Atualizar um assinante.
- `DELETE /assinantes/{id}`: Excluir um assinante.

### Plataformas

- `GET /plataformas`: Listar todas as plataformas.
- `POST /plataformas`: Cadastrar uma nova plataforma.
- `PUT /plataformas/{id}`: Atualizar uma plataforma.
- `DELETE /plataformas/{id}`: Excluir uma plataforma.

### Pagamentos

- `GET /pagamentos`: Listar todos os pagamentos.
- `POST /pagamentos`: Registrar um novo pagamento.
- `PUT /pagamentos/{id}`: Atualizar um pagamento.
- `DELETE /pagamentos/{id}`: Excluir um pagamento.

### Usuários

- `GET /usuarios`: Listar todos os usuários.
- `POST /usuarios`: Cadastrar um novo usuário.
- `PUT /usuarios/{id}`: Atualizar um usuário.
- `DELETE /usuarios/{id}`: Excluir um usuário.

## Segurança

O projeto utiliza Spring Security e JWT para autenticação e autorização. O token JWT deve ser incluído no header das requisições.

## Como Rodar o Projeto

```bash
mvn clean install
java -jar target/vulpes-0.0.1-SNAPSHOT.jar
```

## Testes

O projeto inclui testes unitários e de integração usando JUnit.

## Documentação da API

A documentação da API está disponível através do Swagger em `http://localhost:8080/swagger-ui/`.

## Contribuição

Para contribuir com o projeto, por favor, faça um fork e abra um pull request.
