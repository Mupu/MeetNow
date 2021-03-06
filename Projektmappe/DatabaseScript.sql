
DROP DATABASE meetnow;
CREATE DATABASE IF NOT EXISTS meetnow;

USE meetnow;

CREATE TABLE IF NOT EXISTS Raum (
   RaumId INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	Ort CHAR(50) NOT NULL,
	anzahlStuhl INT(10) UNSIGNED NOT NULL,
	anzahlTisch INT(10) UNSIGNED NOT NULL,
	anzahlLaptop INT(10) UNSIGNED NOT NULL,
	Whiteboard TINYINT(1) NOT NULL,
	Barrierefrei TINYINT(1) NOT NULL,
	Klimaanlage TINYINT(1) NOT NULL,
	PRIMARY KEY (RaumId)
);

CREATE TABLE IF NOT EXISTS Person (
   PersonId INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	Vorname CHAR(50) NOT NULL,
	Nachname CHAR(50) NOT NULL,
	PRIMARY KEY (PersonId)
);

CREATE TABLE IF NOT EXISTS Ausstattungsgegenstand (
   AusstattungsgegenstandId INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	Name CHAR(50) NOT NULL,
	Anzahl INT(10) UNSIGNED NOT NULL DEFAULT '1',
	PRIMARY KEY (AusstattungsgegenstandId)
);

CREATE TABLE IF NOT EXISTS Benutzer (
   BenutzerId INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	PersonId INT(10) UNSIGNED NOT NULL,
	Benutzername CHAR(50) NOT NULL,
	Passwort CHAR(50) NOT NULL,
	AccountStatus TINYINT(1) UNSIGNED NOT NULL COMMENT '0=newDataOnLogin; 1=normal',
	Rechte TINYINT(1) UNSIGNED NOT NULL COMMENT '0=user;1=admin',
	PRIMARY KEY (BenutzerId),
	UNIQUE INDEX Benutzername (Benutzername),
	INDEX Benutzer_FKIndex1 (PersonId),
	CONSTRAINT benutzer_ibfk_1 FOREIGN KEY (PersonId) REFERENCES person (PersonId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Besprechung (
   BesprechungId INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	RaumId INT(10) UNSIGNED NOT NULL,
	BesitzerId INT(10) UNSIGNED NOT NULL,
	Thema CHAR(50) NOT NULL,
	zeitraumStart TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	zeitraumEnde TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (BesprechungId),
	INDEX Besprechung_FKIndex1 (BesitzerId),
	INDEX Besprechung_FKIndex2 (RaumId),
	CONSTRAINT besprechung_ibfk_1 FOREIGN KEY (BesitzerId) REFERENCES benutzer (BenutzerId) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT besprechung_ibfk_2 FOREIGN KEY (RaumId) REFERENCES raum (RaumId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ausleihe (
   BesprechungId INT(10) UNSIGNED NOT NULL,
	AusstattungsgegenstandId INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (BesprechungId, AusstattungsgegenstandId),
	INDEX ausleihe_FKIndex1 (BesprechungId),
	INDEX ausleihe_FKIndex2 (AusstattungsgegenstandId),
	CONSTRAINT ausleihe_ibfk_1 FOREIGN KEY (BesprechungId) REFERENCES besprechung (BesprechungId) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT ausleihe_ibfk_2 FOREIGN KEY (AusstattungsgegenstandId) REFERENCES ausstattungsgegenstand (AusstattungsgegenstandId) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS teilnahme (
   BenutzerId INT(10) UNSIGNED NOT NULL,
	BesprechungId INT(10) UNSIGNED NOT NULL,
	PRIMARY KEY (BenutzerId, BesprechungId),
	INDEX Benutzer_has_Besprechung_FKIndex1 (BenutzerId),
	INDEX Benutzer_has_Besprechung_FKIndex2 (BesprechungId),
	CONSTRAINT teilnahme_ibfk_1 FOREIGN KEY (BenutzerId) REFERENCES benutzer (BenutzerId) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT teilnahme_ibfk_2 FOREIGN KEY (BesprechungId) REFERENCES besprechung (BesprechungId) ON UPDATE CASCADE ON DELETE CASCADE
);

