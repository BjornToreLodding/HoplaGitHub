document.addEventListener("DOMContentLoaded", () => {
    updateLoginState();
    updateUserUI();
    checkAuthStatus();
});

// Oppdater om brukeren er innlogget
function updateLoginState() {
    const token = localStorage.getItem("authToken");
    const loginScreen = document.getElementById("login-screen");
    const appContent = document.getElementById("app-content");

    if (token) {
        loginScreen.classList.add("hidden");
        appContent.classList.remove("hidden");
    } else {
        loginScreen.classList.remove("hidden");
        appContent.classList.add("hidden");
    }
}

// Sjekk auth status for logout-knapp
function checkAuthStatus() {
    const token = localStorage.getItem("authToken");
    const logoutButton = document.getElementById("logout-button");

    if (token) {
        logoutButton.classList.remove("hidden");
    } else {
        logoutButton.classList.add("hidden");
    }
}

// Logg ut
function logout() {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userInfo");
    updateLoginState();
    updateUserUI();
}

// Koble logout-knappen
document.getElementById("logout-button").addEventListener("click", logout);

// Oppdater bruker-UI
function updateUserUI() {
    const token = localStorage.getItem("authToken");
    const userInfo = localStorage.getItem("userInfo");

    const loginButton = document.getElementById("login-button");
    const userInfoElement = document.getElementById("user-info");
    const logoutButton = document.getElementById("logout-button");
    const userText = document.getElementById("user-text");
    const userAvatar = document.getElementById("user-avatar");

    if (!token || !userInfo) {
        if (loginButton) loginButton.classList.remove("hidden");
        if (userInfoElement) userInfoElement.classList.add("hidden");
        return;
    }

    let user = JSON.parse(userInfo);

    if (loginButton) loginButton.classList.add("hidden");
    if (userInfoElement) userInfoElement.classList.remove("hidden");
    if (logoutButton) logoutButton.classList.remove("hidden");

    if (userText) {
        userText.textContent = `Logget inn som: ${user.alias} (${user.name})`;
    }

    if (userAvatar) {
        userAvatar.src = user.pictureUrl || "https://via.placeholder.com/40"; // Fallback-bilde
        userAvatar.classList.remove("hidden");
    }
}

// Login-knapp (dummy login)
document.getElementById("login-submit").addEventListener("click", () => {
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    if (email && password) {
        localStorage.setItem("authToken", "dummy-token");
        localStorage.setItem("userInfo", JSON.stringify({
            alias: "Admin",
            name: email.split('@')[0],
            pictureUrl: "https://via.placeholder.com/40"
        }));

        updateLoginState();
        updateUserUI();
    } else {
        alert("Vennligst fyll ut e-post og passord.");
    }
});

// --- AKTIV LINK ---
function setActiveMenuItem(clickedElement) {
    document.querySelectorAll('nav ul li a').forEach(link => link.classList.remove('active'));
    document.querySelectorAll('#side-menu ul li a').forEach(link => link.classList.remove('active'));

    if (clickedElement) {
        clickedElement.classList.add('active');
    }
}

// --- TOPPMENY KLIKK ---
document.addEventListener('click', function (event) {
    if (event.target.matches('nav a[data-section]')) {
        event.preventDefault();
        const section = event.target.getAttribute('data-section');
        loadSideMenu(section);
        setActiveMenuItem(event.target);
    }
});

