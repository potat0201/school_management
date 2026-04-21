const overviewEl = document.getElementById('overview');
const studentBody = document.getElementById('studentBody');
const classBody = document.getElementById('classBody');
const studentForm = document.getElementById('studentForm');
const messageEl = document.getElementById('message');

async function fetchJson(url, options = {}) {
  const res = await fetch(url, options);
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    throw new Error(err.error || err.message || 'Có lỗi xảy ra');
  }
  return res.json();
}

async function loadOverview() {
  const data = await fetchJson('/api/dashboard/overview');
  overviewEl.innerHTML = Object.entries(data)
    .map(([key, value]) => `<div class="card"><h3>${key}</h3><p>${value}</p></div>`)
    .join('');
}

async function loadStudents() {
  const students = await fetchJson('/api/students');
  studentBody.innerHTML = students
    .map(s => `<tr><td>${s.studentCode || ''}</td><td>${s.name || ''}</td><td>${s.email || ''}</td><td>${s.phone || ''}</td></tr>`)
    .join('');
}

async function loadClasses() {
  const classes = await fetchJson('/api/classes');
  classBody.innerHTML = classes
    .map(c => `<tr><td>${c.classCode || ''}</td><td>${c.subjectId || ''}</td><td>${c.teacherId || ''}</td><td>${c.semesterId || ''}</td></tr>`)
    .join('');
}

studentForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  messageEl.textContent = '';

  const payload = {
    studentCode: document.getElementById('studentCode').value.trim(),
    name: document.getElementById('studentName').value.trim(),
    email: document.getElementById('studentEmail').value.trim(),
    phone: document.getElementById('studentPhone').value.trim(),
  };

  try {
    await fetchJson('/api/students', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    messageEl.textContent = 'Thêm sinh viên thành công';
    messageEl.style.color = '#166534';
    studentForm.reset();
    await Promise.all([loadOverview(), loadStudents()]);
  } catch (err) {
    messageEl.textContent = err.message;
    messageEl.style.color = '#b91c1c';
  }
});

(async function init() {
  try {
    await Promise.all([loadOverview(), loadStudents(), loadClasses()]);
  } catch (err) {
    messageEl.textContent = err.message;
    messageEl.style.color = '#b91c1c';
  }
})();
