const apiUrl = window.appConfig.API_URL || "https://localhost:7128";
// ✅ Bruk ESM-bygget som støtter import Chart from ...
import Chart from 'https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.esm.js';

//const apiUrl = "https://localhost:7128";

export async function render(container) {
    // Overskrift og graf-område
    container.innerHTML = `
        <h2 style="text-align: center;">Statistikk: Nye brukere per måned</h2>
        <div id="chart-area" style="width: 100%; max-width: 1200px; margin: auto; padding: 2em;">
            <canvas id="userChart"></canvas>
        </div>`;

    try {
        const response = await fetch(`${apiUrl}/admin/stats/newusersbymonth`);
        //const response = await fetch(`https://localhost:7128/admin/stats/newusersbymonth`);
        if (!response.ok) throw new Error(`Status: ${response.status}`);

        const data = await response.json();

        const labels = data.map(d => `${d.month}.${d.year}`);
        const counts = data.map(d => d.count);

        const canvas = document.getElementById("userChart");

        // 📏 Sett intern tegneflate-størrelse
        canvas.setAttribute("width", "1200");
        canvas.setAttribute("height", "600");

        // Bygg graf
        new Chart(canvas, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Nye brukere per måned',
                    data: counts,
                    backgroundColor: '#745e4d',
                    borderRadius: 5
                }]
            },
            options: {
                responsive: false,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                },
                plugins: {
                    legend: { position: 'top' },
                    tooltip: { mode: 'index', intersect: false }
                }
            }
        });

    } catch (err) {
        container.innerHTML += `<p style="color: red;">Feil: ${err.message}</p>`;
    }
}



