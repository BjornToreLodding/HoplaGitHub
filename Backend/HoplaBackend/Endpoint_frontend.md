
# Profil

<table>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/b4ebfe4c9253d1e43e64b8b78cf50690/image.png){width="208" height="408"} <br><br>**Status:** <br>si ifra hvis det ønskes forandringer

`Android: Nå mulig å logge inn`
</td>
<td>

Logg inn

* **`POST` hvis passord og epost stemmer med det i databasen ellers feilmelding -\> token**
  * **test@test.no**\*\* Hopla2025!\*\*

POSTMAN

Post https://hopla.onrender.com/users/login/

Body:

```postman_json
{
"email": "test@test.no", 
"password": "Hopla2025!"
}
```

JSON-Response:

```postman_json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOi
IxMjM0NTY3OC0wMDAwLTAwMDAtMDAwMS0xMjM0NTY3ODAwMDEiLCJlbWFpbCI6InR
lc3RAdGVzdC5ubyIsIm5iZiI6MTc0MDQ4NjkwNCwiZXhwIjoxNzQxMDkxNzA0LCJp
YXQiOjE3NDA0ODY5MDR9.Tds78EAr8iZ0Y6_M0f1lcwk11sgAapfSpwXk5T9RdXU",
    "userId": "12345678-0000-0000-0001-123456780001",
    "name": "Magne Baller Ilufta",
    "alias": "MangeBallerILufra",
    "profilePictureURL": "https://images.unsplash.com/
photo-1614203586837-1da2bef106a2?w=200&h=200&fit=crop"
}
```
</td>
</tr>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/b59f667b696efff50d4b81ce91ca3c72/image.png){width="248" height="456"} <br><br>**Status:** <br>si ifra hvis det ønskes forandringer. <br><br>(NB Bruker ikke userID, men Token)

`Android: Bruker lagret informasjon fra login for øyeblikket for å displaye informasjon`
</td>
<td>

Main profil side

* **`Get `request ~~som bruker userID med informasjon~~ :** (NB Bruker ikke userID, men Token)
  * **Brukernavn(alias), epost og bilde**
  * **(Bilde: bruker size 200.dp og clip circleshape)**
* \*\*GET med Authorization:
  * \*\*GET \*\***https://hopla.onrender.com/users/myprofile** Denne erstattes av den under (/users/profile)
  * \*\*Get \*\***https://hopla.onrender.com/users/profile** Denne kan også brukes til å hente andre brukere ved å spesifisere optional ?userid=Guid
  f.eks https://hopla.onrender.com/users/profile?userid=12345678-0000-0000-0001-123456780002 Mer om dette lenger ned.
  * **auth Type Bearer Token**
  * **Token = "LangTokenStringFraResponsenPå/users/login"**

**JSON Response (Eksempel)**

```Postman_JSON
{
    "alias": "MangeBallerILufra",
    "name": "Magne Baller Ilufta",
    "email": "test@test.no",
    "profilePictureUrl": "https://images.unsplash.com/
photo-1614203586837-1da2bef106a2?h=200&w=200&fit=crop"
}
```
</td>
</tr>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/68cec6509720727d6fcc482677031ce9/image.png){width="324" height="565"} <br><br>**Status:** <br>Denne skal virke når databasen blir oppdatert

`Android: nå lagt inn`
</td>
<td>

**Profil -\> Mine hester**

* `Get` request på å hente brukeren som er logget inn sine hester
* Trenger å få: bilde og navn på hesten (hestens id for å kunne brukes til å gå til detalj siden om hver enkelt hest? )
* \*\*GET med Authorization:
  * \*\*GET GET \*\***https://hopla.onrender.com/horses/userhorses/**
  * **auth Type Bearer Token**
  * **Token = "LangTokenStringFraResponsenPå/users/login"**

**Eksempel på response body JSON**

