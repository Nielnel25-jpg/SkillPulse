import { Link } from 'react-router-dom';

export default function ForgotPasswordPage() {
  return (
    <div className="container">
      <div className="card">
        <h1>Forgot password?</h1>
        <p className="subtitle">Password recovery</p>
        <div className="alert alert-error" style={{ marginTop: 0 }}>
          🚧 This feature is coming soon. Please contact your administrator to reset your password.
        </div>
        <Link className="btn btn-primary" to="/login" style={{ marginTop: '0.5rem' }}>
          Back to sign in
        </Link>
      </div>
    </div>
  );
}
