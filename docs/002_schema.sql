--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2
-- Dumped by pg_dump version 15.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: article_author; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.article_author (
    id_article integer NOT NULL,
    id_author integer NOT NULL
);


ALTER TABLE public.article_author OWNER TO postgres;

--
-- Name: article_thematic; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.article_thematic (
    id_article integer NOT NULL,
    id_thematic integer NOT NULL
);


ALTER TABLE public.article_thematic OWNER TO postgres;

--
-- Name: articles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.articles (
    id_article integer NOT NULL,
    title character varying(200) NOT NULL,
    resume text,
    publication_date date,
    price numeric(10,2) DEFAULT 0,
    file_path character varying(255),
    vat_rate integer DEFAULT 6,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    status character varying(20) DEFAULT 'draft'::character varying,
    doi character varying(100),
    refauthor character varying(255),
    keywords text,
    external_author character varying(255)
);


ALTER TABLE public.articles OWNER TO postgres;

--
-- Name: COLUMN articles.publication_date; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.articles.publication_date IS 'Data de lançamento oficial do artigo';


--
-- Name: COLUMN articles.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.articles.status IS 'Estados possíveis: draft, published, archived';


--
-- Name: articles_id_article_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.articles_id_article_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.articles_id_article_seq OWNER TO postgres;

--
-- Name: articles_id_article_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.articles_id_article_seq OWNED BY public.articles.id_article;


--
-- Name: authors; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.authors (
    id_author integer NOT NULL,
    id_user integer NOT NULL,
    affiliation character varying(150),
    status integer DEFAULT 0
);


ALTER TABLE public.authors OWNER TO postgres;

--
-- Name: COLUMN authors.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.authors.status IS '0: Inativo, 1: Ativo, 2: Suspenso';


--
-- Name: authors_id_author_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.authors_id_author_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authors_id_author_seq OWNER TO postgres;

--
-- Name: authors_id_author_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.authors_id_author_seq OWNED BY public.authors.id_author;


--
-- Name: comments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.comments (
    id_comment integer NOT NULL,
    id_article integer NOT NULL,
    id_user integer NOT NULL,
    parent_comment_id integer,
    content text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    status integer DEFAULT 1,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.comments OWNER TO postgres;

--
-- Name: COLUMN comments.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.comments.status IS '0: Pendente, 1: Aprovado, 2: Ocultado/Spam';


--
-- Name: comments_id_comment_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.comments_id_comment_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.comments_id_comment_seq OWNER TO postgres;

--
-- Name: comments_id_comment_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.comments_id_comment_seq OWNED BY public.comments.id_comment;


--
-- Name: purchases; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.purchases (
    id_purchase integer NOT NULL,
    id_user integer NOT NULL,
    id_article integer NOT NULL,
    purchase_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    amount numeric(10,2),
    status character varying(20) DEFAULT 'pending'::character varying
);


ALTER TABLE public.purchases OWNER TO postgres;

--
-- Name: COLUMN purchases.purchase_date; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.purchases.purchase_date IS 'Data e hora exata da transação';


--
-- Name: COLUMN purchases.status; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN public.purchases.status IS 'Estados: pending, paid, failed, refund, canceled';


--
-- Name: purchases_id_purchase_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.purchases_id_purchase_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.purchases_id_purchase_seq OWNER TO postgres;

--
-- Name: purchases_id_purchase_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.purchases_id_purchase_seq OWNED BY public.purchases.id_purchase;


--
-- Name: thematics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.thematics (
    id_thematic integer NOT NULL,
    description character varying(100) NOT NULL
);


ALTER TABLE public.thematics OWNER TO postgres;

--
-- Name: thematics_id_thematic_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.thematics_id_thematic_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.thematics_id_thematic_seq OWNER TO postgres;

--
-- Name: thematics_id_thematic_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.thematics_id_thematic_seq OWNED BY public.thematics.id_thematic;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id_user integer NOT NULL,
    name character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    is_admin boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_user_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_user_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_user_seq OWNER TO postgres;

--
-- Name: users_id_user_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_user_seq OWNED BY public.users.id_user;


--
-- Name: articles id_article; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articles ALTER COLUMN id_article SET DEFAULT nextval('public.articles_id_article_seq'::regclass);


--
-- Name: authors id_author; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authors ALTER COLUMN id_author SET DEFAULT nextval('public.authors_id_author_seq'::regclass);


--
-- Name: comments id_comment; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments ALTER COLUMN id_comment SET DEFAULT nextval('public.comments_id_comment_seq'::regclass);


--
-- Name: purchases id_purchase; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchases ALTER COLUMN id_purchase SET DEFAULT nextval('public.purchases_id_purchase_seq'::regclass);


--
-- Name: thematics id_thematic; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.thematics ALTER COLUMN id_thematic SET DEFAULT nextval('public.thematics_id_thematic_seq'::regclass);


--
-- Name: users id_user; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id_user SET DEFAULT nextval('public.users_id_user_seq'::regclass);


--
-- Name: article_author article_author_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article_author
    ADD CONSTRAINT article_author_pkey PRIMARY KEY (id_article, id_author);


--
-- Name: article_thematic article_thematic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article_thematic
    ADD CONSTRAINT article_thematic_pkey PRIMARY KEY (id_article, id_thematic);


--
-- Name: articles articles_doi_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articles
    ADD CONSTRAINT articles_doi_key UNIQUE (doi);


--
-- Name: articles articles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.articles
    ADD CONSTRAINT articles_pkey PRIMARY KEY (id_article);


--
-- Name: authors authors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authors
    ADD CONSTRAINT authors_pkey PRIMARY KEY (id_author);


--
-- Name: comments comments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id_comment);


