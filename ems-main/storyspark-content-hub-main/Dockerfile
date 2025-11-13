FROM node:18

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

RUN npm run build    # <-- This creates dist/

EXPOSE 8081

CMD ["npm", "run", "preview", "--", "--host", "--port", "8081"]
