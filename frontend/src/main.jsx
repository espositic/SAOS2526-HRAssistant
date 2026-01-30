import React from 'react'
import ReactDOM from 'react-dom/client' // Importa la libreria che sa parlare con il Browser
import App from './App.jsx' // Importa il componente principale
import './index.css' // Importa gli stili globali

// Cerca nel file index.html un <div> vuoto che ha id="root".
// Questo è l'unico punto in cui React tocca il vero HTML della pagina.
ReactDOM.createRoot(document.getElementById('root')).render(

  // React.StrictMode è un wrapper per lo sviluppo.
  // Esegue controlli extra per trovare bug (es. useEffect mancanti).
  // ATTENZIONE: In sviluppo fa eseguire i componenti due volte (è normale vedere doppi log).
  <React.StrictMode>

    {/* Qui "montiamo" l'intera applicazione dentro quel div vuoto. */}
    <App />
    
  </React.StrictMode>,
)