import axios from "axios";

const API = "http://localhost:8080/api/users";

const getAuthHeader = (token) => ({
  headers: {
    Authorization: `Bearer ${token}`,
  },
});

export const fetchUsers = (token) =>
  axios.get(`${API}/getall`, getAuthHeader(token));

export const createUser = (payload, token) =>
  axios.post(`${API}/create`, payload, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });

export const updateUser = (id, data, token) =>
  axios.put(`${API}/${id}`, data, getAuthHeader(token));

export const deleteUser = (id, token) =>
  axios.delete(`${API}/${id}`, getAuthHeader(token));

export const fetchUserProfile = async (token) => {
  const response = await axios.get(`${API}/profile`, getAuthHeader(token));
  return response.data;
};

export const loginUser = async (email, password) => {
  const response = await axios.post("http://localhost:8080/api/auth/login", {
    email,
    password,
  });
  return response.data;
};
