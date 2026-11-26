const BASE = 'http://localhost:8080/api';

async function request(method, path, body, token) {
  const headers = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;
  const res = await fetch(`${BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });
  const data = await res.json().catch(() => null);
  if (!res.ok) throw { status: res.status, message: data?.message || 'Request failed', fields: data?.fields };
  return data;
}

export const api = {
  register: (body) => request('POST', '/auth/register', body),
  login: (body) => request('POST', '/auth/login', body),
  getMe: (id, token) => request('GET', `/admin/users/${id}`, null, token),
  updateMe: (id, body, token) => request('PUT', `/admin/users/${id}`, body, token),

  // skills
  getSkills: (t) => request('GET', '/skills', null, t),
  createSkill: (body, t) => request('POST', '/skills', body, t),

  // instructor: assessments
  myAssessments: (t) => request('GET', '/assessments/mine', null, t),
  createAssessment: (body, t) => request('POST', '/assessments', body, t),
  getAssessment: (id, t) => request('GET', `/assessments/${id}`, null, t),
  addQuestion: (id, body, t) => request('POST', `/assessments/${id}/questions`, body, t),
  removeQuestion: (id, qid, t) => request('DELETE', `/assessments/${id}/questions/${qid}`, null, t),
  setStatus: (id, status, t) => request('PATCH', `/assessments/${id}/status`, { status }, t),
  deleteAssessment: (id, t) => request('DELETE', `/assessments/${id}`, null, t),
  analytics: (threshold, t) => request('GET', `/instructor/analytics?threshold=${threshold}`, null, t),

  // student
  availableAssessments: (t) => request('GET', '/assessments/available', null, t),
  submit: (id, answers, t) => request('POST', `/assessments/${id}/submit`, { answers }, t),
  myResult: (assessmentId, t) => request('GET', `/student/submissions/assessment/${assessmentId}`, null, t),
  myHistory: (t) => request('GET', '/student/submissions', null, t),
  myMastery: (t) => request('GET', '/student/mastery', null, t),
};
