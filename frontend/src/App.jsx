import React from 'react';

// Importiamo i componenti necessari per la gestione delle rotte
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Importiamo le nostre Pagine
import Login from './pages/Login';
import Chat from './pages/Chat';
import AuditLogs from './pages/AuditLogs'; 
import CreateUser from './pages/CreateUser';

// Importiamo i componenti "strutturali"
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import AdminRoute from './components/AdminRoute';

function App() {
  return (

    // È il contenitore che abilita la navigazione senza ricaricare la pagina
    <BrowserRouter>
      {/* --- LAYOUT CON TAILWIND --- 
          className="...": Qui applichiamo gli stili CSS direttamente.
          - flex: Attiva il layout Flexbox (mette gli elementi in fila o colonna).
          - flex-col: Dice "disponi gli elementi in colonna" (dall'alto in basso).
          - min-h-screen: "Minimum Height Screen". Forza l'app a essere alta ALMENO quanto lo schermo del dispositivo.
      */}
      <div className="flex flex-col min-h-screen">

        {/* La Navbar sta fuori dalle <Routes>, quindi sarà VISIBILE IN TUTTE LE PAGINE */}
        <Navbar />
        
        {/* Main Content Area 
            - flex-1: Dice a questo elemento di "prendersi tutto lo spazio libero rimanente".
            - w-full: "Width Full", larghezza 100%.
            - relative: Posizionamento relativo (utile se ci sono elementi sovrapposti dentro).
        */}
        <main className="flex-1 w-full relative">

          {/* Qui dentro cambia il contenuto in base all'URL */}
          <Routes>

            {/* Rotta Pubblica: Chiunque può vedere /login */}
            <Route path="/login" element={<Login />} />
            
            {/* --- ROTTE PROTETTE --- 
                Il componente <Chat /> si trova dentro <PrivateRoute>.
                Se l'utente non è loggato, PrivateRoute lo bloccato prima ancora di caricare Chat.
            */}
            <Route path="/chat" element={<PrivateRoute><Chat /></PrivateRoute>} />
            
            {/* --- ROTTE ADMIN --- 
                Qui usiamo AdminRoute. Se sei loggato ma non sei ADMIN, vieni bloccato.
            */}
            <Route path="/admin/logs" element={<AdminRoute><AuditLogs /></AdminRoute>} />
            <Route path="/admin/create-user" element={<AdminRoute><CreateUser /></AdminRoute>} />
            
            {/* --- GESTIONE ERRORI E DEFAULT --- */}
            {/* Se l'utente va sulla home "/", lo mandiamo alla chat */}
            <Route path="/" element={<Navigate to="/chat" replace />} />
            {/* Se l'utente scrive un URL a caso ("*"), lo mandiamo al login */}
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;