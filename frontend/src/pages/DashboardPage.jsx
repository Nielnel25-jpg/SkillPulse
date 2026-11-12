import { Link, useNavigate } from 'react-router-dom';
import { getUser, logout } from '../auth.js';

export default function DashboardPage() {
  const navigate = useNavigate();
  const user = getUser();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <>
      <nav className="nav">
        <a href="/dashboard" className="brand">SkillPulse</a>
        <div className="nav-links">
          <Link to="/account">Edit Account</Link>
          <button className="btn btn-ghost" onClick={handleLogout}>Logout</button>
        </div>
      </nav>
      <div className="page-content">
        <p className="page-title">Dashboard</p>
        <div className="card">
          <p style={{ marginBottom: '1rem', fontWeight: 600 }}>Your Profile</p>
          <div className="info-row">
            <span className="label">Name</span>
            <span>{user?.fullName}</span>
          </div>
          <div className="info-row">
            <span className="label">Email</span>
            <span>{user?.email}</span>
          </div>
          <div className="info-row">
            <span className="label">Role</span>
            <span className="badge">{user?.role}</span>
          </div>
          <hr className="divider" />
          <p style={{ color: 'var(--muted)', fontSize: '0.875rem' }}>
            More features coming soon. This is the main application area.
          </p>
        </div>
      </div>
    </>
  );
}
