import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ username: '', email: '', password: '', confirm: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (form.password !== form.confirm) { setError('Passwords do not match.'); return; }
    if (form.password.length < 6) { setError('Password must be at least 6 characters.'); return; }

    setLoading(true);
    try {
      await register(form.username, form.email, form.password);
      navigate('/login', { state: { registered: true } });
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card fade-up">
        <div className="auth-header">
          <div className="auth-icon">✦</div>
          <h1 className="auth-title">Create account</h1>
          <p className="auth-sub">Start your DSA journey today</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {error && <div className="alert alert-error">{error}</div>}

          <div className="input-group">
            <label className="input-label">Username</label>
            <input className="input-field" type="text" name="username" placeholder="coderX"
              value={form.username} onChange={handleChange} required autoFocus />
          </div>

          <div className="input-group">
            <label className="input-label">Email</label>
            <input className="input-field" type="email" name="email" placeholder="you@example.com"
              value={form.email} onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label className="input-label">Password</label>
            <input className="input-field" type="password" name="password" placeholder="Min. 6 characters"
              value={form.password} onChange={handleChange} required />
          </div>

          <div className="input-group">
            <label className="input-label">Confirm Password</label>
            <input className="input-field" type="password" name="confirm" placeholder="Repeat password"
              value={form.confirm} onChange={handleChange} required />
          </div>

          <button type="submit" className="btn btn-primary auth-submit" disabled={loading}>
            {loading ? <><span className="spinner" /> Creating account…</> : '→ Create account'}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account?{' '}
          <Link to="/login" className="auth-link">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
