




# Hopla API Endpoints

### Symbolforklaring

| Symbol | Beskrivelse                        | | | Symbol | Beskrivelse                        |
|--------|----------------------------------- |-|-|--------|----------------------------------- |
| 🛠️ |  Status | | |🔒 | Autorisering? |
| ✅ | Ferdig og testet | | |🌍 |Svarer alle |
| 🟢 | Ferdig, men ikke tilstrekkelig testet | | |🔑 | Krever gyldig autenseringstoken |
| ⚠️ | Virker delvis | | |👑 | Krever admin rettigheter |
| ❌ | Ikke laget | | | 📧🔑 | Krever epostlenke med token
| 🧪 | Kun for testing/Eksperimentell  | | |  🔓 | Åpent Endpoint. Test av symbol

## 📖 Hvordan bruke dokumentasjonen

Denne dokumentasjonen gir en detaljert oversikt over API-endepunktene og hvordan de brukes.  
Klikk på et endepunkt for å se spesifikasjonene, inkludert:

### 📖 Hva inneholder dokumentasjonen?
- **🔓🌍 Åpent Endpoint** – Angir om endepunktet krever autentisering eller ikke.
- **📌 Beskrivelse** – Kort forklaring på hva endepunktet gjør.
- **📑 Tilgjengelige parametere:**
  - 🔒 **Header** – `Authorization: Bearer Token` 🔑 (kreves for beskyttede endepunkter).
  - 📂 **Path** – Unike identifikatorer i URL (f.eks. `<horseId>`).
  - 🔎 **Query** – Valgfrie eller påkrevde URL-parametere (f.eks. `?userId=[Guid]`).
  - 📥 **Body** – JSON-data som må sendes i `POST`/`PUT`-forespørsler.
- **💾 Syntaks med Curl** – Eksempel på hvordan forespørselen kan sendes via terminal.
- **📤 Eksempel på JSON-respons** – Hvordan en vellykket respons fra API-et ser ut.
- **📟 HTTP-feilkoder** – Mulige feiltilstander og hva de betyr.

---

### 💡 **Tips for testing**
- Bruk `curl`-eksemplene for å teste API-et direkte fra terminalen.
- Importer `curl`-forespørslene i **Postman** eller **Insomnia** for en mer visuell fremstilling.
- Gå først til /users/login for å motta et token for tilgang til lukkede forespørsler. 
- Husk å inkludere `Authorization: Bearer <TOKEN>` i forespørslene der det kreves.

**Klar til å begynne? Klikk på et endepunkt i dokumentasjonen!**

---

## Endpoints brukt og hestet av Frontend

