const apiUrl = window.appConfig?.API_URL || "https://localhost:7128";
console.log("API URL:", apiUrl);

export async function render(container) {
    container.innerHTML = `
        <h2>Trail Filters</h2>
        <div id='trail-filters'></div>
        <hr>
        <h2>Legg til nytt filter</h2>
        <div id="new-filter-form"></div>
        <pre id="new-filter-output"></pre>
    `;

    const filtersContainer = document.getElementById("trail-filters");
    const newForm = document.getElementById("new-filter-form");
    const output = document.getElementById("new-filter-output");

    let filterList = [];
    let customCounter = 1;

    try {
        const response = await fetch(`${apiUrl}/trailfilters/all`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

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
            } else if (type === "Enum") {
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
            } else if (type === "Bool") {
                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.name = id;
                checkbox.checked = defaultValue === "true";
                wrapper.appendChild(checkbox);
            } else if (type === "Int") {
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

    const displayInput = document.createElement("input");
    displayInput.placeholder = "Visningsnavn";
    newForm.appendChild(displayInput);

    const typeSelect = document.createElement("select");
    ["enum", "multiEnum", "int", "bool"].forEach(t => {
        const opt = document.createElement("option");
        opt.value = t;
        opt.textContent = t;
        typeSelect.appendChild(opt);
    });
    newForm.appendChild(typeSelect);

    const alternativesInput = document.createElement("input");
    alternativesInput.placeholder = "Alternativer (komma-separert)";
    newForm.appendChild(alternativesInput);

    const defaultValueContainer = document.createElement("div");
    newForm.appendChild(defaultValueContainer);

    const activeCheckbox = document.createElement("input");
    activeCheckbox.type = "checkbox";
    activeCheckbox.checked = true;
    newForm.appendChild(document.createTextNode(" Aktiv? "));
    newForm.appendChild(activeCheckbox);

    const addButton = document.createElement("button");
    addButton.textContent = "Legg til filter";
    newForm.appendChild(addButton);

    typeSelect.addEventListener("change", updateVisibility);
    alternativesInput.addEventListener("input", updateVisibility);

    function updateVisibility() {
        const type = typeSelect.value;
        defaultValueContainer.innerHTML = "";

        if (type === "enum" || type === "multiEnum") {
            const options = alternativesInput.value.split(",").map(o => o.trim()).filter(Boolean);

            options.forEach((opt, index) => {
                const input = document.createElement("input");
                input.type = (type === "enum") ? "radio" : "checkbox";
                input.name = "defaultValue";
                input.value = opt;
                input.id = `default-${index}`;

                const label = document.createElement("label");
                label.htmlFor = input.id;
                label.textContent = opt;

                defaultValueContainer.appendChild(input);
                defaultValueContainer.appendChild(label);
                defaultValueContainer.appendChild(document.createElement("br"));
            });
        } else if (type === "int") {
            const input = document.createElement("input");
            input.type = "number";
            input.id = "default-int";
            defaultValueContainer.appendChild(input);
        } else if (type === "bool") {
            const input = document.createElement("input");
            input.type = "checkbox";
            input.id = "default-bool";
            defaultValueContainer.appendChild(document.createTextNode(" Standard: "));
            defaultValueContainer.appendChild(input);
        }
    }

    addButton.addEventListener("click", async e => {
        e.preventDefault();

        const displayName = displayInput.value.trim();
        const type = typeSelect.value;
        const isActive = activeCheckbox.checked;

        const alternatives = (type === "enum" || type === "multiEnum")
            ? alternativesInput.value.split(",").map(v => v.trim()).filter(Boolean)
            : [];

        let defaultValue = null;

        if (type === "enum") {
            const selected = newForm.querySelector("input[name='defaultValue']:checked");
            defaultValue = selected?.value || null;
        } else if (type === "multiEnum") {
            const selected = Array.from(newForm.querySelectorAll("input[name='defaultValue']:checked"));
            defaultValue = selected.map(i => i.value);
        } else if (type === "int") {
            const val = newForm.querySelector("#default-int").value;
            defaultValue = val ? parseInt(val) : null;
        } else if (type === "bool") {
            const val = newForm.querySelector("#default-bool").checked;
            defaultValue = val;
        }

        const payload = {
            DisplayName: displayName,
            Type: type,
            Alternatives: alternatives,
            DefaultValue: defaultValue,
            IsActive: isActive
        };

        try {
            const response = await fetch(`${apiUrl}/trailfilters/admin/trailfilters/definitions/create`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!response.ok) throw new Error("Kunne ikke lagre filter!");

            const result = await response.json();
            alert("Filter lagret!");
            console.log("Lagret filter:", result);

            filterList.push(result);
            output.textContent = JSON.stringify(filterList, null, 2);
        } catch (err) {
            console.error("Feil ved lagring:", err);
            alert("Feil ved lagring av filter.");
        }
    });

    updateVisibility();
}
