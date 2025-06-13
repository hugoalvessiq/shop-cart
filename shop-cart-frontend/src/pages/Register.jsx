import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import { createUser } from "../api/userApi";

const Register = () => {
  const navigate = useNavigate();
  const { user, auth, login } = useAuth();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("USER");
  const [error, setError] = useState("");
  const [keepLogin, setKeepLogin] = useState(false);

  const token = localStorage.getItem("token");

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const payload = {
        name,
        email,
        password,
        role: user?.role === "ADMIN" ? role : "USER",
      };

      await createUser(payload, auth?.accessToken);

      alert("User created successfully!");
      if (token) {
        navigate("/admin/users");
      } else {
        if (keepLogin) {
          await login(email, password);
        }
        navigate("/");
      }
    } catch (err) {
      console.error(err);
      if (err.response?.status === 409) {
        setError("Email already registered.");
      } else {
        setError("Error Creating User");
      }
    }
  };

  const handleKeepLogin = (e) => {
    setKeepLogin(e.target.checked);
  };

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center">
      <div className="max-w-md w-full p-6 bg-white shadow-md rounded-xl">
        <h2 className="text-2xl font-bold mb-6 text-center">Criar Conta</h2>

        {error && <div className="text-red-500 mb-4">{error}</div>}

        <form onSubmit={handleRegister} className="space-y-4">
          <div>
            <label className="block text-sm font-medium">Name</label>
            <input
              type="text"
              required
              className="w-full border rounded px-3 py-2 mt-1"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-medium">Email</label>
            <input
              type="email"
              required
              className="w-full border rounded px-3 py-2 mt-1"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm font-medium">Password</label>
            <input
              type="password"
              required
              className="w-full border rounded px-3 py-2 mt-1"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {user?.role === "ADMIN" && (
            <div>
              <label className="block text-sm font-medium">Role</label>
              <select
                className="w-full border rounded px-3 py-2 mt-1"
                value={role}
                onChange={(e) => setRole(e.target.value)}
              >
                <option value="USER">USER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
          )}
          <button
            type="submit"
            className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
          >
            Register
          </button>
          {!token && (
            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="keep-login"
                checked={keepLogin}
                onChange={handleKeepLogin}
              />
              <label htmlFor="keep-login">Stay connected</label>
            </div>
          )}
        </form>
      </div>
    </div>
  );
};

export default Register;
