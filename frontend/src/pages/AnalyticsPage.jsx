import { useEffect, useState } from 'react';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';
import MasteryBars from '../components/MasteryBars.jsx';

function toCsv(students) {
  const rows = [['Student', 'Points earned', 'Points possible', 'Percent', 'At risk'],
    ...students.map((s) => [s.fullName, s.earned, s.possible, s.percent, s.atRisk ? 'yes' : 'no'])];
  return rows.map((r) => r.map((c) => `"${String(c).replace(/"/g, '""')}"`).join(',')).join('\n');
}

export default function AnalyticsPage() {
  const user = getUser();
  const [threshold, setThreshold] = useState(60);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    api.analytics(threshold, user.token).then(setData).catch((e) => setError(e.message));
  }, [threshold, user.token]);

  function exportCsv() {
    const url = URL.createObjectURL(new Blob([toCsv(data.students)], { type: 'text/csv' }));
    const a = document.createElement('a');
    a.href = url; a.download = 'class-performance.csv'; a.click();
    URL.revokeObjectURL(url);
  }

  return (
    <Layout>
      <p className="page-title">Class analytics</p>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="panel">
        <h2>Topics ranked weakest → strongest</h2>
        {data ? <MasteryBars items={data.skills} /> : <p className="muted">Loading…</p>}
      </div>
      <div className="panel">
        <div className="row-between" style={{ marginBottom: '1rem' }}>
          <h2 style={{ margin: 0 }}>Students</h2>
          <div className="row">
            <label className="muted">At-risk below <input className="input" style={{ width: 70 }} type="number" min="0" max="100" value={threshold} onChange={(e) => setThreshold(Number(e.target.value))} />%</label>
            <button className="btn btn-ghost btn-sm" disabled={!data?.students.length} onClick={exportCsv}>Export CSV</button>
          </div>
        </div>
        {!data ? <p className="muted">Loading…</p> : data.students.length === 0 ? <p className="muted">No submissions yet.</p> : data.students.map((s) => (
          <div className="list-item row-between" key={s.studentId}>
            <strong>{s.fullName}</strong>
            <div className="row"><span className="muted">{s.earned}/{s.possible}</span><span className={`badge ${s.atRisk ? 'badge-red' : 'badge-green'}`}>{s.percent}%{s.atRisk ? ' · at risk' : ''}</span></div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
