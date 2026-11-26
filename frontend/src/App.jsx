import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import ForgotPasswordPage from './pages/ForgotPasswordPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import EditAccountPage from './pages/EditAccountPage.jsx';
import AssessmentsPage from './pages/AssessmentsPage.jsx';
import AssessmentBuilderPage from './pages/AssessmentBuilderPage.jsx';
import AnalyticsPage from './pages/AnalyticsPage.jsx';
import TakeAssessmentPage from './pages/TakeAssessmentPage.jsx';
import ResultPage from './pages/ResultPage.jsx';
import HistoryPage from './pages/HistoryPage.jsx';
import { getUser } from './auth.js';

function PrivateRoute({ children, role }) {
  const user = getUser();
  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to="/dashboard" replace />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/dashboard" element={<PrivateRoute><DashboardPage /></PrivateRoute>} />
      <Route path="/account" element={<PrivateRoute><EditAccountPage /></PrivateRoute>} />
      <Route path="/assessments" element={<PrivateRoute role="INSTRUCTOR"><AssessmentsPage /></PrivateRoute>} />
      <Route path="/assessments/:id" element={<PrivateRoute role="INSTRUCTOR"><AssessmentBuilderPage /></PrivateRoute>} />
      <Route path="/analytics" element={<PrivateRoute role="INSTRUCTOR"><AnalyticsPage /></PrivateRoute>} />
      <Route path="/take/:id" element={<PrivateRoute role="STUDENT"><TakeAssessmentPage /></PrivateRoute>} />
      <Route path="/results/:id" element={<PrivateRoute role="STUDENT"><ResultPage /></PrivateRoute>} />
      <Route path="/history" element={<PrivateRoute role="STUDENT"><HistoryPage /></PrivateRoute>} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
