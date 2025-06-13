import { checkoutOrder } from "../api/orderApi";
import { useNavigate } from "react-router-dom";

export default function CheckoutButton({ fetchCart, fetchProducts }) {
  const navigate = useNavigate();

  const handleCheckout = async () => {
    try {
      await checkoutOrder();
      alert("Order completed successfully!");
      fetchCart();
      fetchProducts();
      navigate("/");
    } catch (error) {
      alert(error.response?.data);
    }
  };

  return (
    <button
      onClick={handleCheckout}
      className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
    >
      Finalize Order
    </button>
  );
}
