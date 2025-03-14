import apiURL from '../../index.js';
export async function render(container) {
    container.innerHTML = "<h2>User Reports</h2><div id='reports-container'></div>";

    const reportsContainer = document.getElementById("reports-container");

    try {
        console.log("Fetching reports from API...");
        const response = await fetch('${apiURL}/userreports/all', {
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
            const { Id, User, EntityName, Message, CreatedAt } = report;
            const reportDiv = document.createElement('div');
            reportDiv.className = 'report';

            // Lag en container for å vise rapportdetaljer
            const reportDetails = document.createElement('div');
            reportDetails.className = 'report-details';

            const userLabel = document.createElement('strong');
            userLabel.textContent = `Report by: ${User ? User.Name : 'Unknown'}`;
            reportDetails.appendChild(userLabel);

            const entityLabel = document.createElement('p');
            entityLabel.textContent = `Entity: ${EntityName}`;
            reportDetails.appendChild(entityLabel);

            const messageLabel = document.createElement('p');
            messageLabel.textContent = `Message: ${Message}`;
            reportDetails.appendChild(messageLabel);

            const createdAtLabel = document.createElement('p');
            createdAtLabel.textContent = `Reported at: ${new Date(CreatedAt).toLocaleString()}`;
            reportDetails.appendChild(createdAtLabel);

            // Legg til rapportdetaljene i containeren
            reportDiv.appendChild(reportDetails);
            reportsContainer.appendChild(reportDiv);
        });
    } catch (error) {
        console.error('Feil ved henting av rapporter:', error);
    }
}
