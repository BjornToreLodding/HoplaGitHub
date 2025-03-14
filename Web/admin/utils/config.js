// /utils/config.js
const apiURL = process.env.API_URL || 'https://localhost:7128';  // Fallback for lokal utvikling
//const apiURL = 'https://localhost:7128';
console.log(apiURL)
export default apiURL;
