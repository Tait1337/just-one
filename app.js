var express = require('express');
var app = require('express')();
var http = require('http').createServer(app);
var path = require('path');
var favicon = require('serve-favicon');
var io = require('socket.io')(http);

var minPlayer = 3;
var maxPlayer = 7; // TODO: support higher count of players
var deckSize = maxPlayer * 2 -1;
var cards = [
    ['Schmetterling', 'Puder', 'Porzellan', 'Markt', 'Kokon'],
    ['Pfeffer', 'Dreieck', 'Puppe', 'Italien', 'Bühne'],
    ['Asterix', 'Roboter', 'Komödie', 'Sprache', 'Friseur'],
    ['Revolver', 'Maske', 'Gift', 'Kernkraft', 'Fluss'],
    ['Pistole', 'Dracula', 'Brille', 'Zunge', 'Ellenbogen'],
    ['Buffy', 'Vulkan', 'Leidenschaft', 'Hahn', 'Elektrizität'],
    ['Gold', 'Geschichte', 'Bier', 'Familie', 'Stoßstange'],
    ['Herz', 'Moskau', 'Pool', 'Einhorn', 'Orange'],
    ['Zwerg', 'Zirkus', 'Elvis', 'Rasenmäher', 'Stein'],
    ['Radio', 'Birne', 'Gladiator', 'Sonne', 'Getreide'],
    ['Google', 'Bogen', 'Mars', 'Golf', 'Ticket'],
    ['Fuchs', 'Polizei', 'Simpson', 'Donut', 'Zeitung'],
    ['Start-up', 'Wald', 'Waage', 'Zeus', 'Schnecke'],
    ['Dorf', 'Wahnsinn', 'Flamme', 'Zombie', 'Joker'],
    ['Bulldogge', 'Narbe', 'Kupfer', 'Agent', 'Geburtstag'],
    ['Limette', 'Untersuchung', 'Gotik', 'Titanic', 'Maschiene'],
    ['Raupe', 'Humor', 'Ecke', 'Antarktis', 'Strand'],
    ['Torte', 'Astronaut', 'Feige', 'Automat', 'Glocke'],
    ['Hammer', 'Chile', 'Graffiti', 'Stacheldraht', 'Ballon'],
    ['Terminator', 'Brief', 'Kugel', 'Paradies', 'Ei'],
    ['Holliwood', 'Mücke', 'Lanze', 'Börse', 'Wohnwagen'],
    ['Barbie', 'Schokolade', 'Schnee', 'Krawatte', 'Wind'],
    ['Gedanke', 'Frankenstein', 'Strahl', 'Akzent', 'Dusche'],
    ['Gift', 'Stern', 'Wolf', 'Jones', 'Geburtstag'],
    ['Gewehr', 'Aluminium', 'Bär', 'Delfin', 'Strahlen'],
    ['Diener', 'Feuerwehr', 'Glas', 'Kloß', 'Griechenland'],
    ['Bauernhof', 'Harke', 'Computer', 'Nest', 'Antenne'],
    ['Karies', 'Ärmel', 'Gremlins', 'Poker', 'Kuchen'],
    ['Sombrero', 'Kleeblatt', 'Taste', 'Käse', 'Chewbacce'],
    ['Serie', 'Nest', 'Gewürz', 'Eiszeit', 'Kommando'],
    ['Pfingsten', 'Gummi', 'Topf', 'Flash', 'Tunnel'],
    ['Frankreich', 'Mond', 'Kantine', 'Kaiserpinguin', 'Werkzeug'],
    ['Meister', 'Pony', 'Rätsel', 'Alkohol', 'Lava'],
    ['Kanone', 'Freund', 'Höllenfeuer', 'Bakterien', 'Soße'],
    ['Leber', 'Brombeere', 'Tempel', 'Sand', 'Pommes'],
    ['Schmerzmittel', 'Amor', 'Regenschirm', 'Sprung', 'Wurzel'],
    ['Krokodil', 'Schmuck', 'U-Bahn', 'Brille', 'Flacon'],
    ['Papagei', 'Ast', 'Krake', 'Zyklus', 'Oper'],
    ['Doktor', 'Erdbeere', 'Blase', 'Asche', 'Täter'],
    ['Wunderkerze', 'Hans', 'Casino', 'Bonbon', 'Brunnen'],
    ['Misthaufen', 'Zauberer', 'Hockey', 'Banane', 'Helene'],
    ['Kleid', 'Facebook', 'Eichel', 'Gräte', 'Würdenträger'],
    ['Tradition', 'Schlange', 'Pokal', 'Picasso', 'Uhr'],
    ['Stempel', 'Vampir', 'Monopol', 'Ente', 'Digital'],
    ['Mafia', 'Groß', 'Hamster', 'Flocke', 'Beurteilung'],
    ['Ziege', 'Brot', 'Teufel', 'Grundschule', 'Normal'],
    ['Raumschiff', 'Bowling', 'Churchill', 'Widder', 'Zerqutschen'],
    ['Stock', 'Musketier', 'Faden', 'Tulpe', 'Ikea'],
    ['Europa', 'Zirkus', 'Virus', 'Krokodil', 'Senf'],
    ['Marienenkäfer', 'Senf', 'Scherlock', 'Flasche', 'Virus'],
    ['Anmesie', 'Betrüger', 'Entwurf', 'Prophet', 'Wolke'],
    ['König', 'Motor', 'Friseur', 'Terror', 'Portal'],
    ['Sattel', 'Juwel', 'Gorilla', 'Ufo', 'Nachbar'],
    ['Eidechse', 'Pflanze', 'Erde', 'Planet', 'Schal'],
    ['Punkt', 'Unterarm', 'Schwein', 'Windeln', 'Bienenwarbe'],
    ['Lanze', 'Dart', 'Schimmel', 'Absatz', 'Depression'],
    ['Crew', 'Turnschuhe', 'Gitter', 'Moschee', 'Jaguar'],
    ['Tot', 'Amor', 'Fliese', 'Dieb', 'Antike'],
    ['Rutsche', 'Eisen', 'Peru', 'Poker', 'Fliege'],
    ['Recht', 'Berater', 'Schweiz', 'Fernglass', 'Forscher'],
    ['Kamin', 'Pinzette', 'Diagonale', 'Schritte', 'Musik'],
    ['Hamster', 'Autokino', 'Netzwerk', 'Leder', 'Entdecker'],
    ['Revolution', 'Anwalt', 'Ehefrau', 'Dromedar', 'Historiker'],
    ['Dachfenster', 'Auster', 'Dosenöffner', 'Kabine', 'Deckel'],
    ['Bazooka', 'Zugriff', 'Rollschuhe', 'Schlaganfall', 'Sessellift'],
    ['Haselnuss', 'Wendekreis', 'Vertrag', 'Trick', 'Aroma'],
    ['Harfe', 'Schaltung', 'Temperatur', 'Diamant', 'Balance'],
    ['Pilot', 'Scherz', 'Dolmetscher', 'Mandel', 'Spule'],
    ['Keim', 'Tentakel', 'Pilz', 'Knieschoner', 'Hotel'],
    ['Bote', 'Bank', 'Vorspeise ', 'Paris', 'Kapelle'],
    ['Pfad', 'Kapelle', 'Hals', 'Luft', 'Kampf'],
    ['Warze', 'Sommer', 'Familie', 'Zwiebel', 'Tempel'],
    ['Lokomotive', 'Legende', 'Sandwich', 'Karton', 'Urlaub'],
    ['Reifen', 'Walross', 'Schlamm', 'Kofferraum', 'Verein'],
    ['Teenager', 'Senat', 'Selbstmord', 'Wald', 'Container'],
    ['Allianz', 'Mittagessen', 'Junge', 'Literatur', 'Kanu'],
    ['Strichcode', 'Berlin', 'Zucht', 'Welt', 'Kind'],
    ['Schwiegertochter', 'Meile', 'Nachname', 'Jahreswechsel', 'Leinwand'],
    ['Hut', 'Gürtel', 'Fernsehsender', 'Frau', 'Mann'],
    ['Wahrheit', 'Spaß', 'Geige', 'Puzzel', 'Bierfass'],
    ['Schmalz', 'Himmel', 'Papst', 'Spielzeugauto', 'Erzieher'],
    ['Segelflugzeug', 'Telefonbuch', 'Handy', 'Messgerät', 'Locher'],
    ['Regen', 'Schrift', 'Rennen', 'Satz', 'Anzeige'],
    ['Drucker', 'Müllabfuhr', 'Unterschrift', 'Immobilie', 'Salz'],
    ['Strom', 'Hafen', 'Opa', 'Arbeit', 'Autogramm'],
    ['Büro', 'Strafzettel', 'Bundestag', 'Aktie', 'Mikrofon'],
    ['DVD', 'Fabel', 'Akku', 'Bauer', 'Kindergarten'],
    ['Taufe', 'Bergbahn', 'Holz', 'Telefonnummer', 'Astrologie'],
    ['Herbst', 'Roman', 'Schadenfreude', 'Folterkeller', 'Kamera'],
    ['Signal', 'Ofen', 'PKW', 'Dame', 'Reisepass'],
    ['Bombe', 'Ständer', 'Verbrechen', 'Zange', 'Achse'],
    ['Kommentar', 'Kommentar', 'Kind', 'Brot', 'Quelle'],
    ['Fitnessstudio', 'Tonne', 'Natur', 'Abendbrot', 'Auskunft'],
    ['Verbot', 'Brille', 'Mann', 'Tunnel', 'Universum'],
    ['Müll', 'Zelt', 'Wand', 'Vater', 'Postkarte'],
    ['Kabel', 'Papierflieger', 'LKW', 'Kegel', 'Anschlag'],
    ['Flugbegleiter', 'Copilot', 'Ehe', 'Radler', 'Ingenieur'],
    ['Anwalt', 'Verlag', 'Emoji', 'Zugabteil', 'Segelschiff'],
    ['Ohrring', 'Reich', 'Zoll', 'China', 'Infrastruktur'],
    ['Trend', 'Training', 'Spülmaschine', 'Lampenfieber', 'Hexe'],
    ['Sauerstoff', 'Abflug', 'Schallplattenspieler', 'Kaserne', 'Smartphone'],
    ['Verein', 'Schlüssel', 'Schublade', 'Katze', 'Brötchen'],
    ['Ausstellung', 'Vogelfutter', 'Butter', 'Socke', 'Comic'],
    ['Boot', 'Dessert', 'Tennis', 'Koteletten', 'Alaska'],
    ['Kiwi', 'Schuppen', 'Plagiat', 'Soldat', 'Krankenwagen'],
    ['Fleisch', 'Scheidung', 'Muskel', 'Keller', 'Gasse'],
    ['Hamburger', 'Kiefer', 'Kohle', 'Plan', 'Pass'],
    ['Rügen', 'See', 'Schild', 'Tor', 'Weizen'],
    ['Rute', 'Otter', 'Niete', 'Spinne', 'Zylinder'],
    ['Wanze', 'Maus', 'Löffel', 'Bremse', 'Blatt']
];

