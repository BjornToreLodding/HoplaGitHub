export function render(container) {
    const title = document.createElement("h2");
    title.textContent = "Hesteliste";

    const list = document.createElement("ul");

    fetch("/api/horses")
        .then(response => response.json())
        .then(horses => {
            horses.forEach(horse => {
                let li = document.createElement("li");
                li.textContent = horse.name;
                list.appendChild(li);
            });
        })
        .catch(error => {
            console.error("Feil ved henting av hester:", error);
            list.innerHTML = "<li>Kunne ikke laste hesteliste</li>";
        });

    container.appendChild(title);
    container.appendChild(list);
}
