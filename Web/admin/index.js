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
    const topMenuList = document.getElementById("top-menu-list");

    if (token) {
        loginScreen.classList.add("hidden");
        appContent.classList.remove("hidden");
//<li><a href="#" data-section="trails">Trails</a></li>
        topMenuList.innerHTML = `
            <li><a href="#" data-section="tools">Tools</a></li>
            <li><a href="#" data-section="stats">Stats</a></li>
            <li><a href="#" data-section="reports">Reports</a></li>
            
            <li style="color: white; font-family: 'GeorgiaProBlack', serif; font-size: 28px; padding: 0 150px;">Hopla Adminportal</li>

            <li id="user-info" style="display: flex; align-items: center; gap: 10px;">
                <img id="user-avatar" src="" alt="Profilbilde" class="profile-pic hidden">
                <div id="user-text"></div>
                <button id="logout-button" class="button-brown hidden">Logg ut</button>
            </li>
        `;
    } else {
        loginScreen.classList.remove("hidden");
        appContent.classList.add("hidden");
        topMenuList.innerHTML = `
        <center><li style="color: white; font-family: 'GeorgiaProBlack', serif; font-size: 28px; padding: 0 150px;">Hopla Adminportal</li></center>
        `
    }

    const logoutButton = document.getElementById("logout-button");
    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
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
    if (userText) userText.innerHTML = `<div style="display: flex; flex-direction: column; line-height: 1.2;">
    <span>Logget inn som:</span>
    <center>${user.alias}</center>
    <small>(${user.name})</small>
</div>`;
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
                { name: "Nye brukere", action: "loadContent('stats', 'newusers')" },
                { name: "Turer", action: "loadContent('stats', 'newusers-fixed')" },
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
