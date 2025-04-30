const apiUrl = window.appConfig?.API_URL || "https://localhost:7128";
console.log("API URL:", apiUrl);

export async function render(container) {
    container.innerHTML = `
        
        <h2>Trail Filters</h2>
        <h3>Standard Verdier</h3>
        <div id='trail-filters'></div>
        <hr>
        <h3>Legg til nytt filter</h3>
        <div id="new-filter-form"></div>
        <pre id="new-filter-output"></pre>
    `;

    const filtersContainer = document.getElementById("trail-filters");
    const newForm = document.getElementById("new-filter-form");
    const output = document.getElementById("new-filter-output");

    let filterList = [];
    let customCounter = 1;
    //
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
            section.className = "existing-filter";

            const label = document.createElement("label");
            label.textContent = displayName;
            label.htmlFor = id;
            section.appendChild(label);

            if ((type === "Enum" || type === "MultiEnum") && options?.length) {
                const optionGroup = document.createElement("div");
                optionGroup.className = "options-inline";

                options.forEach(opt => {
                    const wrapper = document.createElement("label");
                    const input = document.createElement("input");
                    input.type = type === "Enum" ? "radio" : "checkbox";
                    input.disabled = true;
                    input.checked = Array.isArray(defaultValue)
                        ? defaultValue.includes(opt)
                        : defaultValue === opt;
                    wrapper.appendChild(input);
                    wrapper.appendChild(document.createTextNode(" " + opt));
                    optionGroup.appendChild(wrapper);
                });
                section.appendChild(optionGroup);
            }

            if (type === "Bool") {
                const boolGroup = document.createElement("div");
                boolGroup.className = "options-inline";

                ["true", "false"].forEach(val => {
                    const wrapper = document.createElement("label");
                    const input = document.createElement("input");
                    input.type = "radio";
                    input.disabled = true;
                    input.checked = String(defaultValue) === val;
                    wrapper.appendChild(input);
                    wrapper.appendChild(document.createTextNode(" " + val));
                    boolGroup.appendChild(wrapper);
                });
                section.appendChild(boolGroup);
            }

            if (type === "Int") {
                const input = document.createElement("input");
                input.type = "number";
                input.name = id;
                input.value = defaultValue || 0;
                input.style.width = "50px";
                input.disabled = true;
                section.appendChild(input);
            }

            filtersContainer.appendChild(section);
        });
    } catch (error) {
        console.error('Feil ved henting av filtrene:', error);
    }

    // resten av koden for oppretting av filter (ikke endret)...

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
    addButton.className = "button-brown";
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