```Postman_JSON
[
    {
        "id": "12345678-0000-0000-0002-123456780001",
        "name": "Flodhest",
        "horsePictureUrl": "https://images.unsplash.com/
        photo-1599053581540-248ea75b59cb?h=64&w=64&fit=crop"
    },
    {
        "id": "12345678-0000-0000-0002-123456780018",
        "name": "Kronprins Durek",
        "horsePictureUrl": "https://images.unsplash.com/
        photo-1438283173091-5dbf5c5a3206?h=64&w=64&fit=crop"
    },
    {
        "id": "12345678-0000-0000-0002-123456780025",
        "name": "Fola Blakken",
        "horsePictureUrl": "https://images.unsplash.com/
        photo-1438283173091-5dbf5c5a3206?h=64&w=64&fit=crop"
    }
]
```
</td>
</tr>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/6ea310ba1383bdd11b753b2f2803cf05/image.png){width="316" height="517"} <br><br>**Status:** <br>Denne skal virke når databasen blir oppdatert

`Android: lagt inn`
</td>
<td>

**Profil -\> Mine hester -\> Velge en spesifikk hest**

* Get request ut ifra hestens id for å hente: navn, bilde, rase og alder/fødselsdato
* \*\*GET med Authorization:
  * \*\*GET \*\***https://hopla.onrender.com/horses/{horseGuid}**
  * **auth Type Bearer Token** (kan enkelt deaktiveres)
  * **Token = "LangTokenStringFraResponsenPå/users/login"**

  **Eksempel på response body**

```Postman_JSON
  {
    "name": "Flodhest",
    "horsePictureUrl": "https://images.unsplash.com/
    photo-1599053581540-248ea75b59cb?h=200&w=200&fit=crop",
    "dob": "2017-01-25T15:18:15.586439Z",
    "age": 8,
    "breed": "Zebra"
}
```
</td>
</tr>
<tr>
<td>

![image.png](uploads/abb5dca0140be056c4f38b06913611e8/image.png){width="322" height="603"}

\
\
**Status:** \
BT: Denne skal virke nå

`Android: Lagt til venner liste side`
</td>
<td>

**Profil -\> Venner**

