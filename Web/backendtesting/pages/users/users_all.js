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
            profileImage.src = user.profilePictureUrl || "default-profile.png"; // Fallback hvis ingen URL
            profileImage.alt = `${user.name}'s profilbilde`;
            profileImage.className = "profile-picture"; // Bruk en CSS-klasse for styling

            // Opprett tekst med navn og alias
            const userText = document.createElement("span");
            userText.textContent = `${user.name} (${user.alias})`;

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

            // Legg til elementer i brukerlisten
            userItem.appendChild(profileImage);
            userItem.appendChild(userText);
            userItem.appendChild(loginButton);
            userList.appendChild(userItem);
        });
    } catch (error) {
        console.error("Feil ved henting av brukere:", error);
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