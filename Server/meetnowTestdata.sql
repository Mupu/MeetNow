USE meetnow;

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

insert into role (name) values ("USER");
insert into role (name) values ("ADMIN");
insert into role (name) values ("SUPERADMIN");

insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (1, "Hans", "12345:dd3f1660ff3df7d267fe70f565c0f109:c21c45e3523f86499f8f5f706ae5fd26cff393cc7d8f98a051630650bc523b96acb80eed57ae697b74b7507c0bcf03b8ea052ca5d4b6951f4ce3bfe2580baa9f43393d5bd7929b6d8bc259e5ca0aa63363b9cbcaf6bc887b87772a6a103115d5c7894927f1f8734c708a296fbb0ae275a4fc1989be0307a97f5616bc81417ff7120301651de40f08fc4ca07d80c41fe928f7cfb7ffdff068630451032308edcab93f7b748e45422e8aeeb3a4da48d7b16eb0a69dd1969a5dd881dc1096912d9703f448ff57cfd7b30a46c5107ef021ddce12899bb9954ca2e1273e9eab4754492f49b7bf8c46e8e546dc2fde617f9774842a97c78273555891325dc416ce550a", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (2, "Peter", "12345:972d23497f7e22d1cb9da60cf45510fb:7a13ad98a812e738acd1dc9c4738159daa84be842a5e18d3d2415d17d069315185f26bac747cb7b15970bac5896cea456b64c3440ee0cedbc29b4b4710069b3e866dbc80ea0e0eb840427ea1bb78e043816844c4913c438710873f5805f09ae8c6822acb25c152e196697f71564382e8465cbdef41afa63ce1d7dbba0dabb4fedfe21dba6a10efd6b99acc694f87b11428cbb0021636436f3f8ff727e44b03f8a79afb1b64efa54e72624c16ce4cd5191778a3eca2f707ed3170f02476ee7e81c59cfafc8669ba304db89dfee313d3f9ba62b5e3dfd5f604cd894dbdbc65a613b74439322693c55cb98c2bbf7213540167d8fee77353b3a42eca069ec9e513e2", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (3, "Alex", "12345:e33eb273629626913a809244d98872f9:a048332bb58910b062c986565d3529a3fd751504bc11e1a7c3b36022e8fdf48a5652ed3451c3761910312d05ad5a7f5cfbc6361eeb69dabab9322748387ee52739fe68ea7d1bec8659c0d7ca8d0fe28a41e17bf156c73f649d802f392607e604a0e1d7b3c3bc0460e2f66300a2a34c6090e34e1838144103339306649496a46aa16f728b02e6ad42e47df3f83a705db8ca63ef0ae3afeefff07ffd55598252047f54dbbf4b72dc3bbc7a9b1e6c7b42c375cf5acf9ba3aaf62bc9f07c20b01cc9dd1bebdccc873cd45b40f58440a6fdbd88959fb00bdd2631e5c6ac8cce9427a269f6c6e40eefa1b42d8fc194800caceec3e108dc8cb4be221980809cd3cab5bd", 1, 1);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (4, "Karl", "12345:9adba7fec4a65b690ca904183115851f:799455f094084ec4d7ae0b4fef6ecc932bd4cf7b809cfe4ac8415ee32cc42c975b6c8c14d1f8d416a22f18c8f0161201610acbb4279d6c441220aa8a396390a4b25b5e367a4556f226d9128eed66f2cab834300b3c912df72b6f37521aece62a3fa3c825610135a96370ab43e0a2ef07c7de4438fc1597b61d236df2ab00539ec915a2db70d9fa730014151c95105832df8215cdae007f57f282c60133315735e186af42c494e373e934d0e10c881f2a79afe15761c22338a318a63c0a865989aeac4b7a77601694331e6f95b9b705b43ba0f7fb71aa215e1b7eb936bf5a12e18b3f574541e785858beb091f6ae258c1b0fb497edbf6e06b50f31f81dc34e235", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (5, "Ursula", "12345:222b3227668f2425b054c9413a5c9229:8f99d860c686e96a0eea34a834e04d9dd6569ac4cce2ed8c017b7eafae0b06bb542fc80008d679a7a7fb603d87709d3a3cfca86f2fa4856db018f3f7c519986f2a2f2a15f832f507e05011d956bb4263be5bc73f4df59fe8be49d6d213433095f4838e2542c3fdf70ab7bb54da7535ba3ec596dd1fb8965835507b1ccaab5c10f1beaf03c978ce17885060936ad90616360774a2316ada9a233e3041876fa73a2eecf22a6ee7c9e2d88140253f420e945eb54e6d8680787a1ed4de1a0b01026a57d4a9db9190227e4583643fb3011b7f3cd5dc44445158ab8e3a89c1536092cdbb1c19125d4361638f35d383a524e4657e55316914290b97257458699392d7dd", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (6, "Maria", "12345:9b9cc8f84971fd904a23fd87dfbfa6bf:2143c6c6c0607fecb879e6ebdebd4fc81a405e5af9597da1ad098f02c71cf95d29ebc1e1a708a28d5fe04ee632cdc22e1581898ffdcb62aad71b526476487bc85b3b909b9141b5a608b2b16225037a87833bbbc8afe5692af7f30b319af0f5121de1ceca5825f3ef0880928ce98fab6217a605956b08b96a30ae8f0010d2112ae60038e6f5e2757a1e384ce7c8b632a1a0886788afe710deb0d0a9dd02ba24e568a9c73b3515d006ffab357222788bfcab4310365540762eed28b377712e18b4afe8a221c8e8ef613c9375422eb7956d06e9d287335d36a7403f5fd6e898d42d163f14ec8957d1f7b730844aeaa99b2e37f12703b4e7989bc440fd852c56a320", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (7, "Monika", "12345:07473093ce3b38d352dd026c42723aa5:3ca9f9769ec95649a92133e35211f5db6143cc6a807105ff54d8e0cafdfe8ce3f6b6e3af2d0ba884735e168ed824fbd4261e6b2cf2bcfe67e645826f51376b6b074608e229cafc8211175045060624ea532b84dccf334b95e7aaddab389349c47053f10ad8f6d95f7dca38468dd4a49bf90cdbc53a7427789300649db272b94dc68f2c4cbdc46086748da42de4dcfc7803c1d7bb050848dd0c4b36f55a4abde839a29d782c36191c79c8da28c9abcd3de2b1fd71d7957c64860c1fdfc1dff293cbb9d9538084ce598633390727d3ab6db840095a5f378b6f2d865382fbe1091ace8a92838a5e3dc606177fcc44403b5ebe818d4ecd71e955cce22216bb93d062", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (8, "Sophie", "12345:b9879bc14bf5c626bf12ca82163bc0be:678f2510cdf681b1072b3bfd209e4c8a99fb210997e2d176e1145fac065264f6d595b9701abdec79fe1fdd23f0694035f4f6e7879e95fb6444ad1fbebbd805960ca4a0770a99dcca6949666cff067ec7173c25d175e794caa0d78e025ef1240e1687e9bb317c3404fe50a847fda1d5d60eae1f5417ba2c0dcb6b2df592e8582ffc18b9b82f3a2a7a19b014fd05790551f4f5969b9b3271fb445b6373ca66fae14823ef4fa39fe5ea8864a6679db066ffaba61b156bf05b2fe58937741f4dd3a95c9c6a5de45eb4a84816f1f4f5af498dfefc77b6af11624ffad0f1358ba0739cae5ed2ad7824c956b5e1da075178acd8b0c5b78ae82fa5c1903ae009f2d48343", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (9, "Sara", "12345:896726789951771d43da915591194aba:d3fc9ad17e37ffdb12309521e4680edf577531149d6cea630c88b20f5887115e6e74aa727bf0ff4d89dfebad3dfbf1b7b61562a7846e3e52a0df8ceaed6b75c03e461eb69eef5c3072bafba00e74085dfbf0601ce221ae1772d6f325ca028ff4f4e8d0a4a8882fd95cb86c6a386d972fdcb816c419a8dd4554d6451ef2e15f4f1bd9b992c0093b803ee932525c6f4d43c5a7eb43a9ce80295375875e0c2d4b7b60ce86650b0874531d0521481a45bf9190a9de19d4f13c625d7601a9e3a534bd23e48db1d22b51441f9b386c15a48fdff0db615a69ed1e3af92ebdeee8513eea72ac994c56e8e953ec77993a8ee74b06dfa55f6d7924de32a97db762dbaecd21", 1, 0);
insert into benutzer (personId, benutzername, `passwort`, accountstatus, rechte) values (10, "RoxannePeters", "12345:a02960e6be668dc9398a059b9e7a9ee1:ead2ae0ff67446f00bf3d28a99f80515c8848500fc3e23a80e4ced0b3e2713710e0b325081abb941d094b0b77424bd3628fea15a7d234bb61cbdce197bfcf5090c2dce2722373d9ef0b5d9d25593c6ca4884dead50672092f3dbc19ad3e2bd06312b847ad16559f52d8d32a20203db13437f9621697f708ee06da922d83598b0a76801101dfd1c9cee317097c447c0dd327784767b67a0570a1932a72528a12c46bff236a4dd67aef77232cd410f7b9b5d08de691009c53f217ba5a4b8069a5ee8b3b51ccd6b862e78f32f86de302897fa9dbb2962b385617c766e586691565a2893ca3c7c32d7a8211faaf2e581397b76b44e01d165571ff265f717beae929e", 0, 0);

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

insert into user_role values (3,1);
insert into user_role values (3,2);
insert into user_role values (3,3);

insert into user_role values (1,1);
insert into user_role values (2,1);
insert into user_role values (4,1);
insert into user_role values (5,1);
insert into user_role values (6,1);
insert into user_role values (7,1);
insert into user_role values (8,1);
insert into user_role values (9,1);
insert into user_role values (10,1);