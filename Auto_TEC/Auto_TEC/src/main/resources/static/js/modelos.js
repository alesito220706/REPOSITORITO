document.addEventListener("DOMContentLoaded", () => {
    // 1. SELECTORES
    const menuLinks = Array.from(document.querySelectorAll(".model-link"));
    const heroImage = document.getElementById("heroImage");
    const heroContent = document.getElementById("heroContent");
    const title = document.querySelector(".display-title");
    const desc = document.querySelector(".display-desc");
    const specsRow = document.getElementById("heroSpecs");

    // Instancia de Modales
    const modalModelo = new bootstrap.Modal(document.getElementById("modalModelo"));
    const modalCotiza = new bootstrap.Modal(document.getElementById("modalCotiza"));

    let currentIndex = 0;

    // 2. MAPEO DE DATOS (Centraliza todo lo de Thymeleaf)
    const modelos = menuLinks.map(el => ({
        id: el.dataset.id,
        name: el.textContent.trim(),
        desc: el.dataset.desc || "",
        src: el.dataset.img || "",
        year: el.dataset.year || "N/A",
        engine: el.dataset.engine || "N/A",
        power: el.dataset.power || "N/A",
        price: el.dataset.price || "Consultar",
        equipment: el.dataset.equipment ? el.dataset.equipment.split(';') : []
    }));

    // 3. FUNCIÓN DE CAMBIO CINEMÁTICO
    function updateShowcase(index) {
        const m = modelos[index];
        currentIndex = index;

        // Fase de Salida
        heroImage.classList.remove('active');
        heroContent.classList.remove('active');

        setTimeout(() => {
            // Actualización de Datos
            heroImage.src = m.src;
            title.textContent = m.name;
            desc.textContent = m.desc;
            
            specsRow.innerHTML = `
                <div class="spec-item"><i class="fas fa-bolt me-2"></i> ${m.power}</div>
                <div class="spec-item"><i class="fas fa-microchip me-2"></i> ${m.engine}</div>
                <div class="spec-item"><i class="fas fa-calendar-alt me-2"></i> ${m.year}</div>
            `;

            // Fase de Entrada
            heroImage.classList.add('active');
            heroContent.classList.add('active');

            // Actualizar Menú
            menuLinks.forEach((l, i) => l.classList.toggle('active', i === index));
        }, 400);
    }

    // 4. EVENTOS DE CURSOR
    menuLinks.forEach((link, index) => {
        link.addEventListener('mouseenter', () => updateShowcase(index));
    });

    // 5. FUNCIONALIDAD DEL BOTÓN "EXPLORAR" (Modal Detalles)
    document.getElementById("btnExplorarIngenieria").onclick = () => {
        const m = modelos[currentIndex];
        document.getElementById("modalNombre").textContent = m.name;
        document.getElementById("modalImagen").src = m.src;
        document.getElementById("modalDescripcion").textContent = m.desc;
        
        document.getElementById("modalDetailsGrid").innerHTML = `
            <div class="row g-4 text-white">
                <div class="col-6"><span class="text-gold small d-block">MOTORIZACIÓN</span><strong>${m.engine}</strong></div>
                <div class="col-6"><span class="text-gold small d-block">VALOR</span><strong>${m.price}</strong></div>
                <div class="col-12">
                    <span class="text-gold small d-block mb-2">DOTACIÓN TÉCNICA</span>
                    <ul class="list-unstyled small text-secondary">
                        ${m.equipment.map(e => `<li><i class="fas fa-check text-gold me-2"></i>${e}</li>`).join('')}
                    </ul>
                </div>
            </div>
        `;
        modalModelo.show();
    };

    // 6. FLUJO DE COTIZACIÓN
    document.getElementById("btnSolicitarCotizacion").onclick = () => {
        document.getElementById("cotizaModelo").value = modelos[currentIndex].name;
        modalModelo.hide();
        setTimeout(() => modalCotiza.show(), 500);
    };

    // 7. ENVÍO FORMULARIO (AJAX)
    document.getElementById("cotizaForm").onsubmit = function(e) {
        e.preventDefault();
        const btn = document.getElementById("cotizaSubmit");
        btn.disabled = true;
        btn.textContent = "Procesando...";

        const data = {
            nombre: document.getElementById("cotizaNombre").value,
            email: document.getElementById("cotizaEmail").value,
            modelo: document.getElementById("cotizaModelo").value
        };

        fetch('/cotizar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
        .then(res => {
            if(res.ok) {
                alert("Su solicitud de cotización VIP ha sido enviada.");
                modalCotiza.hide();
                this.reset();
            } else { throw new Error(); }
        })
        .catch(() => alert("Error al conectar con el servidor."))
        .finally(() => {
            btn.disabled = false;
            btn.textContent = "Confirmar Solicitud";
        });
    };

    // Inicializar con el primer modelo
    if(modelos.length > 0) updateShowcase(0);
});