# na branch feature
git add .
git commit -m "feature pronta"
git push origin feature-login-registo

# merge via GitHub (Pull Request)

# depois no Rider
git checkout main
git pull
# criação de nova branch
git checkout -b feature-desktop
git push -u origin feature-desktop