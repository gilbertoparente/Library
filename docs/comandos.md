# na branch feature
git add .
git commit -m "feature pronta"
git push origin feature-login-registo

# merge via GitHub (Pull Request)

# depois no Rider
git checkout main
git pul
# criação de nova branch
git checkout -b feature-desktop
git push -u origin feature-desktop

# backup da base de dados
pg_dump -U postgres -d scientific_library --schema-only > C:\Users\gilbe\Desktop\002_schema.sql

# backup completo com dados

pg_dump -U postgres -d scientific_library -F c -f C:\Users\gilbe\Desktop\backup.backup

# restaurar a base de dados

pg_restore -U postgres -d nova_base --disable-triggers C:\Users\gilbe\Desktop\backup.backup



### code first

createdb -U postgres scientific_library

psql -U postgres -d scientific_library -f 002_schema.sql
psql -U postgres -d scientific_library -f 003_seed.sql