const apiUrl = window.appConfig.API_URL || "https://localhost:7128";

export async function renderNewReports(container) {
    try {
        const response = await fetch(`${apiUrl}/admin/userreports/reports?status=new`);
        if (!response.ok) throw new Error(`Status: ${response.status}`);

        const reports = await response.json();

        // Sorter etter CreatedAt nyeste først
        reports.sort((a, b) => new Date(b.created) - new Date(a.created));

        container.innerHTML = reports.map(r => `
            <div class="report" id="report-${r.id}">
                <strong>${r.name || 'Ukjent'}</strong>
                <p>Hva gjelder det: ${r.tabell || 'Ukjent'}</p>
                <p>Message: ${r.message || 'Ingen melding'}</p>
                <p>Reported: ${new Date(r.created).toLocaleString()}</p>

                <label>Status:
                    <select class="status-dropdown">
                        <option value="new" selected>New</option>
                        <option value="inprogress">In Progress</option>
                        <option value="resolved">Resolved</option>
                        <option value="closed">Closed</option>
                    </select>
                </label>
                <textarea placeholder="Feedback..."></textarea>
                <button onclick="saveStatusChange(${r.id})">Lagre</button>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = `<p>Feil ved lasting: ${err.message}</p>`;
    }
}
