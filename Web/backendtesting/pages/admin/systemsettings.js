export async function render(container) {
    container.innerHTML = "<h2>System Settings</h2><div id='settings-container'></div>";

    const settingsContainer = document.getElementById("settings-container");

    try {
        console.log("Fetching settings from API...");
        const response = await fetch('https://localhost:7128/admin/settings/all', {
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
            const originalValue = input.type === 'checkbox' ? input.checked.toString() : input.value;

            const saveButton = document.createElement('button');
            saveButton.textContent = 'Lagre';
            saveButton.className = 'save-button hidden';

            input.addEventListener('input', () => {
                const currentValue = input.type === 'checkbox' ? input.checked.toString() : input.value;
                if (currentValue !== originalValue) {
                    saveButton.classList.remove('hidden'); // Vis knappen
                } else {
                    saveButton.classList.add('hidden'); // Skjul knappen
                }
            });

            saveButton.addEventListener('click', async () => {
                const newValue = input.type === 'checkbox' ? input.checked.toString() : input.value;

                try {
                    const putResponse = await fetch(`https://localhost:7128//admin/settings/${key}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ value: newValue }) // Send ny verdi
                    });

                    if (!putResponse.ok) {
                        throw new Error(`HTTP error! Status: ${putResponse.status}`);
                    }

                    console.log(`Updated setting: ${key} = ${newValue}`);
                    saveButton.classList.add('hidden'); // Skjul knappen etter lagring
                } catch (error) {
                    console.error(`Error updating setting ${key}:`, error);
                }
            });

            settingDiv.appendChild(label);
            settingDiv.appendChild(input);
            settingDiv.appendChild(saveButton);
            settingsContainer.appendChild(settingDiv);
        });
    } catch (error) {
        console.error('Feil ved henting av settings:', error);
    }
}
