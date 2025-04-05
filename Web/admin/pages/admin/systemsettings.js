
const apiUrl = window.appConfig.API_URL || "https://localhost:7128";
console.log("API URL:", apiUrl);

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

            // ➡️ Nå henter vi settings på nytt
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

        settings.forEach(setting => {
            const { key, value, type } = setting;
            const settingDiv = document.createElement('div');
            settingDiv.className = 'setting';

            const label = document.createElement('label');
            label.textContent = key;
            label.htmlFor = key;

            const inputContainer = document.createElement('div');
            inputContainer.style.display = 'flex';
            inputContainer.style.alignItems = 'center';
            inputContainer.style.gap = '8px';
            inputContainer.style.flexWrap = 'wrap';

            let originalValue = value;

            if (type === "csv") {
                if (!csvHeaderInserted) {
                    const csvHeader = document.createElement('div');
                    csvHeader.className = 'csv-header';

                    ['Width', 'Height', 'Fit'].forEach(title => {
                        const col = document.createElement('div');
                        col.textContent = title;
                        csvHeader.appendChild(col);
                    });

                    settingsContainer.appendChild(csvHeader);
                    csvHeaderInserted = true;
                }

                const [widthVal, heightVal, fitVal] = value.split(",");

                const widthInput = document.createElement('input');
                widthInput.type = 'number';
                widthInput.value = widthVal || "";

                const heightInput = document.createElement('input');
                heightInput.type = 'number';
                heightInput.value = heightVal || "";

                const fitInput = document.createElement('input');
                fitInput.type = 'text';
                fitInput.value = fitVal || "";

                const saveButton = document.createElement('button');
                saveButton.textContent = 'Lagre';
                saveButton.className = 'save-button hidden';

                function updateSaveButton() {
                    const newValue = `${widthInput.value},${heightInput.value},${fitInput.value}`;
                    if (newValue !== originalValue) {
                        saveButton.classList.remove('hidden');
                        saveButton.classList.add('show');
                    } else {
                        saveButton.classList.remove('show');
                        saveButton.classList.add('hidden');
                    }
                }

                [widthInput, heightInput, fitInput].forEach(input => {
                    input.addEventListener('input', updateSaveButton);
                });

                saveButton.addEventListener('click', async () => {
                    const newValue = `${widthInput.value},${heightInput.value},${fitInput.value}`;

                    try {
                        const putResponse = await fetch(`${apiUrl}/admin/settings/${key}`, {
                            method: 'PUT',
                            headers: {
                                'Content-Type': 'application/json'
                            },
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

                inputContainer.appendChild(widthInput);
                inputContainer.appendChild(heightInput);
                inputContainer.appendChild(fitInput);
                inputContainer.appendChild(saveButton);
            } else {
                const input = document.createElement('input');
                input.type = type === "bool" ? 'checkbox' : (type === "int" || type === "float" ? 'number' : 'text');
                input.value = value;
                if (type === "bool") input.checked = value === "true";

                const saveButton = document.createElement('button');
                saveButton.textContent = 'Lagre';
                saveButton.className = 'save-button hidden';

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
                            headers: {
                                'Content-Type': 'application/json'
                            },
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
            }

            settingDiv.appendChild(label);
            settingDiv.appendChild(inputContainer);
            settingsContainer.appendChild(settingDiv);
        });
    } catch (error) {
        console.error('Feil ved henting av settings:', error);
    }
}



/*
const apiUrl = window.appConfig.API_URL || "https://localhost:7128"; // Fallback hvis miljøvariabelen ikke er satt
console.log("API URL:", apiUrl);


export async function render(container) {
    container.innerHTML = "<h2>System Settings</h2><div id='settings-container'></div>";

    const settingsContainer = document.getElementById("settings-container");

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

        settings.forEach(setting => {
            const { key, value, type } = setting;
            const settingDiv = document.createElement('div');
            settingDiv.className = 'setting';

            const label = document.createElement('label');
            label.textContent = key;
            label.htmlFor = key;

            let input;
            if (type === "bool") {
                input = document.createElement('input');
                input.type = 'checkbox';
                input.checked = value === "true";
            } else if (type === "int") {
                input = document.createElement('input');
                input.type = 'number';
                input.value = value;
            } else {
                input = document.createElement('input');
                input.type = 'text';
                input.value = value;
            }

            input.id = key;
            // Endret fra const til let for at verdien kan oppdateres senere
            let originalValue = input.type === 'checkbox' ? input.checked.toString() : input.value;

            const saveButton = document.createElement('button');
            saveButton.textContent = 'Lagre';
            saveButton.className = 'save-button hidden';

            console.log("Oppretter lagre-knapp for:", key);
            console.log(saveButton);


            // Opprett en container for input og knapp for å unngå layout-skift
            const inputContainer = document.createElement('div');
            inputContainer.style.display = 'flex';
            inputContainer.style.alignItems = 'center';
            inputContainer.style.gap = '8px';

            input.addEventListener('input', () => {
                const currentValue = input.type === 'checkbox' ? input.checked.toString() : input.value;
                console.log(`Endrer ${key}: ${originalValue} → ${currentValue}`);
            
                if (currentValue !== originalValue) {
                    console.log("Viser lagre-knappen!");
                    saveButton.classList.remove('hidden'); // Fjern hidden hvis den finnes
                    saveButton.classList.add('show');
                } else {
                    console.log("Skjuler lagre-knappen!");
                    saveButton.classList.remove('show');
                    saveButton.classList.add('hidden');
                }
            });
            
            
            

            saveButton.addEventListener('click', async () => {
                const newValue = input.type === 'checkbox' ? input.checked.toString() : input.value;
            
                try {
                    console.log(`Sender PUT-request til serveren med key: ${key} og value: ${newValue}`);
            
                    const putResponse = await fetch(`https://localhost:7128/admin/settings/${key}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ value: newValue }) // Send ny verdi til serveren
                    });
            
                    const responseData = await putResponse.json();
                    console.log('PUT response:', responseData);
            
                    if (!putResponse.ok) {
                        throw new Error(`HTTP error! Status: ${putResponse.status}, message: ${responseData.message || "Unknown error"}`);
                    }
            
                    // Oppdater originalverdien slik at knappens synlighet oppdateres korrekt
                    originalValue = newValue;
                    saveButton.classList.remove('show'); // Skjul knappen
                    console.log(`✅ Setting oppdatert: ${key} = ${newValue}`);
                } catch (error) {
                    console.error(`❌ Feil ved oppdatering av setting ${key}:`, error);
                }
            });
            

            inputContainer.appendChild(input);
            inputContainer.appendChild(saveButton);

            settingDiv.appendChild(label);
            settingDiv.appendChild(inputContainer);
            settingsContainer.appendChild(settingDiv);
        });
    } catch (error) {
        console.error('Feil ved henting av settings:', error);
    }
}
*/