// index.js (komplett og oppdatert)
document.addEventListener("DOMContentLoaded", () => {
    updateLoginState();
    updateUserUI();
    checkAuthStatus();

    const loginButton = document.getElementById("login-submit");
    if (loginButton) {
        loginButton.addEventListener("click", () => {
            const email = document.getElementById("login-email").value;
            const password = document.getElementById("login-password").value;

            if (email && password) {
                localStorage.setItem("authToken", "dummy-token");
                localStorage.setItem("userInfo", JSON.stringify({
                    alias: "Admin",
                    name: email.split('@')[0],
                    pictureUrl: "https://hopla.imgix.net/main.jpg?w=64&h=64&fit=crop"
                }));

                updateLoginState();
                updateUserUI();
            } else {
                alert("Vennligst fyll ut e-post og passord.");
            }
        });
    }
});

function updateLoginState() {
    const token = localStorage.getItem("authToken");
    const loginScreen = document.getElementById("login-screen");
    const appContent = document.getElementById("app-content");
    const topMenu = document.getElementById("top-menu");

    const tools = document.getElementById("menu-tools");
    const stats = document.getElementById("menu-stats");
    const reports = document.getElementById("menu-reports");
    const userInfo = document.getElementById("user-info");
    const logoutButton = document.getElementById("logout-button");

    if (token) {
        loginScreen.classList.add("hidden");
        appContent.classList.remove("hidden");
        topMenu.classList.remove("hidden");

        // Vis menyvalg og bruker
        tools.classList.remove("hidden");
        stats.classList.remove("hidden");
        reports.classList.remove("hidden");
        userInfo.classList.remove("hidden");

        // Koble logg ut-knappen
        logoutButton.classList.remove("hidden");
        logoutButton.removeEventListener("click", logout); // unngå dobbel
        logoutButton.addEventListener("click", logout);
    } else {
        loginScreen.classList.remove("hidden");
        appContent.classList.add("hidden");
        topMenu.classList.remove("hidden");

        // Skjul alle menyvalg og bruker
        tools.classList.add("hidden");
        stats.classList.add("hidden");
        reports.classList.add("hidden");
        userInfo.classList.add("hidden");
        logoutButton.classList.add("hidden");
    }
}




function updateUserUI() {
    const token = localStorage.getItem("authToken");
    const userInfo = localStorage.getItem("userInfo");
    const userInfoElement = document.getElementById("user-info");
    const logoutButton = document.getElementById("logout-button");
    const userText = document.getElementById("user-text");
    const userAvatar = document.getElementById("user-avatar");

    if (!token || !userInfo) {
        if (userInfoElement) userInfoElement.classList.add("hidden");
        return;
    }

    const user = JSON.parse(userInfo);

    if (userInfoElement) userInfoElement.classList.remove("hidden");
    if (logoutButton) logoutButton.classList.remove("hidden");
    if (userText) {
        userText.innerHTML = `
            <div style="display: flex; flex-direction: column; line-height: 1.2;">
                <span>Logget inn som:</span>
                <center>${user.alias || "Ukjent"}</center>
                <center><small>(${user.name || "bruker"})</small></center>
            </div>
        `;
    }
    if (userAvatar) {
        userAvatar.src = user.pictureUrl || "https://hopla.imgix.net/main.jpg?w=64&h=64&fit=crop";
        userAvatar.classList.remove("hidden");
    }
}


function checkAuthStatus() {
    const token = localStorage.getItem("authToken");
    const logoutButton = document.getElementById("logout-button");
    if (logoutButton) {
        logoutButton.classList.toggle("hidden", !token);
    }
}

function logout() {
    localStorage.removeItem("authToken");
    localStorage.removeItem("userInfo");
    updateLoginState();
    updateUserUI();
}

function setActiveMenuItem(clickedElement) {
    document.querySelectorAll('nav ul li a').forEach(link => link.classList.remove('active'));
    document.querySelectorAll('#side-menu ul li a').forEach(link => link.classList.remove('active'));
    if (clickedElement) clickedElement.classList.add('active');
}

document.addEventListener('click', function (event) {
    if (event.target.matches('nav a[data-section]')) {
        event.preventDefault();
        const section = event.target.getAttribute('data-section');
        loadSideMenu(section);
        setActiveMenuItem(event.target);
    }
});

function loadSideMenu(section) {
    const menuList = document.getElementById("side-menu-list");
    menuList.innerHTML = "";

    let menuItems = [];
    switch (section) {
        case "tools":
            menuItems = [
                { name: "SystemSettings", action: "loadContent('admin', 'systemsettings')" },
                { name: "Filter Løyper", action: "loadContent('admin', 'trailfilters')" },
            ]; break;
        case "reports":
            menuItems = [
                //{ name: "Alle", action: "loadContent('reports', 'reports')" },
                //{ name: "Alle2", action: "loadContent('reports', 'all')" },
                { name: "Ubehandlet", action: "loadContent('reports', 'new-reports')" },
                { name: "Under Behandlig", action: "loadContent('reports', 'inprogress')" },
                { name: "Ferdig Behandlig", action: "loadContent('reports', 'resolved')" },
                //{ name: "Lukket", action: "loadContent('admin', 'closed')" }
            ]; break;
        case "stats":
            menuItems = [
                { name: "Fakta", action: "loadContent('stats', 'fakta')" },
                { name: "Nye brukere", action: "loadContent('stats', 'newusers-fixed')" },
                { name: "Turer", action: "loadContent('stats', 'newhikes')" },
                { name: "YNI", action: "loadContent('stats', 'yni')" }
            ]; break;
        case "trails":
            menuItems = [
                { name: "Vis nærmeste løyper", action: "loadContent('turer', 'list')" },
                { name: "Søk etter løype", action: "loadContent('turer', 'list')" }
            ]; break;
    }

    menuItems.forEach(item => {
        const li = document.createElement("li");
        li.innerHTML = `<a href="#" onclick="${item.action}">${item.name}</a>`;
        menuList.appendChild(li);
    });

    menuList.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', function () {
            setActiveMenuItem(this);
        });
    });
}

async function loadContent(section, page, params = {}) {
    const mainContent = document.getElementById("main-content");
    let htmlLoaded = false;
    try {
        const htmlResponse = await fetch(`./pages/${section}/${page}.html`);
        if (htmlResponse.ok) {
            mainContent.innerHTML = await htmlResponse.text();
            htmlLoaded = true;
        }
    } catch (e) {
        console.warn("Ingen HTML-fil funnet:", e);
    }
    
    try {
        console.log("Prøver å importere:", `./pages/${section}/${page}.js`);
        const module = await import(`./pages/${section}/${page}.js`);
        if (module.render) {
            module.render(mainContent, params);
        }
    } catch (error) {
        console.error("Feil ved lasting av JS-modul:", error);
        if (!htmlLoaded) {
            mainContent.innerHTML = `<h2>Kunne ikke finne ${page}.js</h2>`;
        }
    }
    
}
