


# **OBS**

trails/all mangler: beskrivelse av løypen

Skal trails/map få liste med koordinater når zoomlevel er under et vist nivå? Gjerne få lagt inn dette så jeg får testet

GET /stables/{stableId} -\> Kan det sendes med om stallen er privat eller public her? Så det kan brukes til å vise innhold eller ikke? Eller hvordan tenker du med det? Bildet her fører ikke til noe ordentlig sted/ikke noe bilde vises

POST /stables/create -\> Form data istede for raw data for bilder?

# **Kort informasjon**

Backend: :green_book: Lagd :yellow_circle: Delvis laget :red_circle: Ikke lagd

Android: :alien: Lagt inn :grimacing: Delvis lagt inn :smiling_imp: Ikke lagt inn

iOs: :green_apple: Lagt inn :banana: Delvis lagt inn :apple: Ikke lagt inn

# **Liste**

<table>
<tr>
<td>

![image.png](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/b4ebfe4c9253d1e43e64b8b78cf50690/image.png){width="208" height="408"}<br><br>**Status:**<br>si ifra hvis det ønskes forandringer

`Android: Nå mulig å logge inn`

`NB. Har fått en annen response.`

`Hvis redirect = profile, så sendes brukeren til profilsiden.`

`Hvis redirect = update, må brukeren oppdatere brukerinformasjonen sin`

`Det for profil-siden er det nok noe overflødig informasjon, men dette kan bare ignoreres. Det er nødvendig for "update"`
</td>
<td>

## **:green_book: :alien: :green_apple: POST /users/login**

Logg inn

* **`POST` hvis passord og epost stemmer med det i databasen ellers feilmelding -\> token**
  * **test@test.no**\*\* Hopla2025!\*\*

POSTMAN

Post https://hopla.onrender.com/users/login/

Body:

```postman_json
{
"email": "test@test.no", 
"password": "Hopla2025!"
}
```

JSON-Response hvis bruker ikke har registrert name eller alias (f.eks akuratt bekreftet epostadressen.):

```postman_json
{
    "token": "123xyz...XYZ",
    "userId": "2b46f82a-2e38-47b6-a08e-cc62d10f4503",
    "name": null,
    "alias": null,
    "telephone": null,
    "description": null,
    "dob": "2025-03-12T09:38:46.8994Z",
    "pictureUrl": "",
    "redirect": "update"
}
```

**For update, se lengere ned i dokumenter om registrering**

JSON-Response for brukere som har registrert navn og alias

```json
    "token": "123xyz...XYZ",
    "userId": "2b46f82a-2e38-47b6-a08e-cc62d10f4503",
    "name": Hest,
    "alias": Test,
    "telephone": null,
    "description": "Jeg tester hester",
    "dob": "2025-03-12T09:38:46.8994Z",
    "pictureUrl": "",
    "redirect": "profile"
```
</td>
</tr>
<tr>
<td>

![image.png](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/b59f667b696efff50d4b81ce91ca3c72/image.png){width="248" height="456"}<br><br>**Status:**<br>si ifra hvis det ønskes forandringer.<br><br>(NB Bruker ikke userID, men Token)

`Android: Bruker lagret informasjon fra login for øyeblikket for å displaye informasjon`
</td>
<td>

## **:green_book: :alien: :green_apple: GET /users/profile**

Main profil side

* **`Get `request ~~som bruker userID med informasjon~~ :** (NB Bruker ikke userID, men Token)
  * **Brukernavn(alias), epost og bilde**
  * **(Bilde: bruker size 200.dp og clip circleshape)**
* \*\*GET med Authorization:
  * \*\*GET \*\***https://hopla.onrender.com/users/myprofile** Denne erstattes av den under (/users/profile)
  * \*\*Get \*\***https://hopla.onrender.com/users/profile** Denne kan også brukes til å hente andre brukere ved å spesifisere optional ?userid=Guid f.eks https://hopla.onrender.com/users/profile?userid=12345678-0000-0000-0001-123456780002 Mer om dette lenger ned.
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

![image.png](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/68cec6509720727d6fcc482677031ce9/image.png){width="324" height="565"}<br><br>**Status:**<br>Denne skal virke når databasen blir oppdatert

`Android: Lagt inn`
</td>
<td>

## **:green_book: :alien: :green_apple: GET /horses/userhorses**

**Profil -\> Mine hester**

* `Get` request på å hente brukeren som er logget inn sine hester
* Trenger å få: bilde og navn på hesten (hestens id for å kunne brukes til å gå til detalj siden om hver enkelt hest? )
* \*\*GET med Authorization:
  * \*\*GET GET \*\***https://hopla.onrender.com/horses/userhorses/**
  * **auth Type Bearer Token**
  * **Token = "LangTokenStringFraResponsenPå/users/login"**
* GET https://hopla.onrender.com/horses/userhorses?userid=12345678-0000-0000-0001-123456780003 -\> Hester tilhørende brukerid

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

![image.png](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/6ea310ba1383bdd11b753b2f2803cf05/image.png){width="316" height="517"}<br><br>**Status:**<br>Denne skal virke når databasen blir oppdatert

`Android: lagt inn`
</td>
<td>

## **:green_book: :alien: :green_apple: GET /horses/{horseId}**

**Profil -\> Mine hester -\> Velge en spesifikk hest**

* Get request ut ifra hestens id for å hente: navn, bilde, rase og alder/fødselsdato
* \*\*GET med Authorization:
  * \*\*GET \*\***https://hopla.onrender.com/horses/{horseGuid}**
  * **auth Type Bearer Token** (kan enkelt deaktiveres)
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
**Status:**\
BT: Denne skal virke nå

`Android: Lagt til venner liste side & venners venner`
</td>
<td>

## **:green_book: :alien: :green_apple: GET /userrelations/friends**

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

GET https://hopla.onrender.com/userrelations/friends\
Denne henter vennene til innlogget bruker

GET https://hopla.onrender.com/userrelations/friends?userid=12345678-0000-0000-0001-123456780003\
Denne henter vennen til oppgitt userid

Alle brukere: https://hopla.onrender.com/users/all

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
**Status:**\
BT: Denne skal virke nå

`Android: Lagt til følger liste side`
</td>
<td>

## **:green_book: :alien: :green_apple: GET /userrelations/following**

**Profil -\> Følger**

* Get request for å hente brukeren som er logget inn personer som den følger. På denne siden er det alle profiler som er følger
* Trenger bilde, navn og vennestatus enum verdi (id for å vise person detalj siden)

  (bilde: nå er det 64.dp)

**Eksempel på request**

GET https://hopla.onrender.com/userrelations/following\
Denne henter følgere til innlogget bruker

GET https://hopla.onrender.com/userrelations/follwing?userid=12345678-0000-0000-0001-123456780003\
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

`Android: Lagt til må bare fikse det bedre i frontend`
</td>
<td>

## **:green_book: :alien: :apple: GET /users/profile?userid=**

GET request

Profil -\> Venner -\> "navn"

Trenger: id, navn, alias, bilde, beskrivelse, deres delte turer siste 3(både offentlig og venner), antall venner, vennestatus

