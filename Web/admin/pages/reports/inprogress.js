const apiUrl = window.appConfig.API_URL || "https://localhost:7128";
//const apiUrl = "https://localhost:7128";
export async function render(container) {
    try {
        const response = await fetch(`${apiUrl}/admin/userreports/reports?status=inProgress`, {
        //const response = await fetch(`https://localhost:7128/admin/userreports/reports?status=inProgress`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Status: ${response.status}`);

        const reports = await response.json();

        // Sorter etter CreatedAt nyeste først
        reports.sort((a, b) => new Date(b.created) - new Date(a.created));

        container.innerHTML = reports.map(r => `
            <div class="report" data-id="${r.id}" style="width: 60%; margin: 2em auto; padding: 1em; border: 1px solid #999; border-radius: 8px;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <strong>${r.name || 'Ukjent'}</strong>
                    <span><strong>Reported: </strong>${new Date(r.created).toLocaleString()}</span>
                </div>
                <div style="display: flex; justify-content: space-between; margin-bottom: 0.5em;">
                    <div><strong>Message:</strong> ${r.message || 'Ingen melding'}</div>
                    <div><strong>Gjelder:</strong> ${r.tabell || 'Ukjent'}</div>
                </div>            
                <textarea placeholder="Feedback..." rows="4" style="width: 100%; resize: vertical; margin-bottom: 0.5em;">${r.feedback || ''}</textarea>

                <button class="button-brown save-btn" data-status="inProgress">Lagre</button>
                <button class="button-brown save-btn" data-status="Resolved" style="margin-left: 0.5em;">Lagre og marker Ferdig Behandlet</button>
            </div>
        `).join('');

        // Koble på knappene
        container.querySelectorAll('.save-btn').forEach(button => {
            button.addEventListener('click', async () => {
                const reportDiv = button.closest('.report');
                const reportId = reportDiv.dataset.id;
                const newStatus = button.dataset.status;
                const feedback = reportDiv.querySelector("textarea").value;

                try {
                    //const response = await fetch(`https://localhost:7128/admin/userreports/${reportId}`, {
                    const response = await fetch(`${apiUrl}/admin/userreports/${reportId}`, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            status: newStatus,
                            feedback: feedback
                        })
                    });

                    if (!response.ok) {
                        throw new Error(`Feil ved oppdatering. Status: ${response.status}`);
                    }

                    alert(`✅ Lagret og satt status til "${newStatus}"`);
                    render(container); // reloader listen uten sideoppfriskning
                } catch (err) {
                    console.error("Feil ved lagring:", err);
                    alert("❌ Kunne ikke lagre endringer.");
                }
            });
        });

    } catch (err) {
        container.innerHTML = `<p>Feil ved lasting: ${err.message}</p>`;
    }
}