* Get request for å hente brukeren som er logget inn sine venner (andre profiler kan være: venn, følger, "pending request" eller none (ingen forhold). På denne siden er det alle profiler som er venn
* Trenger bilde, navn og vennestatus enum verdi (id for å vise person detalj siden)

  (bilde: nå er det 64.dp)

```
// Enum verdier for vennestatuser jeg bruker nå: 
enum class PersonStatus {
    FRIEND,
    FOLLOWING,
    NONE,
    PENDING
}
```

**Eksempel på request**

GET https://hopla.onrender.com/userrelations/friends \
Denne henter vennene til innlogget bruker

GET https://hopla.onrender.com/userrelations/friends?userid=12345678-0000-0000-0001-123456780003 \
Denne henter vennen til oppgitt userid

**NB!! Begge må ha authorization Bearer Token**

**Eksempel på response body**

```Postman_JSON
[
   {
       "friendId": "12345678-0000-0000-0001-123456780006",
       "friendName": "Anna Louise Sedolfsen",
       "friendAlias": "JockeyAnnaLouise",
       "friendPictureURL": "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?w=64&h=64&fit=crop"
   },
   {
       "friendId": "12345678-0000-0000-0001-123456780005",
       "friendName": "Ann Iren Haakenstad",
       "friendAlias": "JockeyAnnIren",
       "friendPictureURL": "https://images.unsplash.com/photo-1554151228-14d9def656e4?w=64&h=64&fit=crop"
   },
   {
       "friendId": "12345678-0000-0000-0001-123456780004",
       "friendName": "Thea Dyring",
       "friendAlias": "JockeyThea",
       "friendPictureURL": "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=64&h=64&fit=crop"
   }
]
```
</td>
</tr>
<tr>
<td>

![image.png](uploads/c7a6e3a1b0bb4e20c192c65bd6bf5531/image.png){width="309" height="502"}

\
\
**Status:** \
BT: Denne skal virke nå

`Android: Lagt til følger liste side`
</td>
<td>

**Profil -\> Følger**

* Get request for å hente brukeren som er logget inn personer som den følger. På denne siden er det alle profiler som er følger
* Trenger bilde, navn og vennestatus enum verdi (id for å vise person detalj siden)

  (bilde: nå er det 64.dp)

**Eksempel på request**

GET https://hopla.onrender.com/userrelations/following \
Denne henter følgere til innlogget bruker

GET https://hopla.onrender.com/userrelations/follwing?userid=12345678-0000-0000-0001-123456780003 \
Denne henter følgere til oppgitt userid

**NB!! Begge må ha authorization Bearer Token**

**Eksempel på response body**

```Postman_JSON
[
  {
      "followingUserId": "12345678-0000-0000-0001-123456780037",
      "followingUserName": "Høysnue Kåre",
      "followingUserAlias": "AlltidISvime",
      "followingUserPicture": "https://images.unsplash.com/photo-1535201344891-231e85e83c8a?w=64&h=64&fit=crop"
  },
  {
      "followingUserId": "12345678-0000-0000-0001-123456780032",
      "followingUserName": "Stalke Ulf",
      "followingUserAlias": "CreepyStalker",
      "followingUserPicture": "https://images.unsplash.com/photo-1535201344891-231e85e83c8a?w=64&h=64&fit=crop"
  },
  {
      "followingUserId": "12345678-0000-0000-0001-123456780038",
      "followingUserName": "Dag Gjesper Lang",
      "followingUserAlias": "GjesperDagenLang",
      "followingUserPicture": "https://images.unsplash.com/photo-1535201344891-231e85e83c8a?w=64&h=64&fit=crop"
  }
]
```
</td>
</tr>
<tr>
<td>

Har ikke bilde for øyeblikket, profil -\> Venner -\> Trykke på spesifikk venn
</td>
<td>

GET request

Profil -\> Venner -\> "navn"

Trenger: id, navn, alias, bilde, beskrivelse, deres delte turer siste 3(både offentlig og venner), antall venner, vennestatus

(Skal komme knapp der man kan trykke på deres venner og deres hester, knapp til resten av turene deres)

**BT:**

📌 **Beskrivelse:** Henter ut informasjon om en bruker, eller en brukers venn. Endpointet vil tilpasse innholdet ettersom ?userId er oppgitt, om brukeren fra token og user id er venner, følger eller blokkerer. Kanskje også man skulle returnert status? slik at når man åpner profilen så står det at dem er venner? Hvis det ikke blir masse styr?

📑 **Parametere:**
|Parameter| Name | Type     | Påkrevd | Beskrivelse |
|------|-----------|--------|---------|-------------|
| 🔒 Header | `Authorization` | Bearer Token  | 🔑 Ja | Krever autenseringstoken | 
| 🔎 Query | `userId`  | Guid   | 🟡 Nei   | ID-en til brukeren |

#### 🔎 Query:

`?userId=[Guid]` - 🟡 Valgfritt: Henter bruker hvis spesifisert. Hvis utelatt hentes bruker ut fra Bearer Token.

#### 💾 Syntax:
```bash
curl -X GET "https://hopla.onrender.com/users/profile?userId=[Guid]" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>"
```
📤 **Eksempel på respons uten query:**
```json
{
    "alias": "MangeBallerILufra",
    "name": "Magne Baller Ilufta",
    "email": "test@test.no",
    "profilePictureUrl": "https://images.unsplash.com/
photo-1614203586837-1da2bef106a2?h=200&w=200&fit=crop"
}
```
📤 **Eksempel på respons med query når bruker fra token er venner med userId fra query:**
```json
{
    "id": "12345678-0000-0000-0001-123456780002",
    "name": "Kamuf Larsen",
    "pictureUrl": "https://plus.unsplash.com/premium_photo-1661868397660-8c52f33c5934?w=200&h=200&fit=crop",
    "alias": "Kamuflasjen",
    "description": "Har utmerket meg spesielt i kunsten å balansere en pinnsvin på hodet mens jeg hopper på trampoline. Er den eneste i historien som har vunnet en sjakkturnering ved å blunke strategisk til motstanderne. Har en medfødt evne til å forstå hva lamaer prøver å si, og kan navigere i ukjente byer ved å lukte seg frem til nærmeste pannekakebod. En gang syklet jeg over en innsjø – ingen vet helt hvordan, men teoriene involverer både helium og viljestyrke.",
    "dob": "2025-03-03T11:06:09.918987Z",
    "created_at": "2025-03-03T11:06:09.918987Z",
    "friendsCount": 1,
    "horseCount": 1
}
```

📤 **Eksempel på respons med query når bruker fra token ikke er venner med userId fra query:**
```json
{
    "id": "12345678-0000-0000-0001-123456780009",
    "name": "Dag Jesper Lang",
    "pictureUrl": "https://images.unsplash.com/photo-1568038479111-87bf80659645?w=200&h=200&fit=crop",
    "alias": "JesperDagenLang",
    "description": "Jeg har en fascinerende evne til å snakke lenge om ting jeg egentlig ikke forstår. En gang forklarte jeg kvantefysikk for en gjeng måker – de var ikke imponert. Jeg mener fortsatt at jeg burde fått en æresdoktorgrad i ‘usannsynlige livsvalg’ og ‘avansert prokrastinering’. På CV-en min står det at jeg er en ‘problemløser’, men det gjelder hovedsakelig problemer jeg selv har skapt.",
    "created_at": "2025-03-03T11:06:11.324384Z"
}
```

📤 **Eksempel på respons med query når bruker fra token er blokkert av userId fra query:**
```json
{ Tomt? skal vi vise noe i det hele tatt? }
```



📟 **Mulige statuskoder:**
- ✅ `200 OK` – Hester ble hentet.
- ❌ `401 Unauthorized` - Ingen eller ugyldig token sendt.
- ❌ `404 Not Found` – Bruker ikke funnet.

</td>
</tr>
<tr>
<td>

Har ikke bilde for øyeblikket, profil -\> Følger -\> Trykke på spesifikk person
</td>
<td>

GET request

Profil -\> Følger -\> "navn"

Trenger: id, navn, alias, bilde, beskrivelse, deres delte turer siste 3(bare offentlig), antall venner, vennestatus

**Samme som over**

(Her skal man ikke kunne trykke på deres hester eller venner, knapp for resten av turene deres)
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_144252_com.example.hopla\[1\].jpg](uploads/6ba2e122049ff6aefa3667996dcc60e3/Screenshot_20250303_144252_com.example.hopla_1_.jpg){width="283" height="567"}
</td>
<td>

