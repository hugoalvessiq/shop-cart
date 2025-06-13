import React, { useEffect, useState } from "react";
import useAuth from "../hooks/useAuth";
import { useNavigate } from "react-router-dom";
import { deleteUser, updateUser } from "../api/userApi";
import { getCart } from "../api/cartApi";

const Profile = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const [cart, setCart] = useState(null);

  const fetchCart = () => {
    if (token) {
      getCart().then((res) => setCart(res.data));
    } else {
      return;
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  useEffect(() => {
    if (user) {
      setForm({ name: user.name, email: user.email, password: "" });
    }
  }, [user]);

  const prepareUpdatePayload = ({ name, email, password }) => {
    const payload = { name, email };
    if (password.trim() !== "") {
      payload.password = password;
    }
    return payload;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = prepareUpdatePayload(form);

    try {
      await updateUser(user.id, payload, token);
      alert("Data updated. For security, please log in again!");
      logout();
      navigate("/login");
    } catch (error) {
      console.error("Error updating profile: ", error);
      alert("Error updating profile.");
    }
  };

  const handleDelete = async () => {
    const hasItems = cart?.items?.length > 0;
    const confirmationMessage = hasItems
      ? "Você ainda tem itens no carrinho. Deseja mesmo excluir a conta?"
      : "Tem certeza que deseja excluir a conta?";

    if (confirm(confirmationMessage)) {
      try {
        await deleteUser(user.id, token);
        alert("Conta excluída.");
        logout();
        navigate("/");
      } catch (error) {
        console.error(
          "Error deleting account! ",
          error.response?.data || error.message
        );
        alert("Erro ao excluir conta!");
      }
    }
  };

  return (
    <div className="max-w-md mx-auto p-6 bg-white shadow rounded-xl mt-10">
      <h2 className="text-2xl font-bold mb-4 text-center">My Profile</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block">Name</label>
          <input
            type="text"
            className="border rounded w-full p-2"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </div>
        <div>
          <label className="block">Email</label>
          <input
            type="email"
            className="border rounded w-full p-2"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />
        </div>
        <div>
          <label className="block">Password (optional)</label>
          <input
            type="password"
            className="border rounded w-full p-2"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            placeholder="Leave blank to leave unchanged."
            minLength={6}
          />
        </div>
        <div className="flex gap-2">
          <button
            type="submit"
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
          >
            Save
          </button>
          <button
            type="button"
            onClick={handleDelete}
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
          >
            Delete Account
          </button>
        </div>
      </form>
    </div>
  );
};

export default Profile;
