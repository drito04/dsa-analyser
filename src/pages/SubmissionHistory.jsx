import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { submissionsApi } from '../api/api';
import './SubmissionHistory.css';

function formatDate(ts) {
  return new Date(ts).toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

export default function SubmissionHistory() {
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [expanded, setExpanded] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const res = await submissionsApi.getMine();
        setSubmissions(res.data.data || []);
      } catch {
        setError('Failed to load submission history.');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
    <div className="history-page fade-up">
      <div className="container">
        <div className="history-header">
          <h1 className="history-title">
            <span className="amber">{">"}</span> My Submissions
          </h1>
          <p className="muted">{submissions.length} total submission{submissions.length !== 1 ? 's' : ''}</p>
        </div>

        {error && <div className="alert alert-error">{error}</div>}

        {loading ? (
          <div className="history-loading">
            <span className="spinner" />
            <span className="muted">Loading history…</span>
          </div>
        ) : submissions.length === 0 ? (
          <div className="history-empty card">
            <p className="muted">No submissions yet.</p>
            <Link to="/problems" className="btn btn-primary" style={{ marginTop: 16 }}>
              → Start solving
            </Link>
          </div>
        ) : (
          <div className="history-list">
            {submissions.map((s) => (
              <div key={s.id} className="submission-card card">
                <div className="sub-header" onClick={() => setExpanded(expanded === s.id ? null : s.id)}>
                  <div className="sub-left">
                    <span className="sub-id muted">#{s.id}</span>
                    <Link
                      to={`/problems/${s.problemId}`}
                      className="sub-title"
                      onClick={e => e.stopPropagation()}>
                      {s.problemTitle || `Problem #${s.problemId}`}
                    </Link>
                    <span className="tag">{s.language}</span>
                  </div>
                  <div className="sub-right">
                    <code className="sub-complexity amber">{s.timeComplexity}</code>
                    <code className="sub-complexity blue">{s.spaceComplexity}</code>
                    <span className="sub-date muted">{formatDate(s.submittedAt)}</span>
                    <span className="expand-icon muted">{expanded === s.id ? '▲' : '▼'}</span>
                  </div>
                </div>

                {expanded === s.id && (
                  <div className="sub-body fade-up">
                    {s.code && (
                      <div className="sub-section">
                        <span className="section-label">Code submitted</span>
                        <pre className="sub-code">{s.code}</pre>
                      </div>
                    )}
                    {s.feedback && (
                      <div className="sub-section">
                        <span className="section-label">AI Feedback</span>
                        <p className="sub-feedback">{s.feedback}</p>
                      </div>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
