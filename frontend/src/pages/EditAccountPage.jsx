import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api.js';
import { getUser, saveUser, logout } from '../auth.js';

export default function EditAccountPage() {
  const navigate = useNavigate();
  const user = getUser();

  const [form, setForm] = useState({
    fullName: user?.fullName || '',
    email: user?.email || '',
    password: '',
    studentNumber: '',
    department: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user?.id) return;
    let isMounted = true;
    api.getMe(user.id, user.token)
      .then((data) => {
        if (!isMounted || !data) return;
        setForm((prev) => ({
          ...prev,
          fullName: data.fullName || prev.fullName,
          email: data.email || prev.email,
          studentNumber: data.studentNumber || '',
          department: data.department || '',
        }));
      })
      .catch(() => { });
    return () => { isMounted = false; };
  }, [user?.id, user?.token]);

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  async function submit(e) {
    e.preventDefault();
    setError(''); setFieldErrors({}); setSuccess('');
    setLoading(true);
    try {
      const body = {};
      if (form.fullName && form.fullName.trim() !== user.fullName) {
        body.fullName = form.fullName.trim();
      }
      if (form.email && form.email.trim().toLowerCase() !== user.email?.toLowerCase()) {
        body.email = form.email.trim();
      }
      if (form.password) {
        body.password = form.password;
      }
      if (user?.role === 'STUDENT' && form.studentNumber !== undefined) {
        body.studentNumber = form.studentNumber.trim();
      }
      if (user?.role === 'INSTRUCTOR' && form.department !== undefined) {
        body.department = form.department.trim();
      }

      const updated = await api.updateMe(user.id, body, user.token);
      saveUser({
        ...user,
        fullName: updated.fullName,
        email: updated.email,
      });
      setForm((f) => ({ ...f, password: '' }));
      setSuccess('Account updated successfully!');
    } catch (err) {
      if (err.fields) setFieldErrors(err.fields);
      else setError(err.message || 'Update failed');
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() { logout(); navigate('/login'); }

  return (
    <>
      <nav className="nav">
        <a href="/dashboard" className="brand">SkillPulse</a>
        <div className="nav-links">
          <Link to="/dashboard">Dashboard</Link>
          <button className="btn btn-ghost" onClick={handleLogout}>Logout</button>
        </div>
      </nav>
      <div className="page-content">
        <p className="page-title">Edit Account</p>
        <div className="card">
          {error && <div className="alert alert-error">{error}</div>}
          {success && <div className="alert alert-success">{success}</div>}
          <form onSubmit={submit}>
            <div className="form-group">
              <label>Full Name</label>
              <input value={form.fullName} onChange={set('fullName')} />
              {fieldErrors.fullName && <p className="field-error">{fieldErrors.fullName}</p>}
            </div>
            <div className="form-group">
              <label>Email</label>
              <input type="email" value={form.email} onChange={set('email')} />
              {fieldErrors.email && <p className="field-error">{fieldErrors.email}</p>}
            </div>
            <div className="form-group">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <label style={{ margin: 0 }}>
                  New Password <span style={{ color: 'var(--muted)', fontWeight: 400 }}>(leave blank to keep)</span>
                </label>
                <button
                  type="button"
                  className="btn btn-ghost"
                  style={{ fontSize: '0.75rem' }}
                  onClick={() => setShowPassword((s) => !s)}
                >
                  {showPassword ? 'Hide' : 'Show'}
                </button>
              </div>
              <input
                type={showPassword ? 'text' : 'password'}
                value={form.password}
                onChange={set('password')}
                minLength={8}
                style={{ marginTop: '0.375rem' }}
              />
              {fieldErrors.password && <p className="field-error">{fieldErrors.password}</p>}
            </div>
            {user?.role === 'STUDENT' && (
              <div className="form-group">
                <label>Student Number</label>
                <input value={form.studentNumber} onChange={set('studentNumber')} />
              </div>
            )}
            {user?.role === 'INSTRUCTOR' && (
              <div className="form-group">
                <label>Department</label>
                <input value={form.department} onChange={set('department')} />
              </div>
            )}
            <button className="btn btn-primary" disabled={loading}>
              {loading ? 'Saving…' : 'Save changes'}
            </button>
          </form>
        </div>
      </div>
    </>
  );
}
