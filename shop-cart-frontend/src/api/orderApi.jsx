import axios from "axios";
import React from "react";

const API = "http://localhost:8080/api/orders";

export const checkoutOrder = async () => {
  return await axios.post(`${API}/checkout`, null, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem("token")}`,
    },
  });
};