Profil -\> Mine turer

GET request

Trenger: navn, bilde(r), dato, tid, lengde, status (offentlig, privat, kun venner)
</td>
</tr>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/b59f667b696efff50d4b81ce91ca3c72/image.png){width="244" height="448"}
</td>
<td>

Profil -\> Endre profilbilde

Skal frontend sende et bilde

Trenger iallfall endpoint for å bytte profilbilde her
</td>
</tr>
<tr>
<td>

![image.png](uploads/46a60d398a0249dff5e40c607f8e2e20/image.png){width="320" height="579"}
</td>
<td>

(Alle disse sidene skal displaye lister på samme måte)

* Get request for: id, navn, bilde, stjerner (0-5)
* Hvordan få likt status for brukeren på disse løypene?
* Hente de 10 første? Så når brukeren blar ned så sendes ny request på de 10 neste osv?
* Sorteres etter nyeste øverst
* Tenke på: offentlig, privat, kun venner?

**Løyper -\> Første side**

* Vise alle løyper som brukere har lagt inn i appen. Flest stjerner øverst (hvis likt antall stjerner, nyeste av de øverst. Runde opp så det er f.eks 5 istede for 4.6 så sortere). Løypene må være offentlig eller fra venner

**Løyper -\> Icon 2 fra venstre**

* Vise løyper nærmest brukerens posisjon

**Løyper -\> Hjerte ikon**

* Kun løyper som brukeren har trykket liker på

**Løyper -\> Stjerne ikon (bytte til 2 personers ikon)**

* Løyper til brukere brukeren følger og venner med
</td>
</tr>
<tr>
<td>

![image.png](uploads/6d56ffc28e83cf25bbf1f62fb664e3f6/image.png){width="307" height="576"}
</td>
<td>

Alle innlegg her skal sorteres etter at det nyeste vises øverst

Brukere skal kunne gi dem "likes". Skal stå hvor mange likes innlegget har fått (symbol skal prøve å være logoen)

**Hjem -\> Alt**

* Denne skal inneholde: Nye løyper lagt til, nye kommentarer på løyper fra alle brukere i appen.
* 10 og 10 innlegg etter hvert som man blar blir hentet (samme måte som over)
  * Hvis løype: bilde, id, navn, løype beskrivelse, id og brukernavn på bruker som har registrert løypa
  * Hvis ny kommentar på løype: løype navn, løype id, kommentaren selv, evt bilde lagt til i kommentaren, brukernavn og brukerid til brukeren som la til kommentaren

