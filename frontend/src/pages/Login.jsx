import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const Login = () => {
  // --- STATO (Hooks) ---
  // Definiamo variabili che React "osserva". Se cambiano, la pagina si aggiorna.
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(false); // Diventa true se il login fallisce
  const [loading, setLoading] = useState(false); // Diventa true mentre aspettiamo il server
  const navigate = useNavigate(); // Funzione per cambiare pagina

  // Funzione chiamata quando l'utente preme "Invio" o clicca sul bottone
  const handleLogin = async (e) => {
    e.preventDefault(); // Impedisce al browser di ricaricare la pagina
    setLoading(true);
    setError(false);
    
    try {
        // Chiamata POST al backend (URL: /auth/login)
        const response = await api.post('/auth/login', { 
          email: username, 
          password: password 
        });
        
        // Se il server risponde OK, salviamo il Token JWT nel browser.
        localStorage.setItem('token', response.data.token);

        // Navighiamo verso chat.
        navigate('/chat', { replace: true });
    } catch (err) {
        console.error(err);
        setError(true); // Mostra il messaggio rosso di errore
    } finally {
        setLoading(false); // Ferma l'animazione di caricamento
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-slate-50 px-4">
      
      {/* SEZIONE LOGO */}
      <div className="mb-10 text-center animate-fade-in-down">
        <div className="w-20 h-20 bg-blue-600 rounded-2xl mx-auto mb-6 flex items-center justify-center shadow-xl shadow-blue-200">
           <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 10V3L4 14h7v7l9-11h-7z"/></svg>
        </div>
        <h1 className="text-3xl font-light tracking-tight text-slate-800">
          Benvenuto in <span className="font-black text-blue-600">HR Assistant</span>
        </h1>
      </div>

      {/* CARD LOGIN */}
      <div className="card-unified w-full max-w-md p-8 sm:p-10">
        <form onSubmit={handleLogin} className="space-y-6">
          
          <div>
            <label className="label-unified">Email</label>
            <input 
              type="text" 
              className="input-unified"
              placeholder="Inserisci la tua email"
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          
          <div>
            <label className="label-unified">Password</label>
            <input 
              type="password" 
              className="input-unified"
              placeholder="••••••••"
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          {/* MESSAGGIO DI ERRORE CONDIZIONALE */}
          {error && (
            <div className="p-3 bg-red-50 border border-red-100 rounded-xl text-center text-xs font-bold text-red-500">
              Credenziali non valide. Riprova.
            </div>
          )}

          <button disabled={loading} className="btn-primary">
            {loading ? 'Accesso in corso...' : 'Accedi'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;