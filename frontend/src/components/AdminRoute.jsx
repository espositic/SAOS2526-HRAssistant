import { Navigate } from 'react-router-dom';
import { getAuthData } from '../services/auth';

/**
 * Componente Wrapper (Involucro) per proteggere le rotte sensibili.
 * Funziona come una matrioska: avvolge la pagina reale (children).
 * * @param {Object} props - Le proprietà del componente.
 * @param {React.ReactNode} props.children - La pagina che vogliamo proteggere (es. <CreateUser />).
 */
const AdminRoute = ({ children }) => {
    // Recuperiamo Token e Ruolo dal LocalStorage
    const auth = getAuthData();

    // Se l'utente non è loggato, lo rispediamo al Login.
    // "replace" serve a non lasciare traccia nella cronologia del browser.
    if (!auth) return <Navigate to="/login" replace />;
  
    // L'utente è loggato, ma è un semplice dipendente?
    // Se il ruolo non è HR_ADMIN, non può vedere questa pagina.
    if (auth.role !== 'HR_ADMIN') {
        return <Navigate to="/chat" replace />;
    }

    // Se siamo arrivati qui, l'utente è loggato ED è un Admin.
    return children;
};

export default AdminRoute;