**Hjem -\> (ikon 2 personer)**

* Samme som over, men alle løyper og kommentarer som hentes må være venn eller følge med brukeren som er logget in
* Også hente for venner:
  * Stjerner gitt av venner på løyper (trenger da num stjerner, løype navn og løype id, brukerid og brukernavn)
  * Hvis venner har lagt til nye hester kan det også komme her

**Hjem -\> Hjerte**

* Oppdateringer og kommentarer på løyper man har likt

**Hjem -\> Område**

* Samme som første bare at det er en viss avstand fra brukeren på alt som vises her

**Hjem -\> Populært siste 30 dager**

* Samme som første bare sortert etter likes
</td>
</tr>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/b4ebfe4c9253d1e43e64b8b78cf50690/image.png){width="208" height="408"}
</td>
<td>

Startsiden:

Glemt passord: sender med en epost i requesten, som den da må sjekke at den finnes i databasen for så å på en måte sende en mail der brukeren kan bytte passordet sitt?

Opprett bruker:

trinn 1: epost, passord

trinn 2: alias, navn, beskrivelse (optional), fødselsdato, tlf (optional), bilde (optional)
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_153409_com.example.hopla\[1\].jpg](uploads/48f1f5a098d2503eeea4aa1b65124930/Screenshot_20250303_153409_com.example.hopla_1_.jpg)
</td>
<td>

Profil -\> Innstillinger -\> Send en rapport (skal også legges inn: innlegg(hjem), løyper, profiler, community)

Bruker sender med: navn på rapport, beskrivelse og userid (skal senere displayes i nettsiden)

Sende med riktig tabell

Hvis innlegg: innleggid,

Hvis løyper: løypeid

Hvis profiler: profilid

Hvis community: communityid.

Profil -\> Innstillinger -\> Slett bruker

Bruker må skrive inn passordet sitt som må bekreftes stemmer (Sjekkes i backend) så slette brukeren om det stemmer eller feilmelding hvis ikke
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_154409_com.example.hopla\[1\].jpg](uploads/29558607e424a4acb2bc443c5240107f/Screenshot_20250303_154409_com.example.hopla_1_.jpg)
</td>
<td>

Endpoint som henter alle filtere i databasen. Da er det lettere å endre i senere tid hvis Hopla vil legge til nye/slette enn å hardkode navnene. 

Nå har jeg det satt opp slik (ikke lagt til "riktig" filter):

![image.png](uploads/ecddb58073732bbca48a85d2f3230d6e/image.png){width="383" height="207"}
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_153900_com.example.hopla\[1\].jpg](uploads/f2a20e8cf7fed8f256f7ddf95b2f2190/Screenshot_20250303_153900_com.example.hopla_1_.jpg)
</td>
<td>

Løyper -\> Kart

Hente alle start-koordinater til løyper (longitude og latitude)

\-\> Hvordan skal man hente de innenfor kartutsnittet?

BT oppdaterer 
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_154024_com.example.hopla\[1\].jpg](uploads/43f5349ec9d480b13b70e8304a4bb46f/Screenshot_20250303_154024_com.example.hopla_1_.jpg)
</td>
<td>

Løyper -\> Kart -\> Trykke på en spesifikk løype

Da tegnes en strek mellom alle koordinatene som tilhører løypa fra start-koordinatet

BT oppdaterer
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_154741_com.example.hopla\[1\].jpg](uploads/dda7d3a7724bd06b1087879b2f05aa12/Screenshot_20250303_154741_com.example.hopla_1_.jpg)
</td>
<td>

Community/Fellesskap/Grupper

Ta bort hjerte, bytte hjerte symbol med "mine grupper symbol" 

Symbol av "checked symbol" som viser at du er medlem ellers ikke noe

For å bli med: offentlig bare å trykke på  bli med, hvis privat må man sende forespørsel 

Hente: navn, bilde, id, medlemstatus sorteres etter nærmest brukeren 

Hvis man henter eks. 10 og 10 grupper etter som brukeren blar nedover.

hvordan gjøres det med muligheten til å søke gjennom grupper (sende med hver og hver bokstav etterhvert som man skriver som da viser de 10 øverste som passer hvis man ikke blar nedover) 
</td>
</tr>
</table>

