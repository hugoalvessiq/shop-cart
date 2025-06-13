import { Link, useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";

const Navbar = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="bg-blue-600 text-white p-4 flex justify-between items-center">
      <h1 className="text-xl font-bold">
        <Link to="/">🛒 SuperMall</Link>
      </h1>
      <div className="flex gap-4 items-center">
        <span>
          Welcome,{" "}
          <Link to={`/perfil`}>{user ? user.name : "Visitor"}! ⚙️</Link>
        </span>
        {isAuthenticated ? (
          <>
            {user?.role === "ADMIN" && (
              <>
                <Link
                  to="/admin/users"
                  className="bg-yellow-500 px-3 py-1 rounded hover:bg-yellow-600"
                >
                  Users
                </Link>
                <Link
                  to="/admin/products"
                  className="bg-yellow-500 px-3 py-1 rounded hover:bg-yellow-600"
                >
                  Products
                </Link>
              </>
            )}
            <button
              onClick={handleLogout}
              className="bg-red-500 px-3 py-1 rounded hover:bg-red-600 cursor-pointer"
            >
              Logout
            </button>
          </>
        ) : (
          <Link
            to="/login"
            className="bg-green-500 px-3 py-1 rounded hover:bg-green-600"
          >
            Login
          </Link>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
