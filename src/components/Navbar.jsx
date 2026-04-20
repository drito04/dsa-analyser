import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const { pathname } = useLocation();

  const handleLogout = () => { logout(); navigate('/login'); };

  return (
    <nav className="navbar">
      <div className="container navbar-inner">
        {/* Logo */}
        <Link to="/" className="navbar-logo">
          <span className="logo-bracket">[</span>
          <span className="logo-text">DSA</span>
          <span className="logo-dot">·</span>
          <span className="logo-sub">analyzer</span>
          <span className="logo-bracket">]</span>
        </Link>

        {/* Nav links */}
        <div className="navbar-links">
          <Link to="/problems" className={`nav-link ${pathname.startsWith('/problems') ? 'active' : ''}`}>
            Problems
          </Link>
          {isAuthenticated && (
            <Link to="/history" className={`nav-link ${pathname === '/history' ? 'active' : ''}`}>
              History
            </Link>
          )}
        </div>

        {/* Right side */}
        <div className="navbar-right">
          {isAuthenticated ? (
            <>
              <span className="nav-user">
                <span className="user-dot" />
                {user.username}
              </span>
              <button className="btn btn-ghost" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login"  className="btn btn-ghost">Login</Link>
              <Link to="/register" className="btn btn-primary">Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
