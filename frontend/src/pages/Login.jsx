import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

    const handleLogin = async (e) => {
    e.preventDefault();
    try {
        // Prova a mandare 'email' invece di 'username' se il tuo backend lo richiede
        const response = await api.post('/auth/login', { 
        email: username, // qui usiamo il valore dello stato 'username' ma lo battezziamo 'email'
        password: password 
        });
        
        localStorage.setItem('token', response.data.token);
        navigate('/chat', { replace: true });
    } catch (err) {
        console.error(err); // Guarda in console l'errore esatto!
        alert("Errore durante l'accesso.");
    }
    };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-slate-50">
      {/* Sezione Logo HRAssistant */}
      <div className="mb-8 text-center">
        <div className="w-16 h-16 bg-blue-600 rounded-xl mx-auto mb-4 flex items-center justify-center shadow-lg shadow-blue-200">
           <span className="text-white font-bold text-2xl">HR</span>
        </div>
        <h1 className="text-2xl font-light tracking-tight text-slate-800">
          Assistant <span className="font-bold text-blue-600">Portal</span>
        </h1>
      </div>

      <form onSubmit={handleLogin} className="bg-white p-10 rounded-2xl shadow-sm border border-slate-200 w-full max-w-md">
        <div className="space-y-6">
          <div>
            <label className="text-sm font-medium text-slate-600 ml-1">Utente</label>
            <input 
              type="text" 
              className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl mt-1 focus:ring-2 focus:ring-blue-400 outline-none transition-all"
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="text-sm font-medium text-slate-600 ml-1">Password</label>
            <input 
              type="password" 
              className="w-full p-3 bg-slate-50 border border-slate-200 rounded-xl mt-1 focus:ring-2 focus:ring-blue-400 outline-none transition-all"
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button className="w-full bg-blue-600 text-white p-4 rounded-xl font-semibold hover:bg-blue-700 hover:shadow-lg hover:shadow-blue-100 transition-all">
            Entra nel Sistema
          </button>
        </div>
      </form>
    </div>
  );
};

export default Login;