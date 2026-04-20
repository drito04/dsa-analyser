import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { problemsApi } from '../api/api';
import './ProblemList.css';

const DIFFICULTIES = ['ALL', 'EASY', 'MEDIUM', 'HARD'];
const TAGS = ['ALL', 'arrays', 'strings', 'dp', 'trees', 'graphs', 'sorting', 'binary-search', 'linked-list', 'recursion'];

function DiffBadge({ difficulty }) {
  const cls = difficulty?.toLowerCase();
  return <span className={`badge badge-${cls}`}>{difficulty}</span>;
}

export default function ProblemList() {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [difficulty, setDifficulty] = useState('ALL');
  const [tag, setTag] = useState('ALL');
  const [search, setSearch] = useState('');

  useEffect(() => {
    const fetch = async () => {
      setLoading(true);
      setError('');
      try {
        const params = {};
        if (difficulty !== 'ALL') params.difficulty = difficulty;
        if (tag !== 'ALL') params.tag = tag;
        const res = await problemsApi.getAll(params);
        setProblems(res.data.data || []);
      } catch {
        setError('Failed to load problems. Is the backend running?');
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [difficulty, tag]);

  const filtered = problems.filter(p =>
    p.title.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="problems-page fade-up">
      <div className="container">
        {/* Header */}
        <div className="problems-header">
          <div>
            <h1 className="problems-title">
              <span className="amber">{">"}</span> Problem Set
            </h1>
            <p className="problems-sub muted">
              {loading ? '…' : `${filtered.length} problem${filtered.length !== 1 ? 's' : ''}`} available
            </p>
          </div>
        </div>

        {/* Filters */}
        <div className="filters-bar">
          <input
            className="input-field search-input"
            placeholder="Search problems…"
            value={search}
            onChange={e => setSearch(e.target.value)}
          />

          <div className="filter-group">
            <span className="filter-label muted">Difficulty:</span>
            {DIFFICULTIES.map(d => (
              <button key={d}
                className={`filter-btn ${difficulty === d ? 'active' : ''}`}
                onClick={() => setDifficulty(d)}>
                {d}
              </button>
            ))}
          </div>

          <div className="filter-group">
            <span className="filter-label muted">Tag:</span>
            <select className="input-field tag-select"
              value={tag} onChange={e => setTag(e.target.value)}>
              {TAGS.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
        </div>

        {/* Content */}
        {error && <div className="alert alert-error">{error}</div>}

        {loading ? (
          <div className="problems-loading">
            <span className="spinner" />
            <span className="muted">Loading problems…</span>
          </div>
        ) : filtered.length === 0 ? (
          <div className="problems-empty">
            <span className="muted">No problems match your filters.</span>
          </div>
        ) : (
          <div className="problems-table">
            {/* Table head */}
            <div className="table-head">
              <span>#</span>
              <span>Title</span>
              <span>Difficulty</span>
              <span>Tags</span>
              <span>Optimal</span>
            </div>

            {/* Rows */}
            {filtered.map((p, idx) => (
              <Link to={`/problems/${p.id}`} key={p.id} className="table-row card">
                <span className="row-num muted">{idx + 1}</span>
                <span className="row-title">{p.title}</span>
                <span><DiffBadge difficulty={p.difficulty} /></span>
                <span className="row-tags">
                  {p.tags?.split(',').slice(0, 3).map(t => (
                    <span key={t} className="tag">{t.trim()}</span>
                  ))}
                </span>
                <span className="row-complexity muted">{p.optimalComplexity || '—'}</span>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
