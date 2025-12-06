document.addEventListener('DOMContentLoaded', () => {
    // Configuración del gráfico de citas mensuales
    const citasChartElement = document.getElementById('citasChart');
    
    // Solo inicializar el gráfico si el elemento existe
    if (citasChartElement) {
        const ctx = citasChartElement.getContext('2d');
        
        // Destruir cualquier gráfico existente para evitar duplicados
        if (window.citasChartInstance) {
            window.citasChartInstance.destroy();
        }
        
        const gradient = ctx.createLinearGradient(0, 0, 0, 120);
        gradient.addColorStop(0, 'rgba(212, 175, 55, 0.2)');
        gradient.addColorStop(1, 'rgba(212, 175, 55, 0)');

        window.citasChartInstance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
                datasets: [{
                    label: 'Citas Mensuales',
                    data: [65, 59, 80, 81, 56, 55, 40, 45, 58, 62, 75, 70],
                    borderColor: '#d4af37',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true,
                    backgroundColor: gradient,
                    pointBackgroundColor: '#d4af37',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: '#d4af37',
                    pointRadius: 3,
                    pointHoverRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                layout: {
                    padding: {
                        top: 5,
                        bottom: 5
                    }
                },
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        titleColor: '#d4af37',
                        bodyColor: '#fff',
                        borderColor: '#d4af37',
                        borderWidth: 1,
                        padding: 8,
                        displayColors: false,
                        titleFont: {
                            size: 11
                        },
                        bodyFont: {
                            size: 10
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(212, 175, 55, 0.1)',
                            drawBorder: false
                        },
                        ticks: {
                            color: 'rgba(255, 255, 255, 0.7)',
                            padding: 5,
                            font: {
                                size: 10
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            color: 'rgba(255, 255, 255, 0.7)',
                            padding: 5,
                            font: {
                                size: 10
                            }
                        }
                    }
                }
            }
        });
    }

    // Función para actualizar números con animación
    function animateValue(element, start, end, duration) {
        // Validar que end sea un número válido
        if (isNaN(end) || !isFinite(end)) {
            element.textContent = start;
            return;
        }
        
        // Evitar que se anime múltiples veces
        if (element.dataset.animated === 'true') {
            return;
        }
        element.dataset.animated = 'true';
        
        let startTimestamp = null;
        const step = (timestamp) => {
            if (!startTimestamp) startTimestamp = timestamp;
            const progress = Math.min((timestamp - startTimestamp) / duration, 1);
            const current = Math.floor(progress * (end - start) + start);
            element.textContent = current;
            
            if (progress < 1) {
                window.requestAnimationFrame(step);
            } else {
                // Asegurar que el valor final sea exacto
                element.textContent = end;
            }
        };
        window.requestAnimationFrame(step);
    }

    // Animar todos los números de estadísticas
    document.querySelectorAll('.stat-number').forEach(stat => {
        // Obtener el valor original del elemento
        const originalValue = stat.textContent.trim();
        const value = parseInt(originalValue);
        
        // Solo animar si es un número válido
        if (!isNaN(value) && isFinite(value) && value >= 0) {
            animateValue(stat, 0, value, 2000);
        }
    });
});