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
    * Crie uma base de dados chamada `scientific_library`.
    * Execute o script SQL (fornecido na pasta `/docs`) para criar as tabelas, relacionamentos e inserir dados iniciais.
    * Opcionalmente na pasta /docs, tem lá um ficheiro backup da basde de dados que poderá restaurar no Postgres

2.  **Configuração de Ligação:**
    No ficheiro `src/main/resources/application.properties`, configure as credenciais de acesso ao seu servidor local:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/ scientific_library
    spring.datasource.username=o_teu_utilizador
    spring.datasource.password=a_tua_password
    
    # Hibernate - Database First Mode
    # 'validate' garante que as entidades Java correspondem exatamente às tabelas existentes
    spring.jpa.hibernate.ddl-auto=validate
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    ```

### 2. Mapeamento de Entidades
As classes no pacote `com.gilbertoparente.library.entities` foram criadas para espelhar  a estrutura das tabelas existentes, utilizando anotações JPA para definir chaves primárias, estrangeiras e tabelas de associação (como `article_thematic`).


## 📋 Funcionalidades Implementadas

### 🔐 Autenticação e Perfis
* Sistema de Login seguro com validação na base de dados.
* Diferenciação de permissões entre autores, alunos e administradores.

### 📑 Gestão de Artigos (Módulo Principal)
* **Listagem Avançada:** Tabela dinâmica com suporte a `FilteredList` para pesquisa em tempo real (Título, Autor, DOI).
* **Filtros:** Segmentação por Estado (Publicado/Rascunho) e Tipo de Acesso (Pago/Gratuito).
* **Visualização:** Visualização de detalhes lateral sem troca de contexto, exibindo resumo, DOI e preços calculados.
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
# 📚 Open Library - Sistema de Gestão de Biblioteca Científica

Este projeto consiste numa solução de software distribuída e integrada para a gestão, submissão e consumo de artigos científicos. O ecossistema é suportado por uma arquitetura em camadas e partilha uma base de dados relacional comum, unificando uma interface de administração local com um portal público global.

---

## 🏗️ Decisões Arquiteturais e Tecnológicas

A conceção da plataforma baseou-se em decisões estratégicas que priorizam o desempenho, a segurança e a manutenibilidade do código:

* **Padrão MVC (Model-View-Controller):** Adotou-se o ecossistema **Spring Boot (Spring MVC)** na Web para segmentar claramente as responsabilidades. Os controladores (`Controllers`) gerem as requisições HTTP, os modelos (`Models`) transportam os dados encapsulados, e as vistas (`Views`) renderizam a interface final.
* **Thymeleaf como Motor de Templates:** Optou-se pelo Thymeleaf para a renderização no lado do servidor (*Server-Side Rendering - SSR*). Isto garante uma forte integração nativa com os objetos do Spring, otimiza o carregamento inicial e simplifica de forma robusta a gestão de sessões HTTP.
* **Abordagem Monolítica Modular (Partilha de BLL):** A aplicação Web não possui acesso isolado à Base de Dados. Ela consome os mesmos serviços de negócio (`ArticleService`, `AuthorService`, `UserService`) que a aplicação Desktop através de injeção de dependências da **BLL (Business Logic Layer)**. Qualquer regra de validação alterada na BLL aplica-se automaticamente a ambas as plataformas, eliminando a duplicação de código.
* **Bootstrap 5 e Design Responsivo:** A interface Web utiliza o Bootstrap 5 para garantir que a experiência de leitura e submissão se adapta com fluidez tanto a computadores como a dispositivos móveis.

---

## 🔒 Módulos Funcionais e Segurança Web

### 1. Portal de Autenticação e Controlo Individualizado
* **Criptografia Assimétrica:** As palavras-passe são cifradas recorrendo ao algoritmo `BCryptPasswordEncoder` antes de serem persistidas na base de dados (PostgreSQL), garantindo a conformidade com as boas práticas de segurança e RGPD.
* **Gestão Estrita de Sessão:** O sistema valida o utilizador e injeta a entidade completa na `HttpSession` como `loggedUser`. Adicionalmente, geres o papel do utilizador através do `userRole` para impedir que utilizadores comuns ou administradores corrompam dados de autores na Web.

### 2. Dashboard Dinâmico Orientado a Perfis
* **Métricas e Novidades:** Carrega em tempo real o volume de compras do utilizador e os artigos mais recentes da plataforma recorrendo a ordenações decrescentes por ID limitadas na query.
* **Isolamento de Coleção ("Minha Biblioteca"):** Garante que um leitor apenas consegue visualizar e aceder aos artigos que comprou legitimamente via `purchaseRepository.findByUser_IdUser`.
* **Visualização Segura de PDF (Stream de Ficheiros):** A rota de leitura não expõe caminhos físicos de ficheiros no disco. O sistema valida se o utilizador na sessão possui a respetiva compra efetuada e faz o *stream* do binário diretamente para o navegador, mitigando downloads ilegais (DRM básico).

### 3. Fluxo Descentralizado de Publicação Científica
* **Tratamento de Ficheiros Binários:** O formulário utiliza a codificação `enctype="multipart/form-data"` para efetuar o *upload* seguro do manuscrito através de um objeto `MultipartFile`.
* **Resolução de Chaves Estrangeiras:** O controlador resolve dinamicamente a discrepância entre IDs (onde o ID da tabela de utilizadores difere do ID da tabela de autores), mapeando a relação correta através do método `findByUser_IdUser` antes de delegar o salvamento ao `articleService.save(article, file)` da BLL.

