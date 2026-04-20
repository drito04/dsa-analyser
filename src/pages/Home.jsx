import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Home.css';

const FEATURES = [
  {
    icon: '◈',
    title: 'Curated Problem Set',
    desc: '1800+ DSA problems across arrays, trees, graphs, DP and more — organized by difficulty.',
  },
  {
    icon: '⌨',
    title: 'In-Browser Code Editor',
    desc: 'Write solutions in Java, Python, JavaScript or C++ inside a full Monaco editor.',
  },
  {
    icon: '⚡',
    title: 'AI-Powered Analysis',
    desc: 'Get time & space complexity breakdown plus guided hints — never a direct spoiler.',
  },
  {
    icon: '◎',
    title: 'Track Your Progress',
    desc: 'Every submission is saved. Review your past code and AI feedback any time.',
  },
];

export default function Home() {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="home-page">
      {/* ── Hero ─────────────────────────────────────────── */}
      <section className="hero">
        <div className="hero-glow" />

        <div className="container hero-inner">
          <div className="hero-eyebrow">
            <span className="eyebrow-dot" />
            AI-Powered DSA Learning
          </div>

          <h1 className="hero-title">
            Sharpen your<br />
            <span className="hero-title-accent">algorithmic thinking.</span>
          </h1>

          <p className="hero-sub">
            Solve curated DSA problems, submit your code, and let Gemini AI
            evaluate your complexity and guide you toward the optimal solution —
            without giving away the answer.
          </p>

          <div className="hero-cta">
            {isAuthenticated ? (
              <>
                <Link to="/problems" className="btn btn-primary cta-primary">
                  → Browse Problems
                </Link>
                <Link to="/history" className="btn btn-ghost cta-secondary">
                  My Submissions
                </Link>
              </>
            ) : (
              <>
                <Link to="/register" className="btn btn-primary cta-primary">
                  → Get started free
                </Link>
                <Link to="/login" className="btn btn-ghost cta-secondary">
                  Sign in
                </Link>
              </>
            )}
          </div>

          {/* Pill stats */}
          <div className="hero-stats">
            <div className="stat-pill"><span className="amber">1800+</span> Problems</div>
            <div className="stat-divider" />
            <div className="stat-pill"><span className="amber">4</span> Languages</div>
            <div className="stat-divider" />
            <div className="stat-pill"><span className="amber">3</span> Difficulty levels</div>
            <div className="stat-divider" />
            <div className="stat-pill"><span className="green">Gemini</span> AI</div>
          </div>
        </div>
      </section>

      {/* ── Code preview strip ────────────────────────────── */}
      <section className="code-strip">
        <div className="container">
          <div className="code-window">
            <div className="code-titlebar">
              <span className="dot red" />
              <span className="dot amber" />
              <span className="dot green" />
              <span className="code-filename muted">Solution.java</span>
            </div>
            <pre className="code-body">
<span className="tok-keyword">public class</span> <span className="tok-class">Solution</span> {"{"}
  <span className="tok-keyword">public int</span> <span className="tok-fn">maxProfit</span>(<span className="tok-keyword">int</span>[] prices) {"{"}
    <span className="tok-keyword">int</span> minPrice = <span className="tok-num">Integer</span>.MAX_VALUE;
    <span className="tok-keyword">int</span> maxProfit = <span className="tok-num">0</span>;
    <span className="tok-keyword">for</span> (<span className="tok-keyword">int</span> price : prices) {"{"}
      minPrice = <span className="tok-num">Math</span>.min(minPrice, price);
      maxProfit = <span className="tok-num">Math</span>.max(maxProfit, price - minPrice);
    {"}"}
    <span className="tok-keyword">return</span> maxProfit;
  {"}"}
{"}"}
            </pre>
            <div className="analysis-strip">
              <span className="analysis-chip amber">Time: O(n)</span>
              <span className="analysis-chip blue">Space: O(1)</span>
              <span className="analysis-chip green">✓ Optimal</span>
            </div>
          </div>
        </div>
      </section>

      {/* ── Features grid ────────────────────────────────── */}
      <section className="features">
        <div className="container">
          <div className="section-header">
            <span className="section-tag muted">What you get</span>
            <h2 className="section-title">Everything you need to level up</h2>
          </div>

          <div className="features-grid">
            {FEATURES.map((f) => (
              <div key={f.title} className="feature-card card">
                <div className="feature-icon">{f.icon}</div>
                <h3 className="feature-title">{f.title}</h3>
                <p className="feature-desc muted">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA banner ───────────────────────────────────── */}
      <section className="cta-banner">
        <div className="container cta-banner-inner">
          <div>
            <h2 className="cta-banner-title">Ready to start solving?</h2>
            <p className="muted" style={{ marginTop: 6 }}>
              {isAuthenticated
                ? `Welcome back, ${user.username}. Your problems are waiting.`
                : 'Create a free account and begin your first problem in under a minute.'}
            </p>
          </div>
          <div className="cta-banner-actions">
            {isAuthenticated ? (
              <Link to="/problems" className="btn btn-primary">→ Browse Problems</Link>
            ) : (
              <>
                <Link to="/register" className="btn btn-primary">→ Create account</Link>
                <Link to="/login" className="btn btn-ghost">Sign in</Link>
              </>
            )}
          </div>
        </div>
      </section>
    </div>
  );
}
