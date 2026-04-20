import axios from 'axios';

// ── Base instance ──────────────────────────────────────────────
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT to every request if present
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Global response error handler — auto-logout on 401
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// ── Auth endpoints ─────────────────────────────────────────────
export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login:    (data) => api.post('/auth/login', data),
};

// ── Problems endpoints ─────────────────────────────────────────
export const problemsApi = {
  getAll:   (params) => api.get('/problems', { params }),       // ?difficulty=EASY&tag=dp
  getById:  (id)     => api.get(`/problems/${id}`),
};

// ── Analysis endpoint ──────────────────────────────────────────
export const analysisApi = {
  submit: (data) => api.post('/analyze', data),
  // data: { problemId, code, language }
};

// ── Submissions endpoint ───────────────────────────────────────
export const submissionsApi = {
  getMine: () => api.get('/submissions/me'),
};

export default api;
