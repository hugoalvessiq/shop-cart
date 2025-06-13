import React, { useEffect, useState } from "react";
import { fetchUsers, updateUser, deleteUser } from "../api/userApi";
import UserTable from "../components/UserTable";
import { Link } from "react-router-dom";

export default function UserAdmin() {
  const token = localStorage.getItem("token");

  const [users, setUsers] = useState([]);
  const [editingUser, setEditingUser] = useState(null);
  const [form, setForm] = useState({ name: "", email: "", password: "" });

  const loadUsers = async () => {
    try {
      const res = await fetchUsers(token);

      setUsers(res.data);
    } catch (error) {
      console.error("Error loading users:", error);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleEdit = (user) => {
    setEditingUser(user);
    setForm({ name: user.name, email: user.email, password: "" });
  };

  const handleDelete = async (id) => {
    if (confirm("Are you sure you want to delete this user?")) {
      try {
        await deleteUser(id, token);
        loadUsers();
      } catch (error) {
        console.error("Error delete users:", error);
      }
    }
  };

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
      await updateUser(editingUser.id, payload, token);
      setEditingUser(null);
      setForm({ name: "", email: "", password: "" });
      loadUsers();
    } catch (error) {
      console.error("Error update user:", error);
    }
  };

  return (
    <div className="p-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold mb-4">Manage users</h1>
        <Link
          to={`/register`}
          className="text-green-500 hover:text-emerald-600"
        >
          Register new user
        </Link>
      </div>

      <UserTable users={users} onEdit={handleEdit} onDelete={handleDelete} />

      {editingUser && (
        <div className="max-w-md mx-auto mt-5 p-6 bg-white shadow-xl border-1 border-gray-200 rounded-xl">
          <div className="mt-1 pt-4">
            <h2 className="text-xl font-semibold mb-2">Editar Usuário</h2>
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
                  onChange={(e) =>
                    setForm({ ...form, password: e.target.value })
                  }
                  placeholder="Leave blank to not change"
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
                  onClick={() => setEditingUser(null)}
                  className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
