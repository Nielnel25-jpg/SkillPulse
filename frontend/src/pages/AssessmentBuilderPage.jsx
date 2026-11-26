import { useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';

const BLANK = { text: '', options: ['', ''], correctIndex: 0, points: 1, skillId: '' };

export default function AssessmentBuilderPage() {
  const { id } = useParams();
  const user = getUser();
  const [assessment, setAssessment] = useState(null);
  const [skills, setSkills] = useState([]);
  const [q, setQ] = useState(BLANK);
  const [newSkill, setNewSkill] = useState('');
  const [error, setError] = useState('');

  const load = useCallback(() => {
    Promise.all([api.getAssessment(id, user.token), api.getSkills(user.token)])
      .then(([a, s]) => { setAssessment(a); setSkills(s); })
      .catch((e) => setError(e.message));
  }, [id, user.token]);
  useEffect(() => { load(); }, [load]);

  const guard = (fn) => async (...args) => {
    setError('');
    try { await fn(...args); } catch (err) { setError(err.fields ? Object.values(err.fields).join(', ') : err.message); }
  };

  const addSkill = guard(async (e) => {
    e.preventDefault();
    const s = await api.createSkill({ name: newSkill }, user.token);
    setNewSkill('');
    setSkills((prev) => [...prev, s].sort((a, b) => a.name.localeCompare(b.name)));
    setQ((prev) => ({ ...prev, skillId: s.id }));
  });

  const addQuestion = guard(async (e) => {
    e.preventDefault();
    const a = await api.addQuestion(id, {
      text: q.text, options: q.options, correctIndex: q.correctIndex,
      points: Number(q.points) || 1, skillId: Number(q.skillId),
    }, user.token);
    setAssessment(a);
    setQ({ ...BLANK, skillId: q.skillId });
  });

  const removeQuestion = guard(async (qid) => setAssessment(await api.removeQuestion(id, qid, user.token)));
  const setStatus = guard(async (status) => setAssessment(await api.setStatus(id, status, user.token)));

  const setOption = (i, v) => setQ({ ...q, options: q.options.map((o, j) => (j === i ? v : o)) });
  const addOption = () => q.options.length < 6 && setQ({ ...q, options: [...q.options, ''] });
  const dropOption = (i) => q.options.length > 2 && setQ({
    ...q, options: q.options.filter((_, j) => j !== i),
    correctIndex: q.correctIndex === i ? 0 : q.correctIndex > i ? q.correctIndex - 1 : q.correctIndex,
  });

  if (!assessment) return <Layout>{error ? <div className="alert alert-error">{error}</div> : <p className="muted">Loading…</p>}</Layout>;
  const draft = assessment.status === 'DRAFT';

  return (
    <Layout>
      <div className="row-between">
        <p className="page-title">{assessment.title} <span className="badge">{assessment.status}</span></p>
        <div className="row">
          {draft && <button className="btn btn-primary btn-sm" onClick={() => setStatus('PUBLISHED')}>Publish</button>}
          {assessment.status === 'PUBLISHED' && <button className="btn btn-ghost btn-sm" onClick={() => setStatus('CLOSED')}>Close</button>}
        </div>
      </div>
      {error && <div className="alert alert-error">{error}</div>}

      <div className="panel">
        <h2>Questions ({assessment.questions.length})</h2>
        {assessment.questions.length === 0 && <p className="muted">No questions yet.</p>}
        {assessment.questions.map((qq, n) => (
          <div className="list-item" key={qq.id}>
            <div className="row-between">
              <strong>{n + 1}. {qq.text}</strong>
              {draft && <button className="btn btn-danger btn-sm" onClick={() => removeQuestion(qq.id)}>Remove</button>}
            </div>
            <div className="muted">Skill: {qq.skillName} · {qq.points} pt{qq.points > 1 ? 's' : ''}</div>
            <ul style={{ margin: '0.4rem 0 0 1.2rem', fontSize: '0.875rem' }}>
              {qq.options.map((o, i) => <li key={i} style={i === qq.correctIndex ? { color: 'var(--success)', fontWeight: 600 } : undefined}>{o}</li>)}
            </ul>
          </div>
        ))}
      </div>

      {draft && (
        <div className="grid-2">
          <div className="panel">
            <h2>Add question</h2>
            <form onSubmit={addQuestion}>
              <div className="form-group"><label>Question</label>
                <input className="input" required value={q.text} onChange={(e) => setQ({ ...q, text: e.target.value })} /></div>
              <label className="muted">Options (select the correct one)</label>
              {q.options.map((o, i) => (
                <div className="row" key={i} style={{ marginBottom: '0.4rem' }}>
                  <input type="radio" name="correct" checked={q.correctIndex === i} onChange={() => setQ({ ...q, correctIndex: i })} aria-label={`Option ${i + 1} is correct`} />
                  <input className="input" style={{ flex: 1, width: 'auto' }} required value={o} onChange={(e) => setOption(i, e.target.value)} />
                  {q.options.length > 2 && <button type="button" className="btn btn-ghost btn-sm" onClick={() => dropOption(i)}>✕</button>}
                </div>
              ))}
              {q.options.length < 6 && <button type="button" className="btn btn-ghost btn-sm" onClick={addOption}>+ option</button>}
              <div className="row" style={{ marginTop: '1rem' }}>
                <div className="form-group" style={{ flex: 1 }}><label>Skill / topic</label>
                  <select className="input" required value={q.skillId} onChange={(e) => setQ({ ...q, skillId: e.target.value })}>
                    <option value="">Select…</option>
                    {skills.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
                  </select></div>
                <div className="form-group" style={{ width: 90 }}><label>Points</label>
                  <input className="input" type="number" min="1" value={q.points} onChange={(e) => setQ({ ...q, points: e.target.value })} /></div>
              </div>
              <button className="btn btn-primary btn-sm" type="submit">Add question</button>
            </form>
          </div>
          <div className="panel">
            <h2>New skill / topic</h2>
            <form onSubmit={addSkill} className="row">
              <input className="input" style={{ flex: 1, width: 'auto' }} required placeholder="e.g. Quadratic equations" value={newSkill} onChange={(e) => setNewSkill(e.target.value)} />
              <button className="btn btn-primary btn-sm" type="submit">Add</button>
            </form>
            <p className="muted" style={{ marginTop: '0.75rem' }}>Every question is tagged with a skill; mastery is computed per skill.</p>
          </div>
        </div>
      )}
    </Layout>
  );
}
