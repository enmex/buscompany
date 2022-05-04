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
	user_type ENUM('ADMIN', 'CLIENT'),
    PRIMARY KEY (id),
    UNIQUE(login)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `session` (
    id_user INT NOT NULL,
    uuid VARCHAR(60) NOT NULL,
    last_time_active TIME NOT NULL,
    FOREIGN KEY (id_user) REFERENCES `user` (id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `admin` (
    id_user INT NOT NULL,
    position VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_user),
    FOREIGN KEY (id_user) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `client` (
    id_user INT NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
	PRIMARY KEY (id_user),
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
    bus_name VARCHAR(50),
    from_station VARCHAR(60) NOT NULL,
    to_station VARCHAR(60) NOT NULL,
    `start` TIME NOT NULL,
    duration TIME NOT NULL,
    price INT NOT NULL,
    approved BOOL NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (bus_name) REFERENCES bus (bus_name) ON DELETE SET NULL
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE trip_date (
	id INT NOT NULL AUTO_INCREMENT,
    id_trip INT NOT NULL,
    `date` DATE NOT NULL,
    free_places INT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id_trip) REFERENCES trip(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `order` (
	id INT NOT NULL AUTO_INCREMENT,
    id_client INT NOT NULL,
    id_trip_date INT NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(id_client) REFERENCES `user`(id) ON DELETE CASCADE,
    FOREIGN KEY(id_trip_date) REFERENCES trip_date(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE passenger (
	id INT NOT NULL AUTO_INCREMENT, 
    id_order INT NOT NULL,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    passport VARCHAR(50) NOT NULL,
    PRIMARY KEY(id),
    FOREIGN KEY(id_order) REFERENCES `order`(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE seats (
    id_trip_date INT NOT NULL,
    id_passenger INT,
    place_number INT NOT NULL,
    UNIQUE(id_passenger, place_number),
    FOREIGN KEY (id_trip_date) REFERENCES trip_date (id) ON DELETE CASCADE,
    FOREIGN KEY (id_passenger) REFERENCES passenger(id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `schedule` (
	id_trip INT NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    period VARCHAR(50) NOT NULL,
	FOREIGN KEY (id_trip) REFERENCES trip (id) ON DELETE CASCADE
)