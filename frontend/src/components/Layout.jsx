import { Link, useNavigate } from 'react-router-dom';
import { getUser, logout } from '../auth.js';

export default function Layout({ children }) {
  const navigate = useNavigate();
  const user = getUser();

  return (
    <>
      <nav className="nav">
        <Link to="/dashboard" className="brand">SkillPulse</Link>
        <div className="nav-links">
          <Link to="/dashboard">Dashboard</Link>
          {user?.role === 'INSTRUCTOR' && <Link to="/assessments">Assessments</Link>}
          {user?.role === 'INSTRUCTOR' && <Link to="/analytics">Class analytics</Link>}
          {user?.role === 'STUDENT' && <Link to="/history">History</Link>}
          <Link to="/account">Edit Account</Link>
          <button className="btn btn-ghost" onClick={() => { logout(); navigate('/login'); }}>Logout</button>
        </div>
      </nav>
      <div className="page-wide">{children}</div>
    </>
  );
}
