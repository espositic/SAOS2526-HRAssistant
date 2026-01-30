import React, { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { getAuthData } from '../services/auth';

const Navbar = () => {

  // Serve per evidenziare il link attivo in blu.
  const location = useLocation();

  // Stato locale per memorizzare i dati dell'utente (Nome, Ruolo)
  const [auth, setAuth] = useState(null);

  // Rilegge il token. 
  // Viene eseguito ogni volta che cambia la pagina.
  useEffect(() => {
    setAuth(getAuthData());
  }, [location]);

  // Se non c'è l'utente non mostrare la navbar.
  if (!auth) return null;

  // Verifica Admin
  const isAdmin = auth.role === 'HR_ADMIN' || auth.role === 'ROLE_HR_ADMIN';
  
  // Se siamo sulla pagina corrente allora sfondo blu e testo blu.
  // Altrimenti testo grigio.
  const getLinkClass = (path) => {
    const isActive = location.pathname === path;
    return isActive 
      ? "bg-blue-50 text-blue-600 px-4 py-2 rounded-xl text-sm font-bold transition-all"
      : "text-slate-500 hover:text-blue-600 hover:bg-slate-50 px-4 py-2 rounded-xl text-sm font-medium transition-all";
  };

  return (
    <nav className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-slate-200 px-6 py-3">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        
        {/* PARTE SINISTRA: Logo + Menu */}
        <div className="flex items-center gap-8">
          {/* LOGO */}  
          <Link to="/chat" className="flex items-center gap-2 group">
            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center shadow-lg shadow-blue-200 group-hover:scale-105 transition-transform">
               <span className="text-white font-bold text-sm">HR</span>
            </div>
            <span className="font-extrabold text-slate-800 text-lg tracking-tight">Assistant</span>
          </Link>

          <div className="h-6 w-px bg-slate-200 mx-2"></div> {/* Separatore verticale sottile */}

          {/* LINK DI NAVIGAZIONE */}  
          <div className="flex items-center gap-1">
            <Link to="/chat" className={getLinkClass('/chat')}>Chat</Link>

            {/* RENDERING CONDIZIONALE:
                Questo pezzo di codice HTML esiste SOLO se isAdmin è true.
            */}
            {isAdmin && (
              <>
                <Link to="/admin/logs" className={getLinkClass('/admin/logs')}>Audit Logs</Link>
                <Link to="/admin/create-user" className={getLinkClass('/admin/create-user')}>Gestione Utenti</Link>
              </>
            )}
          </div>
        </div>
        
        {/* PARTE DESTRA: Info Utente + Logout */}
        <div className="flex items-center gap-4 pl-6 border-l border-slate-100">
          <div className="text-right hidden sm:block">
            <p className="text-sm font-bold text-slate-800">{auth.username}</p>
            <p className="text-[10px] text-slate-400 font-bold uppercase tracking-widest">{auth.role}</p>
          </div>
          
          {/* BOTTONE LOGOUT */}
          <button 
            onClick={() => { localStorage.removeItem('token'); window.location.replace('/login'); }}
            className="group relative p-2 text-slate-400 hover:text-red-500 transition-colors"
            title="Esci"
          >

            {/* Icona di uscita (SVG) */}
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;