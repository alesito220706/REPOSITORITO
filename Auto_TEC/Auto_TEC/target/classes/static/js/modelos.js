document.addEventListener("DOMContentLoaded", () => {
  // Elementos del DOM
  const items = Array.from(document.querySelectorAll(".modelo-item"));
  const modalEl = document.getElementById("modalModelo");
  const modal = modalEl ? new bootstrap.Modal(modalEl) : null;
  const modalTitulo = document.getElementById("modalModeloLabel");
  const modalImagen = document.getElementById("modalImagen");
  const modalDescripcion = document.getElementById("modalDescripcion");
  const modalDetails = document.getElementById('modalDetails');

  // Área de preview
  const previewArea = document.querySelector(".preview-area");
  const previewImg = previewArea?.querySelector(".preview-img");
  const previewDetails = previewArea?.querySelector(".preview-details");
  const previewTitle = previewDetails?.querySelector(".preview-title");
  const previewDescription = previewDetails?.querySelector(".preview-description");
  const previewSpecs = previewDetails?.querySelector(".preview-specs");

  // Asegurarse de que los elementos de preview existan
  if (!previewArea || !previewImg || !previewDetails) {
    console.error('Elementos de preview no encontrados. Verificar HTML.');
    return;
  }

  // Crear tooltip personalizado
  const tooltip = document.createElement("div");
  tooltip.className = "tooltip-custom";
  tooltip.setAttribute('aria-hidden','true');
  document.body.appendChild(tooltip);

  // Construir array de modelos con metadatos adicionales
  const modelos = items.map(el => ({
    name: el.dataset.name || el.textContent.trim(),
    desc: el.dataset.desc || el.textContent.trim(),
    src: el.dataset.img || el.dataset.src || '',
    year: el.dataset.year || '',
    engine: el.dataset.engine || '',
    power: el.dataset.power || '',
    price: el.dataset.price || '',
    equipment: el.dataset.equipment ? el.dataset.equipment.split(';') : [],
    el: el
  }));

  // Elementos de navegación del preview
  const previewPrev = document.getElementById('previewPrev');
  const previewNext = document.getElementById('previewNext');
  const previewOpenModal = document.getElementById('previewOpenModal');

  let currentIndex = modelos.length > 0 ? 0 : -1;

  // Si no hay modelos, salir silenciosamente
  if (modelos.length === 0) {
    console.warn('No se encontraron elementos .modelo-item en la página modelos.');
    return;
  }

  // Función para mostrar tooltip con información del "cargo" (modelo)
  function showTooltip(item, x, y) {
    const rect = item.getBoundingClientRect();
    const modelData = {
      name: item.textContent.trim(),
      year: item.dataset.year || 'N/A',
      engine: item.dataset.engine || 'N/A',
      power: item.dataset.power || 'N/A',
      price: item.dataset.price || 'Consultar'
    };

    tooltip.innerHTML = `
      <div class="tooltip-header">${modelData.name}</div>
      <div class="tooltip-details">
        <span class="tooltip-label">Año:</span>
        <span class="tooltip-value">${modelData.year}</span>
        
        <span class="tooltip-label">Motor:</span>
        <span class="tooltip-value">${modelData.engine}</span>
        
        <span class="tooltip-label">Potencia:</span>
        <span class="tooltip-value">${modelData.power}</span>
        
        <span class="tooltip-label">Precio:</span>
        <span class="tooltip-value">${modelData.price}</span>
      </div>
    `;

    // Posicionar tooltip
    const tooltipWidth = tooltip.offsetWidth;
    const tooltipHeight = tooltip.offsetHeight;
    
    let posX = rect.right + 15;
    let posY = rect.top - (tooltipHeight / 2) + (rect.height / 2);

    // Ajustar si se sale de la pantalla
    if (posX + tooltipWidth > window.innerWidth - 20) {
      posX = rect.left - tooltipWidth - 15;
    }

    if (posY + tooltipHeight > window.innerHeight - 20) {
      posY = window.innerHeight - tooltipHeight - 20;
    }

    if (posY < 20) {
      posY = 20;
    }

    tooltip.style.left = posX + 'px';
    tooltip.style.top = posY + 'px';
    tooltip.classList.add('show');
  }

  function hideTooltip() {
    tooltip.classList.remove('show');
  }

  function showPreview(modelo) {
    if (!modelo || !previewImg || !previewDetails) return;
    
    // Activar área de preview
    previewArea.classList.add('active');
    
    // Actualizar fondo con efecto blur
    const background = document.querySelector('.preview-background');
    if (background) {
      background.style.backgroundImage = `url(${modelo.src})`;
    }

    // Actualizar imagen con animación
    if (modelo.src) {
      const img = new Image();
      img.onload = () => {
        previewImg.src = modelo.src;
        previewImg.alt = modelo.name;
        previewImg.style.display = 'block';
        requestAnimationFrame(() => previewImg.classList.add('show'));
      };
      img.src = modelo.src;
    }

    // Actualizar detalles
    const title = previewDetails.querySelector('.preview-title');
    const description = previewDetails.querySelector('.preview-description');
    if (title) title.textContent = modelo.name;
    if (description) description.textContent = modelo.desc;
    
    // Actualizar especificaciones técnicas con animación
    if (previewSpecs) {
      const specs = [
        { icon: 'calendar', label: 'Año', value: modelo.year },
        { icon: 'engine', label: 'Motor', value: modelo.engine },
        { icon: 'tachometer-alt', label: 'Potencia', value: modelo.power },
        { icon: 'tag', label: 'Precio', value: modelo.price }
      ];

      previewSpecs.innerHTML = specs.map((spec, index) => `
        <div class="spec-item" style="animation: fadeInUp 0.3s ease forwards ${index * 0.1}s">
          <i class="fas fa-${spec.icon}"></i>
          <span class="spec-label">${spec.label}</span>
          <span class="spec-value">${spec.value || 'N/A'}</span>
        </div>
      `).join('');
    }
    
    // Mostrar detalles con animación
    previewDetails.style.display = 'block';
    requestAnimationFrame(() => previewDetails.classList.add('show'));

    // Configurar controles de navegación
    const prevBtn = previewArea.querySelector('.prev-model');
    const nextBtn = previewArea.querySelector('.next-model');
    
    if (prevBtn) {
      prevBtn.onclick = () => {
        const prevIndex = (currentIndex - 1 + modelos.length) % modelos.length;
        showPreview(modelos[prevIndex]);
        currentIndex = prevIndex;
        syncPreview();
      };
    }
    
    if (nextBtn) {
      nextBtn.onclick = () => {
        const nextIndex = (currentIndex + 1) % modelos.length;
        showPreview(modelos[nextIndex]);
        currentIndex = nextIndex;
        syncPreview();
      };
    }
  }

  function hidePreview() {
    previewArea.classList.remove('active');
    
    if (previewImg) {
      previewImg.classList.remove('show');
      setTimeout(() => previewImg.style.display = 'none', 300);
    }
    
    if (previewDetails) {
      previewDetails.classList.remove('show');
      setTimeout(() => previewDetails.style.display = 'none', 300);
    }
  }

  function populateModalDetails(meta) {
    if (!modalDetails) return;
    const equipHtml = meta.equipment && meta.equipment.length ? `<ul class="mb-0">${meta.equipment.map(e => `<li>${e}</li>`).join('')}</ul>` : '';
    modalDetails.innerHTML = `
      <div class="small text-muted mb-2">
        <strong>Año:</strong> ${meta.year || 'N/A'} &nbsp; • &nbsp; <strong>Motor:</strong> ${meta.engine || 'N/A'}
      </div>
      <div class="small text-muted mb-2"><strong>Potencia:</strong> ${meta.power || 'N/A'} &nbsp; • &nbsp; <strong>Precio:</strong> ${meta.price || 'Consultar'}</div>
      <div class="small text-light"><strong>Equipamiento:</strong>${equipHtml}</div>
    `;
  }

  function openModalAt(index) {
    if (!modal) return;
    if (index < 0 || index >= modelos.length) return;
    const m = modelos[index];
    const item = m.el;
    modalTitulo.textContent = m.name;
    modalImagen.src = m.src || '';
    if (modalDescripcion) modalDescripcion.textContent = m.desc || '';
    populateModalDetails(m);
    items.forEach(i => i.classList.remove('active'));
    item.classList.add('active');
    currentIndex = index;
    modal.show();
    updateCounter();
  }

  function showNext() { openModalAt((currentIndex + 1) % modelos.length); }
  function showPrev() { openModalAt((currentIndex - 1 + modelos.length) % modelos.length); }

  // Añadir controles prev/next al modal
  if (modalEl) {
    const nav = document.createElement('div');
    nav.className = 'modal-nav d-flex gap-2 position-absolute';
    const prevBtn = document.createElement('button');
    prevBtn.type = 'button';
    prevBtn.className = 'prev btn btn-sm';
    prevBtn.innerHTML = '<i class="fas fa-chevron-left"></i>';
    const nextBtn = document.createElement('button');
    nextBtn.type = 'button';
    nextBtn.className = 'next btn btn-sm';
    nextBtn.innerHTML = '<i class="fas fa-chevron-right"></i>';
    nav.appendChild(prevBtn);
    nav.appendChild(nextBtn);
    const content = modalEl.querySelector('.modal-content');
    if (content) content.appendChild(nav);
    prevBtn.addEventListener('click', (e) => { e.stopPropagation(); showPrev(); });
    nextBtn.addEventListener('click', (e) => { e.stopPropagation(); showNext(); });
  }

  items.forEach((item, idx) => {
    const m = modelos[idx];
    const imgSrc = m.src;

    // Eventos de mouse para cada item
    item.addEventListener('mouseenter', (e) => {
      // Remover active de todos los items
      items.forEach(i => i.classList.remove('active'));
      
      // Activar item actual
      item.classList.add('active');
      
      // Mostrar preview del modelo
      const modelo = modelos[idx];
      if (modelo) {
        showPreview(modelo);
        showTooltip(item, e.clientX, e.clientY);
      }
      
      currentIndex = idx;
      syncPreview();
    });
    
    item.addEventListener('mousemove', (e) => {
      if (tooltip.classList.contains('show')) {
        const rect = item.getBoundingClientRect();
        showTooltip(item, e.clientX, e.clientY);
      }
    });
    
    item.addEventListener('mouseleave', () => {
      // Solo ocultar tooltip, mantener preview
      hideTooltip();
      item.classList.remove('active');
    });

    // Mantener preview al hacer hover en el área de preview
    previewArea.addEventListener('mouseenter', () => {
      if (currentIndex === idx) {
        item.classList.add('active');
      }
    });
    
    previewArea.addEventListener('mouseleave', () => {
      hidePreview();
      item.classList.remove('active');
    });

    // Click → abrir modal en posición idx
    item.addEventListener('click', () => openModalAt(idx));
  });

  // Generar thumbs dinámicos
  const modalThumbs = document.getElementById('modalThumbs');
  function renderThumbs() {
    if (!modalThumbs) return;
    modalThumbs.innerHTML = '';
    modelos.forEach((it, i) => {
      const src = it.src;
      const thumb = document.createElement('img');
      thumb.src = src;
      thumb.loading = 'lazy';
      thumb.className = 'thumb-item rounded';
      thumb.alt = it.name;
      thumb.addEventListener('click', (e) => { e.stopPropagation(); openModalAt(i); });
      modalThumbs.appendChild(thumb);
    });
  }
  renderThumbs();

  // Actualizar contador
  const modalCounter = document.getElementById('modalCounter');
  function updateCounter() {
    if (!modalCounter) return;
    const safeIndex = currentIndex >= 0 ? currentIndex : 0;
    modalCounter.textContent = (safeIndex + 1) + ' / ' + modelos.length;
    // resaltar thumb activo
    const thumbs = Array.from(document.querySelectorAll('.thumb-item'));
    thumbs.forEach((t, i) => t.classList.toggle('active-thumb', i === currentIndex));
  }

  // sincronizar preview principal con currentIndex
  function syncPreview() {
    const m = modelos[currentIndex] || modelos[0];
    if (!m) return;
    if (previewDetails) {
      const title = previewDetails.querySelector('.preview-title');
      const description = previewDetails.querySelector('.preview-description');
      if (title) title.textContent = m.name;
      if (description) description.textContent = m.desc;
    }
    if (previewImg && m.src) {
      previewImg.src = m.src;
      previewImg.style.display = 'block';
      setTimeout(() => previewImg.classList.add('show'), 50);
    }
    items.forEach((it, i) => it.classList.toggle('active', i === currentIndex));
    updateCounter();
  }

  // controles preview prev/next/open modal
  if (previewPrev) previewPrev.addEventListener('click', () => { currentIndex = (currentIndex - 1 + modelos.length) % modelos.length; syncPreview(); });
  if (previewNext) previewNext.addEventListener('click', () => { currentIndex = (currentIndex + 1) % modelos.length; syncPreview(); });
  if (previewOpenModal) previewOpenModal.addEventListener('click', () => openModalAt(currentIndex));

  // ----- Cotización (botón + modal) -----
  const btnSolicitar = document.getElementById('btnSolicitar');
  const modalCotizaEl = document.getElementById('modalCotiza');
  const modalCotiza = modalCotizaEl ? new bootstrap.Modal(modalCotizaEl) : null;
  const cotizaModelo = document.getElementById('cotizaModelo');
  if (btnSolicitar) {
    btnSolicitar.addEventListener('click', () => {
      const nombreModelo = modelos[currentIndex] ? modelos[currentIndex].name : '';
      if (cotizaModelo) cotizaModelo.value = nombreModelo;
      modalCotiza?.show();
    });
  }

  // Enviar cotización (AJAX)
  const cotizaForm = document.getElementById('cotizaForm');
  if (cotizaForm) {
    const submitBtn = document.getElementById('cotizaSubmit');
    const submitSpinner = document.getElementById('cotizaSpinner');
    const submitText = document.getElementById('cotizaSubmitText');

    function setSubmitting(isSubmitting) {
      if (!submitBtn) return;
      submitBtn.disabled = isSubmitting;
      if (isSubmitting) {
        submitSpinner.classList.remove('d-none');
        submitText.textContent = 'Enviando...';
      } else {
        submitSpinner.classList.add('d-none');
        submitText.textContent = 'Enviar';
      }
    }

    function validateCotiza() {
      const nameEl = document.getElementById('cotizaNombre');
      const emailEl = document.getElementById('cotizaEmail');
      const modelEl = document.getElementById('cotizaModelo');
      let ok = true;

      if (!nameEl || nameEl.value.trim().length < 3) {
        nameEl.classList.add('is-invalid');
        ok = false;
      } else {
        nameEl.classList.remove('is-invalid');
      }

      const emailVal = emailEl ? emailEl.value.trim() : '';
      const emailRe = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
      if (!emailEl || !emailRe.test(emailVal)) {
        emailEl.classList.add('is-invalid');
        ok = false;
      } else {
        emailEl.classList.remove('is-invalid');
      }

      if (!modelEl || !modelEl.value.trim()) {
        modelEl.classList.add('is-invalid');
        ok = false;
      } else {
        modelEl.classList.remove('is-invalid');
      }

      return ok;
    }

    cotizaForm.addEventListener('submit', (e) => {
      e.preventDefault();
      if (!validateCotiza()) {
        if (typeof mostrarBootstrapToast === 'function') mostrarBootstrapToast('Rellena correctamente los campos del formulario.', 'warning', 4000);
        return;
      }

      const data = {
        nombre: document.getElementById('cotizaNombre').value.trim(),
        email: document.getElementById('cotizaEmail').value.trim(),
        modelo: document.getElementById('cotizaModelo').value.trim()
      };

      setSubmitting(true);

      fetch('/cotizar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
      }).then(resp => {
        setSubmitting(false);
        if (resp.ok) return resp.json().catch(() => ({}));
        return resp.json().then(j => { throw new Error(j.message || 'Error en envío'); }).catch(() => { throw new Error('Error en envío'); });
      }).then(() => {
        modalCotiza?.hide();
        // limpiar formulario
        cotizaForm.reset();
        if (typeof mostrarBootstrapToast === 'function') mostrarBootstrapToast('Solicitud enviada. Nos pondremos en contacto pronto.', 'success', 4000);
        else alert('Solicitud enviada. Nos pondremos en contacto pronto.');
      }).catch((err) => {
        console.warn('Error enviando cotización:', err);
        setSubmitting(false);
        modalCotiza?.hide();
        const msg = err?.message || 'No se pudo enviar. Intenta nuevamente.';
        if (typeof mostrarBootstrapToast === 'function') mostrarBootstrapToast(msg, 'danger', 6000);
        else alert(msg);
      });
    });
  }

  // Navegación por teclado: flecha arriba/abajo para seleccionar, Enter para abrir
  let focused = 0;
  function focusItem(i) {
    items.forEach(it => it.classList.remove('focused'));
    focused = (i + items.length) % items.length;
    const el = items[focused];
    el.classList.add('focused');
    el.scrollIntoView({ block: 'center', behavior: 'smooth' });
  }
  if (items.length) focusItem(0);

  document.addEventListener('keydown', (e) => {
    if (document.querySelector('.modal.show')) {
      if (e.key === 'ArrowRight') { e.preventDefault(); showNext(); }
      if (e.key === 'ArrowLeft') { e.preventDefault(); showPrev(); }
      if (e.key === 'Escape') { modal?.hide(); }
      return;
    }
    if (e.key === 'ArrowDown') { e.preventDefault(); focusItem(focused + 1); }
    if (e.key === 'ArrowUp') { e.preventDefault(); focusItem(focused - 1); }
    if (e.key === 'Enter') { e.preventDefault(); openModalAt(focused); }
  });

  // Cerrar preview si se redimensiona
  window.addEventListener('resize', hidePreview);
  window.addEventListener('resize', hideTooltip);

});
document.body.classList.add("preview-open");
document.body.classList.remove("preview-open");