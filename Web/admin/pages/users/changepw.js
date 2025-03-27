export function render(mainContent) {
    const token = localStorage.getItem("authToken");
    
    if (!token) {
        alert("Du må være innlogget for å bytte passord.");
        loadContent('users', 'login'); // Send brukeren til login
        return;
    }

    mainContent.innerHTML = `
        <div class="password-container">
            <h2>Bytt Passord</h2>
            <input type="password" id="old-password" placeholder="Nåværende passord">
            <input type="password" id="new-password" placeholder="Nytt passord">
            <input type="password" id="confirm-password" placeholder="Bekreft nytt passord">
            <ul class="password-requirements">
                <li id="length-check">❌ Minst 8 tegn</li>
                <li id="uppercase-check">❌ Minst én stor bokstav</li>
                <li id="number-check">❌ Minst ett tall</li>
                <li id="special-check">❌ Minst ett spesialtegn (!@#$%^&*)</li>
            </ul>
            <button id="change-password-button" disabled>Bytt Passord</button>
        </div>
    `;

    document.getElementById("new-password").addEventListener("input", validatePassword);
    document.getElementById("change-password-button").addEventListener("click", changePassword);
}

// Validerer passord i sanntid
function validatePassword() {
    const password = document.getElementById("new-password").value;
    const lengthCheck = document.getElementById("length-check");
    const uppercaseCheck = document.getElementById("uppercase-check");
    const numberCheck = document.getElementById("number-check");
    const specialCheck = document.getElementById("special-check");
    const changeButton = document.getElementById("change-password-button");

    // Sjekker om passordet oppfyller kravene
    lengthCheck.innerHTML = password.length >= 8 ? "✅ Minst 8 tegn" : "❌ Minst 8 tegn";
    uppercaseCheck.innerHTML = /[A-Z]/.test(password) ? "✅ Minst én stor bokstav" : "❌ Minst én stor bokstav";
    numberCheck.innerHTML = /[0-9]/.test(password) ? "✅ Minst ett tall" : "❌ Minst ett tall";
    specialCheck.innerHTML = /[!@#$%^&*]/.test(password) ? "✅ Minst ett spesialtegn" : "❌ Minst ett spesialtegn";

    // Aktiverer knappen kun hvis alle krav er oppfylt
    const allValid = [lengthCheck, uppercaseCheck, numberCheck, specialCheck].every(item => item.innerHTML.startsWith("✅"));
    changeButton.disabled = !allValid;
}

// Bytter passord via API
async function changePassword() {
    const token = localStorage.getItem("authToken");
    const oldPassword = document.getElementById("old-password").value;
    const newPassword = document.getElementById("new-password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    if (newPassword !== confirmPassword) {
        alert("Passordene matcher ikke.");
        return;
    }

    const response = await fetch("https://hopla.onrender.com/users/change-password", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ oldPassword, newPassword })
    });

    if (response.ok) {
        alert("Passordet er endret!");
        loadContent('users', 'dashboard'); // Gå til dashboard etter endring
    } else {
        alert("Kunne ikke endre passord. Sjekk at det gamle passordet er riktig.");
    }
}
