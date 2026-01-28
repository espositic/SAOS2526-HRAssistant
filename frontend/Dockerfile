FROM node:20-alpine AS build
WORKDIR /app

# Copiamo i file dei pacchetti PRIMA del resto del codice
# Questo permette a Docker di riutilizzare i pacchetti scaricati se non cambiano
COPY package*.json ./
RUN npm install

# Poi copiamo il resto e buildiamo
COPY . .
RUN npm run build

# Stage Finale (Nginx)
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]