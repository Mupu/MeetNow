/* */
insert into person (vorname, nachname) values ("Hans", "Müller");
insert into person (vorname, nachname) values ("Peter", "Fischer");
insert into person (vorname, nachname) values ("Alex", "Schmidt");
insert into person (vorname, nachname) values ("Karl", "Becker");
insert into person (vorname, nachname) values ("Ursula", "Hoffmann");
insert into person (vorname, nachname) values ("Maria", "Klein");
insert into person (vorname, nachname) values ("Monika", "Mayer");
insert into person (vorname, nachname) values ("Sophie", "Schneider");
insert into person (vorname, nachname) values ("Sara", "Koch");
insert into person (vorname, nachname) values ("Roxanne", "Peters");

insert into raum (ort, anzahlStuhl, anzahlTisch, anzahlLaptop, Whiteboard, Barrierefrei, Klimaanlage) values ("oben", 10, 5, 10, 1, 0, 1);
insert into raum (ort, anzahlStuhl, anzahlTisch, anzahlLaptop, Whiteboard, Barrierefrei, Klimaanlage) values ("unten", 5,0, 5, 1, 1, 1);
insert into raum (ort, anzahlStuhl, anzahlTisch, anzahlLaptop, Whiteboard, Barrierefrei, Klimaanlage) values ("mitte", 8, 4, 8, 1, 0, 0);

insert into ausstattungsgegenstand (name, anzahl) values ("Beamer", 2);
insert into ausstattungsgegenstand (name, anzahl) values ("Stifftpaket", 10);
insert into ausstattungsgegenstand (name, anzahl) values ("Lautsprecher", 6);

insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (1, "Hans", "Müller", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (2, "Peter", "Fischer", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (3, "Alex", "Schmidt", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (4, "Karl", "Becker", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (5, "Ursula", "Hoffmann", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (6, "Maria", "Klein", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (7, "Monika", "Mayer", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (8, "Sophie", "Schneider", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (9, "Sara", "Koch", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (10, "Roxanne", "Peters", 1, 0);

insert into besprechung (raumId, besitzerId, thema, zeitraumStart, zeitraumEnde) values (1, 1, "test1", "2018-10-29 13:10:00.000", "2018-10-29 14:10:00.000");
insert into besprechung (raumId, besitzerId, thema, zeitraumStart, zeitraumEnde) values (2, 9, "test2", "2018-10-29 13:10:00.000", "2018-10-29 14:10:00.000");
insert into besprechung (raumId, besitzerId, thema, zeitraumStart, zeitraumEnde) values (1, 6, "test3", "2018-10-29 15:10:00.000", "2018-10-29 16:10:00.000");

insert into ausleihe values (1, 1);
insert into ausleihe values (1, 3);
insert into ausleihe values (2, 2);


insert into teilnahme values (1, 1);
insert into teilnahme values (4, 1);
insert into teilnahme values (6, 1);

insert into teilnahme values (9, 2);
insert into teilnahme values (3, 2);
insert into teilnahme values (10, 2);

insert into teilnahme values (6, 3);
insert into teilnahme values (5, 3);
insert into teilnahme values (9, 3);