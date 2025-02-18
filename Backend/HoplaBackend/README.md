



# API Endpoints

## Oversikt over APIets Endpoints og hvordan man bruker det.

- Hva man kan sende med av path, parametere, body
- Hva det returnerer
- Eksempel med curl
- Hvilke feilkoder de kan returnere

---

## Endpoints Oversikt

| 🔄 | Metode | Endpoint | Beskrivelse/Parameters |
|------|--------|-------------------------------|-------------|
| ✅ | **GET** | [`/div/helloworld`](#get-divhelloworld) | Sjekke om APIet svarer |
| ✅ | **GET** | [`/div/status`](#get-divstatus) | - oppetid, requestcount + errorcount. |
| ❌ | GET | [`/users/{userid}`](#get-usersuserid) | - Info om en bruker |
| ❌ | POST | [`/users`](#post-users) | - opprette ny bruker |
| ❌ | PUT | [`/users/{userid}`](#put-usersuserid) | - endre noe på en bruker |
| ❌ | DELETE | [`/users/{userid}`](#delete-usersuserid) | - slette en bruker |
| ✅ | GET | [`/horses/{userId}`](#get-horsesuserid) | - liste en brukers hester |
| ❌ | POST | [`/horses`](#post-horses) | - registrere ny hest |
| ❌ | PUT | [`/horses`](#put-horses) | |
| ❌ | DELETE | [`/horses`](#delete-horses) | |
| ✅ | GET | [`/userrelations/friends/{userid}`](#get-userrelationsfriendsuserid) | |
| ✅ | GET | [`/userrelations/requests/{userid}`](#get-userrelationsrequestsuserid) | |
| ✅ | GET | [`/userrelations/blocked/{userid}`](#get-userrelationsblocksuserid) | |
| ✅ | POST | [`/userrelations/friendrequests`](#post-userrelationsfriendrequestsuserid) | |
| ✅ | POST | [`/userrelations/block`](#post-userrelationsblock) | |
| ✅ | PUT | [`/userrelations/{userrelationid}`](#put-userrelationsuserrelationid) | |
| ✅ | DELETE | [`/userrelations/{fromUserId}/{toUserId}`](#delete-userrelationsfromuseridtouserid) | |
| ⚠️ | GET | [`/trail/closest`](#get-trailclosest) | Query=?latitude=x&longitude=y&limit=n&offset=m |
| ⚠️ | GET | [`/trail/list`](#get-traillist) | - Query= ?zoomlevel&filters |
| ❌ | POST | [`/trail`](#post-trail) | Bruker data fra en Ride og lager en løype av det. |
| ❌ | PUT | [`/trail/{trailId}`](#put-trailtrailid) | evt endre en trail |
| ❌ | DELETE | [`/trail/{trailId}`](#delete-trailtrailid) | |
| ✅ | GET | [`/rides/user/{userId}`](#get-ridesuseruserid) | |
| ✅ | GET | [`/rides/{rideId}/details`](#get-ridesrideiddetails) | Detaljer fra en tur |
| ✅ | GET | [`/rides/{rideId}/trackingdata`](#get-ridesrideidtrackingdata) | Fullstendig liste med koordinater på en ride |
| ✅ | GET | [`/rides`](#get-rides) | |
| ✅ | POST | [`/rides`](#post-rides) | |
| ❌ | PUT | [`/rides/{rideId}`](#put-ridesrideid) | - Nesten utviklet. Trenger testing bildeopplegg |
| ✅ | GET | [`/messages/{userId}`](#get-messagesuserid) | - Viser siste melding mottatt fra alle brukere |
| ✅ | GET | [`/messages/{sUserId}/{rUserId}`](#get-messagessuseridruserid) | - Viser meldinger mellom 2 brukere, DESC Time |
| ✅ | POST | [`/messages`](#post-messages) | - Sende melding til en annen bruker. |
| ❌ | PUT | [`/messages/{messageId}`](#put-messagesmessageid) | - Endre en melding? |
| ❌ | DELETE | [`/messages/{messageId}`](#delete-messagesmessageid) | - Slette en melding |
| ❌ | GET | [`/stables/list`](#get-stableslist) | -Viser alle staller, evt nærmeste staller |
| ❌ | POST | [`/stables`](#post-stables) | - Registrere ny stall |
| ❌ | ??? | [`/stableusers`](#stableusers) | Kommer senere, men dette er medlemmer av en stall. |
| ✅ | GET | [`/stablemessages/{stableId}`](#get-stablemessagesstableid) | |
| ✅ | POST | [`/stablemessages`](#post-stablemessages) | |
| ❌ | PUT | [`/stablemessages/{stableMessageId}`](#put-stablemessagesstablemessageid) | Endre en melding? |

---

## MockData (Testdata)
Ved å kjøre APIene under, så opprettes mockdata som er hardkodet i egne mock-filer. Disse blir det kun laget en generell dokumentasjon på som gjelder alle Mock-endpoints.

| Status | Metode | Endpoint | Beskrivelse |
|--------|---------|-------------|----------|
| ✅ | POST | `/mock/cleardatabase` | Sletter all eksempeldata |
| ❌ | POST |  `/mock/createdatabase` | Oppretter eksempeldata |
| ✅ | POST | `/mock/createcontent` | Oppretter eksempeldata Tenkte /createdatabase skulle erstatte denne|
| ✅ | POST |  `/mock/createusers` | Oppretter testbrukere |
| ✅ | POST |  `/mock/createhorses` | Oppretter testhester |
| ✅ | POST | `/mock/createfriendrequests` | Genererer venneforespørsler |
| ✅ | POST | `/mock/createmessages` | Lager testmeldinger |
| ✅ | POST | `/mock/createstables` | Oppretter teststaller |
| ❌ | POST | `/mock/createstableusers` | Oppretter brukere til staller |
| ✅ | POST | `/mock/createstablemessages` | Genererer stallmeldinger |
| ✅ | POST | `/mock/createrides` | Oppretter test-rideturer |
| ❌ | POST | `/mock/createridedetails` | Oppretter detaljer for rideturer |
| ❌ | POST | `/mock/createridetrackingdata` | Oppretter GPS-spor for rideturer |
| ❌ | POST | `/mock/createridereviews` | Oppretter anmeldelser av rideturer |
| ✅ | POST | `/mock/createtrails` | Oppretter test-løyper |
| ❌ | POST | `/mock/createtraildetails` | Oppretter detaljer om løyper |
| ❌ | POST | `/mock/createtrailreviews` | Oppretter anmeldelser av løyper |
| ❌ | POST | `/mock/createtrailfilters` | Oppretter filtre for løyper |

✅ = Fullført
<br>⚠️ = Utviklet, men har feil eller at ikke alle queries er utviklet. 
<br>❌ = Ikke utviklet ennå


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

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `id`  | string | ✅ Ja   | ID-en til ressursen |

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
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Forespørselen var vellykket.
- ⚠️ `400 Bad Request` – Feil i forespørselen (manglende eller ugyldige parametere).
- ❌ `404 Not Found` – Ressursen ble ikke funnet.

---



---

## API-detaljer

### GET /div/helloworld
**Beskrivelse:** Sjekker om API-et svarer.

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/div/helloworld"
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – API-et er oppe.

---

### GET /div/status
**Beskrivelse:** Returnerer API-status, inkludert oppetid, request count og error count.

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/div/status"
```
📌 **Eksempel på respons:**
```json
{
  "uptime": "24 hours",
  "request_count": 10000,
  "error_count": 5
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – API-status returnert.


---

## API-detaljer

### GET /users/{userid}

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Henter informasjon om en spesifikk bruker.

---

### POST /users

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Oppretter en ny bruker.

---

### PUT /users/{userid}

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Endrer informasjon om en bruker.

---

### DELETE /users/{userid}

🚧🚧🚧 *Under utvikling, kommer senere*

**Beskrivelse:** Sletter en bruker.


---

## API-detaljer

### GET /horses/{userId}
**Beskrivelse:** Henter en liste over en brukers hester.

📌 **Path-parametere:**
| Parameter | Type   | Påkrevd | Beskrivelse |
|-----------|--------|---------|-------------|
| `userId`  | string | ✅ Ja   | ID-en til brukeren |

📌 **Eksempel:**
```bash
curl -X GET "https://hopla.onrender.com/horses/12345"
```
📌 **Eksempel på respons:**
```json
{
  "horses": [
    { "id": "h1", "name": "Thunder", "breed": "Arabian", "age": 7 },
    { "id": "h2", "name": "Storm", "breed": "Friesian", "age": 5 }
  ]
}
```
📌 **Mulige statuskoder:**
- ✅ `200 OK` – Hester ble hentet.
- ❌ `404 Not Found` – Ingen hester funnet for brukeren.

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

### GET /trail/closest
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


### GET /trail/list
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

