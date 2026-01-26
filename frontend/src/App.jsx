import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Importiamo i componenti che abbiamo creato
// Assicurati che i nomi dei file siano ESATTAMENTE questi
import Login from './pages/Login';
import Chat from './pages/Chat';
import PrivateRoute from './components/PrivateRoute';

/**
 * COMPONENTE PRINCIPALE APP
 * Gestisce la navigazione tra Login e Chat.
 */
function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50">
        <Routes>
          {/* 1. Rotta Pubblica: Login */}
          <Route path="/login" element={<Login />} />

          {/* 2. Rotta Protetta: Chat (Accessibile solo se loggati) */}
          <Route 
            path="/chat" 
            element={
              <PrivateRoute>
                <Chat />
              </PrivateRoute>
            } 
          />

          {/* 3. Redirezione automatica: 
              Se vai su '/', ti manda in /chat (che ti rimbalzerà al login se non sei autenticato) */}
          <Route path="/" element={<Navigate to="/chat" replace />} />

          {/* 4. Gestione errore 404: Qualsiasi altra cosa manda al login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;