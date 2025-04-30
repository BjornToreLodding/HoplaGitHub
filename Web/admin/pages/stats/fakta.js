const apiUrl = window.appConfig?.API_URL || "https://localhost:7128";

export async function render(container) {
    container.innerHTML = `<h2>Systemfakta</h2><p>Henter data...</p>`;

    try {
        const [hello, status, database] = await Promise.all([
            fetchData('/div/helloworld'),
            fetchData('/div/status'),
            fetchData('/div/database')
        ]);

        container.innerHTML = `
            <div class="fakta-box">
                <h3>/div/helloworld</h3>
                <pre>${JSON.stringify(hello, null, 2)}</pre>
            </div>
            <div class="fakta-box">
                <h3>/div/status</h3>
                <ul>
                    <li><strong>Oppetid:</strong> ${status.uptime}</li>
                    <li><strong>Antall forespørsler:</strong> ${status.requestCount}</li>
                    <li><strong>Antall feil:</strong> ${status.errorCount}</li>
                </ul>
            </div>
            <div class="fakta-box">
                <h3>/div/database</h3>
                <p><strong>Status:</strong> ${database.status}</p>
                <p>${database.message}</p>
            </div>
        `;
    } catch (err) {
        console.error("Feil under henting av fakta:", err);
        container.innerHTML = `<p style="color: red;">❌ Kunne ikke hente systemdata: ${err.message}</p>`;
    }
}

async function fetchData(endpoint) {
    const response = await fetch(`${apiUrl}${endpoint}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`Status ${response.status} fra ${endpoint}`);
    }

    return await response.json();
}
