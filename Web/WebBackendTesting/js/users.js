function loadUserPage(page) {
    fetch(`../../pages/users/${page}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("user-content").innerHTML = html;
        })
        .catch(error => console.error("Feil ved lasting av bruker-side:", error));
}
