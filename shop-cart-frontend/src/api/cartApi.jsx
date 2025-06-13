import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

export const getProducts = () => axios.get(`${BASE_URL}/products`);

export const getCart = () =>
  axios.get(`${BASE_URL}/cart`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

export const updateCart = (productId, quantity) =>
  axios.post(`${BASE_URL}/cart/add/${productId}?quantity=${quantity}`, null, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

export const removeOneFromCart = (productId) =>
  axios.delete(`${BASE_URL}/cart/remove/${productId}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

export const addToCart = (productId) =>
  axios.post(`${BASE_URL}/cart/add/${productId}?quantity=1`, null, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });

export const removeFromCart = (productId) =>
  axios.delete(`${BASE_URL}/cart/remove/${productId}`, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });
