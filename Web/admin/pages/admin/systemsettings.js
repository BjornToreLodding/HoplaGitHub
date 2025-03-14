const apiUrl = window.appConfig.API_URL || "https://localhost:7128"; // Fallback hvis miljøvariabelen ikke er satt
console.log("API URL:", apiUrl);


export async function render(container) {
    container.innerHTML = "<h2>System Settings</h2><div id='settings-container'></div>";

    const settingsContainer = document.getElementById("settings-container");

    try {
        console.log("Fetching settings from API...");
        const response = await fetch(`${apiURL}/admin/settings/all`, {
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
