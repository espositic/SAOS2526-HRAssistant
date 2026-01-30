/**
 * Funzione che estrae le informazioni dell'utente dal Token salvato.
 * Il JWT è composto da 3 parti divise da un punto: Header.Payload.Signature
 */
export const getAuthData = () => {
  // Recuperiamo la stringa criptata dal localStorage
  const token = localStorage.getItem('token');

  // Verifica esistenza token
  if (!token) return null;

  try {
    /**
     * DECODIFICA DEL PAYLOAD
     * token.split('.')[1] -> Prende la seconda parte del token (il Payload).
     * window.atob(...)    -> Decodifica la stringa da Base64 a testo normale.
     * JSON.parse(...)     -> Trasforma il testo in un oggetto JavaScript utilizzabile.
     */
    const payload = JSON.parse(window.atob(token.split('.')[1]));

    // Ritorniamo un oggetto pulito con i dati che ci servono per la UI
    return {
      username: payload.sub, // 'sub' è lo standard JWT per l'identificativo (la nostra email)
      role: payload.role // Il ruolo (HR_ADMIN o USER) che abbiamo inserito nel Backend
    };
  } catch (e) {
    // Se il token è corrotto o manipolato, la decodifica fallisce
    return null;
  }
};