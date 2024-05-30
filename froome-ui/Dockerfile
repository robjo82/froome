# Étape 1 : Construire l'application Vue.js
FROM node:16-alpine as build-stage

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers package.json et package-lock.json
COPY package*.json ./

# Installer les dépendances
RUN npm install

# Copier le reste des fichiers de l'application
COPY . .

# Construire l'application pour la production
RUN npm run build

# Étape 2 : Servir l'application avec Nginx
FROM nginx:stable-alpine as production-stage

# Copier les fichiers de build de l'étape précédente
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copier le fichier de configuration Nginx
COPY nginx.conf.template /etc/nginx/nginx.conf.template
COPY entrypoint.sh /entrypoint.sh

# Donner les permissions d'exécution au script d'entrée
RUN chmod +x /entrypoint.sh

# Exposer le port 80
EXPOSE 80

# Utiliser le script d'entrée pour lancer Nginx
CMD ["/entrypoint.sh"]