(Skal komme knapp der man kan trykke på deres venner og deres hester, knapp til resten av turene deres)

Vilde bruker: https://hopla.onrender.com/users/profile?userId=12345678-0000-0000-0001-123456780001&pageNumber=1

**BT:**

:pushpin: **Beskrivelse:** Henter ut informasjon om en bruker, eller en brukers venn. Endpointet vil tilpasse innholdet ettersom ?userId er oppgitt, om brukeren fra token og user id er venner, følger eller blokkerer. Kanskje også man skulle returnert status? slik at når man åpner profilen så står det at dem er venner? Hvis det ikke blir masse styr?

:bookmark_tabs: **Parametere:**

| Parameter | Name | Type | Påkrevd | Beskrivelse |
|-----------|------|------|---------|-------------|
| :lock: Header | `Authorization` | Bearer Token | :key: Ja | Krever autenseringstoken |
| :mag_right: Query | `userId` | Guid | :yellow_circle: Nei | ID-en til brukeren |
| :mag_right: Query | `pageNumber` | int | :yellow_circle: Nei | Side nummer |
| :mag_right: Query | `pageSize` | int | :yellow_circle: Nei | Antall resultater pr side |

:mag_right: Query:

* `?userId=[Guid]` - :yellow_circle: Valgfritt: Henter bruker hvis spesifisert. Hvis utelatt hentes bruker ut fra Bearer Token.
* `?pageNumber=[int]` - :yellow_circle: Valgfritt: Viser neste resultater. Hvis ikke oppgitt, settes denne til 1.
* `?pageSize=[int]` - :yellow_circle: Valgfritt: Antall resultater pr side. Hvis ikke oppgitt, settes denne til angit verdi i SystemSettings

:floppy_disk: Syntax:

```bash
curl -X GET "https://hopla.onrender.com/users/profile?userId=[Guid]" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>"
```

:outbox_tray: **Eksempel på respons uten query:**

```json
{
    "alias": "MangeBallerILufra",
    "name": "Magne Baller Ilufta",
    "email": "test@test.no",
    "profilePictureUrl": "https://images.unsplash.com/
photo-1614203586837-1da2bef106a2?h=200&w=200&fit=crop"
}
```

:outbox_tray: **Eksempel på respons med query når bruker fra token er venner med userId fra query:**

```json
{
    "id": "12345678-0000-0000-0001-123456780002",
    "name": "Kamuf Larsen",
    "pictureUrl": "https://plus.unsplash.com/premium_photo-1661868397660-8c52f33c5934?w=200&h=200&fit=crop",
    "alias": "Kamuflasjen",
    "description": "Har utmerket meg spesielt i kunsten å balansere en pinnsvin på hodet mens jeg hopper på trampoline. Er den eneste i historien som har vunnet en sjakkturnering ved å blunke strategisk til motstanderne. Har en medfødt evne til å forstå hva lamaer prøver å si, og kan navigere i ukjente byer ved å lukte seg frem til nærmeste pannekakebod. En gang syklet jeg over en innsjø – ingen vet helt hvordan, men teoriene involverer både helium og viljestyrke.",
    "dob": "2025-03-07T21:41:44.639116Z",
    "created_at": "2025-03-07T21:41:44.639116Z",
    "friendsCount": 1,
    "horseCount": 1,
    "relationStatus": "FRIENDS",
    "userHikes": [
        {
            "id": "12345678-0000-0000-0011-123456780029",
            "trailName": "Stabekkløypa",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        },
        {
            "id": "12345678-0000-0000-0011-123456780028",
            "trailName": "Høvikrunden",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        },
        {
            "id": "12345678-0000-0000-0011-123456780027",
            "trailName": "Fornebutravbane",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        }
    ],
    "page": 1,
    "size": 3
}
```

:outbox_tray: **Eksempel på respons med query når bruker fra token ikke er venner med userId fra query:**

```json
{
    "id": "12345678-0000-0000-0001-123456780009",
    "name": "Dag Jesper Lang",
    "pictureUrl": "https://images.unsplash.com/photo-1568038479111-87bf80659645?w=200&h=200&fit=crop",
    "alias": "JesperDagenLang",
    "description": "Jeg har en fascinerende evne til å snakke lenge om ting jeg egentlig ikke forstår. En gang forklarte jeg kvantefysikk for en gjeng måker – de var ikke imponert. Jeg mener fortsatt at jeg burde fått en æresdoktorgrad i ‘usannsynlige livsvalg’ og ‘avansert prokrastinering’. På CV-en min står det at jeg er en ‘problemløser’, men det gjelder hovedsakelig problemer jeg selv har skapt.",
    "created_at": "2025-03-07T19:20:35.720707Z",
    "relationStatus": "PENDING",
    "userHikes": [
        {
            "id": "12345678-0000-0000-0011-123456780062",
            "trailName": "Lommedalsrunden",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        },
        {
            "id": "12345678-0000-0000-0011-123456780061",
            "trailName": "Gjøviksruta",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        },
        {
            "id": "12345678-0000-0000-0011-123456780060",
            "trailName": "Biriløypa",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        }
    ],
    "page": 1,
    "size": 3
}
```

:outbox_tray: **Eksempel på respons med query når bruker fra token er blokkert av userId fra query:**

```json
{ Tomt? skal vi vise noe i det hele tatt? }
```

:pager: **Mulige statuskoder:**

* :white_check_mark: `200 OK` – Brukeren ble hentet.
* :x: `401 Unauthorized` - Ingen eller ugyldig token sendt.
* :x: `404 Not Found` – Bruker ikke funnet.
* :x: `500 Internal Server Error` – Server Feil.
</td>
</tr>
<tr>
<td>

Har ikke bilde for øyeblikket, profil -\> Følger -\> Trykke på spesifikk person

`Android: Samme som over`
</td>
<td>

## **:green_book: :alien: :apple: GET /userrelations/following?userid=**

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

`Android: Lagt til med pagenumber øker når knappen last mer trykkes på`
</td>
<td>

## **:green_book: :alien: :apple: GET /userhikes/user**

Profil -\> Mine turer

GET request

Trenger: navn, bilde(r), dato, tid, lengde, status (offentlig, privat, kun venner)

**BT: Har laget dette endpointet:**

\*\*### **GET /usershikes/user**

:pushpin: **Beskrivelse:** Henter ut informasjon om turer til liste som vises på f.eks profil eller turoversikt til en bruker.

:bookmark_tabs: **Parametere:**

| Parameter | Name | Type | Påkrevd | Beskrivelse |
|-----------|------|------|---------|-------------|
| :lock: Header | `Authorization` | Bearer Token | :key: Ja | Krever autenseringstoken |
| :mag_right: Query | `userId` | Guid | :yellow_circle: Nei | ID-en til brukeren |
| :mag_right: Query | `pageNumber` | int | :yellow_circle: Nei | Side nummer |
| :mag_right: Query | `pageSize` | int | :yellow_circle: Nei | Antall resultater pr side |

\*\*#### **:mag_right: Query:**

