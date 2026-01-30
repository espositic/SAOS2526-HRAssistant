# Usiamo Node.js per buildare l'app
FROM node:20-alpine AS build
WORKDIR /app

# Copiamo solo i file dei pacchetti. Se non aggiungi nuove librerie, 
# Docker salterà il comando "npm install" nelle build successive, risparmiando minuti.
COPY package*.json ./
RUN npm install

# Copiamo tutto il resto del codice sorgente
COPY . .
# Generiamo la cartella /dist (codice HTML/JS compresso e ottimizzato)
RUN npm run build

# Usiamo Nginx, un server web per servire file statici.
FROM nginx:stable-alpine
# Prendiamo solo il risultato della build dallo stage precedente
# Copiamo il contenuto di /app/dist nella cartella dove Nginx cerca i siti web
COPY --from=build /app/dist /usr/share/nginx/html
# Copiamo la nostra configurazione personalizzata di Nginx (per gestire il routing di React)
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Esponiamo la porta 80 (standard HTTP)
EXPOSE 80
# Avviamo Nginx in primo piano
CMD ["nginx", "-g", "daemon off;"]