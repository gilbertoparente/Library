-- =========================
-- USERS
-- =========================
INSERT INTO public.users (name, email, password, is_admin)
VALUES
    ('João Silva', 'joao.silva@email.com', 'hash123', true),
    ('Maria Santos', 'maria.santos@email.com', 'hash123', false),
    ('Pedro Costa', 'pedro.costa@email.com', 'hash123', false),
    ('Ana Ferreira', 'ana.ferreira@email.com', 'hash123', false);

-- =========================
-- AUTHORS (ligados a users)
-- =========================
INSERT INTO public.authors (id_user, affiliation, status)
VALUES
    (1, 'Universidade do Porto', 1),
    (2, 'Instituto Politécnico de Lisboa', 1),
    (3, 'Universidade de Coimbra', 1);

-- =========================
-- THEMATICS
-- =========================
INSERT INTO public.thematics (description)
VALUES
    ('Tecnologia'),
    ('Ciência'),
    ('Educação'),
    ('Saúde'),
    ('Economia');

-- =========================
-- ARTICLES
-- =========================
INSERT INTO public.articles
(title, resume, publication_date, price, file_path, status, doi, keywords, external_author)
VALUES
    (
        'Introdução à Inteligência Artificial',
        'Um artigo introdutório sobre conceitos fundamentais de IA.',
        '2024-01-15',
        9.99,
        '/artigos/ia_intro.pdf',
        'published',
        '10.1000/ia001',
        'IA, machine learning, tecnologia',
        NULL
    ),
    (
        'Avanços na Medicina Moderna',
        'Discussão sobre novas técnicas e descobertas médicas.',
        '2024-02-10',
        12.50,
        '/artigos/medicina.pdf',
        'published',
        '10.1000/med002',
        'medicina, saúde, inovação',
        NULL
    ),
    (
        'O Futuro da Educação Digital',
        'Análise das tendências da educação online.',
        '2024-03-05',
        0,
        '/artigos/educacao.pdf',
        'draft',
        '10.1000/edu003',
        'educação, e-learning, tecnologia',
        'Dr. Carlos Mendes'
    );

-- =========================
-- ARTICLE_AUTHOR (N:N)
-- =========================
INSERT INTO public.article_author (id_article, id_author)
VALUES
    (1, 1),
    (1, 2),
    (2, 3),
    (3, 2);

-- =========================
-- ARTICLE_THEMATIC (N:N)
-- =========================
INSERT INTO public.article_thematic (id_article, id_thematic)
VALUES
    (1, 1),
    (1, 2),
    (2, 4),
    (3, 3),
    (3, 1);

-- =========================
-- COMMENTS (com hierarquia)
-- =========================
INSERT INTO public.comments (id_article, id_user, parent_comment_id, content, status)
VALUES
    (1, 2, NULL, 'Excelente introdução ao tema!', 1),
    (1, 3, 1, 'Concordo, muito bem explicado.', 1),
    (2, 4, NULL, 'Artigo bastante informativo.', 1),
    (3, 2, NULL, 'Aguardando publicação final.', 0);

-- =========================
-- PURCHASES
-- =========================
INSERT INTO public.purchases (id_user, id_article, amount, status)
VALUES
    (2, 1, 9.99, 'paid'),
    (3, 2, 12.50, 'paid'),
    (4, 1, 9.99, 'pending');
