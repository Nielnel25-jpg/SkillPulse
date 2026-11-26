import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';

export default function HistoryPage() {
  const user = getUser();
  const [rows, setRows] = useState(null);
  const [from, setFrom] = useState('');
  const [to, setTo] = useState('');
  const [error, setError] = useState('');

  useEffect(() => { api.myHistory(user.token).then(setRows).catch((e) => setError(e.message)); }, [user.token]);

  const filtered = useMemo(() => (rows ?? []).filter((r) => {
    const d = r.submittedAt.slice(0, 10);
    return (!from || d >= from) && (!to || d <= to);
  }), [rows, from, to]);

  return (
    <Layout>
      <p className="page-title">Assessment history</p>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="panel">
        <div className="row" style={{ marginBottom: '1rem' }}>
          <label className="muted">From <input className="input" type="date" value={from} onChange={(e) => setFrom(e.target.value)} /></label>
          <label className="muted">To <input className="input" type="date" value={to} onChange={(e) => setTo(e.target.value)} /></label>
        </div>
        {!rows ? <p className="muted">Loading…</p> : filtered.length === 0 ? <p className="muted">No submissions.</p> : filtered.map((r) => (
          <div className="list-item row-between" key={r.id}>
            <div><strong>{r.assessmentTitle}</strong><div className="muted">{new Date(r.submittedAt).toLocaleString()}</div></div>
            <div className="row"><span className="badge">{r.score}/{r.maxScore} · {r.percent}%</span><Link className="link-btn" to={`/results/${r.assessmentId}`}>Review</Link></div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
