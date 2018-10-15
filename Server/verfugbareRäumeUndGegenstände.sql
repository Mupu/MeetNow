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