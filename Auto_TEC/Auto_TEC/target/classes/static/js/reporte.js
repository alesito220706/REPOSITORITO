document.addEventListener("DOMContentLoaded", function() {
    // Configuración global para el estilo "Elite"
    Chart.defaults.color = "#666666";
    Chart.defaults.font.family = "'Inter', sans-serif";

    // 1. Gráfico de Ventas por Mes
    const ctxMes = document.getElementById('ventasMesChart').getContext('2d');
    new Chart(ctxMes, {
        type: 'line',
        data: {
            labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
            datasets: [{
                label: 'Ventas ($)',
                data: [120000, 190000, 150000, 220000, 180000, 250000, 280000, 200000, 240000, 300000, 270000, 350000],
                borderColor: '#d4af37',
                backgroundColor: 'rgba(212, 175, 55, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { position: 'bottom', labels: { color: '#fff' } } },
            scales: {
                x: { ticks: { color: '#666' } },
                y: { beginAtZero: true, ticks: { color: '#666', callback: (val) => '$' + val.toLocaleString() } }
            }
        }
    });

    // 2. Gráfico de Ventas por Empleado
    const ctxEmpleado = document.getElementById('ventasEmpleadoChart').getContext('2d');
    new Chart(ctxEmpleado, {
        type: 'bar',
        data: {
            labels: ['Ana Torres', 'Luis Ramos', 'Sofia Mendoza', 'Carlos Díaz'],
            datasets: [{
                label: 'Cantidad',
                data: [15, 8, 22, 12],
                backgroundColor: '#d4af37'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { ticks: { color: '#666' } },
                y: { beginAtZero: true, ticks: { color: '#666', stepSize: 5 } }
            }
        }
    });
});

// Función de filtro
function filtrarReporte() {
    const inicio = document.getElementById('fechaInicio').value;
    const fin = document.getElementById('fechaFin').value;
    if(!inicio || !fin) {
        alert("Por favor seleccione ambas fechas");
        return;
    }
    console.log('Filtrando datos del:', inicio, 'al:', fin);
}

function exportarPDF() { alert('Generando PDF...'); }
function exportarExcel() { alert('Generando Excel...'); }