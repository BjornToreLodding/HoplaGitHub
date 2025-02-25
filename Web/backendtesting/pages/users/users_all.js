export async function render(container) {
    container.innerHTML = "<h2>Alle brukere</h2><ul id='user-list'></ul>";

    const userList = document.getElementById("user-list");

    try {
        console.log("Fetching users from API...");
        const response = await fetch("https://localhost:7128/users/all", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        let users = await response.json();
        console.log("Received users:", users);

        users.forEach(user => {
            const userItem = document.createElement("li");
            userItem.className = "user-item";

            // Opprett bilde-element
            const profileImage = document.createElement("img");
            profileImage.src = user.profilePictureUrl ? user.profilePictureUrl : "default-profile.png";
            profileImage.alt = `${user.name}'s profilbilde`;
            profileImage.className = "profile-picture";

            // Opprett tekst med navn og alias
            const userText = document.createElement("span");
            userText.textContent = `${user.name} (${user.alias})`;

            // "Logg inn som"-knapp (skjult til brukeren trykker)
            const loginButton = document.createElement("button");
            loginButton.textContent = "Logg inn som";
            loginButton.className = "login-btn hidden";

            // "Vis profilside"-knapp (skjult til brukeren trykker)
            const profileButton = document.createElement("button");
            profileButton.textContent = "Vis profilside";
            profileButton.className = "profile-btn hidden";

            // Hover-effekt: Marker brukeren
            userItem.addEventListener("mouseover", () => userItem.classList.add("hover"));
            userItem.addEventListener("mouseout", () => userItem.classList.remove("hover"));

            // Klikk på bruker: Vis/skjul knapper
            userItem.addEventListener("click", () => {
                const isVisible = !loginButton.classList.contains("hidden");

                // Skjul alle andre knapper først
                document.querySelectorAll(".login-btn, .profile-btn").forEach(btn => btn.classList.add("hidden"));

                if (!isVisible) {
                    loginButton.classList.remove("hidden");
                    profileButton.classList.remove("hidden");
                }
            });

            // Logg inn-knappens funksjonalitet (tilbake til original versjon)
            // Logg inn-knappens funksjonalitet
            loginButton.addEventListener("click", async () => {
                try {
                    console.log(`➡️  Prøver å logge inn som: ${user.name} (ID: ${user.id})`);

                    const loginResponse = await fetch("https://localhost:7128/users/login/test", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ id: user.id })
                    });

                    if (!loginResponse.ok) {
                        throw new Error(`🚨 Login feilet! Status: ${loginResponse.status}`);
                    }

                    const loginData = await loginResponse.json();
                    console.log("✅ Login-data mottatt:", loginData);

                    if (!loginData.token || !loginData.name || !loginData.alias) {
                        throw new Error("🚨 Feil: Manglende data i responsen!");
                    }

                    // Lagrer brukerinfo og token i localStorage
                    localStorage.setItem("authToken", loginData.token);
                    localStorage.setItem("userInfo", JSON.stringify({
                        id: loginData.userId,
                        name: loginData.name,
                        alias: loginData.alias,
                        profilePictureURL: loginData.profilePictureURL
                    }));

                    console.log("🛠️  Oppdatert localStorage:", localStorage.getItem("userInfo"));

                    // Oppdater UI etter innlogging
                    updateUserUI();

                    // Naviger til dashboardet etter kort forsinkelse
                    setTimeout(() => {
                        loadContent('users', 'dashboard');
                    }, 500);

                } catch (error) {
                    console.error("❌ Feil ved innlogging:", error.message || error);
                    alert("Kunne ikke logge inn. Sjekk konsollen for detaljer.");
                }
            });

            // Vis profilside-knappens funksjonalitet (dette er det eneste nye!)
            profileButton.addEventListener("click", () => {
                const userId = user.id || user.userId;  // Håndter evt. feilaktig property-navn
                console.log(`➡️ Åpner profilside for bruker: ${user.name} (ID: ${userId})`);
                loadContent('users', 'profile', { userId });
            });


            // Legg til elementer i brukerlisten
            userItem.appendChild(profileImage);
            userItem.appendChild(userText);
            userItem.appendChild(loginButton);
            userItem.appendChild(profileButton);
            userList.appendChild(userItem);
        });
    } catch (error) {
        console.error("❌ Feil ved henting av brukere:", error);
        container.innerHTML += "<p>Kunne ikke laste brukere.</p>";
    }
}


/*export async function render(container) {
    container.innerHTML = "<h2>Alle brukere</h2><ul id='user-list'></ul>";

    const userList = document.getElementById("user-list");

    try {
        console.log("Fetching users from API...");
        const response = await fetch("https://localhost:7128/users/all", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        let users = await response.json();
        console.log("Received users:", users);

        users.forEach(user => {
            const userItem = document.createElement("li");
            userItem.className = "user-item";
            userItem.textContent = `${user.name} (${user.alias})`;
            userItem.dataset.userId = user.id;

            // "Logg inn som"-knapp (skjult til brukeren trykker)
            const loginButton = document.createElement("button");
            loginButton.textContent = "Logg inn som";
            loginButton.className = "login-btn hidden";

            // Hover-effekt: Marker brukeren
            userItem.addEventListener("mouseover", () => {
                userItem.classList.add("hover");
            });
            userItem.addEventListener("mouseout", () => {
                userItem.classList.remove("hover");
            });

            // Klikk på bruker: Vis login-knapp
            userItem.addEventListener("click", () => {
                document.querySelectorAll(".login-btn").forEach(btn => btn.classList.add("hidden"));
                loginButton.classList.remove("hidden");
            });

            // Logg inn-knappens funksjonalitet
            loginButton.addEventListener("click", async () => {
                try {
                    console.log(`Logging in as ${user.name}...`);

                    const loginResponse = await fetch("https://localhost:7128/users/login/test", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({ id: user.id })  // Sender bruker-ID i body
                    });

                    if (!loginResponse.ok) {
                        throw new Error(`Login failed! Status: ${loginResponse.status}`);
                    }

                    const loginData = await loginResponse.json();
                    console.log("Login successful! Token received:", loginData.token);

                    // Lagrer token i localStorage
                    localStorage.setItem("authToken", loginData.token);

                    alert(`Logget inn som ${user.name}`);
                } catch (error) {
                    console.error("Error logging in:", error);
                    alert("Kunne ikke logge inn.");
                }
            });

            userItem.appendChild(loginButton);
            userList.appendChild(userItem);
        });
    } catch (error) {
        console.error("Feil ved henting av brukere:", error);
        container.innerHTML += "<p>Kunne ikke laste brukere.</p>";
    }
}
*/