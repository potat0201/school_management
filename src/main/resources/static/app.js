/* =============================================
   SCHOOL MANAGEMENT - FULL SPA (Vanilla JS)
   ============================================= */

// ---------- STATE ----------
const cache = {}; // subjects, teachers, semesters, classes, students
const store = new Map(); // item store keyed by _id for onclick refs
let _sid = 0;
function put(item) { const k = '_' + (_sid++); store.set(k, item); return k; }
function get(k) { return store.get(k); }

// ---------- API ----------
async function api(method, path, body) {
  const h = { 'Content-Type': 'application/json' };
  const tok = localStorage.getItem('token');
  if (tok) h['Authorization'] = 'Bearer ' + tok;
  const opts = { method, headers: h };
  if (body !== undefined && body !== null) opts.body = JSON.stringify(body);
  const res = await fetch('/api' + path, opts);
  if (res.status === 401) { doLogout(); throw new Error('Phiên hết hạn, vui lòng đăng nhập lại'); }
  if (res.status === 204) return null;
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || data.message || 'Lỗi server');
  return data;
}

// ---------- TOAST ----------
function toast(msg, type = 'success') {
  const el = document.createElement('div');
  el.className = 'toast toast-' + type;
  el.textContent = msg;
  document.getElementById('toastWrap').appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

// ---------- MODAL ----------
function modal(title, html) {
  document.getElementById('modalTitle').textContent = title;
  document.getElementById('modalBody').innerHTML = html;
  document.getElementById('modalOverlay').style.display = 'flex';
}
function closeModal() { document.getElementById('modalOverlay').style.display = 'none'; }

// ---------- HELPERS ----------
function esc(s) {
  return String(s ?? '').replace(/[&<>"']/g, c =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}
function fmtDate(d) { return d ? new Date(d).toLocaleDateString('vi-VN') : ''; }
function toISO(d) { return d ? new Date(d).toISOString() : null; }
function toDateInput(d) { return d ? new Date(d).toISOString().split('T')[0] : ''; }

// ---------- NAVIGATION ----------
let curSection = 'dashboard';
function nav(section) {
  document.querySelectorAll('.section').forEach(s => s.style.display = 'none');
  document.getElementById(section + 'Section').style.display = 'block';
  document.querySelectorAll('.nav-link').forEach(l => l.classList.toggle('active', l.dataset.section === section));
  curSection = section;
  ({ dashboard: loadDashboard, users: loadUsers, students: loadStudents,
     teachers: loadTeachers, subjects: loadSubjects, semesters: loadSemesters,
     classes: loadClasses, enrollments: loadEnrollmentsPage, attendance: loadAttendancePage
  })[section]?.();
}

// ---------- AUTH ----------
async function doLogin(username, password) {
  const data = await api('POST', '/auth/login', { username, password });
  localStorage.setItem('token', data.token);
  localStorage.setItem('username', data.username);
  localStorage.setItem('role', data.role || '');
  showApp();
}
function doLogout() {
  ['token', 'username', 'role'].forEach(k => localStorage.removeItem(k));
  document.getElementById('mainApp').style.display = 'none';
  document.getElementById('loginScreen').style.display = 'flex';
}
function showApp() {
  document.getElementById('loginScreen').style.display = 'none';
  document.getElementById('mainApp').style.display = 'flex';
  document.getElementById('sidebarUser').textContent =
    (localStorage.getItem('username') || '') + ' [' + (localStorage.getItem('role') || '') + ']';
  nav('dashboard');
}

// ---------- DASHBOARD ----------
async function loadDashboard() {
  try {
    const d = await api('GET', '/dashboard/overview');
    const labels = { students: 'Sinh viên', teachers: 'Giảng viên', subjects: 'Môn học',
      semesters: 'Học kỳ', classes: 'Lớp học', enrollments: 'Đăng ký', attendanceBuckets: 'Sổ điểm danh' };
    document.getElementById('dashboardCards').innerHTML = Object.entries(d).map(([k, v]) =>
      `<div class="stat-card"><div class="stat-value">${v}</div><div class="stat-label">${labels[k] || k}</div></div>`
    ).join('');
  } catch (e) { toast(e.message, 'error'); }
}

// ---------- USERS ----------
async function loadUsers() {
  try {
    const list = await api('GET', '/auth/users');
    document.getElementById('usersBody').innerHTML = list.length ? list.map(u => {
      const k = put(u);
      const roleLabel = u.role === 'ROLE_ADMIN' ? 'Admin' : u.role === 'ROLE_TEACHER' ? 'Giảng viên' : 'Sinh viên';
      const activeLabel = u.isActive !== false
        ? '<span style="color:#16a34a">● Hoạt động</span>'
        : '<span style="color:#dc2626">● Đã khóa</span>';
      const createdAt = u.createdAt ? new Date(u.createdAt).toLocaleDateString('vi-VN') : '—';
      return `<tr>
        <td>${esc(u.username)}</td>
        <td>${esc(roleLabel)}</td>
        <td>${activeLabel}</td>
        <td>${createdAt}</td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="toggleUserActive('${k}')">${u.isActive !== false ? 'Khóa' : 'Mở khóa'}</button>
          <button class="btn btn-sm btn-danger" onclick="delUser('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="5" class="empty">Chưa có tài khoản nào</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
async function toggleUserActive(k) {
  const u = get(k);
  if (!confirm(`${u.isActive !== false ? 'Khóa' : 'Mở khóa'} tài khoản "${u.username}"?`)) return;
  try {
    await api('PATCH', `/auth/users/${u.id}/toggle-active`);
    toast('Cập nhật trạng thái thành công');
    loadUsers();
  } catch (err) { toast(err.message, 'error'); }
}
async function delUser(k) {
  const u = get(k);
  if (!confirm(`Xóa tài khoản "${u.username}"?`)) return;
  try {
    await api('DELETE', `/auth/users/${u.id}`);
    toast('Đã xóa tài khoản');
    loadUsers();
  } catch (err) { toast(err.message, 'error'); }
}
function showAddUserModal() {
  modal('Thêm tài khoản', `
    <form onsubmit="submitAddUser(event)">
      <div class="field"><label>Tên đăng nhập *</label><input name="username" required autocomplete="off"></div>
      <div class="field"><label>Mật khẩu *</label><input name="password" type="password" required autocomplete="new-password"></div>
      <div class="field"><label>Vai trò</label>
        <select name="role">
          <option value="ROLE_ADMIN">ROLE_ADMIN</option>
          <option value="ROLE_TEACHER">ROLE_TEACHER</option>
          <option value="ROLE_STUDENT" selected>ROLE_STUDENT</option>
        </select>
      </div>
      <div class="field"><label>Profile ID (tùy chọn)</label><input name="profileId" placeholder="ID sinh viên hoặc giảng viên"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Tạo tài khoản</button>
      </div>
    </form>`);
}
async function submitAddUser(e) {
  e.preventDefault();
  const fd = new FormData(e.target);
  const data = Object.fromEntries(fd);
  try {
    await api('POST', '/auth/register', data);
    toast('Tạo tài khoản thành công'); closeModal();
    loadUsers();
  } catch (err) { toast(err.message, 'error'); }
}

// ---------- STUDENTS ----------
async function loadStudents() {
  try {
    const list = await api('GET', '/students');
    cache.students = list;
    document.getElementById('studentsBody').innerHTML = list.length ? list.map(s => {
      const k = put(s);
      return `<tr>
        <td>${esc(s.studentCode)}</td><td>${esc(s.name)}</td>
        <td>${esc(s.email)}</td><td>${esc(s.phone||'')}</td>
        <td><strong>${(s.gpa||0).toFixed(2)}</strong></td><td>${s.totalCredits||0}</td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="editStudentModal('${k}')">Sửa</button>
          <button class="btn btn-sm btn-danger" onclick="delStudent('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="7" class="empty">Chưa có sinh viên</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
function addStudentModal() {
  modal('Thêm sinh viên', `
    <form onsubmit="submitStudent(event)">
      <div class="field"><label>Mã sinh viên *</label><input name="studentCode" required></div>
      <div class="field"><label>Họ tên *</label><input name="name" required></div>
      <div class="field"><label>Email *</label><input name="email" type="email" required></div>
      <div class="field"><label>Số điện thoại</label><input name="phone"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Thêm</button>
      </div>
    </form>`);
}
function editStudentModal(k) {
  const s = get(k);
  modal('Sửa sinh viên', `
    <form onsubmit="submitUpdateStudent(event,'${s.id}','${esc(s.studentCode)}')">
      <div class="field"><label>Mã sinh viên</label><input value="${esc(s.studentCode)}" disabled></div>
      <div class="field"><label>Họ tên *</label><input name="name" value="${esc(s.name)}" required></div>
      <div class="field"><label>Email *</label><input name="email" type="email" value="${esc(s.email)}" required></div>
      <div class="field"><label>Số điện thoại</label><input name="phone" value="${esc(s.phone||'')}"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Lưu</button>
      </div>
    </form>`);
}
async function submitStudent(e) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  try { await api('POST', '/students', d); toast('Thêm sinh viên thành công'); closeModal(); loadStudents(); }
  catch (err) { toast(err.message, 'error'); }
}
async function submitUpdateStudent(e, id, code) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  d.studentCode = code;
  try { await api('PUT', '/students/' + id, d); toast('Cập nhật thành công'); closeModal(); loadStudents(); }
  catch (err) { toast(err.message, 'error'); }
}
async function delStudent(k) {
  const s = get(k);
  if (!confirm('Xóa sinh viên "' + s.name + '"?')) return;
  try { await api('DELETE', '/students/' + s.id); toast('Đã xóa'); loadStudents(); }
  catch (err) { toast(err.message, 'error'); }
}

// ---------- TEACHERS ----------
async function loadTeachers() {
  try {
    const list = await api('GET', '/teachers');
    cache.teachers = list;
    document.getElementById('teachersBody').innerHTML = list.length ? list.map(t => {
      const k = put(t);
      return `<tr>
        <td>${esc(t.teacherCode)}</td><td>${esc(t.name)}</td>
        <td>${esc(t.email||'')}</td><td>${esc(t.department||'')}</td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="editTeacherModal('${k}')">Sửa</button>
          <button class="btn btn-sm btn-danger" onclick="delTeacher('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="5" class="empty">Chưa có giảng viên</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
function addTeacherModal() {
  modal('Thêm giảng viên', `
    <form onsubmit="submitTeacher(event)">
      <div class="field"><label>Mã giảng viên *</label><input name="teacherCode" required></div>
      <div class="field"><label>Họ tên *</label><input name="name" required></div>
      <div class="field"><label>Email *</label><input name="email" type="email" required></div>
      <div class="field"><label>Khoa/Bộ môn</label><input name="department"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Thêm</button>
      </div>
    </form>`);
}
function editTeacherModal(k) {
  const t = get(k);
  modal('Sửa giảng viên', `
    <form onsubmit="submitUpdateTeacher(event,'${t.id}','${esc(t.teacherCode)}')">
      <div class="field"><label>Mã giảng viên</label><input value="${esc(t.teacherCode)}" disabled></div>
      <div class="field"><label>Họ tên *</label><input name="name" value="${esc(t.name)}" required></div>
      <div class="field"><label>Email *</label><input name="email" type="email" value="${esc(t.email||'')}" required></div>
      <div class="field"><label>Khoa/Bộ môn</label><input name="department" value="${esc(t.department||'')}"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Lưu</button>
      </div>
    </form>`);
}
async function submitTeacher(e) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  try { await api('POST', '/teachers', d); toast('Thêm giảng viên thành công'); closeModal(); loadTeachers(); }
  catch (err) { toast(err.message, 'error'); }
}
async function submitUpdateTeacher(e, id, code) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  d.teacherCode = code;
  try { await api('PUT', '/teachers/' + id, d); toast('Cập nhật thành công'); closeModal(); loadTeachers(); }
  catch (err) { toast(err.message, 'error'); }
}
async function delTeacher(k) {
  const t = get(k);
  if (!confirm('Xóa giảng viên "' + t.name + '"?')) return;
  try { await api('DELETE', '/teachers/' + t.id); toast('Đã xóa'); loadTeachers(); }
  catch (err) { toast(err.message, 'error'); }
}

// ---------- SUBJECTS ----------
async function loadSubjects() {
  try {
    const list = await api('GET', '/subjects');
    cache.subjects = list;
    document.getElementById('subjectsBody').innerHTML = list.length ? list.map(s => {
      const k = put(s);
      return `<tr>
        <td>${esc(s.subjectCode)}</td><td>${esc(s.name)}</td>
        <td>${s.credits}</td><td>${esc(s.department||'')}</td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="editSubjectModal('${k}')">Sửa</button>
          <button class="btn btn-sm btn-danger" onclick="delSubject('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="5" class="empty">Chưa có môn học</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
function addSubjectModal() {
  modal('Thêm môn học', `
    <form onsubmit="submitSubject(event)">
      <div class="field"><label>Mã môn học *</label><input name="subjectCode" required></div>
      <div class="field"><label>Tên môn học *</label><input name="name" required></div>
      <div class="field"><label>Số tín chỉ *</label><input name="credits" type="number" min="1" max="10" required></div>
      <div class="field"><label>Khoa</label><input name="department"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Thêm</button>
      </div>
    </form>`);
}
function editSubjectModal(k) {
  const s = get(k);
  modal('Sửa môn học', `
    <form onsubmit="submitUpdateSubject(event,'${s.id}','${esc(s.subjectCode)}')">
      <div class="field"><label>Mã môn học</label><input value="${esc(s.subjectCode)}" disabled></div>
      <div class="field"><label>Tên môn học *</label><input name="name" value="${esc(s.name)}" required></div>
      <div class="field"><label>Số tín chỉ *</label><input name="credits" type="number" value="${s.credits}" min="1" max="10" required></div>
      <div class="field"><label>Khoa</label><input name="department" value="${esc(s.department||'')}"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Lưu</button>
      </div>
    </form>`);
}
async function submitSubject(e) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  d.credits = parseInt(d.credits);
  try { await api('POST', '/subjects', d); toast('Thêm môn học thành công'); closeModal(); loadSubjects(); }
  catch (err) { toast(err.message, 'error'); }
}
async function submitUpdateSubject(e, id, code) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  d.subjectCode = code; d.credits = parseInt(d.credits);
  try { await api('PUT', '/subjects/' + id, d); toast('Cập nhật thành công'); closeModal(); loadSubjects(); }
  catch (err) { toast(err.message, 'error'); }
}
async function delSubject(k) {
  const s = get(k);
  if (!confirm('Xóa môn học "' + s.name + '"?')) return;
  try { await api('DELETE', '/subjects/' + s.id); toast('Đã xóa'); loadSubjects(); }
  catch (err) { toast(err.message, 'error'); }
}

// ---------- SEMESTERS ----------
async function loadSemesters() {
  try {
    const list = await api('GET', '/semesters');
    cache.semesters = list;
    document.getElementById('semestersBody').innerHTML = list.length ? list.map(s => {
      const k = put(s);
      return `<tr>
        <td>${esc(s.semesterCode)}</td><td>${esc(s.name||'')}</td>
        <td>${fmtDate(s.startDate)}</td><td>${fmtDate(s.endDate)}</td>
        <td><span class="badge ${s.isRegistrationOpen ? 'badge-success' : 'badge-gray'}">${s.isRegistrationOpen ? 'Mở' : 'Đóng'}</span></td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="editSemesterModal('${k}')">Sửa</button>
          <button class="btn btn-sm btn-danger" onclick="delSemester('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="6" class="empty">Chưa có học kỳ</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
function addSemesterModal() {
  modal('Thêm học kỳ', `
    <form onsubmit="submitSemester(event)">
      <div class="field"><label>Mã học kỳ *</label><input name="semesterCode" required></div>
      <div class="field"><label>Tên học kỳ</label><input name="name"></div>
      <div class="field"><label>Ngày bắt đầu</label><input name="startDate" type="date"></div>
      <div class="field"><label>Ngày kết thúc</label><input name="endDate" type="date"></div>
      <div class="field checkbox-field"><label><input type="checkbox" name="regOpen"> Mở đăng ký</label></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Thêm</button>
      </div>
    </form>`);
}
function editSemesterModal(k) {
  const s = get(k);
  modal('Sửa học kỳ', `
    <form onsubmit="submitUpdateSemester(event,'${s.id}','${esc(s.semesterCode)}')">
      <div class="field"><label>Mã học kỳ</label><input value="${esc(s.semesterCode)}" disabled></div>
      <div class="field"><label>Tên học kỳ</label><input name="name" value="${esc(s.name||'')}"></div>
      <div class="field"><label>Ngày bắt đầu</label><input name="startDate" type="date" value="${toDateInput(s.startDate)}"></div>
      <div class="field"><label>Ngày kết thúc</label><input name="endDate" type="date" value="${toDateInput(s.endDate)}"></div>
      <div class="field checkbox-field"><label><input type="checkbox" name="regOpen" ${s.isRegistrationOpen ? 'checked' : ''}> Mở đăng ký</label></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Lưu</button>
      </div>
    </form>`);
}
function buildSemesterPayload(fd, form) {
  const d = Object.fromEntries(fd);
  d.isRegistrationOpen = form.querySelector('[name="regOpen"]').checked;
  if (d.startDate) d.startDate = toISO(d.startDate);
  if (d.endDate) d.endDate = toISO(d.endDate);
  delete d.regOpen;
  return d;
}
async function submitSemester(e) {
  e.preventDefault();
  const d = buildSemesterPayload(new FormData(e.target), e.target);
  try { await api('POST', '/semesters', d); toast('Thêm học kỳ thành công'); closeModal(); loadSemesters(); }
  catch (err) { toast(err.message, 'error'); }
}
async function submitUpdateSemester(e, id, code) {
  e.preventDefault();
  const d = buildSemesterPayload(new FormData(e.target), e.target);
  d.semesterCode = code;
  try { await api('PUT', '/semesters/' + id, d); toast('Cập nhật thành công'); closeModal(); loadSemesters(); }
  catch (err) { toast(err.message, 'error'); }
}
async function delSemester(k) {
  const s = get(k);
  if (!confirm('Xóa học kỳ "' + s.semesterCode + '"?')) return;
  try { await api('DELETE', '/semesters/' + s.id); toast('Đã xóa'); loadSemesters(); }
  catch (err) { toast(err.message, 'error'); }
}

// ---------- CLASSES ----------
async function ensureDropdowns() {
  if (!cache.subjects) cache.subjects = await api('GET', '/subjects');
  if (!cache.teachers) cache.teachers = await api('GET', '/teachers');
  if (!cache.semesters) cache.semesters = await api('GET', '/semesters');
}
function lblSubject(id) { const s = (cache.subjects||[]).find(x=>x.id===id); return s ? s.subjectCode+' - '+s.name : id; }
function lblTeacher(id) { const t = (cache.teachers||[]).find(x=>x.id===id); return t ? t.teacherCode+' - '+t.name : id; }
function lblSemester(id) { const s = (cache.semesters||[]).find(x=>x.id===id); return s ? s.semesterCode : id; }
function lblClass(id) { const c = (cache.classes||[]).find(x=>x.id===id); return c ? c.classCode : id; }
function lblStudent(studentId) {
  for (const c of (cache.classes||[])) {
    const sv = (c.students||[]).find(s=>s.studentId===studentId);
    if (sv) return sv.studentCode + ' - ' + sv.name;
  }
  return studentId;
}

async function loadClasses() {
  try {
    await ensureDropdowns();
    const list = await api('GET', '/classes');
    cache.classes = list;
    document.getElementById('classesBody').innerHTML = list.length ? list.map(c => {
      const k = put(c);
      const sched = (c.schedules||[]).map(s=>`${s.dayOfWeek} ${s.timeSlot} (${s.room})`).join('<br>');
      return `<tr>
        <td><strong>${esc(c.classCode)}</strong></td>
        <td class="small-text">${lblSubject(c.subjectId)}</td>
        <td class="small-text">${lblTeacher(c.teacherId)}</td>
        <td>${lblSemester(c.semesterId)}</td>
        <td class="small-text">${sched||'—'}</td>
        <td>${(c.students||[]).length}</td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="editClassModal('${k}')">Sửa</button>
          <button class="btn btn-sm btn-danger" onclick="delClass('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="7" class="empty">Chưa có lớp học</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}

let schedIdx = 0;
function schedRow(i, s={}) {
  const days = ['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'];
  return `<div class="schedule-row" id="sr${i}">
    <select class="sch-day">
      ${days.map(d=>`<option ${s.dayOfWeek===d?'selected':''}>${d}</option>`).join('')}
    </select>
    <input class="sch-time" placeholder="07:00-09:00" value="${esc(s.timeSlot||'')}">
    <input class="sch-room" placeholder="A101" value="${esc(s.room||'')}">
    <button type="button" class="btn btn-sm btn-danger" onclick="this.closest('.schedule-row').remove()">✕</button>
  </div>`;
}
function addSchedRow() {
  const wrap = document.getElementById('schedWrap');
  if (!wrap) return;
  wrap.insertAdjacentHTML('beforeend', schedRow(schedIdx++));
}
function collectSchedules() {
  return Array.from(document.querySelectorAll('.schedule-row')).map(r=>({
    dayOfWeek: r.querySelector('.sch-day').value,
    timeSlot: r.querySelector('.sch-time').value,
    room: r.querySelector('.sch-room').value,
  })).filter(s=>s.timeSlot&&s.room);
}

function classFormHtml(c={}) {
  const subs = (cache.subjects||[]).map(s=>`<option value="${s.id}" ${c.subjectId===s.id?'selected':''}>${s.subjectCode} - ${esc(s.name)}</option>`).join('');
  const tchs = (cache.teachers||[]).map(t=>`<option value="${t.id}" ${c.teacherId===t.id?'selected':''}>${t.teacherCode} - ${esc(t.name)}</option>`).join('');
  const sems = (cache.semesters||[]).map(s=>`<option value="${s.id}" ${c.semesterId===s.id?'selected':''}>${s.semesterCode}</option>`).join('');
  schedIdx = (c.schedules||[]).length;
  const rows = (c.schedules||[]).map((s,i)=>schedRow(i,s)).join('');
  return `
    <div class="field"><label>Mã lớp *</label><input id="cf-code" value="${esc(c.classCode||'')}" ${c.id?'disabled':''} required></div>
    <div class="field"><label>Môn học *</label><select id="cf-subj" required><option value="">-- Chọn môn --</option>${subs}</select></div>
    <div class="field"><label>Giảng viên *</label><select id="cf-teacher" required><option value="">-- Chọn GV --</option>${tchs}</select></div>
    <div class="field"><label>Học kỳ *</label><select id="cf-sem" required><option value="">-- Chọn HK --</option>${sems}</select></div>
    <div class="field">
      <label>Lịch học <button type="button" class="btn btn-sm" onclick="addSchedRow()">+ Thêm buổi</button></label>
      <div id="schedWrap">${rows}</div>
    </div>`;
}
function collectClassData(classCode) {
  return {
    classCode: classCode || document.getElementById('cf-code').value,
    subjectId: document.getElementById('cf-subj').value,
    teacherId: document.getElementById('cf-teacher').value,
    semesterId: document.getElementById('cf-sem').value,
    schedules: collectSchedules(),
  };
}

async function addClassModal() {
  await ensureDropdowns(); schedIdx = 0;
  modal('Thêm lớp học', classFormHtml() + `
    <div class="modal-actions">
      <button type="button" class="btn" onclick="closeModal()">Hủy</button>
      <button type="button" class="btn btn-primary" onclick="submitAddClass()">Thêm</button>
    </div>`);
}
async function editClassModal(k) {
  await ensureDropdowns();
  const c = get(k);
  modal('Sửa lớp học', classFormHtml(c) + `
    <div class="modal-actions">
      <button type="button" class="btn" onclick="closeModal()">Hủy</button>
      <button type="button" class="btn btn-primary" onclick="submitUpdateClass('${c.id}','${esc(c.classCode)}')">Lưu</button>
    </div>`);
}
async function submitAddClass() {
  const d = collectClassData();
  if (!d.classCode || !d.subjectId || !d.teacherId || !d.semesterId) { toast('Vui lòng điền đầy đủ thông tin bắt buộc', 'error'); return; }
  try { await api('POST', '/classes', d); toast('Thêm lớp học thành công'); closeModal(); loadClasses(); }
  catch (err) { toast(err.message, 'error'); }
}
async function submitUpdateClass(id, code) {
  const d = collectClassData(code);
  try { await api('PUT', '/classes/' + id, d); toast('Cập nhật thành công'); closeModal(); loadClasses(); }
  catch (err) { toast(err.message, 'error'); }
}
async function delClass(k) {
  const c = get(k);
  if (!confirm('Xóa lớp "' + c.classCode + '"?')) return;
  try { await api('DELETE', '/classes/' + c.id); toast('Đã xóa'); loadClasses(); }
  catch (err) { toast(err.message, 'error'); }
}

// ---------- ENROLLMENTS ----------
async function loadEnrollmentsPage() {
  if (!cache.classes) {
    try { cache.classes = await api('GET', '/classes'); }
    catch(e) { cache.classes = []; toast('Không thể tải danh sách lớp: ' + e.message, 'error'); }
  }
  const sel = document.getElementById('enrollFilterClass');
  if (sel.options.length <= 1) {
    sel.innerHTML = '<option value="">-- Tất cả --</option>' +
      (cache.classes||[]).map(c=>`<option value="${c.id}">${c.classCode}</option>`).join('');
    sel.onchange = loadEnrollments;
  }
  await loadEnrollments();
}
async function loadEnrollments() {
  try {
    const classId = document.getElementById('enrollFilterClass').value;
    const list = await api('GET', '/enrollments' + (classId ? '?classId=' + classId : ''));
    document.getElementById('enrollmentsBody').innerHTML = list.length ? list.map(e => {
      const k = put(e);
      const sc = e.scores || {};
      const finalVal = sc['final'] ?? sc.finalExam;
      return `<tr>
        <td class="small-text">${lblStudent(e.studentId)}</td>
        <td>${lblClass(e.classId)}</td>
        <td>${sc.midterm != null ? sc.midterm : '—'}</td>
        <td>${sc.assignment != null ? sc.assignment : '—'}</td>
        <td>${finalVal != null ? finalVal : '—'}</td>
        <td><strong>${e.finalScore != null ? e.finalScore.toFixed(2) : '—'}</strong></td>
        <td>
          <button class="btn btn-sm btn-warning" onclick="scoresModal('${k}')">Điểm</button>
          <button class="btn btn-sm btn-danger" onclick="delEnrollment('${k}')">Xóa</button>
        </td></tr>`;
    }).join('') : '<tr><td colspan="7" class="empty">Chưa có đăng ký học</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
async function addEnrollmentModal() {
  const [students, classes] = await Promise.all([
    cache.students ? Promise.resolve(cache.students) : api('GET', '/students'),
    cache.classes  ? Promise.resolve(cache.classes)  : api('GET', '/classes'),
  ]);
  cache.students = students; cache.classes = classes;
  modal('Đăng ký học', `
    <form onsubmit="submitEnroll(event)">
      <div class="field"><label>Sinh viên *</label>
        <select name="studentId" required>
          <option value="">-- Chọn sinh viên --</option>
          ${students.map(s=>`<option value="${s.id}">${s.studentCode} - ${esc(s.name)}</option>`).join('')}
        </select>
      </div>
      <div class="field"><label>Lớp học *</label>
        <select name="classId" required>
          <option value="">-- Chọn lớp --</option>
          ${classes.map(c=>`<option value="${c.id}">${c.classCode}</option>`).join('')}
        </select>
      </div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Đăng ký</button>
      </div>
    </form>`);
}
async function submitEnroll(e) {
  e.preventDefault();
  const d = Object.fromEntries(new FormData(e.target));
  try { await api('POST', '/enrollments/register', d); toast('Đăng ký thành công'); closeModal(); loadEnrollments(); }
  catch (err) { toast(err.message, 'error'); }
}
function scoresModal(k) {
  const e = get(k);
  const sc = e.scores || {};
  const finalVal = sc['final'] ?? sc.finalExam ?? '';
  modal('Cập nhật điểm', `
    <form onsubmit="submitScores(event,'${e.id}')">
      <p class="hint">Điểm TK = Giữa kỳ×30% + Bài tập×20% + Cuối kỳ×50%</p>
      <div class="field"><label>Điểm giữa kỳ (0–10)</label>
        <input name="midterm" type="number" step="0.1" min="0" max="10" value="${sc.midterm ?? ''}">
      </div>
      <div class="field"><label>Điểm bài tập (0–10)</label>
        <input name="assignment" type="number" step="0.1" min="0" max="10" value="${sc.assignment ?? ''}">
      </div>
      <div class="field"><label>Điểm cuối kỳ (0–10)</label>
        <input name="final" type="number" step="0.1" min="0" max="10" value="${finalVal}">
      </div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Lưu điểm</button>
      </div>
    </form>`);
}
async function submitScores(e, id) {
  e.preventDefault();
  const fd = new FormData(e.target);
  const d = {};
  if (fd.get('midterm')) d.midterm = parseFloat(fd.get('midterm'));
  if (fd.get('assignment')) d.assignment = parseFloat(fd.get('assignment'));
  if (fd.get('final')) d.final = parseFloat(fd.get('final'));
  try { await api('PUT', '/enrollments/' + id + '/scores', d); toast('Lưu điểm thành công'); closeModal(); loadEnrollments(); }
  catch (err) { toast(err.message, 'error'); }
}
async function delEnrollment(k) {
  if (!confirm('Xóa đăng ký học này?')) return;
  const e = get(k);
  try { await api('DELETE', '/enrollments/' + e.id); toast('Đã xóa'); loadEnrollments(); }
  catch (err) { toast(err.message, 'error'); }
}

// ---------- ATTENDANCE ----------
async function loadAttendancePage() {
  if (!cache.classes) {
    try { cache.classes = await api('GET', '/classes'); }
    catch(e) { cache.classes = []; toast('Không thể tải danh sách lớp: ' + e.message, 'error'); }
  }
  const sel = document.getElementById('attendFilterClass');
  if (sel.options.length <= 1) {
    sel.innerHTML = '<option value="">-- Tất cả --</option>' +
      (cache.classes||[]).map(c=>`<option value="${c.id}">${c.classCode}</option>`).join('');
    sel.onchange = loadAttendance;
  }
  await loadAttendance();
}
async function loadAttendance() {
  try {
    const classId = document.getElementById('attendFilterClass').value;
    const list = await api('GET', '/attendance' + (classId ? '?classId=' + classId : ''));
    document.getElementById('attendanceBody').innerHTML = list.length ? list.map(r => {
      const k = put(r);
      const sm = r.summary || {};
      return `<tr>
        <td class="small-text">${lblStudent(r.studentId)}</td>
        <td>${lblClass(r.classId)}</td>
        <td class="text-success">${sm.totalPresent||0}</td>
        <td class="text-danger">${sm.totalAbsent||0}</td>
        <td class="text-warning">${sm.totalLate||0}</td>
        <td><button class="btn btn-sm" onclick="attendRecordsModal('${k}')">Chi tiết</button></td>
      </tr>`;
    }).join('') : '<tr><td colspan="6" class="empty">Chưa có dữ liệu điểm danh</td></tr>';
  } catch (e) { toast(e.message, 'error'); }
}
async function addAttendanceModal() {
  if (!cache.classes) cache.classes = await api('GET', '/classes');
  modal('Điểm danh', `
    <form onsubmit="submitAttendance(event)">
      <div class="field"><label>Lớp học *</label>
        <select id="adt-class" required onchange="fillAttendStudents()">
          <option value="">-- Chọn lớp --</option>
          ${(cache.classes||[]).map(c=>`<option value="${c.id}">${c.classCode}</option>`).join('')}
        </select>
      </div>
      <div class="field"><label>Sinh viên *</label>
        <select name="studentId" id="adt-student" required>
          <option value="">-- Chọn lớp trước --</option>
        </select>
      </div>
      <div class="field"><label>Trạng thái *</label>
        <select name="status" required>
          <option value="PRESENT">✅ Có mặt</option>
          <option value="ABSENT">❌ Vắng mặt</option>
          <option value="LATE">⏰ Đến trễ</option>
        </select>
      </div>
      <div class="field"><label>Ngày điểm danh</label>
        <input name="date" type="date" value="${new Date().toISOString().split('T')[0]}">
      </div>
      <div class="field"><label>Ghi chú</label><input name="note"></div>
      <div class="modal-actions">
        <button type="button" class="btn" onclick="closeModal()">Hủy</button>
        <button type="submit" class="btn btn-primary">Điểm danh</button>
      </div>
    </form>`);
}
function fillAttendStudents() {
  const classId = document.getElementById('adt-class').value;
  const cls = (cache.classes||[]).find(c=>c.id===classId);
  const sel = document.getElementById('adt-student');
  if (!cls || !(cls.students||[]).length) {
    sel.innerHTML = '<option value="">Lớp chưa có sinh viên</option>'; return;
  }
  sel.innerHTML = cls.students.map(s=>`<option value="${s.studentId}">${s.studentCode} - ${esc(s.name)}</option>`).join('');
}
async function submitAttendance(e) {
  e.preventDefault();
  const fd = new FormData(e.target);
  const classId = document.getElementById('adt-class').value;
  const d = {
    classId, studentId: fd.get('studentId'), status: fd.get('status'),
    note: fd.get('note') || null,
    date: fd.get('date') ? toISO(fd.get('date')) : null,
  };
  try { await api('POST', '/attendance/mark', d); toast('Điểm danh thành công'); closeModal(); loadAttendance(); }
  catch (err) { toast(err.message, 'error'); }
}
function attendRecordsModal(k) {
  const r = get(k);
  const sm = r.summary || {};
  const rows = (r.records||[]).map(rec=>`
    <tr>
      <td>${rec.date ? new Date(rec.date).toLocaleDateString('vi-VN') : '—'}</td>
      <td><span class="badge ${rec.status==='PRESENT'?'badge-success':rec.status==='ABSENT'?'badge-danger':'badge-warning'}">${rec.status}</span></td>
      <td>${esc(rec.note||'')}</td>
    </tr>`).join('') || '<tr><td colspan="3" class="empty">Chưa có bản ghi</td></tr>';
  modal('Chi tiết điểm danh', `
    <div style="display:flex;gap:16px;margin-bottom:16px">
      <span class="badge badge-success">Có mặt: ${sm.totalPresent||0}</span>
      <span class="badge badge-danger">Vắng: ${sm.totalAbsent||0}</span>
      <span class="badge badge-warning">Trễ: ${sm.totalLate||0}</span>
    </div>
    <div class="table-wrap">
      <table><thead><tr><th>Ngày</th><th>Trạng thái</th><th>Ghi chú</th></tr></thead>
      <tbody>${rows}</tbody></table>
    </div>
    <div class="modal-actions"><button class="btn" onclick="closeModal()">Đóng</button></div>`);
}

// ---------- INIT ----------
function init() {
  // Login form
  document.getElementById('loginForm').addEventListener('submit', async e => {
    e.preventDefault();
    const btn = document.getElementById('loginBtn');
    const errEl = document.getElementById('loginError');
    errEl.style.display = 'none';
    btn.disabled = true; btn.textContent = 'Đang đăng nhập...';
    try {
      await doLogin(document.getElementById('loginUsername').value, document.getElementById('loginPassword').value);
    } catch (err) {
      errEl.textContent = err.message; errEl.style.display = 'block';
    } finally { btn.disabled = false; btn.textContent = 'Đăng nhập'; }
  });

  // Logout
  document.getElementById('logoutBtn').addEventListener('click', doLogout);

  // Nav links
  document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', e => { e.preventDefault(); nav(link.dataset.section); });
  });

  // Section buttons
  document.getElementById('addUserBtn').addEventListener('click', showAddUserModal);
  document.getElementById('addStudentBtn').addEventListener('click', addStudentModal);
  document.getElementById('addTeacherBtn').addEventListener('click', addTeacherModal);
  document.getElementById('addSubjectBtn').addEventListener('click', addSubjectModal);
  document.getElementById('addSemesterBtn').addEventListener('click', addSemesterModal);
  document.getElementById('addClassBtn').addEventListener('click', addClassModal);
  document.getElementById('addEnrollmentBtn').addEventListener('click', addEnrollmentModal);
  document.getElementById('addAttendanceBtn').addEventListener('click', addAttendanceModal);

  // Close modal on overlay click
  document.getElementById('modalOverlay').addEventListener('click', e => {
    if (e.target === document.getElementById('modalOverlay')) closeModal();
  });
  document.getElementById('modalCloseBtn').addEventListener('click', closeModal);

  // Auto-login if token exists
  if (localStorage.getItem('token')) showApp();
}

init();
