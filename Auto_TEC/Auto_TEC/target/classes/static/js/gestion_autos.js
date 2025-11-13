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
