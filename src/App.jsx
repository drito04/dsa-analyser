import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';

import Navbar           from './components/Navbar';
import ProtectedRoute   from './components/ProtectedRoute';

import Login            from './pages/Login';
import Register         from './pages/Register';
import ProblemList      from './pages/ProblemList';
import ProblemDetail    from './pages/ProblemDetail';
import SubmissionHistory from './pages/SubmissionHistory';
import Home from './pages/Home'
import './styles/globals.css';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="page">
          <Navbar />
          <main style={{ flex: 1 }}>
            <Routes>
              {/* Public */}
              <Route path="/login"    element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Semi-public (problem list & detail visible without login) */}
              <Route path="/problems"     element={<ProblemList />} />
              <Route path="/problems/:id" element={<ProblemDetail />} />

              {/* Protected */}
              <Route path="/history" element={
                <ProtectedRoute><SubmissionHistory /></ProtectedRoute>
              } />

              {/* Default */}
              <Route path="/" element={<Home />} />
              <Route path="/" element={<Navigate to="/" replace />} />
              <Route path="*" element={<Navigate to="/" replace />} />
              
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}
