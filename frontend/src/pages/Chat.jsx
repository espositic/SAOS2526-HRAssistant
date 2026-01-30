import React, { useState, useEffect, useRef } from 'react';
import api from '../services/api';

const Chat = () => {
  
  // messages: Array che contiene tutta la cronologia
  const [messages, setMessages] = useState([]);

  // input: Quello che l'utente sta scrivendo nella casella di testo
  const [input, setInput] = useState('');

  // loading: Booleano (true/false) per mostrare i pallini che saltano mentre l'AI pensa
  const [loading, setLoading] = useState(false);

  // useRef: Serve per l'auto-scroll
  const scrollRef = useRef(null);

  // --- Auto-Scroll ---
  useEffect(() => {

    // Ogni volta che cambia la lista dei messaggi o lo stato di caricamento
    // ...scorri automaticamente la pagina fino all'elemento "scrollRef" (il fondo).
    scrollRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  // --- LOGICA DI INVIO ---
  const sendMessage = async (e) => {
    e.preventDefault(); // Evita che il form ricarichi la pagina
    if (!input.trim()) return; // Se l'utente invia spazi vuoti, blocca tutto.

    // Aggiungiamo il messaggio dell'utente alla lista, senza aspettare il server.
    // Questo rende l'app percepita come istantanea.
    const userMsg = { text: input, type: 'user' };
    setMessages(prev => [...prev, userMsg]);

    // Salviamo l'input in una variabile temp e puliamo la casella di testo
    const currentInput = input;
    setInput('');
    setLoading(true); // Accendiamo l'animazione di attesa

    try {
      // Inviamo il JSON all'endpoint /api/chat
      const res = await api.post('/api/chat', { question: currentInput });

      // Aggiungiamo la risposta del bot alla lista dei messaggi.
      setMessages(prev => [...prev, { text: res.data.answer, type: 'bot' }]);
    } catch (err) {
      
      // Gestione Errori: Se il server è giù o scatta il Rate Limit (429)
      setMessages(prev => [...prev, { text: "Servizio momentaneamente non disponibile.", type: 'bot' }]);
    } finally {
      setLoading(false); // Spegniamo l'animazione
    }
  };

  return (
    <div className="h-[calc(100vh-73px)] bg-slate-50 w-full flex justify-center">
      
      {/* Box Bianco della Chat */}
      <div className="w-full max-w-5xl bg-white h-full flex flex-col shadow-2xl shadow-slate-200/50 border-x border-slate-200">
        
        {/* Area Messaggi */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-8 space-y-6 bg-slate-50/30">

          {/* STATO VUOTO: Se non ci sono messaggi, mostra l'icona di benvenuto */}
          {messages.length === 0 && (
            <div className="h-full flex flex-col items-center justify-center text-center opacity-50 space-y-4">
               <div className="w-16 h-16 bg-slate-100 rounded-2xl flex items-center justify-center">
                  
                  {/* ...Icona SVG... */}
                  <svg className="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/></svg>
               </div>
               <p className="text-slate-500 font-medium">HR Assistant è pronto. Chiedi pure.</p>
            </div>
          )}

          {/* LISTA MESSAGGI: Mappiamo l'array messages in bolle di chat */}
          {messages.map((m, i) => (

            // Flexbox condizionale: Se è "user" allinea a DESTRA (end), se "bot" a SINISTRA (start)
            <div key={i} className={`flex ${m.type === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`
                max-w-[85%] sm:max-w-md p-5 rounded-2xl shadow-sm text-sm leading-relaxed
                ${m.type === 'user' 
                  ? 'bg-slate-800 text-white rounded-br-none' 
                  : 'bg-white border border-slate-200 text-slate-700 rounded-bl-none'}
              `}>
                {m.text}
              </div>
            </div>
          ))}
          
          {/* ANIMAZIONE CARICAMENTO */}
          {loading && (
            <div className="flex justify-start">
               <div className="bg-white border border-slate-200 p-4 rounded-2xl rounded-bl-none flex gap-2 items-center">
                  <span className="w-2 h-2 bg-blue-500 rounded-full animate-bounce"></span>
                  <span className="w-2 h-2 bg-blue-500 rounded-full animate-bounce delay-75"></span>
                  <span className="w-2 h-2 bg-blue-500 rounded-full animate-bounce delay-150"></span>
               </div>
            </div>
          )}

          {/* Ancora invisibile per l'autoscroll */}
          <div ref={scrollRef} />
        </div>

        {/* Input Area */}
        <div className="p-6 bg-white border-t border-slate-100">
          <form onSubmit={sendMessage} className="relative flex items-center">
            <input 
              value={input} 
              onChange={(e) => setInput(e.target.value)}
              placeholder="Scrivi una domanda..."
              className="input-unified pr-16 shadow-lg shadow-slate-100/50"
              autoFocus
            />

            {/* Tasto Invio */}
            <button 
              type="submit"
              disabled={!input.trim() || loading}
              className="absolute right-2 p-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:hover:bg-blue-600 transition-all shadow-md shadow-blue-200"
            >

              {/* Icona aeroplanino */}
              <svg className="w-5 h-5 rotate-90" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
              </svg>
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Chat;