# userVerwalten

select p.Vorname, p.Nachname, p.Email, b.*, group_concat(r.Name) as rechte from benutzer as b
left join person p on b.PersonId = p.PersonId
right join user_role u on b.BenutzerId = u.BenutzerId
right join role r on u.RoleId = r.RoleId
group by b.BenutzerId
;

# verfügbare gegenstände
select AusstattungsgegenstandId, sum(Anzahl) as verfugbar                   # übrige gegenstände zum gewählten zeitraum
from (select AusstattungsgegenstandId, Anzahl from ausstattungsgegenstand   # alle gegenstände mit max anzahl
      union
      select a.AusstattungsgegenstandId, sum(Anzahl) * (-1) as benutzt                # alle gegenstände die mit der reservieanzahl
      from ausleihe as a                                                              # zu dem bestimmten zeitraum
             left join (select BesprechungId        #alle bnrn die zu dem zeitpunkt belegt sind
                        from besprechung
                        where ('2018-10-29 13:30:00' between zeitraumStart and zeitraumEnde)
                           or ('2018-10-29 14:10:00' between zeitraumStart and zeitraumEnde)) as b
               on a.BesprechungId = b.BesprechungId
      where b.BesprechungId is not null
      group by AusstattungsgegenstandId
     ) as letzte
group by AusstattungsgegenstandId
having verfugbar > 0;

#verfügbare räume
select r.RaumId
from raum as r
left join (select RaumId
                     from besprechung
                     where ('2018-10-29 13:30:00' between zeitraumStart and zeitraumEnde)
                        or ('2018-10-29 14:10:00' between zeitraumStart and zeitraumEnde)
                     group by RaumId) as b
    on r.RaumId = b.RaumId
where b.RaumId is not null