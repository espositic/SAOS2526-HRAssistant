import React, { useState } from 'react';
import api from '../services/api';

const CreateUser = () => {
  // --- STATO DEL FORM ---
  // Raggruppiamo i campi in un unico oggetto  
  const [formData, setFormData] = useState({ 
    email: '', 
    password: '', 
    role: 'USER' 
  });

  // Stato per i messaggi di successo o errore
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);

  // --- GESTIONE INVIO ---
  const handleSubmit = async (e) => {
    e.preventDefault(); // Blocca il refresh della pagina
    setLoading(true);
    setStatus(null);

    try {
      // Chiamata POST al Backend: passiamo l'intero oggetto formData
      const res = await api.post('/admin/users/create', formData);

      // Se il server risponde 200 OK:
      setStatus({ type: 'success', msg: 'Utente creato con successo!' });

      // Puliamo il form per un nuovo inserimento
      setFormData({ email: '', password: '', role: 'USER' });
    } catch (err) {

      // Gestiamo l'errore: mostriamo il messaggio che arriva dal server  
      console.error("Errore:", err.response);
      setStatus({ 
        type: 'error', 
        msg: err.response?.data?.message || 'Errore durante la creazione dell\'utente.' 
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-6 pt-12">
      {/* HEADER */}
      <div className="mb-8 text-center">
        <h2 className="page-title">Crea un Nuovo Utente</h2>
        <span className="page-subtitle">Configurazione Ruoli e Accessi</span>
      </div>

      <div className="card-unified p-8 sm:p-10">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 gap-6">
            
            {/* EMAIL */}
            <div>
              <label className="label-unified">Email Aziendale</label>
              <input 
                type="email" 
                placeholder="nome.cognome@azienda.com" 
                required 
                className="input-unified"
                value={formData.email} 
                onChange={(e) => setFormData({...formData, email: e.target.value})} 
              />
            </div>

            {/* PASSWORD */}
            <div>
              <label className="label-unified">Password</label>
              <input 
                type="password" 
                placeholder="••••••••" 
                required 
                className="input-unified"
                value={formData.password} 
                onChange={(e) => setFormData({...formData, password: e.target.value})} 
              />
            </div>

            {/* RUOLO */}
            <div>
              <label className="label-unified">Ruolo Assegnato</label>
              <div className="relative">
                <select 
                  className="input-unified appearance-none cursor-pointer" 
                  value={formData.role}
                  onChange={(e) => setFormData({...formData, role: e.target.value})}
                >
                  <option value="USER">User</option>
                  <option value="HR_ADMIN">HR Admin</option>
                </select>
                {/* fRECCIA */}
                <div className="absolute right-4 top-1/2 -translate-y-1/2 pointer-events-none text-slate-500">
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path></svg>
                </div>
              </div>
            </div>
          </div>

          {/* FEEDBACK VISIVO */}
          {status && (
            <div className={`p-4 rounded-xl text-sm font-bold flex items-center gap-2 ${
              status.type === 'success' ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-600'
            }`}>
              <span className="text-xl">{status.type === 'success' ? '✓' : '⚠'}</span>
              {status.msg}
            </div>
          )}

          {/* BOTTONE DI INVIO */}
          <button disabled={loading} className="btn-primary mt-4 flex items-center justify-center gap-2">
            {loading ? 'Creazione in corso...' : 'Registra Utente'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateUser;