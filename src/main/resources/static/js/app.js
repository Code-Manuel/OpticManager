/* ────────────────────────────────────────────
   OpticManager – Frontend App
   ──────────────────────────────────────────── */

const API = '/api/patients';

const state = {
  patients: [],
  selectedId: null,
  prescriptions: [],
  editingPatientId: null,
  editingRxId: null,
};

// ── Utility ───────────────────────────────────

function initials(first, last) {
  return ((first?.[0] ?? '') + (last?.[0] ?? '')).toUpperCase();
}

function formatDate(iso) {
  if (!iso) return '';
  const d = new Date(iso + (iso.length === 10 ? 'T00:00:00' : ''));
  return d.toLocaleDateString('it-IT', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

function formatRxVal(v) {
  if (v == null) return '–';
  const n = parseFloat(v);
  return (n >= 0 ? '+' : '') + n.toFixed(2);
}

function age(birthIso) {
  if (!birthIso) return '';
  const diff = Date.now() - new Date(birthIso + 'T00:00:00').getTime();
  return Math.floor(diff / (365.25 * 24 * 3600 * 1000)) + ' anni';
}

// ── Toast ─────────────────────────────────────

function toast(msg, type = 'info') {
  const icons = {
    success: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>',
    error:   '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>',
    info:    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
  };
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.innerHTML = icons[type] + `<span>${msg}</span>`;
  document.getElementById('toast-container').appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

// ── API calls ─────────────────────────────────

async function apiFetch(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({}));
    const msg = err.error || err.message || `Errore ${res.status}`;
    throw new Error(msg);
  }
  if (res.status === 204) return null;
  return res.json();
}

async function loadPatients(search = '') {
  const url = search
    ? `${API}?search=${encodeURIComponent(search)}&size=100&sort=lastName`
    : `${API}?size=100&sort=lastName`;
  const data = await apiFetch(url);
  state.patients = data.content ?? [];
  renderPatientList();
}

async function loadPrescriptions(patientId) {
  const data = await apiFetch(`${API}/${patientId}/prescriptions`);
  state.prescriptions = data ?? [];
  renderPrescriptions();
}

// ── Render: Patient List ───────────────────────

function renderPatientList() {
  const list = document.getElementById('patient-list');
  const count = document.getElementById('patient-count');
  count.textContent = `${state.patients.length} pazienti`;

  if (!state.patients.length) {
    list.innerHTML = `
      <div class="empty-list">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
          <circle cx="9" cy="7" r="4"/>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
        </svg>
        <p>Nessun paziente trovato</p>
      </div>`;
    return;
  }

  list.innerHTML = state.patients.map(p => `
    <div class="patient-item ${p.id === state.selectedId ? 'active' : ''}"
         data-id="${p.id}">
      <div class="patient-avatar">${initials(p.firstName, p.lastName)}</div>
      <div class="patient-item-info">
        <div class="patient-item-name">${p.lastName} ${p.firstName}</div>
        <div class="patient-item-cf">${p.fiscalCode}</div>
      </div>
    </div>
  `).join('');

  list.querySelectorAll('.patient-item').forEach(el => {
    el.addEventListener('click', () => selectPatient(+el.dataset.id));
  });
}

// ── Render: Patient Detail ─────────────────────

function renderPatientDetail(p) {
  document.getElementById('welcome-state').style.display = 'none';
  const panel = document.getElementById('detail-panel');
  panel.style.display = 'flex';

  document.getElementById('detail-avatar').textContent = initials(p.firstName, p.lastName);
  document.getElementById('detail-name').textContent = `${p.firstName} ${p.lastName}`;

  const meta = document.getElementById('detail-meta');
  const cfEl   = document.getElementById('detail-cf');
  const bEl    = document.getElementById('detail-birth');
  const phEl   = document.getElementById('detail-phone');
  const emEl   = document.getElementById('detail-email');

  cfEl.textContent  = p.fiscalCode || '';
  bEl.textContent   = p.birthDate ? `${formatDate(p.birthDate)} (${age(p.birthDate)})` : '';
  phEl.textContent  = p.phone || '';
  emEl.textContent  = p.email || '';
}

// ── Render: Prescriptions ─────────────────────

function renderPrescriptions() {
  const container = document.getElementById('prescriptions-list');
  if (!state.prescriptions.length) {
    container.innerHTML = `<div class="empty-rx"><p>Nessuna prescrizione registrata</p></div>`;
    return;
  }

  container.innerHTML = state.prescriptions.map(rx => `
    <div class="rx-card">
      <div class="rx-card-header">
        <span class="rx-date">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
            <line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/>
            <line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
          ${formatDate(rx.visitDate)}
        </span>
        <div class="rx-card-actions">
          <button class="btn-icon pdf" title="Scarica PDF" onclick="downloadRxPdf(${rx.id})">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="12" y1="18" x2="12" y2="12"/>
              <polyline points="9 15 12 18 15 15"/>
            </svg>
          </button>
          <button class="btn-icon" title="Modifica" onclick="openEditRx(${rx.id})">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
            </svg>
          </button>
          <button class="btn-icon danger" title="Elimina" onclick="deleteRx(${rx.id})">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="3 6 5 6 21 6"/>
              <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/>
              <path d="M10 11v6M14 11v6"/>
            </svg>
          </button>
        </div>
      </div>
      <div class="rx-body">
        <div class="rx-eyes">
          <div class="rx-eye">
            <div class="rx-eye-label od">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="4"/>
                <path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z"/>
              </svg>
              OD – Destro
            </div>
            <div class="rx-values">
              <span class="rx-chip">${formatRxVal(rx.sphereOD)} <span class="rx-chip-label">sph</span></span>
              <span class="rx-chip">${formatRxVal(rx.cylinderOD)} <span class="rx-chip-label">cyl</span></span>
              ${rx.axisOD != null ? `<span class="rx-chip">${rx.axisOD}° <span class="rx-chip-label">ax</span></span>` : ''}
            </div>
          </div>
          <div class="rx-eye">
            <div class="rx-eye-label os">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="4"/>
                <path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z"/>
              </svg>
              OS – Sinistro
            </div>
            <div class="rx-values">
              <span class="rx-chip">${formatRxVal(rx.sphereOS)} <span class="rx-chip-label">sph</span></span>
              <span class="rx-chip">${formatRxVal(rx.cylinderOS)} <span class="rx-chip-label">cyl</span></span>
              ${rx.axisOS != null ? `<span class="rx-chip">${rx.axisOS}° <span class="rx-chip-label">ax</span></span>` : ''}
            </div>
          </div>
        </div>
        ${rx.pupillaryDistance != null ? `<div class="rx-pd">↔ DP: <strong>${rx.pupillaryDistance} mm</strong></div>` : ''}
        ${rx.notes ? `<div class="rx-notes">${rx.notes}</div>` : ''}
      </div>
    </div>
  `).join('');
}

// ── Select Patient ─────────────────────────────

async function selectPatient(id) {
  state.selectedId = id;
  renderPatientList();
  const p = state.patients.find(x => x.id === id);
  if (p) {
    renderPatientDetail(p);
    await loadPrescriptions(id);
  }
}

// ── Modal helpers ─────────────────────────────

function openModal(id) {
  document.getElementById(id).classList.add('open');
}

function closeModal(id) {
  document.getElementById(id).classList.remove('open');
}

function clearFormErrors(form) {
  form.querySelectorAll('.field-error').forEach(e => e.textContent = '');
  form.querySelectorAll('input.error, textarea.error').forEach(e => e.classList.remove('error'));
}

function setFormErrors(form, errors) {
  Object.entries(errors).forEach(([field, msg]) => {
    const input = form.querySelector(`[name="${field}"]`);
    if (input) {
      input.classList.add('error');
      const errEl = input.parentElement.querySelector('.field-error');
      if (errEl) errEl.textContent = msg;
    }
  });
}

function formToJson(form) {
  const fd = new FormData(form);
  const obj = {};
  fd.forEach((val, key) => {
    const trimmed = val.toString().trim();
    if (trimmed !== '') obj[key] = trimmed;
  });
  return obj;
}

// ── Patient Modal ─────────────────────────────

function openNewPatient() {
  state.editingPatientId = null;
  document.getElementById('patient-modal-title').textContent = 'Nuovo paziente';
  document.getElementById('patient-submit-btn').textContent = 'Salva';
  const form = document.getElementById('patient-form');
  form.reset();
  clearFormErrors(form);
  openModal('patient-modal');
}

function openEditPatient() {
  const p = state.patients.find(x => x.id === state.selectedId);
  if (!p) return;
  state.editingPatientId = p.id;
  document.getElementById('patient-modal-title').textContent = 'Modifica paziente';
  const form = document.getElementById('patient-form');
  form.reset();
  clearFormErrors(form);

  form.querySelector('[name=firstName]').value   = p.firstName ?? '';
  form.querySelector('[name=lastName]').value    = p.lastName ?? '';
  form.querySelector('[name=fiscalCode]').value  = p.fiscalCode ?? '';
  form.querySelector('[name=birthDate]').value   = p.birthDate ?? '';
  form.querySelector('[name=phone]').value       = p.phone ?? '';
  form.querySelector('[name=email]').value       = p.email ?? '';
  openModal('patient-modal');
}

document.getElementById('patient-form').addEventListener('submit', async e => {
  e.preventDefault();
  const form = e.target;
  clearFormErrors(form);
  const body = formToJson(form);

  const btn = document.getElementById('patient-submit-btn');
  btn.disabled = true;
  btn.innerHTML = '<span class="loading-spinner"></span> Salvo…';

  try {
    if (state.editingPatientId) {
      await apiFetch(`${API}/${state.editingPatientId}`, { method: 'PUT', body: JSON.stringify(body) });
      toast('Paziente aggiornato', 'success');
    } else {
      const created = await apiFetch(API, { method: 'POST', body: JSON.stringify(body) });
      toast('Paziente creato', 'success');
      state.selectedId = created.id;
    }
    closeModal('patient-modal');
    const search = document.getElementById('search-input').value.trim();
    await loadPatients(search);
    if (state.selectedId) await selectPatient(state.selectedId);
  } catch (err) {
    toast(err.message, 'error');
    if (err.fieldErrors) setFormErrors(form, err.fieldErrors);
  } finally {
    btn.disabled = false;
    btn.textContent = 'Salva';
  }
});

async function deletePatient() {
  if (!state.selectedId) return;
  const p = state.patients.find(x => x.id === state.selectedId);
  if (!confirm(`Eliminare il paziente "${p?.firstName} ${p?.lastName}"?\nVerranno eliminate anche tutte le sue prescrizioni.`)) return;
  try {
    await apiFetch(`${API}/${state.selectedId}`, { method: 'DELETE' });
    toast('Paziente eliminato', 'success');
    state.selectedId = null;
    document.getElementById('detail-panel').style.display = 'none';
    document.getElementById('welcome-state').style.display = 'flex';
    await loadPatients();
  } catch (err) {
    toast(err.message, 'error');
  }
}

// ── Prescription Modal ────────────────────────

function openNewRx() {
  if (!state.selectedId) return;
  state.editingRxId = null;
  document.getElementById('rx-modal-title').textContent = 'Nuova prescrizione';
  const form = document.getElementById('rx-form');
  form.reset();
  clearFormErrors(form);
  form.querySelector('[name=visitDate]').value = new Date().toISOString().slice(0, 10);
  openModal('rx-modal');
}

function openEditRx(rxId) {
  const rx = state.prescriptions.find(x => x.id === rxId);
  if (!rx) return;
  state.editingRxId = rxId;
  document.getElementById('rx-modal-title').textContent = 'Modifica prescrizione';
  const form = document.getElementById('rx-form');
  form.reset();
  clearFormErrors(form);

  const set = (name, val) => { if (val != null) form.querySelector(`[name=${name}]`).value = val; };
  set('visitDate', rx.visitDate);
  set('sphereOD', rx.sphereOD);
  set('cylinderOD', rx.cylinderOD);
  set('axisOD', rx.axisOD);
  set('sphereOS', rx.sphereOS);
  set('cylinderOS', rx.cylinderOS);
  set('axisOS', rx.axisOS);
  set('pupillaryDistance', rx.pupillaryDistance);
  set('notes', rx.notes);
  openModal('rx-modal');
}

document.getElementById('rx-form').addEventListener('submit', async e => {
  e.preventDefault();
  const form = e.target;
  clearFormErrors(form);
  const raw = formToJson(form);

  // Convert numeric fields
  const numFields = ['sphereOD','cylinderOD','axisOD','sphereOS','cylinderOS','axisOS','pupillaryDistance'];
  numFields.forEach(f => { if (raw[f] !== undefined) raw[f] = Number(raw[f]); });

  const btn = document.getElementById('rx-submit-btn');
  btn.disabled = true;
  btn.innerHTML = '<span class="loading-spinner"></span> Salvo…';

  try {
    if (state.editingRxId) {
      await apiFetch(`${API}/${state.selectedId}/prescriptions/${state.editingRxId}`,
        { method: 'PUT', body: JSON.stringify(raw) });
      toast('Prescrizione aggiornata', 'success');
    } else {
      await apiFetch(`${API}/${state.selectedId}/prescriptions`,
        { method: 'POST', body: JSON.stringify(raw) });
      toast('Prescrizione aggiunta', 'success');
    }
    closeModal('rx-modal');
    await loadPrescriptions(state.selectedId);
  } catch (err) {
    toast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Salva';
  }
});

function downloadRxPdf(rxId) {
  const url = `/api/patients/${state.selectedId}/prescriptions/${rxId}/pdf`;
  const a = document.createElement('a');
  a.href = url;
  a.download = '';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  toast('Download PDF avviato', 'info');
}

async function deleteRx(rxId) {  const rx = state.prescriptions.find(x => x.id === rxId);
  if (!confirm(`Eliminare la prescrizione del ${formatDate(rx?.visitDate)}?`)) return;
  try {
    await apiFetch(`${API}/${state.selectedId}/prescriptions/${rxId}`, { method: 'DELETE' });
    toast('Prescrizione eliminata', 'success');
    await loadPrescriptions(state.selectedId);
  } catch (err) {
    toast(err.message, 'error');
  }
}

// ── Event Wiring ──────────────────────────────

document.getElementById('btn-new-patient').addEventListener('click', openNewPatient);
document.getElementById('btn-new-patient-welcome').addEventListener('click', openNewPatient);
document.getElementById('btn-edit-patient').addEventListener('click', openEditPatient);
document.getElementById('btn-delete-patient').addEventListener('click', deletePatient);
document.getElementById('btn-new-rx').addEventListener('click', openNewRx);

document.querySelectorAll('[data-close]').forEach(btn => {
  btn.addEventListener('click', () => closeModal(btn.dataset.close));
});

document.querySelectorAll('.modal-overlay').forEach(overlay => {
  overlay.addEventListener('click', e => {
    if (e.target === overlay) overlay.classList.remove('open');
  });
});

document.addEventListener('keydown', e => {
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal-overlay.open').forEach(m => m.classList.remove('open'));
  }
});

// Uppercase fiscal code while typing
document.querySelector('[name=fiscalCode]')?.addEventListener('input', e => {
  const start = e.target.selectionStart;
  e.target.value = e.target.value.toUpperCase();
  e.target.setSelectionRange(start, start);
});

// Search debounce
let searchTimer;
document.getElementById('search-input').addEventListener('input', e => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(() => loadPatients(e.target.value.trim()), 280);
});

// ── Init ──────────────────────────────────────
loadPatients();
