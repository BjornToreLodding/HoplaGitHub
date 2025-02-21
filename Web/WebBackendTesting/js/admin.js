function loadAdminPage(page) {
    fetch(`../../pages/admin/${page}`)
        .then(response => response.text())
        .then(html => {
            document.getElementById("admin-content").innerHTML = html;
        })
        .catch(error => console.error("Feil ved lasting av admin-side:", error));
}