* `?userId=[Guid]` - :yellow_circle: Valgfritt: Henter bruker hvis spesifisert. Hvis utelatt hentes bruker ut fra Bearer Token.
* `?pageNumber=[int]` - :yellow_circle: Valgfritt: Viser neste resultater. Hvis ikke oppgitt, settes denne til 1.
* `?pageSize=[int]` - :yellow_circle: Valgfritt: Antall resultater pr side. Hvis ikke oppgitt, settes denne til angit verdi i SystemSettings

**:floppy_disk: Syntax:**

```bash
curl -X GET "https://hopla.onrender.com/userhikes/user?userId=[Guid]&pageNumber=[int]&pageSize=[int]" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>"
```

:outbox_tray: **Eksempel på respons med queryene pageNumber=7 og pageSize=2**

```json
{
    "userHikes": [
        {
            "id": "12345678-0000-0000-0011-123456780017",
            "trailName": "Høvikrunden",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        },
        {
            "id": "12345678-0000-0000-0011-123456780016",
            "trailName": "Fornebutravbane",
            "length": 16.54,
            "duration": 50.75,
            "pictureUrl": ""
        }
    ],
    "page": 7,
    "size": 2
}
```

:pager: **Mulige statuskoder:**

* :white_check_mark: `200 OK` – Brukeren ble hentet.
* :x: `401 Unauthorized` - Ingen eller ugyldig token sendt.'
* :x: `404 Not Found` – Bruker ikke funnet.
* :x: `500 Internal Server Error` – Server feil.
</td>
</tr>
<tr>
<td>

![image.png](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/b59f667b696efff50d4b81ce91ca3c72/image.png){width="244" height="448"}
</td>
<td>

## **:green_book: :alien: :apple: PUT /upload**

Profil -\> Endre profilbilde

upload.html

```html

            const formData = new FormData();
            formData.append("image", fileInput.files[0]);
            formData.append("table", tableSelect);
            formData.append("entityId", guidInput); // Riktig parameter-navn

            try {
                const response = await fetch("https://hopla.onrender.com/upload", {
                    method: "PUT",
                    body: formData
                });
            }
```

OBS!! Authorization er aktivert, så dette må også med i Requesten.

Lagrer opplastet fil som {Guid}.jpg, f.eks bilde.jpg vil bli omdøpt til 4e66b1a6-2f18-4e35-8e89-eee6c3886a1b.jpg og lagret i databasen på riktig id.

Kan testes med dette i postman:

```PostMan_formdata

Key         Value
image       bilde.jpg
table       Users
entityId    12345678-0000-0000-0001-123456780001
```

Response:

```json
{
    "filePath": "/uploads/99e06dfe-2999-4d90-a5bf-087ba1e0df99.jpg"
}
```

Profil -\> Bytte brukernavn

## **:green_book: :grimacing: :apple: PUT /users/update**

(mangler fungerende Dob)

Body:

```json
{
    "Name": "",
    "Alias": "",
    "Telephone": "",
    "Description": "",
    "Year": NNNN,
    "Month": NN, // 1-12
    "Day": NN //1-31
}
```

Response:

```json
{
    "message": "Brukerinformasjon oppdatert."
}
```

Profil -\> Bytte epost

## **:green_book: :alien: :apple: POST /users/change-email**

body:

```json
{
    "NewEmail": "test@test.no",
    "Password": "Hopla2025!"
}
```

Mottar epost med beskjed om å bekrefte epostadressen ved å trykke på en lenke..

Response trinn1:

```json
{
    "message": "E-post sendt. Sjekk innboksen og trykk på lenken for å bekrefte registreringen. Sjekk evt søppelpost. Eposten må verifiseres innen 24 timer"
}
```

Åpne epost og trykk på aktiveringslenke. I noen tilfeller har det tatt opptil 30 minutter før eposten har kommet frem.

Profil -\> bytte passord

## **:green_book: :alien: :apple: PUT /users/change-password**

body:

```json
{
    "OldPassword": "GammeltPass0rd!",
    "NewPassword": "Hopla2025!",
    "ConfirmPassword": "Hopla2025!"
}
```

response:

```json
{
    "message": "Passordet er endret"
}
```
</td>
</tr>
<tr>
<td>

![image.png](uploads/46a60d398a0249dff5e40c607f8e2e20/image.png){width="320" height="579"}

`Android: Lagt til løyper første side, mangler om logget inn bruker har likt løypen eller ikke`
</td>
<td>

## **:green_book: :alien: :apple: GET /trails/all**

(Alle disse sidene skal displaye lister på samme måte)

* Get request for: id, navn, bilde, stjerner (0-5)
* Hvordan få likt status for brukeren på disse løypene?
* Hente de 10 første? Så når brukeren blar ned så sendes ny request på de 10 neste osv?
* Sorteres etter nyeste øverst
* Tenke på: offentlig, privat, kun venner?

:alien: :apple: **Løyper -\> Første side**

https://hopla.onrender.com/trails/all

**query:**

* search= Skriv inn noe som matcher navnet på løypa.
* sort= (ikke i bruk enda, men tenkte stars skulle være option. Akuratt nå er det hardcoded at den sorterer på averagerating(stars))
* pageNumber Optional. Hvis ikke oppgitt, settes den til 1
* pageSize Optional. Hvis ikke oppgitt settes den til 10

:alien: :apple: **eks**

https://hopla.onrender.com/trails/all?search=øvik&pagenumber=1&pagesize=5

**Response eksempel**

```json
{
    "trails": [
        {
            "id": "12345678-0000-0000-0021-123456780017",
            "name": "Høvikrunden",
            "pictureUrl": "https://images.unsplash.com/photo-1615729947596-a598e5de0ab3?h=140&fit=crop",
            "averageRating": 4,
            "isFavorite": false
        },
        {
            "id": "12345678-0000-0000-0021-123456780002",
            "name": "Gjøviksruta",
            "pictureUrl": "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?h=140&fit=crop",
            "averageRating": 1,
            "isFavorite": true
        }
    ],
    "pageNumber": 1,
    "pageSize": 5
}
```

* Vise alle løyper som brukere har lagt inn i appen. Flest stjerner øverst (hvis likt antall stjerner, nyeste av de øverst. Runde opp så det er f.eks 5 istede for 4.6 så sortere). Løypene må være offentlig eller fra venner

:alien: :apple: **Løyper -\> Icon 2 fra venstre**

* Vise løyper nærmest brukerens posisjon

**eks**

https://hopla.onrender.com:7128/trails/list

Mangler i response: bilde, averageRating og "liktstatus" **dette er med nå**

**query**

* ?latitude= må være med
* ?longitude= må være med.
* ?pageNumber= optional settes til 1 som er første side hvis ikke oppgitt
* ?pageSize= optional. antall resultater som returneres. settes til 10 hvis ikke oppgitt

**eks postmann**

https://hopla.onrender.com/trails/list?latitude=60.95458&longitude=10.6315

**Respose eks**

