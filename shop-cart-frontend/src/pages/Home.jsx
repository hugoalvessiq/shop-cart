import { useEffect, useState } from "react";
import {
  getProducts,
  getCart,
  addToCart,
  removeFromCart,
} from "../api/cartApi";
import ProductList from "../components/ProductList";
import Cart from "../components/Cart";
import CheckoutButton from "../components/CheckoutButton";

function Home() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);

  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchProducts();
  }, []);

  useEffect(() => {
    if (cart !== null) {
      fetchProducts();
    }
  }, [cart]);

  const fetchProducts = () => {
    getProducts().then((res) => setProducts(res.data));
  };

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

  const handleAddToCart = (productId) => {
    addToCart(productId).then(() => fetchCart());
  };

  const handleRemoveFromCart = (productId) => {
    removeFromCart(productId).then(() => fetchCart());
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6">
      <main className="p-4">
        <ProductList products={products} onAddToCart={handleAddToCart} />
        {cart?.items.length ? (
          <Cart
            cart={cart}
            onRemoveFromCart={handleRemoveFromCart}
            onAddToCart={handleAddToCart}
          />
        ) : (
          <h1>🛒 Empty Cart</h1>
        )}
        {cart?.items.length > 0 && (
          <CheckoutButton fetchCart={fetchCart} fetchProducts={fetchProducts} />
        )}
      </main>
    </div>
  );
}

export default Home;
