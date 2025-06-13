import React from "react";
import { useNavigate } from "react-router-dom";

function ProductList({ products, onAddToCart }) {
  const navigate = useNavigate();

  const handleAddToCart = (productId) => {
    const token = localStorage.getItem("token");

    if (!token) {
      navigate("/login");
      return;
    }

    try {
      onAddToCart(productId, token);
    } catch (error) {
      console.error("Error adding to cart ", error);
    }
  };

  return (
    <div className="max-w-4xl mx-auto my-10">
      <h1 className="text-3xl font-bold mb-6">Products</h1>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        {products.content &&
          products.content.map((p) => (
            <div
              key={p.id}
              className="border rounded-lg p-4 shadow hover:shadow-lg transition"
            >
              <h2 className="text-xl font-semibold">{p.name}</h2>
              <p className="text-gray-600">R$ {p.price.toFixed(2)}</p>
              <p
                className={`${
                  p.stockQuantity === 0
                    ? "text-red-500"
                    : p.stockQuantity <= 5
                    ? "text-yellow-500"
                    : "text-green-600"
                }`}
              >
                {p.stockQuantity === 0
                  ? "Unavailable"
                  : p.stockQuantity <= 5
                  ? `Only ${p.stockQuantity} left in stock`
                  : `In stock: ${p.stockQuantity}`}
              </p>
              <button
                disabled={p.stockQuantity === 0}
                onClick={() => handleAddToCart(p.id)}
                className={`mt-4 w-full ${
                  p.stockQuantity === 0
                    ? "bg-gray-400 cursor-not-allowed"
                    : "bg-blue-500 hover:bg-blue-600"
                } text-white py-2 px-4 rounded`}
              >
                {p.stockQuantity === 0 ? "Unavailable" : "Add"}
              </button>
            </div>
          ))}
      </div>
    </div>
  );
}

export default ProductList;
