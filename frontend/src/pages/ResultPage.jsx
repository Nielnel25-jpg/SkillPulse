import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';

export default function ResultPage() {
  const { id } = useParams();
  const user = getUser();
  const [result, setResult] = useState(null);
  const [assessment, setAssessment] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([api.myResult(id, user.token), api.getAssessment(id, user.token)])
      .then(([r, a]) => { setResult(r); setAssessment(a); })
      .catch((e) => setError(e.message));
  }, [id, user.token]);

  if (!result) return <Layout>{error ? <div className="alert alert-error">{error}</div> : <p className="muted">Loading…</p>}</Layout>;
  const optionsOf = (qid) => assessment?.questions.find((q) => q.id === qid)?.options ?? [];

  return (
    <Layout>
      <p className="page-title">{result.assessmentTitle}</p>
      <div className="panel">
        <div className="muted">Your score</div>
        <div className="stat">{result.score} / {result.maxScore} <span className="muted">({result.percent}%)</span></div>
      </div>
      {result.review.map((r, n) => (
        <div className="panel" key={r.questionId}>
          <p style={{ marginBottom: '0.6rem' }}><strong>{n + 1}. {r.text}</strong> <span className="badge badge-gray">{r.skillName}</span></p>
          {optionsOf(r.questionId).map((o, i) => {
            const cls = i === r.correctIndex ? 'correct' : i === r.selectedIndex ? 'wrong' : '';
            return <div key={i} className={`option ${cls}`} style={{ cursor: 'default' }}>{o}{i === r.selectedIndex ? ' (your answer)' : ''}</div>;
          })}
          {r.selectedIndex === null && <p className="muted">Not answered.</p>}
        </div>
      ))}
      <Link className="link-btn" to="/dashboard">← Back to dashboard</Link>
    </Layout>
  );
}
