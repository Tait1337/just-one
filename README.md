# Just One

## How to setup development env
Install [Node.js](https://nodejs.org)
Run: npm init

## How to start the application for development
Run: npm start

## How to build the applicaton as docker image
Run: docker build -t justone:1.0.0 .

## How to run the application as docker image
Run: docker run -p 80:3000 -d --name justone justone:1.0.0 && docker logs -f justone