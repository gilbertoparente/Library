-- USERS
INSERT INTO public.users (name, email, password, is_admin) VALUES
                                                               ('João Silva', 'joao@email.com', '123456', true),
                                                               ('Maria Costa', 'maria@email.com', '123456', false),
                                                               ('Pedro Santos', 'pedro@email.com', '123456', false);

-- AUTHORS
INSERT INTO public.authors (id_user, affiliation, status) VALUES
                                                              (1, 'Universidade de Lisboa', 1),
                                                              (2, 'Universidade do Porto', 1);

-- THEMATICS
INSERT INTO public.thematics (description) VALUES
                                               ('Inteligência Artificial'),
                                               ('Bases de Dados'),
                                               ('Engenharia de Software');

-- ARTICLES
INSERT INTO public.articles (title, resume, publication_date, price, doi, keywords)
VALUES
    ('Introdução à Inteligência Artificial',
     'Artigo introdutório sobre conceitos de IA',
     '2024-01-10',
     9.99,
     '10.1000/ia001',
     'IA, machine learning'),

    ('Modelação de Bases de Dados',
     'Aborda modelação relacional e normalização',
     '2024-02-15',
     12.50,
     '10.1000/db001',
     'SQL, normalização');

-- ARTICLE_AUTHOR
INSERT INTO public.article_author (id_article, id_author) VALUES
                                                              (1, 1),
                                                              (2, 2);

-- ARTICLE_THEMATIC
INSERT INTO public.article_thematic (id_article, id_thematic) VALUES
                                                                  (1, 1),
                                                                  (2, 2);

-- COMMENTS (nota: FK recursiva)
INSERT INTO public.comments (id_article, id_user, content)
VALUES
    (1, 2, 'Excelente artigo!'),
    (1, 3, 'Muito útil para iniciantes.');

-- COMMENT REPLY (usa parent_comment_id)
INSERT INTO public.comments (id_article, id_user, parent_comment_id, content)
VALUES
    (1, 1, 1, 'Obrigado pelo feedback!');

-- PURCHASES
INSERT INTO public.purchases (id_user, id_article, amount, status)
VALUES
    (2, 1, 9.99, 'paid'),
    (3, 2, 12.50, 'pending');