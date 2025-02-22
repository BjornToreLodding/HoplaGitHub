document.addEventListener("DOMContentLoaded", () => {
    checkUserStatus(); // Sjekk om bruker er logget inn/admin
    checkAuthStatus(); // Sjekker om brukeren er innlogget ved start
});

function checkAuthStatus() {
    const token = localStorage.getItem("authToken");
    const logoutButton = document.getElementById("logout-button");

    if (token) {
        logoutButton.classList.remove("hidden"); // Vis knappen hvis token finnes
    } else {
        logoutButton.classList.add("hidden"); // Skjul hvis ikke innlogget
    }
}

function logout() {
    localStorage.removeItem("authToken"); // Fjern token
    alert("Du er nå logget ut!");
    checkAuthStatus(); // Oppdater UI etter logout
}

document.getElementById("logout-button").addEventListener("click", logout);

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
                { name: "Bytte Passord", action: "loadContent('users', 'byttepassord')" },
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
