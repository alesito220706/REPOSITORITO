    // ========== /static/js/admin-main.js ==========

    // Filtrar tabla en tiempo real
    function filtrarTabla(tableId, inputElement) {
    var filter = inputElement.value.toUpperCase();
    var table = document.getElementById(tableId);
    var tr = table.getElementsByTagName("tr");

    for (var i = 1; i < tr.length; i++) { // Empieza en 1 para saltar el thead
        var showRow = false;
        var tds = tr[i].getElementsByTagName("td");
        for (var j = 0; j < tds.length - 1; j++) { // Itera sobre las columnas (sin la de Acciones)
            var textValue = tds[j].textContent || tds[j].innerText;
            if (textValue.toUpperCase().indexOf(filter) > -1) {
                showRow = true;
                break;
            }
        }
        tr[i].style.display = showRow ? "" : "none";
    }
}

    // ===== CLIENTES =====

    // Función para confirmar y enviar la eliminación por DELETE
function confirmarEliminarCliente(id, nombreCliente) {
    if (confirm("¿Estás seguro de que deseas eliminar al cliente " + nombreCliente + " (ID: " + id + ")? Esta acción es irreversible.")) {
        // Crear un formulario temporal para enviar la solicitud DELETE/POST
        var form = document.createElement('form');
        form.method = 'POST';
        form.action = '/admin/clientes/' + id; // Usar el ID para la URL
        
        // Agregar un campo oculto para simular el método DELETE (Spring lo interpreta si se usa un filtro)
        // **NOTA:** Esto asume que tienes configurado el HiddenHttpMethodFilter en Spring.
        var hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = '_method';
        hiddenInput.value = 'DELETE';
        form.appendChild(hiddenInput);

        document.body.appendChild(form);
        form.submit();
    }
}

