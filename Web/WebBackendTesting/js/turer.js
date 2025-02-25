function loadTurerPage(page) {
    fetch(`../../pages/turer/${page}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("turer-content").innerHTML = html;
        })
        .catch(error => console.error("Feil ved lasting av bruker-side:", error));
}