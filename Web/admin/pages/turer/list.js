export function render(container) {
    console.log("list.js lastet inn!");

    if (typeof L === "undefined") {
        console.error("Leaflet er ikke lastet inn! Sjekk at du har inkludert det i index.html.");
        return;
    }

    container.innerHTML = `
        <h2>Turer i nærheten</h2>
        <div class="map-container">
            <div id="map"></div>
            <div id="trails-list-container">
                <button id="find-trails">Finn nærmeste turer</button>
                <ul id="trails-list" class="list-container"></ul>
            </div>
        </div>
    `;

    // Initialiser Leaflet-kartet med mindre bredde
    const map = L.map('map').setView([60.795, 10.689], 10);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    document.getElementById("find-trails").addEventListener("click", async () => {
        const center = map.getCenter();
        const latitude = center.lat.toFixed(6);
        const longitude = center.lng.toFixed(6);

        console.log(`Sender forespørsel til API med lat=${latitude}, lon=${longitude}`);

        try {
            const response = await fetch(`https://localhost:7128/trails/list?latitude=${latitude}&longitude=${longitude}`, {
                method: "GET",
                headers: { "Accept": "application/json" }
            });

            if (!response.ok) {
                throw new Error(`Feil ved henting av turer: ${response.status}`);
            }

            const trails = await response.json();
            console.log("Mottatte turer:", trails);

            displayTrails(trails);
        } catch (error) {
            console.error("Feil ved henting av turer:", error);
            alert("Kunne ikke laste turer.");
        }
    });
}

function displayTrails(trails) {
    const listContainer = document.getElementById("trails-list");
    listContainer.innerHTML = "";

    trails.forEach(trail => {
        const formattedDistance = trail.distance.toFixed(2);

        const listItem = document.createElement("li");
        listItem.className = "list-item";
        listItem.dataset.trailId = trail.id;
        listItem.innerHTML = `<strong>${trail.name}</strong> - ${formattedDistance} km`;

        listContainer.appendChild(listItem);
    });

    console.log("Turer lagt til i liste.");
}
