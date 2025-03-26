const apiUrl = window.appConfig?.API_URL || "https://localhost:7128";
console.log("API URL:", apiUrl);

export async function render(container) {
    container.innerHTML = `
        <style>
            #new-filter-form label,
            #new-filter-form input,
            #new-filter-form select,
            #new-filter-form div {
                margin-bottom: 5px;
            }
            #new-filter-form .inline {
                display: flex;
                align-items: center;
                gap: 6px;
            }
            #new-filter-form .radio-group,
            #new-filter-form .default-group {
                display: flex;
                flex-direction: column;
                gap: 4px;
                margin-left: 20px;
                margin-top: 4px;
            }
            #new-filter-form .form-section {
                border: 1px solid #ddd;
                padding: 12px;
                border-radius: 6px;
                margin-bottom: 20px;
                background: #f9f9f9;
            }
            #new-filter-form input[type="number"] {
                width: 50px;
            }
            #new-filter-form .block-label {
                display: block;
                margin-top: 12px;
                font-weight: bold;
            }
            #new-filter-form .indented-input {
                margin-left: 20px;
                margin-top: 4px;
            }
        </style>
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
            const { id, displayName, type, options, defaultValue } = filter;

            const section = document.createElement("div");
            section.className = "form-section";

            const label = document.createElement("label");
            label.textContent = displayName;
            label.htmlFor = id;
            section.appendChild(label);

            if (type === "MultiEnum") {
                const group = document.createElement("div");
                group.className = "radio-group";
                options.forEach(opt => {
                    const row = document.createElement("div");
                    row.className = "inline";

                    const checkbox = document.createElement("input");
                    checkbox.type = "checkbox";
                    checkbox.name = id;
                    checkbox.value = opt;
                    checkbox.checked = defaultValue.includes(opt);

                    const optLabel = document.createElement("label");
                    optLabel.textContent = opt;

                    row.appendChild(checkbox);
                    row.appendChild(optLabel);
                    group.appendChild(row);
                });
                section.appendChild(group);
            } else if (type === "Enum") {
                const group = document.createElement("div");
                group.className = "radio-group";
                options.forEach((opt, index) => {
                    const row = document.createElement("div");
                    row.className = "inline";

                    const radio = document.createElement("input");
                    radio.type = "radio";
                    radio.name = id;
                    radio.value = opt;
                    radio.checked = defaultValue === opt;

                    const optLabel = document.createElement("label");
                    optLabel.textContent = opt;

                    row.appendChild(radio);
                    row.appendChild(optLabel);
                    group.appendChild(row);
                });
                section.appendChild(group);
            } else if (type === "Bool") {
                const group = document.createElement("div");
                group.className = "radio-group";
                ["true", "false"].forEach((val, index) => {
                    const row = document.createElement("div");
                    row.className = "inline";

                    const input = document.createElement("input");
                    input.type = "radio";
                    input.name = id;
                    input.value = val;
                    input.checked = defaultValue === val;

                    const label = document.createElement("label");
                    label.textContent = val;

                    row.appendChild(input);
                    row.appendChild(label);
                    group.appendChild(row);
                });
                section.appendChild(group);
            } else if (type === "Int") {
                const input = document.createElement("input");
                input.type = "number";
                input.name = id;
                input.value = defaultValue || 0;
                section.appendChild(input);
            }

            filtersContainer.appendChild(section);
        });
    } catch (error) {
        console.error('Feil ved henting av filtrene:', error);
    }

    const formSection = document.createElement("div");
    formSection.className = "form-section";

    const displayLabel = document.createElement("label");
    displayLabel.className = "block-label";
    displayLabel.textContent = "Visningsnavn";
    const displayInput = document.createElement("input");
    displayInput.className = "indented-input";
    displayLabel.appendChild(document.createElement("br"));
    displayLabel.appendChild(displayInput);
    formSection.appendChild(displayLabel);

    const typeLabel = document.createElement("label");
    typeLabel.className = "block-label";
    typeLabel.style.marginTop = "20px";
    typeLabel.textContent = "Type";
    formSection.appendChild(typeLabel);

    const typeWrapper = document.createElement("div");
    typeWrapper.className = "radio-group";
    const types = ["enum", "multiEnum", "int", "bool"];
    types.forEach((t, i) => {
        const row = document.createElement("div");
        row.className = "inline";

        const input = document.createElement("input");
        input.type = "radio";
        input.name = "filterType";
        input.value = t;
        input.id = `type-${i}`;
        if (i === 0) input.checked = true;

        const label = document.createElement("label");
        label.htmlFor = input.id;
        label.textContent = t;

        row.appendChild(input);
        row.appendChild(label);
        typeWrapper.appendChild(row);
    });
    formSection.appendChild(typeWrapper);

    const alternativesLabel = document.createElement("label");
    alternativesLabel.className = "block-label";
    alternativesLabel.textContent = "Alternativer (skriv inn valg, separert med komma, f.eks. Grus, Gress, Asfalt)";
    const alternativesInput = document.createElement("input");
    alternativesInput.className = "indented-input";
    alternativesLabel.appendChild(document.createElement("br"));
    alternativesLabel.appendChild(alternativesInput);
    formSection.appendChild(alternativesLabel);

    const defaultValueLabel = document.createElement("label");
    defaultValueLabel.className = "block-label";
    defaultValueLabel.textContent = "Standardverdi(er)";
    const defaultValueContainer = document.createElement("div");
    defaultValueContainer.className = "default-group";
    defaultValueLabel.appendChild(defaultValueContainer);
    formSection.appendChild(defaultValueLabel);

    const activeWrapper = document.createElement("div");
    activeWrapper.className = "inline";
    const activeCheckbox = document.createElement("input");
    activeCheckbox.type = "checkbox";
    activeCheckbox.checked = true;
    const activeLabel = document.createElement("label");
    activeLabel.textContent = " Aktiv? ";
    activeWrapper.appendChild(activeCheckbox);
    activeWrapper.appendChild(activeLabel);
    formSection.appendChild(activeWrapper);

    const addButton = document.createElement("button");
    addButton.textContent = "Legg til filter";
    formSection.appendChild(addButton);

    newForm.appendChild(formSection);

    typeWrapper.addEventListener("change", updateVisibility);
    alternativesInput.addEventListener("input", updateVisibility);

    function updateVisibility() {
        const type = newForm.querySelector("input[name='filterType']:checked")?.value;
        defaultValueContainer.innerHTML = "";

        // Skjul/vis alternatives
        alternativesLabel.style.display = (type === "enum" || type === "multiEnum") ? "block" : "none";

        if (type === "enum" || type === "multiEnum") {
            const options = alternativesInput.value.split(",").map(o => o.trim()).filter(Boolean);
            options.forEach((opt, index) => {
                const row = document.createElement("div");
                row.className = "inline";

                const input = document.createElement("input");
                input.type = (type === "enum") ? "radio" : "checkbox";
                input.name = "defaultValue";
                input.value = opt;
                input.id = `default-${index}`;

                const label = document.createElement("label");
                label.htmlFor = input.id;
                label.textContent = opt;

                row.appendChild(input);
                row.appendChild(label);
                defaultValueContainer.appendChild(row);
            });
        } else if (type === "int") {
            const row = document.createElement("div");
            row.className = "inline";
            const input = document.createElement("input");
            input.type = "number";
            input.id = "default-int";
            input.style.width = "50px";
            row.appendChild(input);
            defaultValueContainer.appendChild(row);
        } else if (type === "bool") {
            ["true", "false"].forEach((val, index) => {
                const row = document.createElement("div");
                row.className = "inline";

                const input = document.createElement("input");
                input.type = "radio";
                input.name = "defaultValueBool";
                input.value = val;
                input.id = `bool-${index}`;

                const label = document.createElement("label");
                label.htmlFor = input.id;
                label.textContent = val;

                row.appendChild(input);
                row.appendChild(label);
                defaultValueContainer.appendChild(row);
            });
        }
    }

    addButton.addEventListener("click", async e => {
        e.preventDefault();

        const displayName = displayInput.value.trim();
        const type = newForm.querySelector("input[name='filterType']:checked")?.value;
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
            const selected = newForm.querySelector("input[name='defaultValueBool']:checked");
            defaultValue = selected?.value === "true";
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