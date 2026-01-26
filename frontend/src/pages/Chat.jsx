import React, { useState, useEffect, useRef } from 'react';
import api from '../services/api';

const Chat = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const scrollRef = useRef(null);

  // Auto-scroll all'ultimo messaggio
  useEffect(() => {
    scrollRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    // Pulizia definitiva della sessione JS per sicurezza (Requirement Esame)
    window.location.replace('/login');
  };

  const sendMessage = async (e) => {
  e.preventDefault();
  if (!input.trim()) return;

  // Aggiungiamo subito il messaggio dell'utente alla UI
  const userMsg = { text: input, type: 'user' };
  setMessages(prev => [...prev, userMsg]);
  const currentInput = input;
  setInput('');

  try {
    const res = await api.post('/api/chat', { question: currentInput });
    
    setMessages(prev => [...prev, { text: res.data.answer, type: 'bot' }]);
  } catch (err) {
    console.error("Errore nella chiamata:", err);
    setMessages(prev => [...prev, { text: "Il bot è momentaneamente offline. Riprova più tardi.", type: 'bot' }]);
  }
};

  return (
    <div className="flex h-screen bg-white">
      {/* Sidebar minimale (opzionale per logo) */}
      <div className="w-20 border-r border-slate-100 flex flex-col items-center py-8">
        <div className="w-10 h-10 bg-blue-50 text-blue-600 rounded-lg flex items-center justify-center font-bold mb-auto">
          HR
        </div>
        <button onClick={handleLogout} title="Esci" className="text-slate-400 hover:text-red-500 transition-colors">
          Logout
        </button>
      </div>

      <div className="flex-1 flex flex-col max-w-4xl mx-auto bg-white">
        {/* Header Chat */}
        <header className="p-6 border-b border-slate-50 flex justify-between items-center">
          <div>
            <h2 className="text-lg font-semibold text-slate-800">Supporto HR</h2>
            <p className="text-xs text-blue-500 font-medium">Sempre online</p>
          </div>
        </header>

        {/* Area Messaggi */}
        <div className="flex-1 overflow-y-auto p-8 space-y-6">
          {messages.map((m, i) => (
            <div key={i} className={`flex ${m.type === 'user' ? 'justify-end' : 'justify-start'}`}>
              <div className={`p-4 rounded-2xl max-w-md ${
                m.type === 'user' 
                ? 'bg-blue-600 text-white rounded-br-none shadow-md shadow-blue-100' 
                : 'bg-slate-50 text-slate-700 rounded-bl-none border border-slate-100'
              }`}>
                {m.text}
              </div>
            </div>
          ))}
          <div ref={scrollRef} />
        </div>

        {/* Input Form */}
        <form onSubmit={sendMessage} className="p-6">
          <div className="relative flex items-center">
            <input 
              value={input} 
              onChange={(e) => setInput(e.target.value)}
              placeholder="Chiedi qualcosa..."
              className="w-full bg-slate-50 border border-slate-200 rounded-2xl px-6 py-4 outline-none focus:border-blue-400 transition-all pr-16"
            />
            <button className="absolute right-3 p-2 text-blue-600 hover:scale-110 transition-transform">
              ➤
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Chat;