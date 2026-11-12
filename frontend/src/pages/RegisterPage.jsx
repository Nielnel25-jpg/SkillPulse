import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../api.js';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', role: 'STUDENT',
    studentNumber: '', department: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  async function submit(e) {
    e.preventDefault();
    setError(''); setFieldErrors({});
    setLoading(true);
    try {
      const body = {
        fullName: form.fullName.trim(),
        email: form.email.trim(),
        password: form.password,
        role: form.role,
        ...(form.role === 'STUDENT' ? { studentNumber: form.studentNumber.trim() } : {}),
        ...(form.role === 'INSTRUCTOR' ? { department: form.department.trim() } : {}),
      };
      await api.register(body);
      navigate('/login');
    } catch (err) {
      if (err.fields) setFieldErrors(err.fields);
      else setError(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="container">
      <div className="card">
        <h1>Create account</h1>
        <p className="subtitle">Join SkillPulse</p>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={submit}>
          <div className="form-group">
            <label>Full Name</label>
            <input value={form.fullName} onChange={set('fullName')} required />
            {fieldErrors.fullName && <p className="field-error">{fieldErrors.fullName}</p>}
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={form.email} onChange={set('email')} required />
            {fieldErrors.email && <p className="field-error">{fieldErrors.email}</p>}
          </div>
          <div className="form-group">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <label style={{ margin: 0 }}>Password</label>
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
              required
              minLength={8}
              style={{ marginTop: '0.375rem' }}
            />
            {fieldErrors.password && <p className="field-error">{fieldErrors.password}</p>}
          </div>
          <div className="form-group">
            <label>Role</label>
            <select value={form.role} onChange={set('role')}>
              <option value="STUDENT">Student</option>
              <option value="INSTRUCTOR">Instructor</option>
            </select>
          </div>
          {form.role === 'STUDENT' && (
            <div className="form-group">
              <label>Student Number</label>
              <input value={form.studentNumber} onChange={set('studentNumber')} />
            </div>
          )}
          {form.role === 'INSTRUCTOR' && (
            <div className="form-group">
              <label>Department</label>
              <input value={form.department} onChange={set('department')} />
            </div>
          )}
          <button className="btn btn-primary" disabled={loading}>
            {loading ? 'Creating account…' : 'Create account'}
          </button>
        </form>
        <div className="link-row">
          <span>Already have an account? <Link className="btn btn-ghost" to="/login">Sign in</Link></span>
        </div>
      </div>
    </div>
  );
}
