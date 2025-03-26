const apiUrl = window.appConfig?.API_URL || "https://localhost:7128";
console.log("API URL:", apiUrl);

export async function render(container) {
    container.innerHTML = "<h2>Trail Filters</h2><div id='trail-filters'></div>";
    const filtersContainer = document.getElementById("trail-filters");

    try {
        const response = await fetch(`${apiUrl}/admin/all`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const filters = await response.json();
        console.log("Received filters:", filters);

        filters.forEach(filter => {
            const { id, name, displayName, type, options, defaultValue } = filter;

            const wrapper = document.createElement("div");
            wrapper.className = "filter";

            const label = document.createElement("label");
            label.textContent = displayName;
            label.htmlFor = id;
            wrapper.appendChild(label);

            if (type === "MultiEnum") {
                const group = document.createElement("div");
                group.className = "checkbox-group";

                options.forEach(opt => {
                    const checkbox = document.createElement("input");
                    checkbox.type = "checkbox";
                    checkbox.name = id;
                    checkbox.value = opt;
                    checkbox.checked = defaultValue.includes(opt);

                    const optLabel = document.createElement("label");
                    optLabel.appendChild(checkbox);
                    optLabel.appendChild(document.createTextNode(" " + opt));

                    group.appendChild(optLabel);
                });
                wrapper.appendChild(group);
            }

            else if (type === "Enum") {
                const select = document.createElement("select");
                select.name = id;

                options.forEach(opt => {
                    const option = document.createElement("option");
                    option.value = opt;
                    option.textContent = opt;
                    option.selected = defaultValue === opt;
                    select.appendChild(option);
                });

                wrapper.appendChild(select);
            }

            else if (type === "Bool") {
                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.name = id;
                checkbox.checked = defaultValue === "true";

                wrapper.appendChild(checkbox);
            }

            else if (type === "Int") {
                const input = document.createElement("input");
                input.type = "number";
                input.name = id;
                input.value = defaultValue || 0;

                wrapper.appendChild(input);
            }

            filtersContainer.appendChild(wrapper);
        });

    } catch (error) {
        console.error('Feil ved henting av filtrene:', error);
    }
}


/*const apiUrl = window.appConfig?.API_URL || "https://localhost:7128";
console.log("API URL:", apiUrl);

export async function render(container) {
    container.innerHTML = "<h2>Trail Filters</h2><div id='trail-filters'></div>";
    const filtersContainer = document.getElementById("trail-filters");

    try {
        const response = await fetch(`${apiUrl}/admin/filters/all`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const filters = await response.json();
        console.log("Received filters:", filters);

        filters.forEach(filter => {
            const { name, displayName, type, options, defaultValue } = filter;

            const wrapper = document.createElement("div");
            wrapper.className = "filter";

            const label = document.createElement("label");
            label.textContent = displayName;
            label.htmlFor = name;
            wrapper.appendChild(label);

            if (type === "MultiEnum") {
                const group = document.createElement("div");
                group.className = "checkbox-group";

                options.forEach(opt => {
                    const checkbox = document.createElement("input");
                    checkbox.type = "checkbox";
                    checkbox.name = name;
                    checkbox.value = opt;
                    checkbox.checked = defaultValue.includes(opt);

                    const optLabel = document.createElement("label");
                    optLabel.appendChild(checkbox);
                    optLabel.appendChild(document.createTextNode(" " + opt));

                    group.appendChild(optLabel);
                });
                wrapper.appendChild(group);
            }

            if (type === "Enum") {
                const select = document.createElement("select");
                select.name = name;

                options.forEach(opt => {
                    const option = document.createElement("option");
                    option.value = opt;
                    option.textContent = opt;
                    option.selected = defaultValue === opt;
                    select.appendChild(option);
                });

                wrapper.appendChild(select);
            }

            filtersContainer.appendChild(wrapper);
        });

    } catch (error) {
        console.error('Feil ved henting av filtrene:', error);
    }
}
*/