document.addEventListener("DOMContentLoaded", function () {
    loadSystemSettings();
});

async function loadSystemSettings() {
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

        const container = document.getElementById('content');
        container.innerHTML = "<h2>System Settings</h2>";

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
            saveButton.className = 'save-button';

            input.addEventListener('input', () => {
                const currentValue = input.type === 'checkbox' ? input.checked.toString() : input.value;
                if (currentValue !== originalValue) {
                    saveButton.classList.add('show');
                } else {
                    saveButton.classList.remove('show');
                }
            });

            saveButton.addEventListener('click', async () => {
                const newValue = input.type === 'checkbox' ? input.checked.toString() : input.value;

                try {
                    const putResponse = await fetch(`https://localhost:7128/admin/settings/${key}`, {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ Value: newValue })
                    });

                    if (!putResponse.ok) {
                        throw new Error(`HTTP error! Status: ${putResponse.status}`);
                    }

                    console.log(`Updated setting: ${key} = ${newValue}`);
                    saveButton.classList.remove('show');
                } catch (error) {
                    console.error(`Error updating setting ${key}:`, error);
                }
            });

            settingDiv.appendChild(label);
            settingDiv.appendChild(input);
            settingDiv.appendChild(saveButton);
            container.appendChild(settingDiv);
        });
    } catch (error) {
        console.error('Feil ved henting av settings:', error);
    }
}
