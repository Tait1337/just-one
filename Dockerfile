############################################################################
# Web Application with Node.JS (918 MB)
#
# build from project root dir with: docker build -t just-one:latest .
# run with: docker run -p 80:80 -d just-one:latest
############################################################################
FROM node:10

# App
WORKDIR /urs/src/app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
EXPOSE 80:3000
ENTRYPOINT [ "node", "app.js"]