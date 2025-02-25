







# Profil

<table>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/b4ebfe4c9253d1e43e64b8b78cf50690/image.png){width="208" height="408"} 
<br><br>**Status:** <br>si ifra hvis det ønskes forrandringer
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

![image.png](/vakvaer/hopla/-/wikis/uploads/b59f667b696efff50d4b81ce91ca3c72/image.png){width="248" height="456"}
<br><br>**Status:** <br>si ifra hvis det ønskes forrandringer. <br><br>(NB Bruker ikke userID, men Token)
</td>
<td>

Main profil side

* **`Get `request ~~som bruker userID med informasjon~~ :** (NB Bruker ikke userID, men Token)
  * **Brukernavn(alias), epost og bilde**
  * **(Bilde: bruker size 200.dp og clip circleshape)**

* **GET med Authorization:
  * **GET https://hopla.onrender.com/users/myprofile**
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

![image.png](/vakvaer/hopla/-/wikis/uploads/68cec6509720727d6fcc482677031ce9/image.png){width="324" height="565"}
<br><br>**Status:** <br>Denne skal virke når databasen blir oppdatert
</td>
<td>

**Profil -\> Mine hester**

* `Get` request på å hente brukeren som er logget inn sine hester
* Trenger å få: bilde og navn på hesten (hestens id for å kunne brukes til å gå til detalj siden om hver enkelt hest? )

* **GET med Authorization:
  * **GET GET https://hopla.onrender.com/horses/userhorses/**
  * **auth Type Bearer Token**
  * **Token = "LangTokenStringFraResponsenPå/users/login"**

 **Eksempel på response body JSON**
```Postman_JSON
[
    {
        "id": "12345678-0000-0000-0002-123456780001",
        "name": "Flodhest",
        "profilePictureUrl": "https://images.unsplash.com/
        photo-1599053581540-248ea75b59cb?h=64&w=64&fit=crop"
    },
    {
        "id": "12345678-0000-0000-0002-123456780018",
        "name": "Kronprins Durek",
        "profilePictureUrl": "https://images.unsplash.com/
        photo-1438283173091-5dbf5c5a3206?h=64&w=64&fit=crop"
    },
    {
        "id": "12345678-0000-0000-0002-123456780025",
        "name": "Fola Blakken",
        "profilePictureUrl": "https://images.unsplash.com/
        photo-1438283173091-5dbf5c5a3206?h=64&w=64&fit=crop"
    }
]
```
</td>
</tr>
<tr>
<td>

![image.png](/vakvaer/hopla/-/wikis/uploads/6ea310ba1383bdd11b753b2f2803cf05/image.png){width="316" height="517"}
<br><br>**Status:** <br>Denne skal virke når databasen blir oppdatert
</td>
<td>

**Profil -\> Mine hester -\> Velge en spesifikk hest**

* Get request ut ifra hestens id for å hente: navn, bilde, rase og alder/fødselsdato

* **GET med Authorization:
  * **GET https://hopla.onrender.com/horses/{horseGuid}**
  * **auth Type Bearer Token** (kan enkelt deaktiveres)
  * **Token = "LangTokenStringFraResponsenPå/users/login"**

  **Eksempel på response body**
```Postman_JSON
  {
    "name": "Flodhest",
    "profilePictureUrl": "https://images.unsplash.com/
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
</td>
<td>

**Profil -\> Venner**

* Get request for å hente brukeren som er logget inn sine venner (andre profiler kan være: venn, følger, "pending request" eller none (ingen forhold). På denne siden er det alle profiler som er venn
* Trenger bilde, navn og vennestatus enum verdi (id for å vise person detalj siden)

  (bilde: nå er det 64.dp)
</td>
</tr>
<tr>
<td>

![image.png](uploads/c7a6e3a1b0bb4e20c192c65bd6bf5531/image.png){width="309" height="502"}
</td>
<td>

**Profil -\> Følger**

* Get request for å hente brukeren som er logget inn personer som den følger. På denne siden er det alle profiler som er følger
* Trenger bilde, navn og vennestatus enum verdi (id for å vise person detalj siden)

  (bilde: nå er det 64.dp)
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
* Hente de 5 første? Så når brukeren blar ned så sendes ny request på de 5 neste osv?

**Løyper -\> Første side**

* Vise alle løyper som brukere har lagt inn i appen. alfabetisk? Etter når de er lagt inn? Etter id 1.2.3 osv?

**Løyper -\> Icon 2 fra venstre**

* Vise løyper nærmest brukerens posisjon

**Løyper -\> Hjerte ikon**

* Kun løyper som brukeren har trykket liker på

**Løyper -\> Stjerne ikon**

* Løyper til brukere brukeren følger
</td>
</tr>
<tr>
<td>

![image.png](uploads/6d56ffc28e83cf25bbf1f62fb664e3f6/image.png){width=307 height=576}
</td>
<td>

Alle innlegg her skal sorteres etter at det nyeste vises øverst

**Hjem** 

* Denne skal inneholde: Nye løyper lagt til, nye kommentarer på løyper fra alle brukere i appen. 
* 5 og 5 innlegg etter hvert som man blar blir hentet (samme måte som over) 
  * Hvis løype: bilde, id, navn, løype beskrivelse, id og brukernavn på bruker som har registrert løypa
  * Hvis ny kommentar på løype: løype navn, løype id, kommentaren selv, evt bilde lagt til i kommentaren, brukernavn og brukerid til brukeren som la til kommentaren

**Hjem -\> Venner** 

* Samme som over, men alle løyper og kommentarer som hentes må være venn med brukeren som er logget in 
* Også hente: 
  * Stjerner gitt av venner på løyper (trenger da num stjerner, løype navn og løype id, brukerid og brukernavn) 

**Hjem -\> Område** 

* Samme som første bare at det er en viss avstand fra brukeren på alt som vises her

**Hjem -\> Følger**

* Vises ikke på bildet men skal legge til (kanskje bytte ut/ta vekk populært og/eller område) 
* Samme som første bare at det kun er brukere som den innloggede brukeren følger
</td>
</tr>
<tr>
<td>

</td>
<td>

</td>
</tr>
</table>

