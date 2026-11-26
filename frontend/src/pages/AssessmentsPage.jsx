import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';

const STATUS_CLASS = { DRAFT: 'badge-gray', PUBLISHED: 'badge-green', CLOSED: 'badge-red' };

export default function AssessmentsPage() {
  const user = getUser();
  const [list, setList] = useState(null);
  const [form, setForm] = useState({ title: '', description: '', dueAt: '', timeLimitMinutes: '' });
  const [error, setError] = useState('');

  const load = () => api.myAssessments(user.token).then(setList).catch((e) => setError(e.message));
  useEffect(() => { load(); }, []); // eslint-disable-line react-hooks/exhaustive-deps

  async function create(e) {
    e.preventDefault();
    setError('');
    try {
      await api.createAssessment({
        title: form.title,
        description: form.description || null,
        dueAt: form.dueAt ? new Date(form.dueAt).toISOString() : null,
        timeLimitMinutes: form.timeLimitMinutes ? Number(form.timeLimitMinutes) : null,
      }, user.token);
      setForm({ title: '', description: '', dueAt: '', timeLimitMinutes: '' });
      load();
    } catch (err) { setError(err.fields ? Object.values(err.fields).join(', ') : err.message); }
  }

  async function remove(id) {
    try { await api.deleteAssessment(id, user.token); load(); } catch (err) { setError(err.message); }
  }

  return (
    <Layout>
      <p className="page-title">Assessments</p>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="panel">
        <h2>New assessment</h2>
        <form onSubmit={create}>
          <div className="form-group"><label>Title</label>
            <input className="input" required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></div>
          <div className="form-group"><label>Description</label>
            <input className="input" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></div>
          <div className="row">
            <div className="form-group"><label>Due (optional)</label>
              <input className="input" type="datetime-local" value={form.dueAt} onChange={(e) => setForm({ ...form, dueAt: e.target.value })} /></div>
            <div className="form-group"><label>Time limit, minutes (optional)</label>
              <input className="input" type="number" min="1" value={form.timeLimitMinutes} onChange={(e) => setForm({ ...form, timeLimitMinutes: e.target.value })} /></div>
          </div>
          <button className="btn btn-primary btn-sm" type="submit">Create draft</button>
        </form>
      </div>
      <div className="panel">
        <h2>Your assessments</h2>
        {!list ? <p className="muted">Loading…</p> : list.length === 0 ? <p className="muted">None yet.</p> : list.map((a) => (
          <div className="list-item row-between" key={a.id}>
            <div>
              <strong>{a.title}</strong> <span className={`badge ${STATUS_CLASS[a.status]}`}>{a.status}</span>
              <div className="muted">{a.questionCount} questions{a.dueAt ? ` · due ${new Date(a.dueAt).toLocaleString()}` : ''}</div>
            </div>
            <div className="row">
              <Link className="link-btn" to={`/assessments/${a.id}`}>{a.status === 'DRAFT' ? 'Edit' : 'View'}</Link>
              {a.status === 'DRAFT' && <button className="btn btn-danger btn-sm" onClick={() => remove(a.id)}>Delete</button>}
            </div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
