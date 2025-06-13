import React from "react";

function Cart({ cart, onAddToCart, onRemoveFromCart }) {
  if (!cart) return null;

  return (
    <div className="max-w-4xl mx-auto my-10">
      <h2 className="text-2xl font-bold mb-6">Carrinho</h2>
      <ul className="space-y-4">
        {cart.items
          .slice()
          .sort((a, b) => a.productName.localeCompare(b.productName))
          .map((item) => (
            <li
              key={item.productId}
              className="flex justify-between items-center p-4 border rounded-lg shadow"
            >
              <div>
                <p className="font-semibold">{item.productName}</p>
                <p className="text-gray-600">Quantity: {item.quantity}</p>
              </div>
              <div className="flex">
                <button
                  onClick={() => onRemoveFromCart(item.productId)}
                  className="bg-red-500 hover:bg-red-600 text-white mr-3.5 px-3 rounded cursor-pointer"
                >
                  -
                </button>
                <span className="font-medium mr-3.5">{item.quantity}</span>
                <button
                  onClick={() => onAddToCart(item.productId)}
                  className="bg-blue-500 hover:bg-blue-600 text-white px-3 rounded cursor-pointer"
                >
                  +
                </button>
              </div>
            </li>
          ))}
      </ul>
    </div>
  );
}

export default Cart;
