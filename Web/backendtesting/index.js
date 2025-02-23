// Kjør ved siden lasting for å sjekke om bruker er innlogget
document.addEventListener("DOMContentLoaded", () => {
    updateUserUI();      // Oppdater brukergrensesnittet basert på om bruker er logget inn
    //checkUserStatus();   // Sjekk om brukeren er admin eller har spesifikke roller
    checkAuthStatus();   // Sjekker om brukeren har en aktiv sesjon
});
function checkUserStatus() {
    console.log("checkUserStatus() ikke implementert ennå.");
}
/*
document.addEventListener("DOMContentLoaded", updateUserUI);
document.addEventListener("DOMContentLoaded", () => {
    checkUserStatus(); // Sjekk om bruker er logget inn/admin
    checkAuthStatus(); // Sjekker om brukeren er innlogget ved start
});
*/
function checkAuthStatus() {
    const token = localStorage.getItem("authToken");
    const logoutButton = document.getElementById("logout-button");

    if (token) {
        logoutButton.classList.remove("hidden"); // Vis knappen hvis token finnes
    } else {
        logoutButton.classList.add("hidden"); // Skjul hvis ikke innlogget
    }
}


function goToLogin() {
    loadContent('users', 'login');
}

// Logg ut bruker
function logout() {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userInfo");
    updateUserUI(); // Oppdater UI etter utlogging
}
/*
function logout() {
    localStorage.removeItem("authToken"); // Fjern token
    alert("Du er nå logget ut!");
    checkAuthStatus(); // Oppdater UI etter logout
}
*/

document.getElementById("logout-button").addEventListener("click", logout);

// Oppdater visning basert på om brukeren er logget inn eller ikke
function updateUserUI() {
    const token = localStorage.getItem("authToken");
    const user = JSON.parse(localStorage.getItem("userInfo"));

    if (token && user) {
        document.getElementById("login-button").classList.add("hidden");
        document.getElementById("user-info").classList.remove("hidden");
        document.getElementById("logout-button").classList.remove("hidden");

        // Oppdater tekst med alias og navn
        document.getElementById("user-text").textContent = `Logget inn som: ${user.alias} (${user.name})`;

        // Sett profilbilde hvis tilgjengelig
        if (user.profilePictureURL) {
            const avatar = document.getElementById("user-avatar");
            avatar.src = user.profilePictureURL;
            avatar.classList.remove("hidden");
        }
    } else {
        // Vis kun login-knappen hvis bruker ikke er logget inn
        document.getElementById("login-button").classList.remove("hidden");
        document.getElementById("user-info").classList.add("hidden");
    }
}


// Laster inn sidemenyen basert på valgt toppmeny
function loadSideMenu(section) {
    const menuList = document.getElementById("side-menu-list");
    menuList.innerHTML = ""; // Tøm tidligere innhold

    let menuItems = [];

    switch (section) {
        case "admin":
            menuItems = [
                { name: "SystemSettings", action: "loadContent('admin', 'systemsettings')" },
                { name: "Rapporter", action: "loadContent('admin', 'rapporter')" },
                { name: "Statistikker", action: "loadContent('admin', 'statistikker')" }
            ];
            break;

        case "testing":
            menuItems = [
                { name: "Vis Alle Brukere", action: "loadContent('users', 'users_all')" },
                { name: "Vis BrukerRapporter", action: "loadContent('admin', 'userreports')" },
                { name: "Velg stall", action: "loadContent('stables', 'velgstallen')" }
            ];
            if (window.selectedStable) {
                menuItems.push(
                    { name: "Medlemmer", action: "loadContent('stables', 'medlemmer')" },
                    { name: "Meldinger", action: "loadContent('stables', 'meldinger')" }
                );
            }
            break;

        case "users":
            menuItems = [
                { name: "Login", action: "loadContent('users', 'login')" },
                { name: "Glemt Passord", action: "loadContent('users', 'glemtpassord')" },
                { name: "Register", action: "loadContent('users', 'register')" },
                { name: "Bytte Passord", action: "loadContent('users', 'changepw')" },
                { name: "Horses", action: "loadContent('users', 'horses')" },
                { name: "TurHistorikk", action: "loadContent('users', 'turhistorikk')" },
                { name: "Meldinger", action: "loadContent('users', 'meldinger')" },
                { name: "Venneforespørsler", action: "loadContent('users', 'venneforesporsler')" },
                { name: "Venner", action: "loadContent('users', 'venner')" },
                { name: "Følger", action: "loadContent('users', 'folger')" },
                { name: "Blokkerte", action: "loadContent('users', 'blokkerte')" },
                { name: "Innstillinger", action: "loadContent('users', 'innstillinger')" }
            ];
            break;

        case "stables":
            menuItems = [
                { name: "Velg stall", action: "loadContent('stables', 'velgstallen')" }
            ];
            if (window.selectedStable) {
                menuItems.push(
                    { name: "Medlemmer", action: "loadContent('stables', 'medlemmer')" },
                    { name: "Meldinger", action: "loadContent('stables', 'meldinger')" }
                );
            }
            break;

        case "turer":
            menuItems = [
                { name: "Liste", action: "loadContent('turer', 'list')" }
            ];
            break;
    }

    menuItems.forEach(item => {
        let li = document.createElement("li");
        li.innerHTML = `<a href="#" onclick="${item.action}">${item.name}</a>`;
        menuList.appendChild(li);
    });
}



// Laster innhold i hovedområdet
async function loadContent(section, page) {
    const mainContent = document.getElementById("main-content");

    try {
        // Last HTML først (hvis den finnes)
        const htmlResponse = await fetch(`./pages/${section}/${page}.html`);
        if (htmlResponse.ok) {
            mainContent.innerHTML = await htmlResponse.text();
        } else {
            mainContent.innerHTML = `<h2>Kunne ikke finne ${page}.html</h2>`;
        }

        // Deretter, last JavaScript-modulen (dersom den finnes)
        const module = await import(`./pages/${section}/${page}.js`);
        if (module.render) {
            module.render(mainContent); // Kall render-funksjonen i modulen
        }
    } catch (error) {
        console.error("Feil ved lasting av siden:", error);
        mainContent.innerHTML = `<h2>Kunne ikke laste inn ${page}.</h2>`;
    }
}

/*

function loadContent(section, page) {
    const mainContent = document.getElementById("main-content");
    mainContent.innerHTML = `<h2>Loading ${page}...</h2>`;

    import(`./pages/${section}/${page}.js`)
        .then(module => {
            mainContent.innerHTML = "";
            module.render(mainContent);
        })
        .catch(error => {
            console.error("Feil ved lasting av modul:", error);
            mainContent.innerHTML = "<h2>Kunne ikke laste inn siden.</h2>";
        });
}
*/