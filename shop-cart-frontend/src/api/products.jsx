import axios from "axios";

export const fetchProducts = () =>
  axios.get("http://localhost:8080/api/products");
