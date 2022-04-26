DROP DATABASE IF EXISTS buscompany;
CREATE DATABASE buscompany;
USE buscompany;

CREATE TABLE `user` (
	id INT NOT NULL AUTO_INCREMENT,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    patronymic VARCHAR(50),
    login VARCHAR(50) NOT NULL,
    `password` VARCHAR(50) NOT NULL,
	user_type ENUM('admin', 'client'),
    PRIMARY KEY (id),
    UNIQUE(login)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `session` (
	id INT NOT NULL AUTO_INCREMENT,
    id_user INT NOT NULL,
    uuid VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES `user` (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `admin` (
	id INT NOT NULL AUTO_INCREMENT,
    id_user INT NOT NULL,
    position VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `client` (
	id INT NOT NULL AUTO_INCREMENT,
    id_user INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE bus (
	id INT NOT NULL AUTO_INCREMENT,
    bus_name VARCHAR (50) NOT NULL,
    seats_number INT DEFAULT 50,
    PRIMARY KEY (id),
    UNIQUE (bus_name)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip (
	id INT NOT NULL AUTO_INCREMENT,
    bus_name VARCHAR(50) NOT NULL,
    from_station VARCHAR(60) NOT NULL,
    to_station VARCHAR(60) NOT NULL,
    `start` TIME NOT NULL,
    duration TIME NOT NULL,
    price INT NOT NULL,
    approved BOOL NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (bus_name) REFERENCES bus (bus_name) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip_date (
	id INT NOT NULL AUTO_INCREMENT,
    id_trip INT NOT NULL,
    `date` DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_trip) REFERENCES trip(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip_schedule (
	id INT NOT NULL AUTO_INCREMENT,
    id_trip INT NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    period VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_trip) REFERENCES trip(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE ticket (
	id INT NOT NULL AUTO_INCREMENT,
    id_client INT NOT NULL,
    id_trip INT NOT NULL,
    `date` DATE NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(id_client) REFERENCES `user`(id),
    FOREIGN KEY(id_trip) REFERENCES trip(id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE passenger (
	id INT NOT NULL AUTO_INCREMENT, 
    id_ticket INT NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    passport VARCHAR(50) NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(passport),
    FOREIGN KEY(id_ticket) REFERENCES ticket(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE occupied_seats (
	id INT NOT NULL AUTO_INCREMENT,
    id_ticket INT NOT NULL,
    id_passenger INT NOT NULL,
    place_number INT NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(id_passenger, place_number),
    FOREIGN KEY (id_ticket) REFERENCES ticket(id) ON DELETE CASCADE,
    FOREIGN KEY (id_passenger) REFERENCES passenger(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;