```json
{
    "trails": [
        {
            "id": "12345678-0000-0000-0021-123456780002",
            "name": "Gjøviksruta",
            "distance": 1.0848015268282347,
            "favorite": false,
            "averageRating": 1,
            "pictureUrl": "https://images.unsplash.com/photo-1472214103451-9374bd1c798e"
        },
        {
            "id": "12345678-0000-0000-0021-123456780001",
            "name": "Biriløypa",
            "distance": 18.3370760175382,
            "favorite": true,
            "averageRating": 1,
            "pictureUrl": "https://images.unsplash.com/photo-1532274402911-5a369e4c4bb5"

        }
    ],
    "pageNumber": 1,
    "pageSize": 2
}
```

:alien: :apple: **Løyper -\> Hjerte ikon**

* Kun løyper som brukeren har trykket liker på

https://hopla.onrender.com:7128/trails/favorites

**query**

* ?pageNumber= optional, settes til å vise første side hvis ikke spesifisert. sidenummer
* ?pageSize= optional hvis ikke oppgitt, settes denne til 10. Antall resultater pr side
* ?filter= optional. kommer senere.

**eks**

https://hopla.onrender.com/trails/favorites?pagenumber=1&pagesize=2

```json{
    "trails": [
        {
            "id": "12345678-0000-0000-0021-123456780001",
            "name": "Biriløypa",
            "favorite": true,
            "averageRating": 1,
            "pictureUrl": "https://images.unsplash.com/photo-1532274402911-5a369e4c4bb5"
        },
        {
            "id": "12345678-0000-0000-0021-123456780003",
            "name": "Lommedalsrunden",
            "favorite": true,
            "averageRating": 1,
            "pictureUrl": "https://images.unsplash.com/photo-1494625927555-6ec4433b1571"
        }
    ],
    "pageNumber": 1,
    "pageSize": 2
}
```

:alien: :apple: **Løyper -\> Stjerne ikon (bytte til 2 personers ikon)**

* Løyper til brukere brukeren følger og venner med

http://hopla.onrender.com:7128/trails/relations ?friends=true & following=true

**query**

* ?friends= hvis true så returneres venners favorittløyper.
* ?following= hvis true så returneres følgeres favorittløyper.
* ?pageNumber= optional, som over
* ?pageSize= optional, som ovenfor

\*_eks_

https://hopla.onrender.com/trails/relations?friends=true&following=true&pagenumber=1&pagesize=2

```json
{
    "trails": [
        {
            "id": "12345678-0000-0000-0021-123456780021",
            "name": "Sjølystturen",
            "isFavorite": true,
            "averageRating": 5,
            "pictureUrl": "https://images.unsplash.com/photo-1504893524553-b855bce32c67"
        },
        {
            "id": "12345678-0000-0000-0021-123456780023",
            "name": "Hønefossrunden",
            "isFavorite": true,
            "averageRating": 5,
            "pictureUrl": "https://images.unsplash.com/photo-1493246507139-91e8fad9978e"
        }
    ],
    "pageNumber": 1,
    "pageSize": 2
}
```
</td>
</tr>
<tr>
<td>

![image.png](uploads/6d56ffc28e83cf25bbf1f62fb664e3f6/image.png){width="307" height="576"}
</td>
<td>

## **:red_circle: :smiling_imp: :apple: Get /home?**

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

![image.png](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/b4ebfe4c9253d1e43e64b8b78cf50690/image.png){width="208" height="408"}

Spørsmål: finnes det en epost jeg kan teste glemt passord på? Eller er det "nok" at jeg for nå tester at jeg sjekker at jeg får riktig respons?
</td>
<td>

## **:green_book: :alien: :apple: POST /users/register**

:alien: :apple: Opprett bruker:

**Trinn 1:** Registrer epost, passord

POST https://hopla.onrender.com/users/register

```json
{
    "Email": "betjent-epost@domene.com",
    "Password": "Hopla2025!"
}
```

**response:**

```message
E-post sendt. Sjekk innboksen og trykk på lenken for å bekrefte registreringen. Sjekk evt søppelpost.
```

**Trinn 2:** Bekreft epostadressen

**Mail innboks/søppelpost:**

```email
FROM: Ikke svar (noreply@hopla.no)
Klikk på lenken for å fullføre registreringen: Bekreft e-post
```

**Når man trykker bekreft, sendes man hit:**

GET https://hopla.onrender.com/users/confirm-email?token=oZZyH9UJ3DgoenPA5jVeoMS22rbjyfbwK1AwwAbL4BE%3D

**eksempel på response:**

```http_message
E-post bekreftet! Du kan nå gå tilbake til appen og logge inn med epost og passord.
```

**Trinn 3:** Logg inn for videre registrering **logger inn med endpoint for login:**

Når man logger inn her får man:

* Token
* redirect = "update" (hvis registreringen er fullført, dvs name og alias er registrert, vil man få redirect = "profile" )

_Se nærmere info om login på enpoint først i dokumentet._

**Trinn 4** Oppdatere profilinfo: alias, navn, beskrivelse (optional), fødselsdato, tlf (optional), bilde (optional)

PUT https://lhopla.onrender.com/users/update

Body:

```json
{
    "id": "2b46f82a-2e38-47b6-a08e-cc62d10f4503",
    "name": "Test",
    "alias": "Hest",
    "description": null,
    "dob": "2005-03-13T09:38:46.8994Z",
    "pictureUrl": ""
}
```

**eksempel på response**

```postman_message
Brukerinformasjon oppdatert.
```

## **:green_book: :alien: :apple: POST /users/reset-password-request**

Glemt passord

sender med en epost i requesten, som den da må sjekke at den finnes i databasen for så å på en måte sende en mail der brukeren kan bytte passordet sitt?

**BT**

Ganske lik som POST /users/register

**Trinn 1**

Endpoint: POST /users/reset-password-request

Body:

```json
{
    "Email": "test@test.no" //for å teste må man skrive inn en epost som eksisterer.
}
```

Response:

```postman
"E-post sendt. Sjekk innboksen og trykk på lenken for å tilbakestille passordet. Sjekk evt søppelpost og Other/Annet mappen. Passordet må tilbakestilles innen 24 timer"
```

```email
FROM: Ikke svar (noreply@hopla.no)
Klikk på lenken for å fullføre registreringen: Bekreft e-post
```

**Trinn 2**

**Når man trykker bekreft, sendes man hit:**

GET https://password.hopla.no/index.html?token=oZZyH9UJ3DgoenPA5jVeoMS22rbjyfbwK1AwwAbL4BE%3D

Nettside for tilbakestilling av passord, for å gjøre det enklest mulig for brukeren. Tungvint å åpne en epost med appen.

![image](uploads/d6f3ab64cfa3ff25ec4e19bcba6cc3da/image.png){width="446" height="338"}

**eksempel på response:**

```http_message
Passord tilbakestilt. Du kan nå logge inn med ditt nye passord.
```

**Trinn 3:** Logg inn for videre registrering **logger inn med endpoint for login:**
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_153409_com.example.hopla\[1\].jpg](uploads/48f1f5a098d2503eeea4aa1b65124930/Screenshot_20250303_153409_com.example.hopla_1_.jpg)
</td>
<td>

## **:green_book: :alien: :apple: POST /userreports/create**

Profil -\> Innstillinger -\> Send en rapport (skal også legges inn: innlegg(hjem), løyper, profiler, community)