// --- LASTE SIDEMENY BASERT PÅ TOPPMENY ---
function loadSideMenu(section) {
    const menuList = document.getElementById("side-menu-list");
    menuList.innerHTML = "";

    let menuItems = [];

    switch (section) {
        case "Tools":
            menuItems = [
                { name: "SystemSettings", action: "loadContent('admin', 'systemsettings')" },
                { name: "Filter Løyper", action: "loadContent('admin', 'trailfilters')" },
            ];
        case "Reports":
            menuItems = [
                { name: "Ubehandlet", action: "loadContent('admin', 'new')" },
                { name: "Under Behandlig", action: "loadContent('admin', 'inprogress')" },
                { name: "Ferdig Behandlig", action: "loadContent('admin', 'resolved')" },
                { name: "Lukket", action: "loadContent('admin', 'closed')" }
                
            ];
            break;
        
        case "Stats":
            menuItems = [
                { name: "Fakta", action: "loadContent('stats', 'fakta')" },
                { name: "Nye brukere", action: "loadContent('stats', 'newusers')" },
                { name: "Turer", action: "loadContent('stats', 'turer')" },
                { name: "YNI", action: "loadContent('stats', 'yni')" }
                
            ];
            break;
    

        case "Experimental":
            menuItems = [
                { name: "Vis Alle Brukere", action: "loadContent('users', 'users_all')" },
                { name: "Vis BrukerRapporter", action: "loadContent('admin', 'userreports')" },
                { name: "Velg stall", action: "loadContent('stables', 'velgstallen')" }
            ];
            break;

        case "Users":
            menuItems = [
                { name: "Login", action: "loadContent('users', 'login')" },
                { name: "Glemt Passord", action: "loadContent('users', 'glemtpassord')" },
                { name: "Register", action: "loadContent('users', 'register')" },
                { name: "Bytte Passord", action: "loadContent('users', 'changepw')" },
                { name: "Horses", action: "loadContent('users', 'horses')" },
                { name: "TurHistorikk", action: "loadContent('users', 'turhistorikk')" },
                { name: "Meldinger", action: "loadContent('users', 'messages_all')" },
                { name: "Venneforespørsler", action: "loadContent('users', 'venneforesporsler')" },
                { name: "Venner", action: "loadContent('users', 'venner')" },
                { name: "Følger", action: "loadContent('users', 'folger')" },
                { name: "Blokkerte", action: "loadContent('users', 'blokkerte')" },
                { name: "Innstillinger", action: "loadContent('users', 'innstillinger')" }
            ];
            break;

        case "Stables":
            menuItems = [
                { name: "Vis Staller", action: "loadContent('stables', 'velgstallen')" },
                { name: "Vis Medlemmer", action: "loadContent('stables', 'velgstallen')" },
                { name: "Vis Meldinger", action: "loadContent('stables', 'velgstallen')" }
            ];
            break;

        case "Trails":
            menuItems = [
                { name: "Vis nærmeste løyper", action: "loadContent('turer', 'list')" },
                { name: "Søk etter løype", action: "loadContent('turer', 'list')" }
            ];
            break;
    }

    menuItems.forEach(item => {
        let li = document.createElement("li");
        li.innerHTML = `<a href="#" onclick="${item.action}">${item.name}</a>`;
        menuList.appendChild(li);
    });

    // Koble også til aktiv-klasse når bruker klikker sidemenyvalg
    menuList.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', function () {
            setActiveMenuItem(this);
        });
    });
}

// --- LASTE INNHOLD ---
async function loadContent(section, page, params = {}) {
    const mainContent = document.getElementById("main-content");

    try {
        const htmlResponse = await fetch(`./pages/${section}/${page}.html`);
        if (htmlResponse.ok) {
            mainContent.innerHTML = await htmlResponse.text();
        } else {
            mainContent.innerHTML = `<h2>Kunne ikke finne ${page}.html</h2>`;
        }

        const module = await import(`./pages/${section}/${page}.js`);
        if (module.render) {
            module.render(mainContent, params);
        }
    } catch (error) {
        console.error("Feil ved lasting av siden:", error);
        mainContent.innerHTML = `<h2>Kunne ikke laste inn ${page}.</h2>`;
    }
}

