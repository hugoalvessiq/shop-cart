import React from "react";
import UserManager from "../components/UserManager";
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSelectUser = async (email, password) => {
    try {
      await login(email, password);
      navigate("/");
    } catch (error) {
      alert("Login failed!");
      console.error(error);
    }
  };
  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <UserManager onSelectUser={handleSelectUser} />
    </div>
  );
};

export default Login;