Bruker sender med: navn på rapport, beskrivelse og userid (skal senere displayes i nettsiden)

Sende med riktig tabell

Hvis innlegg: innleggid,

Hvis løyper: løypeid

Hvis profiler: profilid

Hvis community: communityid.

POST /userreports/create

Body:

```json
{ 
    "EntityId": "12345678-0000-0000-0006-123456780001", //EntityId = innleggId, løypeId, profilId, StableId etc.
    "EntityName": "Stables", //EntityName = Trails, Users, Stables eller hva det er.
    "Category": "Drama", //Optional. Hvis ikke oppgitt, blir den satt til "Annet".
    "Message": "Hesten til Ester er løs"
}
```

UserId registreres automatisk fra Token

Response:

```json
{
    "message": "Report created successfully."
}
```

## **:green_book: :alien: :apple: PATCH /users/delete**

Profil -\> Innstillinger -\> Slett bruker

Bruker må skrive inn passordet sitt som må bekreftes stemmer (Sjekkes i backend) så slette brukeren om det stemmer eller feilmelding hvis ikke

PATCH https://hopla.onrender.com/users/delete

Request Body:

```json
{
    "Password": "Hopla2025!"
}
```

Responce Body:

```json
{
    "message": "Bruker deaktivert."
}
```

**BRUKEREN BLIR IKKE SLETTET!! men deaktivert. Dette for å bevare databaseintegritet da brukeren kan ha opprettet løper som andre brukere kan ha laget turer med osv.**
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_154409_com.example.hopla\[1\].jpg](uploads/29558607e424a4acb2bc443c5240107f/Screenshot_20250303_154409_com.example.hopla_1_.jpg)
</td>
<td>

## **:red_circle: :smiling_imp: :apple: GET /filters?**

Endpoint som henter alle filtere i databasen. Da er det lettere å endre i senere tid hvis Hopla vil legge til nye/slette enn å hardkode navnene.

Nå har jeg det satt opp slik (ikke lagt til "riktig" filter):

![image.png](uploads/ecddb58073732bbca48a85d2f3230d6e/image.png){width="383" height="207"}
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_153900_com.example.hopla\[1\].jpg](uploads/f2a20e8cf7fed8f256f7ddf95b2f2190/Screenshot_20250303_153900_com.example.hopla_1_.jpg)

`Android: Lagt til longitude, latitude og zoomlevel som viser ikoner på kartutsnittet`
</td>
<td>

## **:green_book: :alien: :apple: GET /trails/map**

Løyper -\> Kart

Hente alle start-koordinater til løyper (longitude og latitude)

\-\> Q: Hvordan skal man hente de innenfor kartutsnittet?

\-\> A: Dette gjøres veldig enkelt ved å bruke zoom level for å beregne kartets lat/long-min/max verdier. Så brukes disse verdiene for å sjekke om noen løyper kan være innenfor kartutsnittet.

BT oppdaterer

https://hopla.onrender.com/trails/map?latitude=59.8833&longitude=10.6167&zoomlevel=14

**query**

* ?latitude= påkrevd
* ?longitude= påkrevd
* ?zoomlevel= påkrevd
* ?height= optional. Beregner høyde/bredde-forhold på skjermen, slik at man finner longmax/min
* ?width= optional. Hvis ikke oppgitt, så settes dette til 2400/1080 som er vanlig skjermstørrelse

```json
[
    {
        "id": "12345678-0000-0000-0021-123456780016",
        "name": "Fornebutravbane",
        "latMean": 59.8833,
        "longMean": 10.6167,
        "trailAllCoordinates": null
    },
    {
        "id": "12345678-0000-0000-0021-123456780022",
        "name": "Snarøyatråkket",
        "latMean": 59.879,
        "longMean": 10.608,
        "trailAllCoordinates": null
    }
]
```
</td>
</tr>
<tr>
<td>

![Screenshot_20250303_154741_com.example.hopla\[1\].jpg](uploads/dda7d3a7724bd06b1087879b2f05aa12/Screenshot_20250303_154741_com.example.hopla_1_.jpg)
</td>
<td>

## **:green_book: :alien: :apple: GET /stables/all**

Community/Fellesskap/Grupper

Ta bort hjerte, bytte hjerte symbol med "mine grupper symbol"

Symbol av "checked symbol" som viser at du er medlem ellers ikke noe

For å bli med: offentlig bare å trykke på bli med, hvis privat må man sende forespørsel

Hente: navn, bilde, id, medlemstatus sorteres etter nærmest brukeren

Hvis man henter eks. 10 og 10 grupper etter som brukeren blar nedover.

hvordan gjøres det med muligheten til å søke gjennom grupper (sende med hver og hver bokstav etterhvert som man skriver som da viser de 10 øverste som passer hvis man ikke blar nedover)

Roller i en gruppe: admin, medlem eller ikke medlem

Muligheter for medlemskap i gruppe: ikke-medlem/medlem/request(hvis private)

En gruppe kan være: public eller private

**BT**

Den sorterer nå på distanse

**Query**

* search= (optioanal) tekst som skal matche med stallnavnet
* userID= (opt) viser kun staller hvis userId er medlem
* latitude = latitude til bruker
* longitude = longitude til bruker
* page? = (Optional) side nummer. Hvis ikke oppgitt, settes den til 1
* pageSize? = (Optional) antall treff pr side. Hvis ikke oppgitt, så settes den til 10.

**eks**

```postman
https://hopla.onrender.com/stables/all?search=byen&latitude=60.8&longitude=10.7&pagesize=50&pagenumber=1
```

Response

```json
{
    {
        "stableId": "12345678-0000-0000-0031-123456780029",
        "stableName": "Nesbyen Rideklubb",
        "distance": 90.539,
        "member": false,
        "pictureUrl": "https://hopla.imgix.net/12345678-0000-0000-0031-123456780029.jpg?h=140&w394&crop"
    },
    {
        "stableId": "12345678-0000-0000-0031-123456780158",
        "stableName": "Nesbyen og Omegn Rideklubb",
        "distance": 90.539, 
        "member": false,
        "pictureUrl": "https://hopla.imgix.net/12345678-0000-0000-0031-123456780158.jpg?h=140&w394&crop"
    }
}
```

\*\*Avrunding? på Distanse kan evt frontend gjøre :-) \*\*

sånn cirka

## **:green_book: :alien: :apple: GET /stables/member (?)**

Trenger endpoint her også for å vise kun staller som man er medlem hos (der man trykker på hjerte øverst på bilde)

Dette er lagt inn i GET /stables/all hvor man spesifiserer optional query userid. Her kan man også sjekke hvilke staller andre brukere er medlem i.

GET https://hopla.onrender.com/stables/all?userid=12345678-0000-0000-0001-123456780001&latitude=60&longitude=10

Response:

