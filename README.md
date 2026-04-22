# 📚 Biblioteca Científica - Sistema de Gestão de Repositório

Este projeto foi desenvolvido no âmbito da Unidade Curricular de **Projeto 2** do 2º ano de Engenharia Informática (IPVC). Trata-se de uma aplicação robusta para gestão, publicação e consulta de artigos científicos, integrando uma solução Desktop e persistência em base de dados relacional.

## 🚀 Tecnologias e Ferramentas

* **Linguagem:** Java 17 (LTS)
* **Framework Core:** Spring Boot 3.x
* **Interface Gráfica:** JavaFX (com FXML e Scene Builder)
* **Estilização:** CSS3 e BootstrapFX
* **Persistência (ORM):** Spring Data JPA / Hibernate
* **Base de Dados:** PostgreSQL 15+
* **Gestão de Dependências:** Maven
* **IA Suporte:** Google Gemini (Otimização e Debugging)

---

## 🛠️ Instalação e Configuração Database First

## 🛠️ Instalação e Configuração

### 1. Base de Dados (Abordagem: Database First)
O projeto foi desenvolvido seguindo a abordagem **Database First**, onde a estrutura da base de dados foi desenhada e implementada previamente no PostgreSQL.

1.  **Criação da Base de Dados:**
    * Certifique-se de que o PostgreSQL está em execução.
    * Crie uma base de dados chamada `biblioteca_cientifica`.
    * Execute o script SQL (fornecido na pasta `/database`) para criar as tabelas, relacionamentos e inserir dados iniciais.

2.  **Configuração de Ligação:**
    No ficheiro `src/main/resources/application.properties`, configure as credenciais de acesso ao seu servidor local:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca_cientifica
    spring.datasource.username=o_teu_utilizador
    spring.datasource.password=a_tua_password
    
    # Hibernate - Database First Mode
    # 'validate' garante que as entidades Java correspondem exatamente às tabelas existentes
    spring.jpa.hibernate.ddl-auto=validate
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    ```

### 2. Mapeamento de Entidades
As classes no pacote `com.gilbertoparente.library.entities` foram criadas para espelhar fielmente a estrutura das tabelas existentes, utilizando anotações JPA para definir chaves primárias, estrangeiras e tabelas de associação (como `article_thematic`).



### 1. Base de Dados (Abordagem: Code First)
O projeto utiliza a abordagem **Code First**. Isto significa que a estrutura das tabelas é gerada automaticamente pelo Hibernate a partir das entidades Java.

1.  Crie uma base de dados no PostgreSQL chamada `biblioteca_cientifica`.
2.  No ficheiro `src/main/resources/application.properties`, configure as suas credenciais:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca_cientifica
    spring.datasource.username=o_teu_utilizador
    spring.datasource.password=a_tua_password
    
    # Atualização automática da estrutura (Code First)
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

### 2. Execução no IDE (Rider / IntelliJ)
1.  Importe o projeto como um projeto **Maven**.
2.  Aguarde o download de todas as dependências.
3.  Execute a classe principal `LibraryApplication.java`.
4.  **Nota para JavaFX:** Certifique-se de que o SDK do JavaFX está configurado nos módulos do projeto ou que as dependências Maven do JavaFX estão carregadas.

---

## 📋 Funcionalidades Implementadas

### 🔐 Autenticação e Perfis
* Sistema de Login seguro com validação na base de dados.
* Diferenciação de permissões entre autores, alunos e administradores.

### 📑 Gestão de Artigos (Módulo Principal)
* **Listagem Avançada:** Tabela dinâmica com suporte a `FilteredList` para pesquisa em tempo real (Título, Autor, DOI).
* **Filtros Inteligentes:** Segmentação por Estado (Publicado/Rascunho) e Tipo de Acesso (Pago/Gratuito).
* **Master-Detail:** Visualização de detalhes lateral sem troca de contexto, exibindo resumo, DOI e preços calculados.
* **Associações N:M:** Suporte para múltiplos autores e múltiplas temáticas por artigo através de tabelas associativas.

### ✍️ Edição e Publicação
* Interface intuitiva para criação/edição de artigos.
* Uso de `CheckListView` (ControlsFX) para seleção múltipla de categorias.
* Gestão de ficheiros PDF associados ao repositório.

### 🎨 Interface e UX
* Design moderno baseado em **BootstrapFX**.
* Estilização centralizada em `style.css` para fácil manutenção.
* Feedback visual de operações (Alertas de sucesso/erro).

---

## 🏛️ Arquitetura
O projeto segue uma arquitetura em camadas para garantir a manutenibilidade:
1.  **Entities:** Modelo de dados (POJOs com JPA).
2.  **Repositories:** Interface de comunicação com o PostgreSQL.
3.  **Services:** Camada de lógica de negócio e validações.
4.  **Controllers:** Gestão da interface JavaFX e eventos de utilizador.

---

## ✒️ Autor
* **Gilberto Parente** - *15330* - Engenharia Informática IPVC