class Hint {
    constructor(userid, word) {
        this.userid = userid;
        this.word = word;
        this.status = 0; // 0 = initial; 1 = approved; 2 = declined
    }
    approve(userid, status) {
        if (userid !== this.userid) {
            throw 'user not allowed to update hint status';
        }
        if (status !== 0 || status !== 1 || status != 2) {
            throw 'illegal status code for hint';
        }
        this.status = status;
    }

}

var games = [];
class Game {
    constructor(gameid) {
        this.gameid = gameid;
        this.started = false;
        this.points = 0;
        this.users = [];
        this.lastActiveUserIndex = -1;
        this.deck = [];
        this.lastCardIndex = -1;
        this.selectedCardOption = -1;
        this.hints = [];
        this.guess = undefined;
    }

    join(userid) {
        var user = this.users.find(user => user === userid);
        if (user !== undefined) {
            throw 'Spielername existiert bereits im Spiel';
        }
        this.users.push(userid);
    }

    leave(userid) {
        var userIndex = this.users.findIndex(user => user === userid);
        if (userIndex !== -1) {
            this.users.splice(userIndex, 1);
        }
        if (this.users.length === 0) {
            console.log('Game ' + this.gameid + ' closed');
            var gameIndex = games.findIndex(game => game === this.gameid);
            games.splice(gameIndex, 1);
        }
    }

