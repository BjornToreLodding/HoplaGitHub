const apiUrl = window.appConfig.API_URL || "https://localhost:7128"; // Fallback hvis miljøvariabelen ikke er satt
console.log("API URL:", apiUrl);


export async function render(container) {
    container.innerHTML = "<h2>User Reports</h2><div id='reports-container'></div>";

    const reportsContainer = document.getElementById("reports-container");

    try {
        console.log("Fetching reports from API...");
        const response = await fetch(`${apiUrl}/admin/userreports/reports`, {
        //const response = await fetch(`https://localhost:7128/admin/userreports/reports`, {
        method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        let reports = await response.json();
        console.log("Received reports:", reports);

        reports.forEach(report => {
            // Bruk de nye feltene fra backend-responsen
            const { name, tabell, message, created } = report;
            const reportDiv = document.createElement('div');
            reportDiv.className = 'report';

            // Lag en container for å vise rapportdetaljer
            const reportDetails = document.createElement('div');
            reportDetails.className = 'report-details';

            const userLabel = document.createElement('strong');
            // Bruk 'name' i stedet for 'User'
            userLabel.textContent = `Report by: ${name || 'Unknown'}`;
            reportDetails.appendChild(userLabel);

            const entityLabel = document.createElement('p');
            // Bruk 'tabell' i stedet for 'EntityName'
            entityLabel.textContent = `Hva gjelder det: ${tabell || 'Ukjent'}`;
            reportDetails.appendChild(entityLabel);

            const messageLabel = document.createElement('p');
            // Bruk 'message' i stedet for 'Message'
            messageLabel.textContent = `Message: ${message || 'Ingen melding'}`;
            reportDetails.appendChild(messageLabel);

            const createdAtLabel = document.createElement('p');
            // Bruk 'created' i stedet for 'CreatedAt'
            createdAtLabel.textContent = `Reported at: ${new Date(created).toLocaleString() || 'Ukjent dato'}`;
            reportDetails.appendChild(createdAtLabel);

            // Legg til rapportdetaljene i containeren
            reportDiv.appendChild(reportDetails);
            reportsContainer.appendChild(reportDiv);
        });
    } catch (error) {
        console.error('Feil ved henting av rapporter:', error);
    }
}
