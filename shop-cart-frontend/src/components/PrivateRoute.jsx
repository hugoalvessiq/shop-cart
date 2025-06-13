import React from "react";
import { Navigate } from "react-router-dom";

import useAuth from "../hooks/useAuth";

const PrivateRoute = ({ children, requiredRole }) => {
  const { isAuthenticated, user, loading } = useAuth();

  if (loading) return <div>Loading...</div>;

  if (!isAuthenticated) return <Navigate to="/login" />;

  if (requiredRole && !user.role.includes(requiredRole)) {
    return <Navigate to="/" />;
  }

  return children;
};

export default PrivateRoute;