    startRound() {
        if (this.users.length < minPlayer) {
            throw 'At least ' + minPlayer + ' user are required to play the game';
        }
        if (this.users.length > maxPlayer) {
            throw 'At max ' + maxPlayer + ' user are allowed to play the game';
        }

        // init
        if (this.started === false || this.lastActiveUserIndex === -1 || this.lastCardIndex === -1 || this.deck.length === 0) {
            this.started = true;
            // choose random starter
            this.lastActiveUserIndex = Math.round(Math.random() * (this.users.length - 1));
            // choose random deck of all cards
            var deckIds = new Set();
            while (deckIds.size < deckSize) {
                var randomNumber = Math.floor(Math.random() * Math.floor(cards.length));
                deckIds.add(randomNumber);
            }
            deckIds.forEach(deckId => {
                this.deck.push(cards[deckId]);
            });
        }

        // check if end is reached
        console.log('Spielende: ' + this.lastCardIndex + '/' + (this.deck.length -1) );
        if (this.lastCardIndex === (this.deck.length -1)) {
            io.to(this.gameid).emit('game-ended', this);
        }

        // select next player
        var nextPlayerIndex = this.lastActiveUserIndex + 1;
        if (nextPlayerIndex >= this.users.length) {
            nextPlayerIndex = 0;
        }
        this.lastActiveUserIndex = nextPlayerIndex;

        // select next card
        var nextCardIndex = this.lastCardIndex + 1;
        this.lastCardIndex = nextCardIndex;

        // reset round based variables
        this.hints = [];
        this.selectedCardOption = -1;
    }

    chooseCardOption(selectedOptionIndex) {
        // TODO: allow asking for another option on the card if not understandable
        this.selectedCardOption = selectedOptionIndex;
    }

