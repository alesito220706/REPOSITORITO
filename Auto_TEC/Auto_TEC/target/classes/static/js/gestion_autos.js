// Filtrar tabla
function filtrarTabla() {
    const input = document.getElementById('buscarModelo');
    const filter = input.value.toUpperCase();
    const table = document.getElementById('tablaModelos');
    const tr = table.getElementsByTagName('tr');

    for (let i = 1; i < tr.length; i++) {
        const tdArray = tr[i].getElementsByTagName('td');
        if (tdArray.length === 0) continue;

        let encontrado = false;
        for (let j = 0; j < tdArray.length; j++) {
            const txtValue = tdArray[j].textContent || tdArray[j].innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                encontrado = true;
                break;
            }
        }
        tr[i].style.display = encontrado ? '' : 'none';
    }
}

// Confirmar eliminación
function confirmarEliminar(id, nombre) {
    if (confirm(`¿Estás seguro de eliminar el modelo "${nombre}"?`)) {
        fetch(`/admin/modelos/${id}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                mostrarNotificacion('Modelo eliminado correctamente', 'success');
                setTimeout(() => location.reload(), 1500);
            } else {
                mostrarNotificacion('Error al eliminar el modelo', 'error');
            }
        })
        .catch(() => mostrarNotificacion('Error de conexión', 'error'));
    }
}

// Editar stock
function editarStock(id, stockActual) {
    const nuevoStock = prompt('Ingresa el nuevo stock:', stockActual);

    if (nuevoStock !== null && nuevoStock !== '') {
        const stock = parseInt(nuevoStock);
        if (isNaN(stock) || stock < 0) {
            mostrarNotificacion('Por favor ingresa un número válido', 'error');
            return;
        }

        fetch(`/admin/modelos/${id}/stock`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ stock })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                mostrarNotificacion('Stock actualizado correctamente', 'success');
                setTimeout(() => location.reload(), 1500);
            } else {
                mostrarNotificacion('Error al actualizar el stock', 'error');
            }
        })
        .catch(() => mostrarNotificacion('Error de conexión', 'error'));
    }
}

// Exportar CSV
function exportarTabla() {
    const table = document.getElementById('tablaModelos');
    let csv = [];
    const rows = table.querySelectorAll('tr');
    rows.forEach(row => {
        const cols = row.querySelectorAll('td, th');
        const rowData = [];
        cols.forEach((col, index) => {
            if (index !== cols.length - 1) {
                rowData.push('"' + col.textContent.trim().replace(/"/g, '""') + '"');
            }
        });
        if (rowData.length > 0) csv.push(rowData.join(','));
    });
    const blob = new Blob([csv.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `modelos_${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
    mostrarNotificacion('Archivo descargado correctamente', 'success');
}

// Notificaciones
function mostrarNotificacion(mensaje, tipo = 'info') {
    const notif = document.createElement('div');
    notif.className = `alert alert-${tipo === 'success' ? 'success' : 'danger'} position-fixed`;
    notif.style.cssText = 'top: 20px; right: 20px; min-width: 300px; z-index: 9999; animation: slideIn 0.3s ease-out;';
    notif.innerHTML = `
        <i class="fas fa-${tipo === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        ${mensaje}
        <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
    `;
    document.body.appendChild(notif);
    setTimeout(() => notif.remove(), 5000);
}

const style = document.createElement('style');
style.innerHTML = `
@keyframes slideIn {
    from { transform: translateX(400px); opacity: 0; }
    to { transform: translateX(0); opacity: 1; }
}`;
document.head.appendChild(style);

        document.addEventListener('DOMContentLoaded', function () {
            const crearForm = document.getElementById('crearModeloForm');
            const modalEl = document.getElementById('modalCrearModelo');
            let bc = null;
            try { bc = new BroadcastChannel('modelos_channel'); } catch(e) { /* no soportado */ }

            function escapeHtml(s) {
                if (!s) return '';
                return String(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/\'/g, '&#039;');
            }

            // Migrar imágenes (btn) -> POST /admin/gestion_autos/migrate-images
            const btnMigrar = document.getElementById('btnMigrarImagenes');
            if (btnMigrar) {
                btnMigrar.addEventListener('click', async function () {
                    if (!confirm('¿Deseas ejecutar la migración de imágenes? Por defecto se ejecuta en modo dryRun (ver cambios, no copia).')) return;
                    const dryRun = confirm('¿Modo dryRun? Presiona Aceptar para dry run (no copia), Cancelar para ejecutar copia real.');
                    btnMigrar.disabled = true;
                    try {
                        const resp = await fetch('/admin/gestion_autos/migrate-images', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: 'dryRun=' + (dryRun ? 'true' : 'false') });
                        const json = await resp.json();
                        let msg = `Migración completada. Procesados: ${json.total}`;
                        showAlert(msg, 'info');
                        console.info('Migration result', json);
                    } catch (err) {
                        console.error('Error migrating images:', err);
                        showAlert('Error migrando imágenes: ' + err.message, 'danger');
                    } finally { btnMigrar.disabled = false; }
                });
            }

            function appendModelToTable(model) {
                const tbody = document.querySelector('table tbody');
                if (!tbody) return;
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>
                        <img src="${escapeHtml(model.imagenUrl)}" alt="Imagen" class="modelo-img" onerror="this.src='data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%22200%22 height=%22120%22%3E%3Crect fill=%22%23ddd%22 width=%22200%22 height=%22120%22/%3E%3Ctext fill=%22%23999%22 font-family=%22sans-serif%22 font-size=%2214%22 dy=%2210.5%22 font-weight=%22bold%22 x=%2250%25%22 y=%2250%25%22 text-anchor=%22middle%22%3ESin Imagen%3C/text%3E%3C/svg%3E'">
                    </td>
                    <td>${escapeHtml(model.nombre)}</td>
                    <td>${escapeHtml(model.marca)}</td>
                    <td>$${(Number(model.precio) || 0).toFixed(2)}</td>
                    <td>${escapeHtml(model.stock)}</td>
                    <td>${model.destacado ? '<span class="badge bg-warning"><i class="fas fa-star"></i> Destacado</span>' : ''}</td>
                    <td>
                        <span class="badge ${model.activo ? 'bg-success' : 'bg-danger'} status-badge">${model.activo ? 'Activo' : 'Inactivo'}</span>
                    </td>
                    <td class="table-actions">
                        <a href="/admin/gestion_autos/editar/${model.id}" class="btn btn-sm btn-outline-primary" title="Editar"><i class="fas fa-edit"></i></a>
                        <form action="/admin/gestion_autos/toggle-activo/${model.id}" method="post" class="d-inline">
                            <button type="submit" class="btn btn-sm ${model.activo ? 'btn-outline-warning' : 'btn-outline-success'}" title="${model.activo ? 'Desactivar' : 'Activar'}">
                                <i class="${model.activo ? 'fas fa-eye-slash' : 'fas fa-eye'}"></i>
                            </button>
                        </form>
                        <form action="/admin/gestion_autos/eliminar/${model.id}" method="post" class="d-inline" onsubmit="return confirm('¿Estás seguro de eliminar este modelo?')">
                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Eliminar"><i class="fas fa-trash"></i></button>
                        </form>
                    </td>`;
                tbody.prepend(tr);
            }

            function showAlert(message, type) {
                const main = document.querySelector('.main-content');
                if (!main) return;
                const alert = document.createElement('div');
                alert.className = `alert alert-${type} alert-dismissible fade show`;
                alert.role = 'alert';
                alert.innerHTML = `${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
                main.prepend(alert);
                setTimeout(() => { try { alert.remove(); } catch(_) {} }, 4000);
            }

            if (crearForm) {
                crearForm.addEventListener('submit', async function (e) {
                    e.preventDefault();
                    if (!crearForm.checkValidity()) { crearForm.reportValidity(); return; }
                    const btn = crearForm.querySelector('button[type="submit"]');
                    if (btn) btn.disabled = true;
                    crearForm.querySelectorAll('.invalid-feedback').forEach(fb => fb.textContent = '');
                    crearForm.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
                    const formData = new FormData(crearForm);
                    try {
                        const response = await fetch(crearForm.action, { method: 'POST', body: formData, credentials: 'same-origin', headers: { 'X-Requested-With': 'XMLHttpRequest' } });
                        const ct = (response.headers.get('content-type') || '').toLowerCase();
                        if (response.ok) {
                            let model = {
                                id: Date.now(),
                                nombre: formData.get('nombre'),
                                marca: formData.get('marca'),
                                motor: formData.get('motor'),
                                potencia: formData.get('potencia'),
                                velocidadMax: formData.get('velocidadMax'),
                                aceleracion: formData.get('aceleracion'),
                                anio: formData.get('anio'),
                                precio: formData.get('precio'),
                                stock: formData.get('stock'),
                                equipamiento: formData.get('equipamiento'),
                                imagenUrl: formData.get('imagenUrl'),
                                categoria: formData.get('categoria'),
                                destacado: formData.get('destacado') === 'on' || formData.get('destacado') === 'true',
                                activo: formData.get('activo') === 'on' || formData.get('activo') === 'true',
                                descripcion: formData.get('descripcion')
                            };
                            if (ct.includes('application/json')) {
                                try {
                                    const json = await response.json();
                                    if (json && json.model) model = json.model;
                                    else if (json && json.id) model = json;
                                } catch (err) { /* ignore */ }
                            }
                            appendModelToTable(model);
                            showAlert('Modelo creado correctamente', 'success');
                            try { const bootstrapModal = bootstrap.Modal.getInstance(modalEl) || new bootstrap.Modal(modalEl); bootstrapModal.hide(); } catch (_) {}
                            crearForm.reset();
                            if (bc) bc.postMessage({ type: 'created', model });
                        } else if (response.status === 0 || response.status === 302 || response.redirected) {
                            showAlert('Modelo creado (redirigido). Actualiza la página si corresponde.', 'success');
                        } else {
                            if (ct.includes('application/json')) {
                                const json = await response.json();
                                if (json.errors) {
                                    Object.keys(json.errors).forEach(field => {
                                        const el = crearForm.querySelector('[name="' + field + '"]') || document.getElementById(field + 'Input');
                                        if (el) el.classList.add('is-invalid');
                                        const fb = crearForm.querySelector('.invalid-feedback[data-field="' + field + '"]');
                                        if (fb) fb.textContent = json.errors[field];
                                    });
                                    showAlert('Corrige los errores marcados en el formulario', 'danger');
                                } else if (json.message) { showAlert(json.message, 'danger'); }
                            } else if (ct.includes('text/html')) {
                                const text = await response.text();
                                try {
                                    const parser = new DOMParser();
                                    const doc = parser.parseFromString(text, 'text/html');
                                    const feedbacks = doc.querySelectorAll('.invalid-feedback[data-field]');
                                    if (feedbacks.length) {
                                        feedbacks.forEach(fb => {
                                            const field = fb.getAttribute('data-field');
                                            const msg = fb.textContent.trim();
                                            const el = crearForm.querySelector('[name="' + field + '"]') || document.getElementById(field + 'Input');
                                            if (el) el.classList.add('is-invalid');
                                            const localFb = crearForm.querySelector('.invalid-feedback[data-field="' + field + '"]');
                                            if (localFb) localFb.textContent = msg;
                                        });
                                        showAlert('Corrige los errores marcados en el formulario', 'danger');
                                    } else {
                                        showAlert('Error al crear el modelo', 'danger');
                                        console.error('Error crear modelo:', response.status, text);
                                    }
                                } catch (err) { showAlert('Error al crear el modelo', 'danger'); }
                            } else {
                                const text = await response.text();
                                showAlert('Error al crear el modelo', 'danger');
                                console.error('Error crear modelo:', response.status, text);
                            }
                        }
                    } catch (err) { showAlert('Error de red: ' + err.message, 'danger'); }
                    finally { if (btn) btn.disabled = false; }
                });

                // preview handling for file and url
                const imagenFileCreate = document.getElementById('imagenFileCreate');
                const imagenPreviewCreate = document.getElementById('imagenPreviewCreate');
                if (imagenFileCreate && imagenPreviewCreate) {
                    imagenFileCreate.addEventListener('change', function () {
                        const file = imagenFileCreate.files && imagenFileCreate.files[0]; if (!file) return; const reader = new FileReader(); reader.onload = function (e) { imagenPreviewCreate.src = e.target.result; imagenPreviewCreate.style.display = 'block'; }; reader.readAsDataURL(file);
                    });
                }
                // no URL input on create form - preview only from file input
            }
        });
