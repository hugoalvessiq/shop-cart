import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Navbar from "./components/Navbar";
import PrivateRoute from "./components/PrivateRoute";
import { AuthProvider } from "./context/AuthProvider";
import Register from "./pages/Register";
import UserAdmin from "./components/UserAdmin";
import ProductAdmin from "./components/product/ProductAdmin";
import Profile from "./pages/Profile";

function App() {
  return (
    <AuthProvider>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Protect route to ADMIN */}
        <Route
          path="/admin"
          element={
            <PrivateRoute>
              <div>Admin Page</div>
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/products"
          element={
            <PrivateRoute requiredRole="ADMIN">
              <ProductAdmin />
            </PrivateRoute>
          }
        />

        <Route
          path="/perfil"
          element={
            <PrivateRoute>
              <Profile />
            </PrivateRoute>
          }
        />

        {/* Protected route for any authenticated user */}
        <Route
          path="/perfil"
          element={
            <PrivateRoute>
              <div>My Account</div>
            </PrivateRoute>
          }
        />

        {/* Route to User Administration Panel */}
        <Route
          path="/admin/users"
          element={
            <PrivateRoute>
              <UserAdmin />
            </PrivateRoute>
          }
        />

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </AuthProvider>
  );
}

export default App;
