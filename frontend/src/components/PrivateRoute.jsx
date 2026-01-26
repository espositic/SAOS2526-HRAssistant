import { Navigate } from 'react-router-dom';

const PrivateRoute = ({ children }) => {
  const token = localStorage.getItem('token');
  
  // Se il token esiste, mostra la pagina richiesta (children)
  // Altrimenti, reindirizza al login e "sostituisci" la cronologia
  return token ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;