```json
[
    {
        "stableId": "12345678-0000-0000-0031-123456780005",
        "stableName": "Asker Rideklubb",
        "distance": 30.613510835652864,
        "member": true,
        "pictureUrl": "https://hopla.imgix.net/12345678-0000-0000-0031-123456780005.jpg?h=140&w394&crop"
    },
    {
        "stableId": "12345678-0000-0000-0031-123456780003",
        "stableName": "Sørkedalen Rideklubb",
        "distance": 32.264149828696908,
        "member": true,
        "pictureUrl": "https://hopla.imgix.net/12345678-0000-0000-0031-123456780003.jpg?h=140&w394&crop"
    }
]
```
</td>
</tr>
<tr>
<td>

![Screenshot_20250317_130457_com.example.hopla\[1\].jpg](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/515ed0b7ac115e1ecfc2399bb7939b3c/Screenshot_20250317_130457_com.example.hopla_1_.jpg){width="200" height="400"}![Screenshot_20250317_144901_com.example.hopla\[1\].jpg](uploads/80267b25c43c34dde9ee60ddb8977a9e/Screenshot_20250317_144901_com.example.hopla_1_.jpg)
</td>
<td>

## **:green_book:**  :alien:  **:apple: POST /stables/create**

Legg til nytt fellesskap:

Bruker som som oppretter blir automatisk admin.

Informasjon som må bli lagt til: navn, beskrivelse, bilde, privat/offentlig og posisjon (long, lat)

\*\*POST \*\***https://hopla.onrender.com/stable/create**

Body FORMDATA

```formdata
    Key             Value
    Image           stall.jpg
    Name            Stallione
    Description     Flott Stall
    Latitude        60.01223
    Longitude       10.5433
    PrivateGroup    false
```

Response:

```json
{
    "message": "Stable created successfully.",
    "stableId": "14cfddd6-688a-4700-9408-dc7e6a0b957b"
}
```

Denne lager ny stall i Stables OG bruker som lager stallen blir satt som admin i StableUsers
</td>
</tr>
<tr>
<td>

![Screenshot_20250317_145832_com.example.hopla\[1\].jpg](uploads/42f821da9e20a7c5a20bf7780712d314/Screenshot_20250317_145832_com.example.hopla_1_.jpg)
</td>
<td>

## **:green_book: :alien: :apple: GET /stables/{stableId}**

Community details

Når man klikker inn på enkelte communities. Kommer man inn på postene der.

Hver enkelt post trenger: dato, tid, alias og selve meldingen. Den grønne meldingen er også innlogget bruker sine egne meldinger. Så må kanskje derfor også ha med brukerid for å sjekke om det er egen melding eller ikke?

Ellers trengs: id, bilde, beskrivelse, gruppenavn, medlemsstatus (for å sjekke om innlogget bruker er medlem/admin/ikke-medlem).

Skal også legges til: mulighet for å rapportere community og "gå ut av gruppen"

Admin skal kunne: slette community (?)

**BT**

eks:

```postman
https://hopla.onrender.com:7128/stables/12345678-0000-0000-0031-123456780001
```

Response:

```json
{
    "id": "12345678-0000-0000-0031-123456780001",
    "name": "Skedsmo Rideklubb",
    "description": "",
    "pictureUrl": "12345678-0000-0000-0031-123456780001.jpg",
    "isMember": true
}
```

Videre må man hente meldinger med neste endpoint

## **:green_book: :alien: :apple: GET /stablemessages/{stableId}**

**Query**

* page? = (Optional) side nummer. Hvis ikke oppgitt, settes den til 1
* pageSize? = (Optional) antall treff pr side. Hvis ikke oppgitt, så settes den til 10.
* Annet som burde være med?

eks:

```postman
https://hopla.onrender.com:7128/stablemessages/12345678-0000-0000-0031-123456780001?pagesize=10&pagenumber=1
```

Response:

```json
[
    {
        "content": "Jeg skal ri klokka 11 imorgen. Noen som vil være med?",
        "timestamp": "2025-03-12T18:49:13.743609Z",
        "senderId": "12345678-0000-0000-0001-123456780003",
        "senderAlias": "Embalasjen"
    },
    {
        "content": "Det passer ikke idag, fordi idag kommer det en som skal klippe negler på hestene, så da må alle hestene være tilgjengelig. Har du glemt det?",
        "timestamp": "2025-03-11T18:49:13.743608Z",
        "senderId": "12345678-0000-0000-0001-123456780002",
        "senderAlias": "Kamuflasjen"
    },
    {
        "content": "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?",
        "timestamp": "2025-03-08T18:49:13.743605Z",
        "senderId": "12345678-0000-0000-0001-123456780001",
        "senderAlias": "MangeBallerILufra"
    },
    {
        "content": "Det passer ikke idag, fordi det hestene er fulle etter julebordet. Du burde da skjønne det?",
        "timestamp": "2025-03-07T18:49:13.743604Z",
        "senderId": "12345678-0000-0000-0001-123456780002",
        "senderAlias": "Kamuflasjen"
    },
    {
        "content": "Heisann å hopla ofallerallera. Er det noen her inne som vil være med på ridetur ida?",
        "timestamp": "2025-03-06T18:49:13.743604Z",
        "senderId": "12345678-0000-0000-0001-123456780001",
        "senderAlias": "MangeBallerILufra"
    },
    {
        "content": "Det passer ikke idag, fordi det hestene er pyntet til julebordet. Har du glemt det?",
        "timestamp": "2025-03-05T18:49:13.743604Z",
        "senderId": "12345678-0000-0000-0001-123456780002",
        "senderAlias": "Kamuflasjen"
    }
]
```
</td>
</tr>
<tr>
<td>

![Screenshot_20250319_131751_com.example.hopla\[1\].jpg](uploads/fd03859cf3b73b0879c1f72cd10fab49/Screenshot_20250319_131751_com.example.hopla_1_.jpg)
</td>
<td>

## **:green_book: :smiling_imp: :apple: POST /horses/create**

Legge til ny hest, sende med: navn, rase, alder/dob (?), bilde (kun 1)

eks: https://hopla.onrender.com/horses/create

Body:

```formdata
{
    Key                     Obj     Value
    Image                   File    kingdurek.jpg
    Name                    Text    KongDurek
    Breed                   Text    Lurendreier
    Year                    Text    1969
    Month                   Text    1
    Day                     Text    1
}
```

Response:

```json
Horse Created
```
</td>
</tr>
<tr>
<td></td>
<td>

## **:green_book: :smiling_imp: :apple: POST friendrequest**

Trenger endpoints for å endre på venneforhold:

Hvis knapp legg til venn trykket: endre forhold fra NONE/Følger til request

Hvis knapp følg er trykket på: endre follow fra NONE til følger

Hvis knapp fjern venn er trykker: endre forhold til NONE

Hvis knapp blokker er trykket på: endre til blocked

**BT**

BodyEksempel:

```json
{
    "TargetUserId": "12345678-0000-0000-0001-123456780050",
    "Status": "PENDING"
}
```

Oversikt over hva som brukes i hvilken situasjon

