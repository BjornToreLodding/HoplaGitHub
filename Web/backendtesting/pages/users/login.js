export function render(mainContent) {
    mainContent.innerHTML = `
        <div class="login-container">
            <h2>Logg inn</h2>
            <input type="email" id="email" placeholder="E-post">
            <input type="password" id="password" placeholder="Passord">
            <button id="login-button">Logg inn</button>
        </div>
    `;

    // Finn login-knappen og legg til event listener
    document.getElementById("login-button").addEventListener("click", login);
}

async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("https://localhost:7128/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const data = await response.json();
        localStorage.setItem("authToken", data.token);
        localStorage.setItem("userInfo", JSON.stringify({
            id: data.userId,
            name: data.name,
            alias: data.alias,
            profilePictureURL: data.profilePictureURL
        }));

        updateUserUI();
        loadContent('users', 'dashboard');
    } else {
        alert("Feil e-post eller passord.");
    }
}

// Eksporter login for debugging (valgfritt)
export { login };



/*
function renderLoginPage() {
    document.getElementById("main-content").innerHTML = `
        <div class="login-container">
            <h2>Logg inn</h2>
            <input type="email" id="email" placeholder="E-post">
            <input type="password" id="password" placeholder="Passord">
            <button onclick="login()">Logg inn</button>
        </div>
    `;
}

async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("https://din-backend-url/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const data = await response.json();

        // Lagre token og brukerinfo
        localStorage.setItem("authToken", data.token);
        localStorage.setItem("userInfo", JSON.stringify({
            name: data.name,
            alias: data.alias,
            profilePictureURL: data.profilePictureURL
        }));

        // Oppdater UI og send bruker til users-dashboard
        updateUserUI();
        loadContent('users', 'dashboard');
    } else {
        alert("Feil e-post eller passord.");
    }
}

// Kjør når users/login lastes inn
renderLoginPage();
*/

/*async function login() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("https://din-backend-url/api/users/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const data = await response.json();

        // Lagre token og brukerinfo
        localStorage.setItem("authToken", data.token);
        localStorage.setItem("userInfo", JSON.stringify({
            name: data.name,
            alias: data.alias,
            profilePictureURL: data.profilePictureURL
        }));

        // Oppdater UI og send bruker tilbake til Users-seksjonen
        updateUserUI();
        loadContent('users', 'dashboard');
    } else {
        alert("Feil e-post eller passord.");
    }
}
*/