/*
document.addEventListener("DOMContentLoaded", () => {
    updateUserUI();      // Oppdater brukergrensesnittet basert på om bruker er logget inn
    //checkUserStatus();   // Sjekk om brukeren er admin eller har spesifikke roller
    checkAuthStatus();   // Sjekker om brukeren har en aktiv sesjon
});
function checkUserStatus() {
    console.log("checkUserStatus() ikke implementert ennå.");
}
*/
/*
document.addEventListener("DOMContentLoaded", updateUserUI);
document.addEventListener("DOMContentLoaded", () => {
    checkUserStatus(); // Sjekk om bruker er logget inn/admin
    checkAuthStatus(); // Sjekker om brukeren er innlogget ved start
});
*/
/*
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
*/
/*
function logout() {
    localStorage.removeItem("authToken"); // Fjern token
    alert("Du er nå logget ut!");
    checkAuthStatus(); // Oppdater UI etter logout
}
*/
/*
document.getElementById("logout-button").addEventListener("click", logout);

// Oppdater visning basert på om brukeren er logget inn eller ikke
function updateUserUI() {
    console.log("🛠️ Kjører updateUserUI...");

    const token = localStorage.getItem("authToken");
    const userInfo = localStorage.getItem("userInfo");

    console.log("🔍 Henter fra localStorage:");
    console.log("🔹 Token:", token);
    console.log("🔹 User Info (JSON):", userInfo);

    if (!token || !userInfo) {
        console.log("❌ Ingen gyldig brukerdata funnet.");

        const loginButton = document.getElementById("login-button");
        const userInfoElement = document.getElementById("user-info");

        if (loginButton) loginButton.classList.remove("hidden");
        else console.warn("⚠️ 'login-button' ikke funnet i HTML!");

        if (userInfoElement) userInfoElement.classList.add("hidden");
        else console.warn("⚠️ 'user-info' ikke funnet i HTML!");

        return;
    }

    let user;
    try {
        user = JSON.parse(userInfo);
        console.log("👤 Brukerdata etter parsing:", user);
    } catch (error) {
        console.error("❌ Feil ved parsing av userInfo:", error);
        return;
    }

    // Sjekk at nødvendige elementer finnes
    const loginButton = document.getElementById("login-button");
    const userInfoElement = document.getElementById("user-info");
    const logoutButton = document.getElementById("logout-button");
    const userText = document.getElementById("user-text");
    const userAvatar = document.getElementById("user-avatar");

    if (!userInfoElement || !logoutButton || !userText) {
        console.error("❌ UI-elementer mangler! Sjekk HTML-strukturen.");
        return;
    }

    // Oppdater UI for innlogget bruker
    if (loginButton) loginButton.classList.add("hidden");
    userInfoElement.classList.remove("hidden");
    logoutButton.classList.remove("hidden");

    if (user.alias && user.name) {
        userText.textContent = `Logget inn som: ${user.alias} (${user.name})`;
    } else {
        console.warn("⚠️ Mangler navn eller alias for bruker.");
    }

    if (userAvatar && user.pictureUrl) {
        userAvatar.src = user.pictureUrl;
        userAvatar.classList.remove("hidden");
    } else {
        console.warn("⚠️ Mangler profilbilde.");
    }

    console.log("✅ updateUserUI fullført!");
}
*/
/*function updateUserUI() {
    console.log("🛠️ Kjører updateUserUI...");
    const token = localStorage.getItem("authToken");
    const user = JSON.parse(localStorage.getItem("userInfo"));
    console.log("🔍 Henter fra localStorage:");
    console.log("🔹 Token:", token);
    console.log("🔹 User Info (JSON):", userInfo);

    //if (token && user) {
    if (token || user) {
        console.log("❌ Ingen gyldig brukerdata funnet.");
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
*/

/*
// Laster inn sidemenyen basert på valgt toppmeny
function loadSideMenu(section) {
    const menuList = document.getElementById("side-menu-list");
    menuList.innerHTML = ""; // Tøm tidligere innhold

    let menuItems = [];

    switch (section) {
        case "admin":
            menuItems = [
                { name: "SystemSettings", action: "loadContent('admin', 'systemsettings')" },
                { name: "Filter Løyper", action: "loadContent('admin', 'trailfilters')" },
                { name: "Rapporter", action: "loadContent('admin', 'reports')" },
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
                { name: "Meldinger", action: "loadContent('users', 'messages_all')" },
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

// Laster innhold i hovedområdet med støtte for params
async function loadContent(section, page, params = {}) {  // Legg til params her
    const mainContent = document.getElementById("main-content");

    try {
        // Last HTML først (hvis den finnes)
        const htmlResponse = await fetch(`./pages/${section}/${page}.html`);
        if (htmlResponse.ok) {
            mainContent.innerHTML = await htmlResponse.text();
        } else {
            mainContent.innerHTML = `<h2>Kunne ikke finne ${page}.html</h2>`;
        }

        // Deretter, last JavaScript-modulen og send params
        console.log(`📂 Laster inn: /pages/${section}/${page}.js med params:`, params);
        const module = await import(`./pages/${section}/${page}.js`);

        if (module.render) {
            module.render(mainContent, params);  // Send params videre til render-funksjonen
        }
    } catch (error) {
        console.error("Feil ved lasting av siden:", error);
        mainContent.innerHTML = `<h2>Kunne ikke laste inn ${page}.</h2>`;
    }
}
*/
/*
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
        console.log(`📂 Laster inn: /pages/${section}/${page}.js med params:`, params);
        const module = await import(`./pages/${section}/${page}.js`);
        module.render(document.getElementById("app"), params);
        
        //const module = await import(`./pages/${section}/${page}.js`);
        if (module.render) {
            module.render(mainContent); // Kall render-funksjonen i modulen
        }
    } catch (error) {
        console.error("Feil ved lasting av siden:", error);
        mainContent.innerHTML = `<h2>Kunne ikke laste inn ${page}.</h2>`;
    }
}



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