# Just One Online

Web Application version of Asmodée's Just One board game.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Install Node.js 10 or higher.

```
https://nodejs.org
```

Install Docker.
```
https://docs.docker.com/get-docker/
```

### Installing

Clone the Repository.
```
git clone https://github.com/tait1337/just-one.git
```

Init the Project with npm.
```
./npm init
```

Run the Web Application.
```
npm start
```

Navigate to http://localhost.
Hint: click on the Logo to watch the [introduction Video](https://youtu.be/IzXhC_NQctg).

![Main Page](screenshot_index.png)

Join a game with up to 7 player.

![Gallery Page](screenshot_game.png)

### Configuration

Within [app.js](app.js) can 
* change the minimum and maximum number of players
* change game cards

## Running the tests

No Tests.

## Deployment

The most basic option to run the Application is by building the Dockerimage.

```
./docker build -t just-one:latest .
./docker run -p 80:80 -d just-one:latest
```

## Contributing

I encourage all the developers out there to contribute to the repository and help me to update or expand it.

To contribute just create an issue together with the pull request that contains your features or fixes.

## Versioning

We use [GitHub](https://github.com/) for versioning. For the versions available, see the [tags on this repository](https://github.com/tait1337/just-one/tags). 

## Authors

* **Oliver Tribess** - *Initial work* - [tait1337](https://github.com/tait1337)

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Original Board Game Publisher Asmodée