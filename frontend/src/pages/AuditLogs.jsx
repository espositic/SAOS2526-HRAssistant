import React, { useEffect, useState } from 'react';
import api from '../services/api'; // Il nostro client HTTP configurato con il token

const AuditLogs = () => {

  // --- STATO DEL COMPONENTE ---
  // logs: Contiene la lista dei dati che arriveranno dal Backend. 
  // All'inizio è un array vuoto.
  const [logs, setLogs] = useState([]);

  // loading: Serve per mostrare la scritta "Caricamento..." 
  // mentre aspettiamo il server.
  const [loading, setLoading] = useState(true);
  
  // Esegui questo codice solo una volta, appena la pagina si apre.
  useEffect(() => {
    const fetchLogs = async () => {
      try {

        // Chiamata GET all'endpoint Java.
        // Grazie all'interceptor (in api.js), il Token viene allegato automaticamente.
        const res = await api.get('/admin/logs');

        // Se va tutto bene, salviamo i dati nello stato.
        setLogs(res.data);
      } catch (err) { 
        console.error(err); 
      } finally {

        // Indipendentemente se va bene o male, spegniamo la clessidra di caricamento.
        setLoading(false);
      }
    };

    // Avviamo la funzione asincrona
    fetchLogs();
  }, []);

  return (
    <div className="max-w-6xl mx-auto p-6 pt-12"> 
      
      {/* HEADER DELLA PAGINA */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between mb-8 gap-4">
        <div>
          <h2 className="page-title">Registri di Sistema</h2>
          <span className="page-subtitle">Audit Log</span>
        </div>
        
        {/* Badge contatore */}
        <div className="bg-white border border-slate-200 px-4 py-2 rounded-xl text-sm font-bold shadow-sm text-blue-600 flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-blue-600 animate-pulse"></span>
          {logs.length} Eventi
        </div>
      </div>

      {/* CARD TABELLA */}
      <div className="card-unified">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">

            {/* INTESTAZIONE TABELLA */}
            <thead>
              <tr className="bg-slate-50 border-b border-slate-100 text-slate-500 text-[10px] uppercase tracking-widest">
                <th className="p-6 font-bold">Data</th>
                <th className="p-6 font-bold">Operatore</th>
                <th className="p-6 font-bold">Azione</th>
              </tr>
            </thead>

            {/* CORPO TABELLA */}
            <tbody className="divide-y divide-slate-50">

              {/* RENDERING CONDIZIONALE:
                  - Se sta caricando -> Mostra messaggio attesa.
                  - Se ha finito -> Mappa i dati (Logica Loop).
              */}  
              {loading ? (
                <tr><td colSpan="3" className="p-10 text-center text-slate-400">Caricamento logs...</td></tr>
              ) : logs.map(log => (
                <tr key={log.id} className="hover:bg-blue-50/30 transition-colors duration-200 group">
                  
                  {/* DATA FORMATTATA */}
                  <td className="p-6 text-sm text-slate-500 font-mono">
                    {new Date(log.timestamp).toLocaleString()}
                  </td>

                  {/* OPERATORE CON AVATAR GENERATO */}
                  <td className="p-6">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center font-bold text-xs">
                        {log.username?.charAt(0).toUpperCase()}
                      </div>
                      <span className="font-bold text-slate-700 text-sm">{log.username}</span>
                    </div>
                  </td>

                  {/* AZIONE / DOMANDA */}
                  <td className="p-6 text-slate-600 text-sm leading-relaxed">
                    {log.action || log.question}
                  </td>
                </tr>
              ))}

              {/* CASO LISTA VUOTA */}
              {!loading && logs.length === 0 && (
                <tr>
                  <td colSpan="3" className="p-16 text-center text-slate-400 italic">
                    Nessun evento registrato nel periodo selezionato.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AuditLogs;