| Situasjon | Handling | Metode | Endpoint | Body / Info |
|-----------|----------|--------|----------|-------------|
| **Ingen relasjon** | Følg | `POST` | `/userrelations` | `TargetUserId`, `Status: "FOLLOWING"` |
|  | Vennforespørsel | `POST` | `/userrelations` | `TargetUserId`, `Status: "PENDING"` |
|  | Blokkering | `POST` | `/userrelations` | `TargetUserId`, `Status: "BLOCK"` |
| **Venn** | Fjern venn | `DELETE` | `/userrelations` | `TargetUserId` |
|  | Blokker venn | `PUT` | `/userrelations` | `TargetUserId`, `Status: "BLOCK"`<br>:warning: Sjekk at blokkering går riktig vei |
| **Følger** | Vennforespørsel | `POST` | `/userrelations` | `TargetUserId`, `Status: "PENDING"`<br>:arrow_right: Opprettes som egen relasjon i tillegg til eksisterende "FOLLOWING" |
|  | Blokker | `PUT` | `/userrelations` | `TargetUserId`, `Status: "BLOCK"` |
|  | Slutt å følge | `DELETE` | `/userrelations` | `TargetUserId` |
| **Pending** | Aksepter | `PUT` | `/userrelations` | `TargetUserId`, `Status: "FRIENDS"`<br>:broom: Følger-relasjoner fjernes evt på begge brukerne |
|  | Avvis | `DELETE` | `/userrelations` | `TargetUserId` |
|  | Blokker | `PUT` | `/userrelations` | `TargetUserId`, `Status: "BLOCK"`<br>:warning: Sjekk at blokkering går riktig vei |

</td>
</tr>
<tr>
<td></td>
<td>

## **:green_book: :smiling_imp: :apple: GET /userrelations/requests**

**Trenger også her et POST endpoint der bruker kan godkjenne eller slette forespørseler. Endre fra request til enten NONE eller FRIENDS**

se POST friendrequest ovenfor her.

Endpoint som henter alle venneforespørseler innlogget bruker har.

**BT**

Eksempel:

```postman
GET https://hopla.onrender.com/userrelations/requests
```

Response:

```
[
    {
        "id": "768ee359-6e1f-4b61-83af-4155415ccbc5",
        "fromUserId": "12345678-0000-0000-0001-123456780021",
        "fromUserAlias": "AlltidISvime",
        "fromUserName": "Ole Svimeslåtten"
    },
    ...
    osv.
    ...
    {
        "id": "f23f5fc0-d7ab-4382-88ee-76620a23786d",
        "fromUserId": "12345678-0000-0000-0001-123456780025",
        "fromUserAlias": "KlaraNøff",
        "fromUserName": "Klara Snøfterud"
    }
]
```
</td>
</tr>
<tr>
<td></td>
<td>

## **:red_circle: :smiling_imp: :apple: POST ny tur (ny tur knapp)**

