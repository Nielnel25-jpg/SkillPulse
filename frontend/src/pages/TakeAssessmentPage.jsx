import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';

export default function TakeAssessmentPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const user = getUser();
  const [assessment, setAssessment] = useState(null);
  const [picked, setPicked] = useState({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [secondsLeft, setSecondsLeft] = useState(null);
  const submitRef = useRef(null);

  useEffect(() => {
    api.getAssessment(id, user.token).then((a) => {
      if (a.submitted) { navigate(`/results/${id}`, { replace: true }); return; }
      setAssessment(a);
      if (a.timeLimitMinutes) setSecondsLeft(a.timeLimitMinutes * 60);
    }).catch((e) => setError(e.message));
  }, [id, user.token, navigate]);

  async function submit() {
    setBusy(true);
    setError('');
    try {
      const answers = assessment.questions.map((q) => ({ questionId: q.id, selectedIndex: picked[q.id] ?? null }));
      await api.submit(id, answers, user.token);
      navigate(`/results/${id}`, { replace: true });
    } catch (err) { setError(err.message); setBusy(false); }
  }
  useEffect(() => { submitRef.current = submit; });

  // Client-side countdown; auto-submits at zero.
  useEffect(() => {
    if (secondsLeft === null) return;
    if (secondsLeft <= 0) { submitRef.current(); return; }
    const t = setTimeout(() => setSecondsLeft((s) => s - 1), 1000);
    return () => clearTimeout(t);
  }, [secondsLeft]);

  if (!assessment) return <Layout>{error ? <div className="alert alert-error">{error}</div> : <p className="muted">Loading…</p>}</Layout>;
  const mm = secondsLeft !== null ? `${Math.floor(secondsLeft / 60)}:${String(secondsLeft % 60).padStart(2, '0')}` : null;

  return (
    <Layout>
      <div className="row-between">
        <p className="page-title">{assessment.title}</p>
        {mm && <span className="badge">Time left {mm}</span>}
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      {assessment.questions.map((q, n) => (
        <div className="panel" key={q.id}>
          <p style={{ marginBottom: '0.75rem' }}><strong>{n + 1}. {q.text}</strong> <span className="muted">({q.points} pt{q.points > 1 ? 's' : ''})</span></p>
          {q.options.map((o, i) => (
            <label key={i} className={`option ${picked[q.id] === i ? 'selected' : ''}`}>
              <input type="radio" name={`q${q.id}`} checked={picked[q.id] === i} onChange={() => setPicked({ ...picked, [q.id]: i })} />
              {o}
            </label>
          ))}
        </div>
      ))}
      <button className="btn btn-primary" style={{ width: 'auto' }} disabled={busy} onClick={submit}>
        {busy ? 'Submitting…' : `Submit (${Object.keys(picked).length}/${assessment.questions.length} answered)`}
      </button>
    </Layout>
  );
}
