const apiUrl = window.appConfig.API_URL || "https://localhost:7128";

function showToast(message) {
    let toast = document.createElement('div');
    toast.textContent = message;
    toast.style.position = 'fixed';
    toast.style.top = '20px';
    toast.style.right = '20px';
    toast.style.backgroundColor = '#4CAF50';
    toast.style.color = 'white';
    toast.style.padding = '10px 20px';
    toast.style.borderRadius = '8px';
    toast.style.boxShadow = '0px 0px 10px rgba(0,0,0,0.2)';
    toast.style.zIndex = 1000;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

export async function render(container) {
    container.innerHTML = "<h2>System Settings</h2><div id='settings-container'></div>";

    const settingsContainer = document.getElementById("settings-container");

    const refreshButton = document.createElement('button');
    refreshButton.textContent = '🔄 Refresh Settings Cache';
    refreshButton.className = 'button';
    refreshButton.style.marginBottom = '20px';

    refreshButton.addEventListener('click', async () => {
        try {
            const refreshResponse = await fetch(`${apiUrl}/admin/settings/refresh-cache`, {
                method: 'POST',
            });

            if (!refreshResponse.ok) {
                throw new Error(`HTTP error! Status: ${refreshResponse.status}`);
            }

            showToast("✅ Cache oppdatert!");
            await render(document.querySelector("main"));
        } catch (error) {
            console.error('Feil ved refresh av cache:', error);
            showToast("❌ Feil ved refresh!");
        }
    });

    settingsContainer.appendChild(refreshButton);

    try {
        console.log("Fetching settings from API...");
        const response = await fetch(`${apiUrl}/admin/settings/all`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        let settings = await response.json();
        console.log("Received settings:", settings);

        settings.sort((a, b) => {
            if (a.type === "csv" && b.type !== "csv") return 1;
            if (a.type !== "csv" && b.type === "csv") return -1;
            return a.key.localeCompare(b.key);
        });

        let csvHeaderInserted = false;
        let csvWrapper;

        settings.forEach(setting => {
            const { key, value, type } = setting;

            if (type === "csv") {
                if (!csvHeaderInserted) {
                    const title = document.createElement('h2');
                    title.textContent = 'System Settings for Images';
                    title.className = 'csv-title';
                    settingsContainer.appendChild(title);

                    csvWrapper = document.createElement('div');
                    csvWrapper.style.display = 'flex';
                    csvWrapper.style.flexDirection = 'column';
                    csvWrapper.style.gap = '12px';
                    csvWrapper.style.marginBottom = '20px';

                    const headerRow = document.createElement('div');
                    headerRow.style.display = 'flex';
                    headerRow.style.alignItems = 'center';
                    headerRow.style.gap = '15px';
                    headerRow.style.fontWeight = 'bold';
                    headerRow.style.marginBottom = '8px';

                    const labelSpacer = document.createElement('div');
                    labelSpacer.style.width = '200px';
                    headerRow.appendChild(labelSpacer);

                    ['Width', 'Height', 'Fit'].forEach(titleText => {
                        const col = document.createElement('div');
                        col.textContent = titleText;
                        col.style.width = '90px';
                        headerRow.appendChild(col);
                    });

                    const btnSpacer = document.createElement('div');
                    btnSpacer.style.width = '80px';
                    headerRow.appendChild(btnSpacer);

                    csvWrapper.appendChild(headerRow);
                    settingsContainer.appendChild(csvWrapper);
                    csvHeaderInserted = true;
                }

                let originalValue = value;
                const [widthVal, heightVal, fitVal] = value.split(",");

                const row = document.createElement('div');
                row.style.display = 'flex';
                row.style.alignItems = 'center';
                row.style.gap = '15px';

                const label = document.createElement('label');
                label.textContent = key;
                label.style.width = '200px';
                label.style.flexShrink = '0';
                label.style.fontWeight = 'bold';

                const widthInput = document.createElement('input');
                widthInput.type = 'number';
                widthInput.value = widthVal || "";
                widthInput.style.width = '75px';

                const heightInput = document.createElement('input');
                heightInput.type = 'number';
                heightInput.value = heightVal || "";
                heightInput.style.width = '75px';

                const fitSelect = document.createElement('select');
                const fitOptions = ['clip', 'crop', 'fill', 'fillmax', 'max', 'scale', 'min', 'stretch', 'facearea'];
                fitOptions.forEach(optionValue => {
                    const option = document.createElement('option');
                    option.value = optionValue;
                    option.textContent = optionValue;
                    fitSelect.appendChild(option);
                });
                fitSelect.value = fitVal || "";
                fitSelect.style.width = '100px';

                const saveButton = document.createElement('button');
                saveButton.textContent = 'Lagre';
                saveButton.className = 'save-button hidden';
                saveButton.style.width = '80px';

                [widthInput, heightInput, fitSelect].forEach(input => {
                    input.style.padding = '5px';
                    input.addEventListener('input', updateSaveButton);
                });

                function updateSaveButton() {
                    const newValue = `${widthInput.value},${heightInput.value},${fitSelect.value}`;
                    if (newValue !== originalValue) {
                        saveButton.classList.remove('hidden');
                        saveButton.classList.add('show');
                    } else {
                        saveButton.classList.remove('show');
                        saveButton.classList.add('hidden');
                    }
                }

                saveButton.addEventListener('click', async () => {
                    const newValue = `${widthInput.value},${heightInput.value},${fitSelect.value}`;
                    try {
                        const putResponse = await fetch(`${apiUrl}/admin/settings/${key}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ value: newValue })
                        });

                        const responseData = await putResponse.json();
                        if (!putResponse.ok) {
                            throw new Error(`HTTP error! Status: ${putResponse.status}, message: ${responseData.message || "Unknown error"}`);
                        }

                        originalValue = newValue;
                        saveButton.classList.remove('show');
                        showToast("✅ Setting oppdatert!");
                    } catch (error) {
                        console.error(`Feil ved oppdatering av setting ${key}:`, error);
                    }
                });

                row.appendChild(label);
                row.appendChild(widthInput);
                row.appendChild(heightInput);
                row.appendChild(fitSelect);
                row.appendChild(saveButton);

                csvWrapper.appendChild(row);
            } else {
                const settingDiv = document.createElement('div');
                settingDiv.className = 'setting';

                const label = document.createElement('label');
                label.textContent = key;
                label.htmlFor = key;
                label.style.fontWeight = 'bold';

                const inputContainer = document.createElement('div');
                inputContainer.style.display = 'flex';
                inputContainer.style.alignItems = 'center';
                inputContainer.style.gap = '8px';
                inputContainer.style.flexWrap = 'wrap';

                const input = document.createElement('input');
                input.type = type === "bool" ? 'checkbox' : (type === "int" || type === "float" ? 'number' : 'text');
                input.value = value;
                if (type === "bool") input.checked = value === "true";

                const saveButton = document.createElement('button');
                saveButton.textContent = 'Lagre';
                saveButton.className = 'save-button hidden';

                let originalValue = value;
                input.addEventListener('input', () => {
                    const currentValue = input.type === 'checkbox' ? input.checked.toString() : input.value;
                    if (currentValue !== originalValue) {
                        saveButton.classList.remove('hidden');
                        saveButton.classList.add('show');
                    } else {
                        saveButton.classList.remove('show');
                        saveButton.classList.add('hidden');
                    }
                });

                saveButton.addEventListener('click', async () => {
                    const newValue = input.type === 'checkbox' ? input.checked.toString() : input.value;
                    try {
                        const putResponse = await fetch(`${apiUrl}/admin/settings/${key}`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ value: newValue })
                        });

                        const responseData = await putResponse.json();
                        if (!putResponse.ok) {
                            throw new Error(`HTTP error! Status: ${putResponse.status}, message: ${responseData.message || "Unknown error"}`);
                        }

                        originalValue = newValue;
                        saveButton.classList.remove('show');
                        showToast("✅ Setting oppdatert!");
                    } catch (error) {
                        console.error(`Feil ved oppdatering av setting ${key}:`, error);
                    }
                });

                inputContainer.appendChild(input);
                inputContainer.appendChild(saveButton);

                settingDiv.appendChild(label);
                settingDiv.appendChild(inputContainer);
                settingsContainer.appendChild(settingDiv);
            }
        });
    } catch (error) {
        console.error('Feil ved henting av settings:', error);
    }
}