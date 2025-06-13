import { useEffect, useState } from "react";
import axios from "axios";

const API = "http://localhost:8080/api/products";

const getHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem("token")}`,
});

export default function ProductAdmin() {
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stockQuantity: "",
  });
  const [editing, setEditing] = useState(null);

  const loadProducts = async () => {
    const res = await axios.get(API);
    setProducts(res.data.content || []);
  };

  useEffect(() => {
    loadProducts();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editing) {
        await axios.put(`${API}/${editing.id}`, form, {
          headers: getHeaders(),
        });
      } else {
        await axios.post(API, form, { headers: getHeaders() });
      }
      setForm({
        name: "",
        description: "",
        price: "",
        stockQuantity: "",
      });
      setEditing(null);
      loadProducts();
    } catch (err) {
      console.error("Error saving product:", err);
    }
  };

  const handleDelete = async (id) => {
    if (confirm("Do you want to delete this product?")) {
      try {
        await axios.delete(`${API}/${id}`, { headers: getHeaders() });
        loadProducts();
      } catch (err) {
        console.error("Error saving product:", err);
      }
    }
  };

  const handleEdit = (product) => {
    setEditing(product);
    setForm({
      name: product.name,
      description: product.description,
      price: product.price,
      stockQuantity: product.stockQuantity,
    });
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold text-center mb-4">
        Gerenciar Produtos
      </h1>
      <div className="max-w-md mx-auto mt-5 mb-10 p-6 bg-white shadow-xl border-1 border-gray-200 rounded-xl">
        <form onSubmit={handleSubmit} className="space-y-4 mb-8">
          {editing == null ? (
            <h2 className="ext-2xl font-bold text-center mb-4">Add Product</h2>
          ) : (
            <h2 className="ext-2xl font-bold text-center mb-4">Edit Product</h2>
          )}
          <input
            type="text"
            placeholder="Nome"
            className="w-full border rounded p-2"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
          <input
            type="text"
            placeholder="Descrição"
            className="w-full border rounded p-2"
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            required
          />
          <input
            type="number"
            placeholder="Preço"
            className="w-full border rounded p-2"
            value={form.price}
            onChange={(e) => setForm({ ...form, price: e.target.value })}
            required
          />
          <input
            type="number"
            placeholder="Estoque"
            className="w-full border rounded p-2"
            value={form.stockQuantity}
            onChange={(e) =>
              setForm({ ...form, stockQuantity: e.target.value })
            }
            required
          />
          <div className="flex gap-2">
            <button
              type="submit"
              className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
            >
              {editing ? "Update" : "Add"}
            </button>
            {editing && (
              <button
                type="button"
                onClick={() => {
                  setEditing(null);
                  setForm({
                    name: "",
                    description: "",
                    price: "",
                    stockQuantity: "",
                  });
                }}
                className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
              >
                Cancel
              </button>
            )}
          </div>
        </form>
      </div>

      <div className="m-auto w-4xl">
        <table className="table-auto w-full border">
          <thead className="bg-gray-100">
            <tr>
              <th className="border p-2">ID</th>
              <th className="border p-2">Name</th>
              <th className="border p-2">Price</th>
              <th className="border p-2">Stock</th>
              <th className="border p-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id}>
                <td className="border p-2">{p.id}</td>
                <td className="border p-2">{p.name}</td>
                <td className="border p-2">R$ {p.price}</td>
                <td className="border p-2">{p.stockQuantity}</td>
                <td className="border p-2 flex gap-2">
                  <button
                    onClick={() => handleEdit(p)}
                    className="bg-blue-500 text-white px-2 py-1 rounded hover:bg-blue-600"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(p.id)}
                    className="bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