| 🛠️ | 🔒 | Metode | Endpoint | Beskrivelse/Parameters |
|-----|----|--------|-------------------------------|-------------|
| 🟢 | 🌍 | POST | [`/users/login`](#post-userslogin) | returnerer aut-token |
| 🟢 | 🔑 | GET | [`/users/profile`](#get-usersprofile) | Henter userid fra token. Hvis userid er spesifisert, vises litt mer info som antall venner. |
| 🟢 | 🔑 | GET | [`/horses/userhorses`](#get-horsesuserhorses) | Viser alle hester til innlogget bruker, evt til oppgitt userid (optional) |
| 🟢 | 🔑 | GET | [`/horses/{horseid}`](#get-horseshorseid) | Vise en spesifikk hest, |
| 🟢 | 🔑 | GET | [`/userrelations/friends/[userid]`](#get-userrelationsfriendsuserid) | Viser venner til token eller userid (optional) |
| ❌ | 🔑 | GET | [`/userrelations/requests/`](#get-userrelationsrequests) | viser venneforspørsler |
| 🟢 | 🔑 | GET | [`/userrelations/following/`](#get-userrelationsfollowing) | viser hvem userId følger |
| 🟢 | 🔑 | GET | [`/userhikes/user`](#get-userhikesuser) | viser turene til innlogget bruker ELLER oppgitt userID  |

## Endpoints for Adminportal
| 🛠️ | 🔒 | Metode | Endpoint | Beskrivelse/Parameters |
|-----|----|--------|-------------------------------|-------------|
| ✅ | 👑 | GET | [`/admin/settings/all`](#get-adminsettingsall) | Alle Innstillinger og deres verdier |
| ✅ | 👑 | GET | [`/admin/settings/{keyName}`](#get-adminsettingskeyname) | Vise verdien på en innstilling |
| ✅ | 👑 | PUT | [`/admin/settings/{keyName}`](#put-adminsettingskeyname) | Endre en innstilling |

## Endpoints for Debugging
| 🛠️ | 🔒 | Metode | Endpoint | Beskrivelse/Parameters |
|-----|----|--------|-------------------------------|-------------|
| ⚠️ | 👑 | GET | [`/div/logging`](#get-divlogging) | Her kan man aktivere og deaktivere logging til logtail enklet så man slipper omstart (Burde muligens være POST/PUT for mest mulig RESTful?) |
| ✅ | 🌍 | GET | [`/div/helloworld`](#get-divhelloworld) | Sjekker om APIet svarer |
| ✅ | 🌍 | GET | [`/div/status`](#get-divstatus) | sjekker oppetid, requestcount + errorcount. |
| ✅ | 🌍 | GET | [`/div/database`](#get-divdatabase) | Sjekker om det forbindelse mellom database og backend |
| ✅ | 🧪 | POST | [`/users/login/test`](#post-userslogintest) | logger inn uten passord (kun for enklere testing) returnerer aut-token  |

## Andre Endpoints Eksperimentell
| 🛠️ | 🔒 | Metode | Endpoint | Beskrivelse/Parameters |
|-----|----|--------|-------------------------------|-------------|
| ✅ | 🌍 | POST | [`/users/register`](#post-usersregister) | Trenger rutine for epost osv. |
| ✅ | | POST | [`/users/aut/changepassword`](#post-usersautchangepassword) |  |
| ✅ | 🧪 | POST | [`/users/aut/int/{userId}`](#post-usersautintuserid) | for enkel testing om det fungerer med  |
| ✅ | 🔑 | POST | [`/users/changepassword`](#post-userschangepassword) | authorize etablert, må sende med token. |
| ❌ | 🌍 | POST | [`/users/forgotpassword`](#post-forgotpassword) | Regner med denne skal sende epost med mulighet for nullstilling av passord |
| ❌ | | GET | [`/users/settings`](#get-userssettings) |  |
| ❌ | | PUT | [`/users/settings`](#put-userssettings) |  |
| ✅ | | GET | [`/users/`](#get-usersuserid) | Viser alle registrerte brukere |
| ✅ | | GET | [`/users/{userid}`](#get-usersuserid) | - Info om en bruker (blir nok erstatet av users/profile/{usersID} )|
| ✅ | | POST | [`/users`](#post-users) | Oppretter ny bruker |
| ⚠️ | | PUT | [`/users/{userid}`](#put-usersuserid) | Litt mangelfull |
| ✅ | | DELETE | [`/users/{userid}`](#delete-usersuserid) | - slette en bruker |
| ❌ | | POST | [`/horses`](#post-horses) | Registrere ny hest på bruker |
| ❌ | | PUT | [`/horses`](#put-horses) | |
| ❌ | | DELETE | [`/horses`](#delete-horses) | |
| ✅ | | GET | [`/userrelations/blocked/{userid}`](#get-userrelationsblocksuserid) | viser hvem userId blokkerer |
| ✅ | | POST | [`/userrelations/friendrequests`](#post-userrelationsfriendrequestsuserid) | sende venneforspørsel |
| ❌ | | POST | [`/userrelations/follow/`](#post-userrelationsfollow) | følger en bruker |
| ✅ | | POST | [`/userrelations/block`](#post-userrelationsblock) | blokkerer en bruker |
| ✅ | | PUT | [`/userrelations/{userrelationid}`](#put-userrelationsuserrelationid) | bytter status på en userrelation, f.eks fra request til friend eller fra friend til delete eller block |
| ✅ | | DELETE | [`/userrelations/{fromUserId}/{toUserId}`](#delete-userrelationsfromuseridtouserid) | sletter en userrelation, f.eks blokkering |
| ✅ | | GET | [`/trail/list`](#get-traillist) | Liste over løyper sortert etter næreste først |
| ⚠️ | | GET | [`/trail/map`](#get-trailmap) | Løyper som passer inni kartutsnittet |
| ❌ | | POST | [`/trail`](#post-trail) | Bruker data fra en Ride og lager en løype av det. |
| ❌ | | PUT | [`/trail/{trailId}`](#put-trailtrailid) | evt endre en trail |
| ❌ | | DELETE | [`/trail/{trailId}`](#delete-trailtrailid) | |
| ✅ | | GET | [`/rides/user/{userId}`](#get-ridesuseruserid) | |
| ✅ | | GET | [`/rides/{rideId}/details`](#get-ridesrideiddetails) | Detaljer fra en tur |
| ✅ | | GET | [`/rides/{rideId}/trackingdata`](#get-ridesrideidtrackingdata) | Fullstendig liste med koordinater på en ride |
| ✅ | | GET | [`/rides`](#get-rides) | |
| ✅ | | POST | [`/rides`](#post-rides) | |
| ❌ | | PUT | [`/rides/{rideId}`](#put-ridesrideid) | - Nesten utviklet. Trenger testing bildeopplegg |
| ✅ | | GET | [`/messages/{userId}`](#get-messagesuserid) | Viser siste melding som userId har mottatt fra alle brukere |
| ✅ | | GET | [`/messages/{sUserId}/{rUserId}`](#get-messagessuseridruserid) | Viser meldinger mellom 2 brukere, DESC Time |
| ✅ | | POST | [`/messages`](#post-messages) | Sende melding til en annen bruker. |
| ❌ | | PUT | [`/messages/{messageId}`](#put-messagesmessageid) | Rediger en melding? |
| ❌ | | DELETE | [`/messages/{messageId}`](#delete-messagesmessageid) | Slette en melding |
| ❌ | | GET | [`/stables/list`](#get-stableslist) | -Viser alle staller, evt nærmeste staller |
| ❌ | | POST | [`/stables`](#post-stables) | - Registrere ny stall |
| ❌ | | ??? | [`/stableusers`](#stableusers) | Kommer senere, men dette er medlemmer av en stall. |
| ✅ | | GET | [`/stablemessages/{stableId}`](#get-stablemessagesstableid) | |
| ✅ | | POST | [`/stablemessages`](#post-stablemessages) | |
| ❌ | | PUT | [`/stablemessages/{stableMessageId}`](#put-stablemessagesstablemessageid) | Endre en melding? |

---

## MockData (Testdata)
Ved å kjøre APIene under, så opprettes mockdata som er hardkodet i egne mock-filer. Disse blir det kun laget en generell dokumentasjon på som gjelder alle Mock-endpoints.

| 🛠️ | 🔒 | Metode | Endpoint opprettelse | Endpoint slette | Beskrivelse |
|----|-----|---------|-------------|----------|----|
| ✅ | 🧪 | POST | `/mock/createdatabase` |`/mock/cleardatabase` | Oppretter eller sletter alle mockdata. |
| ✅ | 🧪 | POST | `/mock/createusers` | `/mock/clear` | Oppretter testbrukere |
| ✅ | 🧪 | POST | `/mock/createhorses` | `/mock/clear` | Oppretter testhester |
| ✅ | 🧪 | POST | `/mock/createfriendrequests` |`/mock/clear` |  Genererer venneforespørsler |
| ✅ | 🧪 | POST | `/mock/createmessages` | `/mock/clear` | Lager testmeldinger |
| ✅ | 🧪 | POST | `/mock/createstables` | `/mock/clear` | Oppretter teststaller |
| ❌ | 🧪 | POST | `/mock/createstableusers` | `/mock/clear` |  Oppretter brukere til staller |
| ✅ | 🧪 | POST | `/mock/createstablemessages` | `/mock/clear` | Genererer stallmeldinger |
| ✅ | 🧪 | POST | `/mock/createrides` | `/mock/clear` | Oppretter test-rideturer |
| ❌ | 🧪 | POST | `/mock/createridedetails` | `/mock/clear` | Oppretter detaljer for rideturer |
| ❌ | 🧪 | POST | `/mock/createridetrackingdata` | `/mock/clear` | Oppretter GPS-spor for rideturer |
| ❌ | 🧪 | POST | `/mock/createridereviews` | `/mock/clear` | Oppretter anmeldelser av rideturer |
| ✅ | 🧪 | POST | `/mock/createtrails` | `/mock/clear` | Oppretter test-løyper |
| ❌ | 🧪 | POST | `/mock/createtraildetails` | `/mock/clear` | Oppretter detaljer om løyper |
| ❌ | 🧪 | POST | `/mock/createtrailreviews` | `/mock/clear` | Oppretter anmeldelser av løyper |
| ❌ | 🧪 | POST | `/mock/createtrailfilters` | `/mock/clear` | Oppretter filtre for løyper |
| ✅ | 🧪 | POST | `/mock/createsettings` | `/mock/clearsettings` | Oppretter test-løyper |

---

## Oversikt over APIets Endpoints og hvordan man bruker det.

- Tilgjengelige paths, query, parametere og body ??
- Syntaks
- JSON-response
- Eksempel med curl
- Hvilke statuskoder som kan returnere

## Bruk av mock API

### POST /mock/ alle endpoints
**Beskrivelse:** Oppretter Mockdata til databasen. Dette brukes for testing.

📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/mock/createcontent" \
```
📌 **Mulige statuskoder:**
- ✅ `201 Created` – Mockdata har blitt lagt til i databasen.
- ❌ `400 Bad Request` – Feil input.
- ❌ `505 Server Error` – Noe er feil.


---

## Eksempel-Endpoint med Alle Statuskoder

### GET /example/{id}

**Beskrivelse:** Henter informasjon om en ressurs.

📌 **Parametere:**
| Parameter | Name | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|----|
| 📂 Path      | `id`   | string  | ✅ Ja   | ID-en til ressursen |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/example/123"
```
📌 **Eksempel på respons:**
```json
{
  "id": "123",
  "name": "Eksempelnavn",
  "status": "active"
}
```
📌 **Eksempel på statuskoder:**
- ✅ `200 OK` – Forespørselen var vellykket.
- ⚠️ `400 Bad Request` – Feil i forespørselen (manglende eller ugyldige parametere).
- ❌ `404 Not Found` – Ressursen ble ikke funnet.

---

## API-detaljer

### POST /users/login
🔙 Tilbake til[`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Logger inn ny bruker.

🌍 **Åpent Endpoint - Krever ikke autentisering**

📑 **Parametere**

|Parameter| Name | Type     | Påkrevd | Beskrivelse |
|------|-----------|--------|---------|-------------|
| 📥 Body | `Email`  | String   | ✅ Ja   | brukerens epostadresse |
| 📥 Body | `Password` | String | ✅ Ja   | brukerens passord |

📥 **Request Body:**
```json
{
    "Email": "test@test.no",
    "Password": "Hopla2025!"
}
```
💾 **Syntax:**
```bash
curl -X POST "https://hopla.onrender.com/users/login" \
     -H "Content-Type: application/json" \
     -d '{"Email": "test@test.no","Password": "Hopla2025!"}'
```
📤 **Eksempel på respons:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiIxMjM0NTY3OC0wMDAwLTAwMDAtMDAwMS0xMjM0NTY3ODAwMDEiLCJlbWFpbCI6InRlc3RAdGVzdC5ubyIsIm5iZiI6MTc0MDQ4NjkwNCwiZXhwIjoxNzQxMDkxNzA0LCJpYXQiOjE3NDA0ODY5MDR9.Tds78EAr8iZ0Y6_M0f1lcwk11sgAapfSpwXk5T9RdXU",
    "userId": "12345678-0000-0000-0001-123456780001",
    "name": "Magne Baller Ilufta",
    "alias": "MangeBallerILufra",
    "profilePictureURL": "https://images.unsplash.com/
photo-1614203586837-1da2bef106a2?w=200&h=200&fit=crop"
}

```
🔑 **NB Tokenet må lagres av frontend til senere bruk.**

📟 **Mulige statuskoder:**
- ✅ `200 Ok` – Forespørsel sendt.
- ❌ `401 Unauthorized` – Feil e-post eller passord.

---
### GET /users/profile

🔙 Tilbake til[`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Henter ut informasjon om en bruker.

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

---




### GET /horses/userhorses
🔙 Tilbake til[`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Henter en liste over en brukers hester.

📑 **Parametere:**
|Parameter| Name | Type     | Påkrevd | Beskrivelse |
|------|-----------|--------|---------|-------------|
| 🔒 Header | Authorization | Bearer Token  | 🔑 Ja | Krever autenseringstoken | 
| 🔎 Query | `userId`  | Guid   | 🟡 Nei   | ID-en til brukeren |

#### 🔎 Query
`[?userId]` - 🟡 Valgfritt - userid hentes ut fra Bearer Token, hvis queryen ikke spesifiseres.

#### 💾 Syntaks
```bash
curl -X GET "https://hopla.onrender.com/horses/userhorses?userId=[Guid]" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>"
```
📤 **Eksempel på respons:**
```json
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
    }
]
```
📟 **Mulige statuskoder:(testes)**
- ✅ `200 OK` – Hester ble hentet.
- ❌ `401 Unauthorized` - Ingen eller ugyldig token sendt.
- ❌ `404 Not Found` – Ingen hester funnet for brukeren.

---



### GET /horses/{horseId}
🔙 Tilbake til[`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Henter mer informasjon om en hest.

📑 **Parametere:**
|Parameter| Name | Type     | Påkrevd | Beskrivelse |
|------|-----------|--------|---------|-------------|
| 🔒 Header | Authorization | Bearer Token  | 🔑 Ja | Krever autenseringstoken | 
| 📂 Path | `horseId`  | Guid   | ✅ Ja   | ID-en til hesten |

#### 📂 Path
`<horseId>` - ✅ Påkrevd - Gyldig `Guid` for hestens ID må spesifiseres i URL.

#### 💾 Syntax:
```bash
curl -X GET "https://hopla.onrender.com/horses/<horseId>" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>"
```
📤 **Eksempel på respons:**
```json
{
    "name": "Flodhest",
    "horsePictureUrl": "https://images.unsplash.com/
    photo-1599053581540-248ea75b59cb?h=200&w=200&fit=crop",
    "dob": "2017-01-25T15:18:15.586439Z",
    "age": 8,
    "breed": "Zebra"
}

```
📟 **Mulige statuskoder:**
- ✅ `200 OK` – Hest ble hentet.
- ❌ `401 Unauthorized` - Ingen eller ugyldig token sendt.
- ❌ `404 Not Found` – Hest ikke funnet.


### GET /div/helloworld
🔙 Tilbake til [`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Sjekker om API-et svarer.

💾 **Syntaks:**
```bash
curl -X GET "https://hopla.onrender.com/div/helloworld"
```

📤 **Eksempel på respons:**
```json
{
    "Message": "HelloWorld"
}
```

📟 **Mulige statuskoder:**
- ✅ `200 OK` – API-et er oppe.

---


### GET /usershikes/user

🔙 Tilbake til[`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Henter ut informasjon om turer til liste som vises på f.eks profil eller turoversikt til en bruker.

📑 **Parametere:**
|Parameter| Name | Type     | Påkrevd | Beskrivelse |
|------|-----------|--------|---------|-------------|
| 🔒 Header | `Authorization` | Bearer Token  | 🔑 Ja | Krever autenseringstoken | 
| 🔎 Query | `userId`  | Guid   | 🟡 Nei   | ID-en til brukeren |
| 🔎 Query | `pageNumber`  | int   | 🟡 Nei   | Side nummer |
| 🔎 Query | `pageSize`  | int   | 🟡 Nei   | Antall resultater pr side |


#### 🔎 Query:

`?userId=[Guid]` - 🟡 Valgfritt: Henter bruker hvis spesifisert. Hvis utelatt hentes bruker ut fra Bearer Token.
`?userId=[Guid]` - 🟡 Valgfritt: Viser neste resultater. Hvis ikke oppgitt, settes denne til 1. 
`?userId=[Guid]` - 🟡 Valgfritt: Antall resultater pr side. Hvis ikke oppgitt, settes denne til angit verdi i SystemSettings

#### 💾 Syntax:
```bash
curl -X GET "https://hopla.onrender.com/userhikes/user?userId=[Guid]&pageNumber=[int]&pageSize=[int]" \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <TOKEN>"
```

📤 **Eksempel på respons med queryene pageNumber=7 og pageSize=2**
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


📟 **Mulige statuskoder:**
- ✅ `200 OK` – Hester ble hentet.
- ❌ `401 Unauthorized` - Ingen eller ugyldig token sendt.
- ❌ `404 Not Found` – Bruker ikke funnet.

---


### GET /div/status
🔙 Tilbake til [`Endpoints brukt og testet av frontend`](#endpoints-brukt-og-testet-av-frontend)

📌 **Beskrivelse:** Returnerer API-status, inkludert oppetid, request count og error count.

💾 **Syntaks:**
```bash
curl -X GET "https://hopla.onrender.com/div/status"
```
📤 **Eksempel på respons:**
```json
{
  "uptime": "24 hours",
  "request_count": 10000,
  "error_count": 5
}
```
📟 **Mulige statuskoder:**
- ✅ `200 OK` – API-status returnert.

---
### GET /users/{userid}

**Beskrivelse:** Henter informasjon om en spesifikk bruker.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userId`  | Guid string | ✅ Ja   | ID-en til brukeren |

📌 **Syntax:**
```bash
GET "https://hopla.onrender.com/users/{userId}"
```

**Eksempel**
```bash
curl -X GET "https://hopla.onrender.com/users/12345678-0000-0000-0001-123456780001"
```

📌 **Eksempel på respons:**
```json
{
  "id": "12345678-0000-0000-0001-123456780001",
    "name": "Knugen Kneggason",
    "alias": "Kneggern",
    "email": "kneggeknug",
    "passwordHash": "HashedPassword",
    "profilePictureUrl": null,
    "admin": false,
    "premium": false,
    "verifiedTrail": false,
    "createdAt": "2025-02-18T18:39:20.5969544Z",
    "dob": "2025-02-18T18:39:20.5969547Z",
    "images": [],
    "horses": []
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Bruker ble hentet.
- ❌ `404 Not Found` – Ingen bruker ble funnet.


---

### POST /users

**Beskrivelse:** Oppretter en ny bruker.


📌 **Request Body:**
```json
{
    "Name": "Knugen Kneggason",
    "Alias": "Kneggern",
    "Email": "kneggeknug",
    "PasswordHash": "HashedPassword"
}
```
📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/users/new" \
     -H "Content-Type: application/json" \
     -d '{"Name": "Knugen Kneggason", "Alias": "Kneggern", "Email": "kneggeknug","PasswordHash": "HashedPassword"}'
```
**Eksempel på response:**
```bash
{
    "id": "b57f4c5c-aff5-44b2-8b1e-bec55ebb8719",
    "name": "Knugen Kneggason",
    "alias": "Kneggern",
    "email": "kneggeknug",
    "passwordHash": "HashedPassword",
    "profilePictureUrl": null,
    "admin": false,
    "premium": false,
    "verifiedTrail": false,
    "createdAt": "2025-02-18T18:39:20.5969544Z",
    "dob": "2025-02-18T18:39:20.5969547Z",
    "images": [],
    "horses": []
}
```

📌 **Mulige statuskoder:**
- ✅ `201 Created` – Forespørsel sendt.
- ❌ `400 Bad Request` – Feil input.

---

### PUT /users/{userid}

**Beskrivelse:** Endrer informasjon om en bruker.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userId`  | Guid String | ✅ Ja   | ID-en til brukeren som skal endres |

📌 **Eksempel:**
```bash
{
    "Name": "Knuten Knaggesen",
    "Alias": "KnutKnagg",
    "Email": "knut@knagg.no",
    "PasswordHash": "HashedpassW0rd"
}
```
**Eksempel:**
```bash
curl -X PUT "https://hopla.onrender.com/users/{}" \
     -H "Content-Type: application/json" \
     -d '{"Name": "Knuten Knaggesen", "Alias": "KuntKnagg", "Email": "knut@knagg.no","PasswordHash": "HashedpassW0rd"}'
```

📌 **Mulige statuskoder:**
- ✅ `201 Created` – Forespørsel sendt.
- ❌ `400 Bad Request` – Feil input.

---

### DELETE /users/{userid}

**Beskrivelse:** Sletter en bruker.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userId`  | Guid String | ✅ Ja   | ID-en til brukeren som sletter brukeren |

📌 **Syntax:**
```bash
DELETE "https://hopla.onrender.com/users/delete/{UserId}"
```
**Eksempel på response**
```bash
curl -X DELETE "https://hopla.onrender.com/users/delete/b57f4c5c-aff5-44b2-8b1e-bec55ebb8719"
```

📌 **Mulige statuskoder:**
- ✅ `200 OK` – Relasjonen ble slettet.
- ❌ `404 Not Found` – Relasjonen eksisterer ikke.

---




### POST /horses

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Registrerer en ny hest.

---

### PUT /horses

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Oppdaterer informasjon om en hest.

---

### DELETE /horses

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Sletter en hest.

---

### GET /userrelations/friends/{userid}
**Beskrivelse:** Henter en liste over en brukers venner.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userid`  | string | ✅ Ja   | ID-en til brukeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/userrelations/friends/12345"
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Venneliste returnert.
- ❌ `404 Not Found` – Ingen venner funnet.

---

### GET /userrelations/requests/{userid}
**Beskrivelse:** Henter en liste over en brukers venneforespørsler.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userid`  | string | ✅ Ja   | ID-en til brukeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/userrelations/requests/12345"
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Forespørsler returnert.
- ❌ `404 Not Found` – Ingen forespørsler funnet.

---

### GET /userrelations/blocked/{userid}
**Beskrivelse:** Henter en liste over brukere som er blokkert.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userid`  | string | ✅ Ja   | ID-en til brukeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/userrelations/blocked/12345"
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Blokkeringsliste returnert.
- ❌ `404 Not Found` – Ingen blokkerte brukere funnet.

---

### POST /userrelations/friendrequests
**Beskrivelse:** Sender en venneforespørsel.

📌 **Request Body:**
```json
{
  "fromUserId": "12345",
  "toUserId": "67890"
}
```
📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/userrelations/friendrequests" \
     -H "Content-Type: application/json" \
     -d '{"fromUserId": "12345", "toUserId": "67890"}'
```
📌 **Mulige statuskoder:**
- ✅ `201 Created` – Forespørsel sendt.
- ❌ `400 Bad Request` – Feil input.

---

### POST /userrelations/block
**Beskrivelse:** Blokkerer en bruker.

📌 **Request Body:**
```json
{
  "fromUserId": "12345",
  "toUserId": "67890"
}
```
📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/userrelations/block" \
     -H "Content-Type: application/json" \
     -d '{"fromUserId": "12345", "toUserId": "67890"}'
```
📌 **Mulige statuskoder:**
- ✅ `201 Created` – Brukeren ble blokkert.
- ❌ `400 Bad Request` – Feil input.

---

### PUT /userrelations/{userrelationid}
**Beskrivelse:** Oppdaterer en brukerrelasjon.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userrelationid`  | string | ✅ Ja   | ID-en til relasjonen |

📌 **Request Body:**
```json
{
  "status": "accepted"
}
```
📌 **Eksempel:**
```bash
curl -X PUT "https://hopla.onrender.com/userrelations/123" \
     -H "Content-Type: application/json" \
     -d '{"status": "accepted"}'
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Relasjonen ble oppdatert.
- ❌ `400 Bad Request` – Feil input.
- ❌ `404 Not Found` – Relasjonen eksisterer ikke.

---

### DELETE /userrelations/{fromUserId}/{toUserId}
**Beskrivelse:** Sletter en brukerrelasjon.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `fromUserId`  | string | ✅ Ja   | ID-en til brukeren som fjerner relasjonen |
| `toUserId`  | string | ✅ Ja   | ID-en til den andre brukeren |

📌 **Eksempel:**
```bash
curl -X DELETE "https://hopla.onrender.com/userrelations/123/456"
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Relasjonen ble slettet.
- ❌ `404 Not Found` – Relasjonen eksisterer ikke.

---

### GET /trail/list
🚧🚧🚧 *Under utvikling. Filtere er ikke utviklet. kommer senere*

**Beskrivelse:** Henter nærmeste tilgjengelige løyper basert på posisjon.

📌 **Query-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `latitude`  | float | ✅ Ja   | Breddegrad |
| `longitude` | float | ✅ Ja   | Lengdegrad |
| `limit`     | int   | ❌ Nei  | Antall resultater |
| `offset`    | int   | ❌ Nei  | Hopp over X resultater |
| `filter`    | int   | ❌ Nei  | Filteregenskaper  |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/trail/closest?latitude=59.9139&longitude=10.7522&limit=10&offset=0"
```
📌 **Eksempel på respons:**
```json
{
  "trails": [
    { "id": "t1", "name": "Skogsstien", "distance": 12.5 },
    { "id": "t2", "name": "Fjellruta", "distance": 20.3 }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Løyper returnert.
- ❌ `400 Bad Request` – Feil med forespørselen.
- ❌ `404 Not Found` – Ingen løyper funnet.

---


### GET /trail/map
🚧🚧🚧 *Under utvikling, Filtere er ikke utviklet. kommer senere*

**Beskrivelse:** Henter nærmeste tilgjengelige løyper basert på posisjon.

📌 **Query-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `latitude`  | float | ✅ Ja   | Breddegrad |
| `longitude` | float | ✅ Ja   | Lengdegrad |
| `zoomlevel` | int   | ✅ Ja   | Kartutsnittes Zoomlevel|
| `width`     | int   | ❌ Nei  | Bredde skjerm i pixler |
| `height`    | int   | ❌ Nei  | Høyde skjerm i pixler  |
| `filter`    | int   | ❌ Nei  | Filteregenskaper  |


📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/trails/list?latitude=60.7923&longitude=10.695&zoomlevel=10"
```
📌 **Eksempel på respons:**
```json
{ 
  "trails": [ {
  "id": 1,
  "name": "Biriløypa",
  "latMean": 60.95558,
  "longMean": 10.6115,
  "trailDetails": null,
  "trailAllCoordinates": null,
  "trailFilters": null,
  "trailReviews": []
  } ,
  {
  "id": 2,
  "name": "Gjøviksruta",
  "latMean": 60.7925,
  "longMean": 10.695,
  "trailDetails": null,
  "trailAllCoordinates": null,
  "trailFilters": null,
  "trailReviews": []
   } ]
}

```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Løyper returnert.
- ❌ `400 Bad Request` – Feil med forespørselen.
- ❌ `404 Not Found` – Ingen løyper funnet.

---

### POST /trail
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Oppretter en ny løype basert på ride-data.

---

### PUT /trail/{trailId}
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Oppdaterer en eksisterende løype.

---

### DELETE /trail/{trailId}
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Sletter en løype.

---
### GET /rides/user/{userId}
**Beskrivelse:** Henter en liste over en brukers rides.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userId`  | string | ✅ Ja   | ID-en til brukeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/rides/user/12345"
```
📌 **Eksempel på respons:**
```json
{
  "rides": [
    { "id": "r1", "distance": 15.2, "duration": "1h 30m" },
    { "id": "r2", "distance": 8.4, "duration": "45m" }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Rideturer returnert.
- ❌ `404 Not Found` – Ingen rideturer funnet.

---

### GET /rides/{rideId}/details
**Beskrivelse:** Henter detaljer om en spesifikk ride.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `rideId`  | string | ✅ Ja   | ID-en til rideturen |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/rides/123/details"
```
📌 **Eksempel på respons:**
```json
{
  "id": "123",
  "distance": 15.2,
  "duration": "1h 30m",
  "horse": "Thunder"
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Ride detaljer returnert.
- ❌ `404 Not Found` – Ingen detaljer funnet.

---

### GET /rides/{rideId}/trackingdata
**Beskrivelse:** Henter GPS-spor for en spesifikk ride.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `rideId`  | string | ✅ Ja   | ID-en til rideturen |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/rides/123/trackingdata"
```
📌 **Eksempel på respons:**
```json
{
  "rideId": "123",
  "tracking": [
    { "latitude": 59.9139, "longitude": 10.7522, "timestamp": "2024-02-10T10:00:00Z" },
    { "latitude": 59.9145, "longitude": 10.7530, "timestamp": "2024-02-10T10:05:00Z" }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Trackingdata returnert.
- ❌ `404 Not Found` – Ingen trackingdata funnet.

---

### POST /rides
**Beskrivelse:** Oppretter en ny ride.

📌 **Request Body:**
```json
{
  "userId": "12345",
  "horseId": "h1",
  "distance": 10.2,
  "duration": "1h 15m",
  "route": [
    { "latitude": 59.9139, "longitude": 10.7522 },
    { "latitude": 59.9145, "longitude": 10.7530 }
  ]
}
```
📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/rides" \
     -H "Content-Type: application/json" \
     -d '{"userId": "12345", "horseId": "h1", "distance": 10.2, "duration": "1h 15m", "route": [{"latitude": 59.9139, "longitude": 10.7522}, {"latitude": 59.9145, "longitude": 10.7530}]}'
```
📌 **Mulige statuskoder:**
- ✅ `201 Created` – Ride opprettet.
- ❌ `400 Bad Request` – Feil i forespørselen.

---


### PUT /rides/{rideId}
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Oppdaterer en ride med nye detaljer eller bilder.

---
### GET /messages/{userId}
**Beskrivelse:** Henter en liste over meldinger mottatt av en bruker.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userId`  | string | ✅ Ja   | ID-en til brukeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/messages/12345"
```
📌 **Eksempel på respons:**
```json
{
  "messages": [
    { "id": "m1", "from": "67890", "content": "Hei!", "timestamp": "2024-02-10T12:00:00Z" },
    { "id": "m2", "from": "54321", "content": "Hvordan går det?", "timestamp": "2024-02-10T12:05:00Z" }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Meldinger returnert.
- ❌ `404 Not Found` – Ingen meldinger funnet.

---

### GET /messages/{sUserId}/{rUserId}
**Beskrivelse:** Henter en samtale mellom to brukere.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `sUserId` | string | ✅ Ja   | ID-en til avsenderen |
| `rUserId` | string | ✅ Ja   | ID-en til mottakeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/messages/12345/67890"
```
📌 **Eksempel på respons:**
```json
{
  "conversation": [
    { "id": "m1", "from": "12345", "to": "67890", "content": "Hei!", "timestamp": "2024-02-10T12:00:00Z" },
    { "id": "m2", "from": "67890", "to": "12345", "content": "Hei tilbake!", "timestamp": "2024-02-10T12:02:00Z" }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Samtalen returnert.
- ❌ `404 Not Found` – Ingen samtale funnet.

---

### POST /messages
**Beskrivelse:** Sender en melding til en annen bruker.

📌 **Request Body:**
```json
{
  "fromUserId": "12345",
  "toUserId": "67890",
  "content": "Hei, hvordan går det?"
}
```
📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/messages" \
     -H "Content-Type: application/json" \
     -d '{"fromUserId": "12345", "toUserId": "67890", "content": "Hei, hvordan går det?"}'
```
📌 **Mulige statuskoder:**
- ✅ `201 Created` – Melding sendt.
- ❌ `400 Bad Request` – Feil i forespørselen.

---

### PUT /messages/{messageId}
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Endrer innholdet i en melding.

---

### DELETE /messages/{messageId}
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Sletter en melding fra databasen.

---

### GET /stables/list
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Henter en liste over registrerte staller.

---

### POST /stables
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Registrerer en ny stall.

---

### ??? /stableusers
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Administrasjon av stallbrukere.

---


### GET /stablemessages/{stableId}
**Beskrivelse:** Henter meldinger fra en spesifikk stall.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `stableId`  | string | ✅ Ja   | ID-en til stallen |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/stablemessages/123"
```
📌 **Eksempel på respons:**
```json
{
  "messages": [
    { "id": "m1", "from": "67890", "content": "Velkommen til stallen!", "timestamp": "2024-02-10T14:00:00Z" }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Meldinger returnert.
- ❌ `404 Not Found` – Ingen meldinger funnet.

---

### POST /stablemessages
**Beskrivelse:** Sender en melding i en stall.

📌 **Request Body:**
```json
{
  "stableId": "123",
  "fromUserId": "67890",
  "content": "Hei alle sammen!"
}
```
📌 **Eksempel:**
```bash
curl -X POST "https://hopla.onrender.com/stablemessages" \
     -H "Content-Type: application/json" \
     -d '{"stableId": "123", "fromUserId": "67890", "content": "Hei alle sammen!"}'
```
📌 **Mulige statuskoder:**
- ✅ `201 Created` – Melding sendt.
- ❌ `400 Bad Request` – Feil i forespørselen.

---

### PUT /stablemessages/{stableMessageId}
🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Endrer innholdet i en stall-melding.

---

