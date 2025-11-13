    // ========== /static/js/admin-main.js ==========

    // Filtrar tabla en tiempo real
    function filtrarTabla(tablaId, inputElement) {
        const filter = inputElement.value.toUpperCase();
        const table = document.getElementById(tablaId);
        const tr = table.getElementsByTagName('tr');
        
        for (let i = 1; i < tr.length; i++) {
            const txtValue = tr[i].textContent || tr[i].innerText;
            tr[i].style.display = txtValue.toUpperCase().includes(filter) ? '' : 'none';
        }
    }

    // ===== CLIENTES =====

    // Confirmar eliminación - Cliente
    function confirmarEliminarCliente(id, nombre) {
        if (confirm(`¿Estás seguro de eliminar al cliente "${nombre}"?`)) {
            fetch(`/admin/clientes/${id}`, { method: 'DELETE' })
                .then(r => r.json())
                .then(d => {
                    if (d.success) {
                        alert('Cliente eliminado correctamente');
                        location.reload();
                    } else {
                        alert('Error al eliminar');
                    }
                })
                .catch(e => {
                    console.error('Error:', e);
                    alert('Error de conexión');
                });
        }
    }
    function abrirModalCliente() { new bootstrap.Modal(document.getElementById('modalCliente')).show(); }
    function filtrarTabla(tablaId, input) {
        const filter = input.value.toUpperCase();
        const table = document.getElementById(tablaId);
        const tr = table.getElementsByTagName("tr");
        for (let i = 1; i < tr.length; i++) {
            tr[i].style.display = tr[i].textContent.toUpperCase().includes(filter) ? "" : "none";
        }
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
      inicializarGraficos();
    });

    function inicializarGraficos() {
      // Gráfico de citas por estado
      const citasCtx = document.getElementById('citasChart').getContext('2d');
      new Chart(citasCtx, {
        type: 'doughnut',
        data: {
          labels: ['Pendientes', 'Confirmadas', 'Completadas', 'Canceladas'],
          datasets: [{
            data: [
              [[$,{citasPendientes}]],
              [[$,{citasConfirmadas}]],
              [[$,{citasCompletadas}]],
              [[$,{citasCanceladas}]]
            ],
            backgroundColor: [
              '#f39c12', '#27ae60', '#3498db', '#e74c3c'
            ]
          }]
        }
      });

      // Gráfico de solicitudes
      function inicializarGraficos() {
      // Gráfico de solicitudes
      const solicitudesCtx = document.getElementById('solicitudesChart').getContext('2d');
      new Chart(solicitudesCtx, {
        type: 'bar',
        data: {
          labels: ['Pendientes', 'Evaluando', 'Aprobadas', 'Rechazadas'],
          datasets: [{
            label: 'Solicitudes',
            data: [
              [[$,{solicitudesPendientes}]],
              [[$,{solicitudesEvaluando}]],
              [[$,{solicitudesAprobadas}]],
              [[$,{solicitudesRechazadas}]]
            ],
            backgroundColor: [
              '#f39c12', '#3498db', '#27ae60', '#e74c3c'
            ]
          }]
        }
      });
    }

      // Gráfico de modelos por categoría (datos de ejemplo)
      const modelosCtx = document.getElementById('modelosChart').getContext('2d');
      new Chart(modelosCtx, {
        type: 'pie',
        data: {
          labels: ['Sedán', 'SUV', 'Pickup', 'Deportivo'],
          datasets: [{
            data: [12, 19, 8, 5],
            backgroundColor: [
              '#2c3e50', '#3498db', '#e74c3c', '#f39c12'
            ]
          }]
        }
      });

      // Gráfico de citas mensuales (datos de ejemplo)
      const mensualCtx = document.getElementById('citasMensualesChart').getContext('2d');
      new Chart(mensualCtx, {
        type: 'line',
        data: {
          labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
          datasets: [{
            label: 'Citas Mensuales',
            data: [65, 59, 80, 81, 56, 72],
            borderColor: '#3498db',
            tension: 0.1
          }]
        }
      });
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

    // ====== DASHBOAR ======
     // Inicializar gráficos cuando el documento esté listo
        document.addEventListener('DOMContentLoaded', function() {
            // Gráfico de citas mensuales
            const citasCtx = document.getElementById('citasChart').getContext('2d');
            new Chart(citasCtx, {
                type: 'line',
                data: {
                    labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
                    datasets: [{
                        label: 'Citas Programadas',
                        data: [65, 59, 80, 81, 56, 72, 45, 67, 55, 42, 60, 75],
                        borderColor: '#3498db',
                        backgroundColor: 'rgba(52, 152, 219, 0.1)',
                        tension: 0.4,
                        fill: true
                    }, {
                        label: 'Citas Completadas',
                        data: [28, 48, 40, 65, 46, 55, 30, 52, 38, 35, 48, 60],
                        borderColor: '#27ae60',
                        backgroundColor: 'rgba(39, 174, 96, 0.1)',
                        tension: 0.4,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
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

            // Actualizar contadores en tiempo real (simulado)
            function actualizarContadores() {
                // En una implementación real, esto se conectaría a WebSockets o se actualizaría periódicamente
                console.log('Actualizando contadores...');
            }

            // Actualizar cada 30 segundos
            setInterval(actualizarContadores, 30000);
        });