    chooseHint(hint) {
        var hintIndex = this.hints.findIndex(h => h.userid === hint.userid);
        if (hintIndex !== -1) {
            this.hints.splice(hintIndex, 1);
        }
        this.hints.push(hint);

        var allHintsDeclined = true;
        this.hints.forEach(hint => {
            if (hint.status !== 2) {
                allHintsDeclined = false;
            }
        });
        if (allHintsDeclined) {
            io.to(this.gameid).emit('no-hints-left', this);
        }
    }

    chooseGuess(userid, word) {
        if (this.hints.length != (this.users.length - 1)) {
            throw 'not all hints are provided';
        }
        var hintIndex = this.hints.findIndex(hint => hint.status === 0);
        if (hintIndex !== -1) {
            throw 'not all hints are approved';
        }
        this.guess = word;
        var activeCard = this.deck[this.lastCardIndex];
        var solution = activeCard[this.selectedCardOption];
        if (this.guess.toUpperCase() === solution.toUpperCase()) {
            // win
            this.points = this.points + 1;
        } else {
            // loose
            if ((this.lastCardIndex + 1) === this.deck.length && this.points > 0) {
                this.points = this.points - 1;
            }
        }
    }

    skip(userid) {
        // TODO: support skipping the round
    }
}

function startOrGetGame(gameid) {
    var game = games.find(game => game.gameid === gameid);
    if (game === undefined) {
        game = new Game(gameid);
        games.push(game);
    }
    return game;
}

/* endpoints */
io.on('connection', function (socket) {

    socket.on('join-game', function (payload) {
        console.log(payload.userid + ' joining game ' + payload.gameid);
        try {
            var game = startOrGetGame(payload.gameid);
            if (game.started === true) {
                throw 'Spiel wurde bereits gestartet';
            }
            game.join(payload.userid);
            socket.join(game.gameid);
            game.users.forEach(userid => {
                socket.emit('game-joined', userid);
            });
            socket.broadcast.to(game.gameid).emit('game-joined', payload.userid);;
        } catch (e) {
            console.error('ERROR: ' + e);
            socket.emit('errorMsg', e);
        }
    });

    socket.on('leave-game', function (payload) {
        console.log(payload.userid + ' leaving game ' + payload.gameid);
        try {
            var game = startOrGetGame(payload.gameid);
            game.leave(payload.userid);
            io.to(game.gameid).emit('game-leaved', payload.userid);
            socket.leave(game.gameid);
        } catch (e) {
            console.error('ERROR: ' + e);
            socket.emit('errorMsg', e);
        }
    });

    socket.on('start-game', function (payload) {
        console.log(payload.userid + ' started game/next round ' + payload.gameid);
        try {
            var game = startOrGetGame(payload.gameid);
            game.startRound();
            io.to(game.gameid).emit('game-started', game);
        } catch (e) {
            console.error('ERROR: ' + e);
            socket.emit('errorMsg', e);
        }
    });

    socket.on('choose-cardoption', function (payload) {
        console.log(payload.userid + ' choosed card option #' + payload.selectedCardOption + ' for game ' + payload.gameid);
        try {
            var game = startOrGetGame(payload.gameid);
            game.chooseCardOption(payload.selectedCardOption);
            io.to(game.gameid).emit('cardoption-choosed', game);
        } catch (e) {
            console.error('ERROR: ' + e);
            socket.emit('errorMsg', e);
        }
    });

    socket.on('choose-hint', function (payload) {
        console.log(payload.userid + ' choosed hint/guess ' + payload.hint.word + ' for game ' + payload.gameid + ' status is ' + payload.hint.status);
        try {
            var game = startOrGetGame(payload.gameid);
            if (payload.userid === game.users[game.lastActiveUserIndex]) {
                // choosed guess
                game.chooseGuess(payload.userid, payload.hint.word);
                io.to(game.gameid).emit('guess-committed', game);
            } else {
                // choosed hint
                game.chooseHint(payload.hint);
                if (game.hints.length === game.users.length - 1) {
                    var allChoosed = true;
                    var allCommited = true;
                    game.hints.forEach(hint => {
                        if (hint.status === 0) {
                            allCommited = false;
                        } else {
                            allChoosed = false;
                        }
                    });
                    if (allCommited) {
                        io.to(game.gameid).emit('all-hints-committed', game);
                    } else if (allChoosed) {
                        io.to(game.gameid).emit('all-hints-choosed', game);
                    }
                }
            }
        } catch (e) {
            console.error('ERROR: ' + e);
            socket.emit('errorMsg', e);
        }
    });

});


/* common web server config */
app.use(favicon(path.join(__dirname, 'client', 'favicon.ico')));
app.use('/img', express.static(__dirname + '/client/img'));
app.get('/', function (req, res) {
    res.sendFile(__dirname + '/client/index.html');
});

http.listen(3000, function () {
    console.log('Just One Webserver is listening on *:3000');
});