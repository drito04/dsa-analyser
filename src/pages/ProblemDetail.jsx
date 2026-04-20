import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import { problemsApi, analysisApi } from '../api/api';
import { useAuth } from '../context/AuthContext';
import './ProblemDetail.css';

const LANGUAGES = ['java', 'python', 'javascript', 'cpp'];

const STARTERS = {
  java:       'public class Solution {\n    public void solve() {\n        // Your code here\n    }\n}',
  python:     'def solve():\n    # Your code here\n    pass',
  javascript: 'function solve() {\n    // Your code here\n}',
  cpp:        '#include <bits/stdc++.h>\nusing namespace std;\n\nvoid solve() {\n    // Your code here\n}',
};

function DiffBadge({ difficulty }) {
  const cls = difficulty?.toLowerCase();
  return <span className={`badge badge-${cls}`}>{difficulty}</span>;
}

export default function ProblemDetail() {
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [problem, setProblem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [language, setLanguage] = useState('java');
  const [code, setCode] = useState(STARTERS['java']);

  const [analyzing, setAnalyzing] = useState(false);
  const [analysis, setAnalysis] = useState(null);
  const [analysisError, setAnalysisError] = useState('');

  useEffect(() => {
    (async () => {
      setLoading(true);
      try {
        const res = await problemsApi.getById(id);
        setProblem(res.data.data);
      } catch {
        setError('Problem not found.');
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  const handleLanguageChange = (lang) => {
    setLanguage(lang);
    setCode(STARTERS[lang]);
    setAnalysis(null);
  };

  const handleAnalyze = async () => {
    if (!isAuthenticated) { navigate('/login'); return; }
    setAnalyzing(true);
    setAnalysisError('');
    setAnalysis(null);
    try {
      const res = await analysisApi.submit({ problemId: Number(id), code, language });
      setAnalysis(res.data.data);
    } catch (err) {
      setAnalysisError(err.response?.data?.message || 'Analysis failed. Please try again.');
    } finally {
      setAnalyzing(false);
    }
  };

  if (loading) return (
    <div className="detail-loading">
      <span className="spinner" />
      <span className="muted">Loading problem…</span>
    </div>
  );

  if (error || !problem) return (
    <div className="detail-error container">
      <div className="alert alert-error">{error || 'Problem not found.'}</div>
      <Link to="/problems" className="btn btn-ghost" style={{ marginTop: 16 }}>← Back to problems</Link>
    </div>
  );

  return (
    <div className="detail-page">
      {/* Left panel — problem statement */}
      <div className="detail-left">
        <div className="detail-top-bar">
          <Link to="/problems" className="back-link muted">← Problems</Link>
          <DiffBadge difficulty={problem.difficulty} />
        </div>

        <h1 className="detail-title">{problem.title}</h1>

        {problem.tags && (
          <div className="detail-tags">
            {problem.tags.split(',').map(t => (
              <span key={t} className="tag">{t.trim()}</span>
            ))}
          </div>
        )}

        <div className="detail-section">
          <h3 className="section-label">Description</h3>
          <p className="detail-description">{problem.description}</p>
        </div>

        {problem.constraints && (
          <div className="detail-section">
            <h3 className="section-label">Constraints</h3>
            <pre className="constraints-block">{problem.constraints}</pre>
          </div>
        )}

        {problem.optimalComplexity && (
          <div className="optimal-hint">
            <span className="muted">Optimal complexity:</span>
            <code className="amber">{problem.optimalComplexity}</code>
          </div>
        )}
      </div>

      {/* Right panel — editor + analysis */}
      <div className="detail-right">
        {/* Language selector */}
        <div className="editor-toolbar">
          <div className="lang-tabs">
            {LANGUAGES.map(lang => (
              <button key={lang}
                className={`lang-tab ${language === lang ? 'active' : ''}`}
                onClick={() => handleLanguageChange(lang)}>
                {lang}
              </button>
            ))}
          </div>
          <button
            className="btn btn-primary analyze-btn"
            onClick={handleAnalyze}
            disabled={analyzing || !code.trim()}>
            {analyzing
              ? <><span className="spinner" /> Analyzing…</>
              : isAuthenticated ? '⚡ Analyze' : '🔒 Login to Analyze'
            }
          </button>
        </div>

        {/* Monaco Editor */}
        <div className="editor-wrapper">
          <Editor
            height="100%"
            language={language === 'cpp' ? 'cpp' : language}
            value={code}
            onChange={(val) => setCode(val || '')}
            theme="vs-dark"
            options={{
              fontSize: 13,
              fontFamily: "'JetBrains Mono', monospace",
              fontLigatures: true,
              lineNumbers: 'on',
              minimap: { enabled: false },
              scrollBeyondLastLine: false,
              wordWrap: 'on',
              padding: { top: 16, bottom: 16 },
              renderLineHighlight: 'line',
              scrollbar: { verticalScrollbarSize: 4 },
            }}
          />
        </div>

        {/* Analysis output */}
        {analysis && (
        <div className="analysis-panel fade-up">
          <div className="analysis-header">
            <span className="analysis-title">⚡ AI Analysis</span>
            <span className="muted" style={{ fontSize: 11 }}>Powered by Gemini</span>
          </div>

          <div className="complexity-row">
            <div className="complexity-box">
              <span className="complexity-label muted">Time Complexity</span>
              <code className="complexity-val amber">{analysis.timeComplexity || '—'}</code>
            </div>
            <div className="complexity-box">
              <span className="complexity-label muted">Space Complexity</span>
              <code className="complexity-val blue">{analysis.spaceComplexity || '—'}</code>
            </div>
            <div className="complexity-box">
              <span className="complexity-label muted">Optimal?</span>
              <span className={`complexity-val ${analysis.isOptimal ? 'green' : 'red'}`}>
                {analysis.isOptimal ? '✓ Yes' : '✗ Not yet'}
              </span>
            </div>
            {/* NEW: optimal complexities */}
            {analysis.optimalTimeComplexity && (
              <div className="complexity-box">
                <span className="complexity-label muted">Optimal Time</span>
                <code className="complexity-val green">{analysis.optimalTimeComplexity}</code>
              </div>
            )}
            {analysis.optimalSpaceComplexity && (
              <div className="complexity-box">
                <span className="complexity-label muted">Optimal Space</span>
                <code className="complexity-val green">{analysis.optimalSpaceComplexity}</code>
              </div>
            )}
          </div>

          {analysis.feedback && (
            <div className="feedback-section">
              <h4 className="section-label">Feedback</h4>
              <div className="feedback-body">{analysis.feedback}</div>
            </div>
          )}

          {/* NEW: hints as bullet points */}
          {analysis.hints && analysis.hints.length > 0 && (
            <div className="feedback-section">
              <h4 className="section-label">Hints</h4>
              <ul className="hints-list">
                {analysis.hints.map((hint, i) => (
                  <li key={i} className="hint-item">
                    <span className="hint-num amber">{i + 1}.</span>
                    <span>{hint}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
      </div>
    </div>
  );
}
