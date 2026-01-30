import { Navigate } from 'react-router-dom';

// Componente Wrapper (Involucro).
// "children" rappresenta la pagina che stiamo cercando di visitare.
const PrivateRoute = ({ children }) => {

  // Qui controlliamo solo se il token esiste, non se è valido o meno 
  const token = localStorage.getItem('token');
  
  // Se il token esiste, mostra la pagina richiesta
  // Altrimenti, reindirizza al login
  return token ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;