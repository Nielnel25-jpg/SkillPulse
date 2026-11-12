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
};