--
-- Name: purchases purchases_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT purchases_pkey PRIMARY KEY (id_purchase);


--
-- Name: thematics thematics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.thematics
    ADD CONSTRAINT thematics_pkey PRIMARY KEY (id_thematic);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


--
-- Name: idx_article_title; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_article_title ON public.articles USING btree (title);


--
-- Name: idx_articles_keywords; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_articles_keywords ON public.articles USING gin (to_tsvector('portuguese'::regconfig, keywords));


--
-- Name: idx_comments_article; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_comments_article ON public.comments USING btree (id_article);


--
-- Name: idx_comments_parent; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_comments_parent ON public.comments USING btree (parent_comment_id);


--
-- Name: idx_comments_user; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_comments_user ON public.comments USING btree (id_user);


--
-- Name: idx_thematic_description; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_thematic_description ON public.thematics USING btree (description);


--
-- Name: article_author fk_article_author_article; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article_author
    ADD CONSTRAINT fk_article_author_article FOREIGN KEY (id_article) REFERENCES public.articles(id_article) ON DELETE CASCADE;


--
-- Name: article_author fk_article_author_author; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article_author
    ADD CONSTRAINT fk_article_author_author FOREIGN KEY (id_author) REFERENCES public.authors(id_author) ON DELETE CASCADE;


--
-- Name: article_thematic fk_article_thematic_article; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article_thematic
    ADD CONSTRAINT fk_article_thematic_article FOREIGN KEY (id_article) REFERENCES public.articles(id_article) ON DELETE CASCADE;


--
-- Name: article_thematic fk_article_thematic_thematic; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article_thematic
    ADD CONSTRAINT fk_article_thematic_thematic FOREIGN KEY (id_thematic) REFERENCES public.thematics(id_thematic) ON DELETE CASCADE;


--
-- Name: authors fk_author_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.authors
    ADD CONSTRAINT fk_author_user FOREIGN KEY (id_user) REFERENCES public.users(id_user) ON DELETE CASCADE;


--
-- Name: comments fk_comment_article; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fk_comment_article FOREIGN KEY (id_article) REFERENCES public.articles(id_article) ON DELETE CASCADE;


--
-- Name: comments fk_comment_parent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fk_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES public.comments(id_comment) ON DELETE CASCADE;


--
-- Name: comments fk_comment_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.comments
    ADD CONSTRAINT fk_comment_user FOREIGN KEY (id_user) REFERENCES public.users(id_user) ON DELETE CASCADE;


--
-- Name: purchases fk_purchase_article; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT fk_purchase_article FOREIGN KEY (id_article) REFERENCES public.articles(id_article) ON DELETE SET NULL;


--
-- Name: purchases fk_purchase_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.purchases
    ADD CONSTRAINT fk_purchase_user FOREIGN KEY (id_user) REFERENCES public.users(id_user) ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

