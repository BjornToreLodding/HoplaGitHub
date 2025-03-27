export async function render(container, params) {
    console.log("🔍 Mottatte params i userprofile.js:", params);
    const userId = params?.userId; // Henter userId fra params

    if (!userId) {
        container.innerHTML = "<p>Ingen bruker valgt.</p>";
        return;
    }

    container.innerHTML = `
        <h2>Brukerprofil</h2>
        <div id='profile-info'>Laster...</div>
    `;

    try {
        const response = await fetch(`https://hopla.onrender.com/users/profile/${userId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Kunne ikke hente brukerprofil. Status: ${response.status}`);
        }

        const userProfile = await response.json();
        console.log("🔍 Brukerprofil data mottatt:", userProfile);
        document.getElementById("profile-info").innerHTML = `
        
            <img src="${userProfile.profilePictureURL || 'default-profile.png'}" alt="Profilbilde" class="profile-picture">
            <h3>${userProfile.name} (${userProfile.alias})</h3>
            <p><strong>Email:</strong> ${userProfile.email}</p>
            <p><strong>Beskrivelse:</strong> ${userProfile.description || "Ingen beskrivelse tilgjengelig"}</p>
            <p><strong>Fødselsdato:</strong> ${userProfile.dob ? new Date(userProfile.dob).toLocaleDateString() : "Ikke oppgitt"}</p>
            <p><strong>Opprettet:</strong> ${new Date(userProfile.created_at).toLocaleDateString()}</p>
        `;

    } catch (error) {
        console.error("Feil ved henting av brukerprofil:", error);
        container.innerHTML = "<p>Klarte ikke å laste brukerprofilen.</p>";
    }
}
