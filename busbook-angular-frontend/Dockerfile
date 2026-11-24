# ── Stage 1 : Build Angular ────────────────────────────────────────────────────
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npx ng build --configuration production

# ── Stage 2 : Servir avec Nginx ────────────────────────────────────────────────
FROM nginx:alpine
COPY --from=builder /app/dist/busbook-angular-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
