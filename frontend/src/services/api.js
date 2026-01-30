import axios from 'axios';

// Creiamo un'istanza personalizzata di Axios.
// Invece di scrivere l'URL completo in ogni file, lo definiamo qui una volta sola.
const api = axios.create({
  // baseURL: L'indirizzo del tuo server Spring Boot.
  baseURL: 'https://localhost:8443', 
});

/**
 * INTERCEPTOR DI RICHIESTA
 * È una funzione che viene eseguita AUTOMATICAMENTE prima di ogni chiamata API.
 */
api.interceptors.request.use((config) => {
  // Recuperiamo il Token salvato nel browser durante il Login
  const token = localStorage.getItem('token');

  // Se il token esiste, lo "iniettiamo" nell'Header della richiesta
  if (token) {

    // Questo è quello che il JwtFilter nel Backend andrà a leggere.
    config.headers.Authorization = `Bearer ${token}`;
  }

  // La richiesta prosegue il suo viaggio verso il server
  return config;
});

export default api;