// Función para abrir el modal (Bootstrap 5)
function abrirModalCliente() {
    var modal = new bootstrap.Modal(document.getElementById('modalCliente'));
    modal.show();
}

    // ===== EMPLEADOS =====

    // Confirmar eliminación - Empleado
    function confirmarEliminarEmpleado(id, nombre) {
        if (confirm(`¿Estás seguro de eliminar al empleado "${nombre}"?`)) {
            fetch(`/admin/empleados/${id}`, { method: 'DELETE' })
                .then(r => r.json())
                .then(d => {
                    if (d.success) {
                        alert('Empleado eliminado correctamente');
                        location.reload();
                    } else {
                        alert('Error al eliminar');
                    }
                })
                .catch(e => console.error('Error:', e));
        }
    }

    function abrirModalEmpleado() {
        new bootstrap.Modal(document.getElementById('modalEmpleado')).show();
    }

    // Función placeholder para filtrar la tabla si existe
    function filtrarTabla(tablaId, input) {
        let filter = input.value.toUpperCase();
        let table = document.getElementById(tablaId);
        let trs = table.getElementsByTagName("tr");
        for (let i = 1; i < trs.length; i++) {
            let tds = trs[i].getElementsByTagName("td");
            let show = false;
            for (let j = 0; j < tds.length; j++) {
                if (tds[j].textContent.toUpperCase().indexOf(filter) > -1) {
                    show = true;
                    break;
                }
            }
            trs[i].style.display = show ? "" : "none";
        }
    }


    function abrirModalEmpleado() { new bootstrap.Modal(document.getElementById('modalEmpleado')).show(); }
    function filtrarTabla(tablaId, input) {
        const filter = input.value.toUpperCase();
        const table = document.getElementById(tablaId);
        const tr = table.getElementsByTagName("tr");
        for (let i = 1; i < tr.length; i++) {
            tr[i].style.display = tr[i].textContent.toUpperCase().includes(filter) ? "" : "none";
        }
    }


    // ===== CITAS =====

    // Confirmar eliminación - Cita
    function confirmarEliminarCita(id) {
        if (confirm('¿Estás seguro de eliminar esta cita?')) {
            fetch(`/admin/citas/${id}`, { method: 'DELETE' })
                .then(r => r.json())
                .then(d => {
                    if (d.success) {
                        alert('Cita eliminada correctamente');
                        location.reload();
                    } else {
                        alert('Error al eliminar');
                    }
                })
                .catch(e => console.error('Error:', e));
        }
    }

    // Cambiar estado de cita
    function cambiarEstadoCita(id, nuevoEstado) {
        fetch(`/admin/citas/${id}/estado`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ estado: nuevoEstado })
        })
        .then(r => r.json())
        .then(d => {
            if (d.success) {
                console.log('Estado actualizado');
            } else {
                alert('Error al actualizar estado');
            }
        })
        .catch(e => console.error('Error:', e));
    }

    // ===== SOLICITUDES =====

    // Confirmar eliminación - Solicitud
    function confirmarEliminarSolicitud(id) {
        if (confirm('¿Estás seguro de eliminar esta solicitud?')) {
            fetch(`/admin/solicitudes/${id}`, { method: 'DELETE' })
                .then(r => r.json())
                .then(d => {
                    if (d.success) {
                        alert('Solicitud eliminada correctamente');
                        location.reload();
                    } else {
                        alert('Error al eliminar');
                    }
                })
                .catch(e => console.error('Error:', e));
        }
    }

    // Cambiar estado de solicitud
    function cambiarEstadoSolicitud(id, nuevoEstado) {
        fetch(`/admin/solicitudes/${id}/estado`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ estado: nuevoEstado })
        })
        .then(r => r.json())
        .then(d => {
            if (d.success) {
                console.log('Estado actualizado');
            } else {
                alert('Error al actualizar estado');
            }
        })
        .catch(e => console.error('Error:', e));
    }

    // ===== MODELOS (gestion_autos) =====
    // Funciones para la gestión de modelos en el panel de administración
    class AdminModelosManager {
        constructor() {
            this.init();
        }

        init() {
            this.autoCloseAlerts();
            this.initModalListeners();
        }

        // Auto-cierre de alertas
        autoCloseAlerts() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                setTimeout(() => {
                    if (alert.isConnected) {
                        const bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    }
                }, 5000);
            });
        }

        // Inicializar listeners de modales
        initModalListeners() {
            // Limpiar modal de crear al cerrar
            const crearModal = document.getElementById('modalCrearModelo');
            if (crearModal) {
                crearModal.addEventListener('hidden.bs.modal', () => {
                    this.limpiarFormularioCrear();
                });
            }

            // Limpiar modal de editar al cerrar
            const editarModal = document.getElementById('modalEditarModelo');
            if (editarModal) {
                editarModal.addEventListener('hidden.bs.modal', () => {
                    this.limpiarFormularioEditar();
                });
            }
        }

        // Limpiar formulario de crear
        limpiarFormularioCrear() {
            const form = document.querySelector('form[th\\:object]');
            if (form) {
                form.reset();
                // Restablecer valores por defecto
                const activoSwitch = form.querySelector('input[th\\:field*="activo"]');
                if (activoSwitch) {
                    activoSwitch.checked = true;
                }
            }
        }

        // Limpiar formulario de editar
        limpiarFormularioEditar() {
            const modalBody = document.getElementById('modalEditarBody');
            if (modalBody) {
                modalBody.innerHTML = '';
            }
        }

        // Función para cargar datos del modelo a editar
        async editarModelo(id) {
            try {
                console.log('Cargando modelo con ID:', id);
                
                const response = await fetch(`/admin/gestion_autos/editar/${id}`);
                
                if (!response.ok) {
                    throw new Error(`Error HTTP: ${response.status}`);
                }
                
                const modelo = await response.json();
                
                if (modelo) {
                    this.cargarFormularioEditar(modelo, id);
                } else {
                    throw new Error('No se recibieron datos del modelo');
                }
            } catch (error) {
                console.error('Error al cargar modelo:', error);
                this.mostrarError('Error al cargar los datos del modelo: ' + error.message);
            }
        }

        // Cargar formulario de edición con datos del modelo
        cargarFormularioEditar(modelo, id) {
            const form = document.getElementById('formEditar');
            if (!form) {
                console.error('Formulario de edición no encontrado');
                return;
            }

            form.action = `/admin/gestion_autos/actualizar/${id}`;
            
            const modalBody = document.getElementById('modalEditarBody');
            if (!modalBody) {
                console.error('Modal body no encontrado');
                return;
            }
        }
        // Mostrar mensaje de error
        mostrarError(mensaje) {
            // Crear alerta temporal
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-danger alert-dismissible fade show';
            alertDiv.innerHTML = `
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            
            document.querySelector('.main-content').insertBefore(alertDiv, document.querySelector('.main-content').firstChild);
            
            // Auto-remover después de 5 segundos
            setTimeout(() => {
                if (alertDiv.isConnected) {
                    const bsAlert = new bootstrap.Alert(alertDiv);
                    bsAlert.close();
                }
            }, 5000);
        }

        // Validar formulario antes de enviar
        validarFormulario(form) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;

            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });

            return isValid;
        }
    }

    function confirmarEliminacion() {
        return confirm('¿Estás seguro de eliminar este modelo?');
    }


    document.addEventListener('DOMContentLoaded', function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert.isConnected) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            }, 5000);
        });
    });


    document.addEventListener('DOMContentLoaded', function() {
        window.adminManager = new AdminModelosManager();
        
    
        const forms = document.querySelectorAll('form');
        forms.forEach(form => {
            form.addEventListener('submit', function(e) {
                if (!window.adminManager.validarFormulario(this)) {
                    e.preventDefault();
                    window.adminManager.mostrarError('Por favor complete todos los campos requeridos.');
                }
            });
        });
    });

    // Hacer la función editarModelo disponible globalmente
    window.editarModelo = function(id) {
        if (window.adminManager) {
            window.adminManager.editarModelo(id);
        }
    };

    // ====== FINANCIAMIENTO ======
    document.addEventListener('DOMContentLoaded', function() {
      // Filtrado de solicitudes
      const filterEstado = document.getElementById('filter-estado');
      const searchInput = document.getElementById('search');
      const resetBtn = document.getElementById('reset-filters');
      
      // Solo ejecutar si los elementos existen
      if (filterEstado && searchInput && resetBtn) {
        function filtrarSolicitudes() {
          const estado = filterEstado.value;
          const busqueda = searchInput.value.toLowerCase();
          const solicitudes = document.querySelectorAll('.solicitud-item');
          
          solicitudes.forEach(solicitud => {
            const estadoSolicitud = solicitud.getAttribute('data-estado');
            const texto = solicitud.textContent.toLowerCase();
            
            const coincideEstado = estado === 'all' || estado === estadoSolicitud;
            const coincideBusqueda = texto.includes(busqueda);
            
            if (coincideEstado && coincideBusqueda) {
              solicitud.style.display = 'block';
            } else {
              solicitud.style.display = 'none';
            }
          });
        }
        
        filterEstado.addEventListener('change', filtrarSolicitudes);
        searchInput.addEventListener('input', filtrarSolicitudes);
        
        resetBtn.addEventListener('click', function() {
          filterEstado.value = 'all';
          searchInput.value = '';
          filtrarSolicitudes();
        });
      }
      
      // Modal de detalles
      const detalleModal = document.getElementById('detalleModal');
      if (detalleModal) {
        detalleModal.addEventListener('show.bs.modal', function(event) {
          const button = event.relatedTarget;
          const id = button.getAttribute('data-id');
          
          // En una implementación real, harías una llamada AJAX para obtener los detalles
          // Por ahora, mostramos los datos de la tarjeta
          const card = button.closest('.solicitud-item');
          const cardBody = card.querySelector('.card-body');
          
          document.getElementById('modal-id').textContent = id;
          document.getElementById('modal-nombre').textContent = cardBody.querySelector('.card-title').textContent;
          document.getElementById('modal-email').textContent = cardBody.querySelectorAll('.text-muted span')[0].textContent;
          document.getElementById('modal-modelo').textContent = cardBody.querySelectorAll('.text-muted span')[1].textContent;
          document.getElementById('modal-fecha').textContent = cardBody.querySelectorAll('.text-muted span')[2].textContent;
          document.getElementById('modal-plan').textContent = cardBody.querySelectorAll('.text-muted span')[3].textContent;
          document.getElementById('modal-estado').textContent = card.getAttribute('data-estado');
          document.getElementById('modal-mensaje').textContent = cardBody.querySelector('.mensaje-preview').textContent;
        });
      }
    });


    // ===== REPORTES =====

    document.addEventListener('DOMContentLoaded', function() {
      const citasChartEl = document.getElementById('citasChart');
      const solicitudesChartEl = document.getElementById('solicitudesChart');
      
      // Solo inicializar si estamos en la página de reportes
      if (citasChartEl && solicitudesChartEl) {
        inicializarGraficosReportes();
      }
    });

    function inicializarGraficosReportes() {
      // Gráfico de citas por estado (solo para página de reportes)
      const citasCtx = document.getElementById('citasChart');
      if (citasCtx) {
        new Chart(citasCtx.getContext('2d'), {
          type: 'doughnut',
          data: {
            labels: ['Pendientes', 'Confirmadas', 'Completadas', 'Canceladas'],
            datasets: [{
              data: [10, 20, 30, 5], // Valores de ejemplo
              backgroundColor: [
                '#f39c12', '#27ae60', '#3498db', '#e74c3c'
              ]
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom'
              }
            }
          }
        });
      }

      // Gráfico de solicitudes
      const solicitudesCtx = document.getElementById('solicitudesChart');
      if (solicitudesCtx) {
        new Chart(solicitudesCtx.getContext('2d'), {
          type: 'bar',
          data: {
            labels: ['Pendientes', 'Evaluando', 'Aprobadas', 'Rechazadas'],
            datasets: [{
              label: 'Solicitudes',
              data: [15, 8, 12, 3], // Valores de ejemplo
              backgroundColor: [
                '#f39c12', '#3498db', '#27ae60', '#e74c3c'
              ]
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true
              }
            }
          }
        });
      }

      // Gráfico de modelos por categoría (datos de ejemplo)
      const modelosCtx = document.getElementById('modelosChart');
      if (modelosCtx) {
        new Chart(modelosCtx.getContext('2d'), {
          type: 'pie',
          data: {
            labels: ['Sedán', 'SUV', 'Pickup', 'Deportivo'],
            datasets: [{
              data: [12, 19, 8, 5],
              backgroundColor: [
                '#2c3e50', '#3498db', '#e74c3c', '#f39c12'
              ]
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'bottom'
              }
            }
          }
        });
      }

      // Gráfico de citas mensuales (datos de ejemplo)
      const mensualCtx = document.getElementById('citasMensualesChart');
      if (mensualCtx) {
        new Chart(mensualCtx.getContext('2d'), {
          type: 'line',
          data: {
            labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
            datasets: [{
              label: 'Citas Mensuales',
              data: [65, 59, 80, 81, 56, 72],
              borderColor: '#3498db',
              backgroundColor: 'rgba(52, 152, 219, 0.1)',
              tension: 0.4,
              fill: true
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false
              }
            },
            scales: {
              y: {
                beginAtZero: true
              }
            }
          }
        });
      }
    }

    function filtrarReportes() {
      const fechaInicio = document.getElementById('fechaInicio').value;
      const fechaFin = document.getElementById('fechaFin').value;
      const tipoReporte = document.getElementById('tipoReporte').value;
      
      // Aquí iría la lógica para filtrar los reportes
      console.log('Filtrando reportes:', { fechaInicio, fechaFin, tipoReporte });
      alert('Funcionalidad de filtrado en desarrollo');
    }

    function exportarPDF() {
      // Lógica para exportar a PDF
      alert('Exportando a PDF...');
    }

    function exportarExcel() {
      // Lógica para exportar a Excel
      alert('Exportando a Excel...');
    }

    // ====== DASHBOARD ======
    // Este código se movió a dashboard.js para evitar conflictos