Denne generer en liste koordinater, sammen med tid og distanse fra bruker trykker på start til stopp.\
Da kan brukeren velge å bare trykke lagre eller fylle inn mer informasjon\
(navn på tur, beskrivelse, antall stjerner (?), filtere, bilde, privat/public (?).\
Hvis public så gjøres denne om til en løype også.

## **:red_circle: :smiling_imp: :apple: PUT (?) redigere informasjon om en tur**

Endpoint der informasjonen over skal kunne redigeres i ettertid.\
Hvis public her også må det lages en løype av den.
</td>
</tr>
<tr>
<td>

![Screenshot_20250319_133243_com.example.hopla\[1\].jpg](uploads/19defbdeb9b709df22f7eac59809b9e5/Screenshot_20250319_133243_com.example.hopla_1_.jpg)
</td>
<td>

## **:red_circle: :smiling_imp: :apple: POST ny oppdatering om løype**

Når brukeren poster en ny oppdatering om løypen (trykker på ny oppdatering):\
informasjon post requesten trenger:\
brukerens id, løypens id, oppdateringen, optional bilde, tidspunkt (tid og dato)

## **:red_circle: :smiling_imp: :apple: GET nye oppdateringer om løypen**

Når brukeren trykker på nyeste oppdatering om løypen skal de få:\
selve oppdateringen, tid den ble lagt ut, evt bilde, bruker som har lagt ut

## **:green_book: :smiling_imp: :apple: POST /trails/rate**

**vurdering av løype**

Endpoint som henter hvor mange stjerner brukeren har trykket på og legger de til i totale vurderinger.

**BT**
```postman
https://hopla.onrender.com/trails/rate
```

**Denne lager ny rating. Hvis brukeren har ratet tidligere, så oppdateres denne ratingen.**

Body:
```json
{
    "TrailId": "12345678-0000-0000-0021-123456780001",
    "Rating": 5 //kun heltall mellom 1-5 aksepteres
}
```

Response:
```json
Trail Rated / Updated TrailRating
```

## **:red_circle: :smiling_imp: :apple: GET start tur**

Henter opp alle koordinater til løypen og tegner de opp på kartet, samt tid, og distanse (id for å lagre det som en tur etterpå.

## **:red_circle: :smiling_imp: :apple: POST ny tur**

Etter turen er ferdig (brukeren er på sluttkoordinatet eller trykker på stopp.

Turen lagres som en ny tur koblet til brukeren
</td>
</tr>
</table>

# **Android bilder**

Sist oppdatert: 17.03

#### **Profil**

![Screenshot_20250317_124010_com.example.hopla\[1\].jpg](uploads/f8190e21008789c6e3e58247da22c021/Screenshot_20250317_124010_com.example.hopla_1_.jpg){width="162" height="324"}![Screenshot_20250317_124013_com.example.hopla\[1\].jpg](uploads/552463dd776087f1eb5f473dd34c8f60/Screenshot_20250317_124013_com.example.hopla_1_.jpg){width="163" height="326"}

#### **Innstillinger**

#### **![Screenshot_20250317_124017_com.example.hopla\[1\].jpg](uploads/36fb2c9be1d8808d86b4bcd2966ba39f/Screenshot_20250317_124017_com.example.hopla_1_.jpg){width="173" height="346"}![Screenshot_20250317_124019_com.example.hopla\[1\].jpg](uploads/a7169e8e79125cbdb133a84416b55b23/Screenshot_20250317_124019_com.example.hopla_1_.jpg){width="172" height="344"}**

#### **Mine turer**

![Screenshot_20250317_124334_com.example.hopla\[1\].jpg](uploads/f06cc71ef8673c8afe7519b9495f5249/Screenshot_20250317_124334_com.example.hopla_1_.jpg){width="170" height="340"}

#### **Mine hester Detaljer hester**

![Screenshot_20250317_124339_com.example.hopla\[1\].jpg](uploads/99c3c75e8ca782c180eea4ae4e5f3c14/Screenshot_20250317_124339_com.example.hopla_1_.jpg){width="180" height="360"}![Screenshot_20250317_124343_com.example.hopla\[1\].jpg](uploads/8c112519d0d5552045e638f050d645b6/Screenshot_20250317_124343_com.example.hopla_1_.jpg){width="181" height="362"}

#### **Venner**

Brukers venner--------------Venns detaljer--------------Venners hester------------Venners venner

![Screenshot_20250317_124723_com.example.hopla\[1\].jpg](uploads/efbe8d4623b04f7f3843ec43a602a165/Screenshot_20250317_124723_com.example.hopla_1_.jpg){width="175" height="350"} ![Screenshot_20250317_124736_com.example.hopla\[1\].jpg](uploads/1b2f2b35581588d950eb7bb382dbbb7f/Screenshot_20250317_124736_com.example.hopla_1_.jpg){width="175" height="350"}![Screenshot_20250317_124739_com.example.hopla\[1\].jpg](uploads/885be5e7017df675cfe7a1802e7bff97/Screenshot_20250317_124739_com.example.hopla_1_.jpg){width="175" height="350"}![Screenshot_20250317_124744_com.example.hopla\[1\].jpg](uploads/6367179be9512955e12b66e10bfb6621/Screenshot_20250317_124744_com.example.hopla_1_.jpg){width="175" height="350"}

Venners profil lengre ned----Venners blokker/rapporter

![Screenshot_20250317_125226_com.example.hopla\[1\].jpg](uploads/0631ad3c215f5cf58125ca1ebe2dee1a/Screenshot_20250317_125226_com.example.hopla_1_.jpg){width="167" height="334"}![Screenshot_20250317_124751_com.example.hopla\[1\].jpg](https://gitlab.stud.idi.ntnu.no/vakvaer/hopla/-/wikis/uploads/64369284df67c6bffbf52c7cf99ad8f4/Screenshot_20250317_124751_com.example.hopla_1_.jpg){width="168" height="336"}

#### **Følger**

Bruker følger----------------Følger detaljer-----------Følger "valg"

![Screenshot_20250317_130004_com.example.hopla\[1\].jpg](uploads/c788a2cf85ae88661f4544fc17db72f9/Screenshot_20250317_130004_com.example.hopla_1_.jpg){width="167" height="334"}![Screenshot_20250317_130008_com.example.hopla\[1\].jpg](uploads/a6eff64ea0f0c6f526416b473e6b3f9f/Screenshot_20250317_130008_com.example.hopla_1_.jpg){width="168" height="336"}![Screenshot_20250317_130012_com.example.hopla\[1\].jpg](uploads/9138d3f3908741745c585ee3e872b3b3/Screenshot_20250317_130012_com.example.hopla_1_.jpg){width="167" height="334"}

#### **Alle brukere**

Alle brukere-------------------Relation=None-------------Relation=Pending

![Screenshot_20250317_130021_com.example.hopla\[1\].jpg](uploads/0587ebbfcce70acbbf2846fec2871771/Screenshot_20250317_130021_com.example.hopla_1_.jpg){width="180" height="360"}![Screenshot_20250317_130207_com.example.hopla\[1\].jpg](uploads/78c1f253c2416a1e6d2722779a534eb4/Screenshot_20250317_130207_com.example.hopla_1_.jpg){width="180" height="360"}![Screenshot_20250317_130258_com.example.hopla\[1\].jpg](uploads/0e7e86915a87a1bb496a3bf4c793a62d/Screenshot_20250317_130258_com.example.hopla_1_.jpg){width="180" height="360"}

#### **Fellesskap**

Fellesskap første side(posisjon)----Likte fellesskap---------------Legge til nytt fellesskap-------Legge til posisjon nytt fellesskap

`OBS OBS!!! `Se detaljer i listen over hvordan disse sidene skal endres

![Screenshot_20250317_130448_com.example.hopla\[1\].jpg](uploads/31857f6cdf35d1c99add55fb1dc755b2/Screenshot_20250317_130448_com.example.hopla_1_.jpg){width="201" height="402"}![Screenshot_20250317_130454_com.example.hopla\[1\].jpg](uploads/ef5e329f36c653c84261a1cd99a72a09/Screenshot_20250317_130454_com.example.hopla_1_.jpg){width="201" height="402"}![Screenshot_20250317_130457_com.example.hopla\[1\].jpg](uploads/515ed0b7ac115e1ecfc2399bb7939b3c/Screenshot_20250317_130457_com.example.hopla_1_.jpg){width="200" height="400"}![Screenshot_20250317_144901_com.example.hopla\[1\].jpg](uploads/fdd7aacc85f2074567744417322013be/Screenshot_20250317_144901_com.example.hopla_1_.jpg){width="201" height="402"}

Community skjerm ------------- Beskrivelse av community (når man trykker på i icon)

![Screenshot_20250317_145505_com.example.hopla\[1\].jpg](uploads/967fafb066d5d0615535d399235241d5/Screenshot_20250317_145505_com.example.hopla_1_.jpg){width="194" height="388"}![Screenshot_20250317_145508_com.example.hopla\[1\].jpg](uploads/9d0123cae0afe57f3d95ea37944d0267/Screenshot_20250317_145508_com.example.hopla_1_.jpg){width="196" height="392"}

#### **Ny tur (skal nok endres litt)**

Start på ny tur-----------------Når ny tur er ferdig

![Screenshot_20250317_130846_com.example.hopla\[1\].jpg](uploads/b02689a03b7be70d2222649f1782de2e/Screenshot_20250317_130846_com.example.hopla_1_.jpg){width="177" height="354"}![Screenshot_20250317_130854_com.example.hopla\[1\].jpg](uploads/06fde64999c226b8a16adfa913341360/Screenshot_20250317_130854_com.example.hopla_1_.jpg){width="178" height="356"}

#### **Løyper**

(Ja jeg vet jeg har "ødelagt" kvaliteten på bildene litt, fiks kommer :laughing: )

Første side---------------------Nær bruker-----------------Likte løyper-------------Venner og følger

![Screenshot_20250317_131317_com.example.hopla\[1\].jpg](uploads/9820fada4a00b2b31186886b7c81cd35/Screenshot_20250317_131317_com.example.hopla_1_.jpg){width="179" height="358"}![Screenshot_20250317_131321_com.example.hopla\[1\].jpg](uploads/14b2a266703f867ec7725c444f2bdd2a/Screenshot_20250317_131321_com.example.hopla_1_.jpg){width="179" height="358"}![Screenshot_20250317_131325_com.example.hopla\[1\].jpg](uploads/e21464de32a27d0b3ac75c35d28b2175/Screenshot_20250317_131325_com.example.hopla_1_.jpg){width="179" height="358"}![Screenshot_20250317_131330_com.example.hopla\[1\].jpg](uploads/b2d1f71e1203233ed0a3a99c7e406687/Screenshot_20250317_131330_com.example.hopla_1_.jpg){width="179" height="358"}

Filter liste

![Screenshot_20250317_131335_com.example.hopla\[1\].jpg](uploads/f2b34108e4a09755dee7f313047a304e/Screenshot_20250317_131335_com.example.hopla_1_.jpg){width="189" height="378"}

#### **Hjem**

Mer detaljer om denne siden i listen over, men foreløpig design

![Screenshot_20250317_131734_com.example.hopla\[1\].jpg](uploads/ba7873a84dd59fba092f29812c914512/Screenshot_20250317_131734_com.example.hopla_1_.jpg){width="187" height="374"}![Screenshot_20250317_131738_com.example.hopla\[1\].jpg](uploads/8a7357e0e7d17c961b860ea6b753cd62/Screenshot_20250317_131738_com.example.hopla_1_.jpg){width="188" height="376"}