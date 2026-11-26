import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api.js';
import { getUser } from '../auth.js';
import Layout from '../components/Layout.jsx';
import MasteryBars from '../components/MasteryBars.jsx';

function StudentView({ user }) {
  const [mastery, setMastery] = useState(null);
  const [available, setAvailable] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([api.myMastery(user.token), api.availableAssessments(user.token)])
      .then(([m, a]) => { setMastery(m); setAvailable(a); })
      .catch((e) => setError(e.message));
  }, [user.token]);

  return (
    <>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="panel">
        <h2>Your skill mastery <span className="muted">(weakest first)</span></h2>
        {mastery ? <MasteryBars items={mastery} /> : <p className="muted">Loading…</p>}
      </div>
      <div className="panel">
        <h2>Available assessments</h2>
        {!available ? <p className="muted">Loading…</p> : available.length === 0 ? <p className="muted">Nothing published yet.</p> :
          available.map((a) => (
            <div className="list-item row-between" key={a.id}>
              <div>
                <strong>{a.title}</strong>
                <div className="muted">{a.questionCount} questions · {a.instructorName}{a.dueAt ? ` · due ${new Date(a.dueAt).toLocaleString()}` : ''}</div>
              </div>
              {a.submitted
                ? <Link className="link-btn" to={`/results/${a.id}`}>View result</Link>
                : <Link className="btn btn-primary btn-sm" to={`/take/${a.id}`}>Start</Link>}
            </div>
          ))}
      </div>
    </>
  );
}

function InstructorView({ user }) {
  const [list, setList] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([api.myAssessments(user.token), api.analytics(60, user.token)])
      .then(([l, a]) => { setList(l); setAnalytics(a); })
      .catch((e) => setError(e.message));
  }, [user.token]);

  return (
    <>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="grid-2">
        <div className="panel"><div className="muted">Assessments</div><div className="stat">{list?.length ?? '–'}</div></div>
        <div className="panel"><div className="muted">Submissions received</div><div className="stat">{analytics?.submissions ?? '–'}</div></div>
      </div>
      <div className="panel">
        <div className="row-between"><h2>Weakest topics across your class</h2><Link className="link-btn" to="/analytics">Full analytics →</Link></div>
        {analytics ? <MasteryBars items={analytics.skills.slice(0, 5)} /> : <p className="muted">Loading…</p>}
      </div>
    </>
  );
}

export default function DashboardPage() {
  const user = getUser();
  return (
    <Layout>
      <p className="page-title">Welcome, {user?.fullName} <span className="badge">{user?.role}</span></p>
      {user?.role === 'STUDENT' && <StudentView user={user} />}
      {user?.role === 'INSTRUCTOR' && <InstructorView user={user} />}
      {user?.role === 'ADMIN' && <div className="panel"><p className="muted">Admin tools (user management, skill taxonomy) are available through the API; UI coming next.</p></div>}
    </Layout>
  );
}
