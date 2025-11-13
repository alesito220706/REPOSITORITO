// -------------------- ELEMENTOS --------------------
const loader = document.getElementById("loader");
const contenido = document.getElementById("contenido");
// loginContainer eliminado porque no existe en esta página
const saludoBanner = document.getElementById("saludo-banner");
const saludoNombre = document.getElementById("saludo-nombre");
const btnCerrarSaludo = document.getElementById("btn-cerrar-saludo");
const loginForm = document.getElementById("loginForm");
const loginError = document.getElementById("login-error");
const toastNotif = document.getElementById("toast-notificacion");
const toastMsg = document.getElementById("toast-mensaje");
const modalCotizacion = new bootstrap.Modal(document.getElementById('modalCotizacion'));
const modalDetalles = new bootstrap.Modal(document.getElementById("modalDetalles")); // instancia global
const formCotizacion = document.getElementById('formCotizacion');
const autoSeleccionadoInput = document.getElementById('autoSeleccionado');

// -------------------- LOADER --------------------
window.addEventListener("load", () => {
  loader.style.opacity = "0";
  setTimeout(() => {
    loader.style.display = "none";
  }, 1200);
});

// -------------------- MOSTRAR DETALLES --------------------
function mostrarDetalles(modelo) {
  const detalle = detallesAutos[modelo];
  if (!detalle) return;

  const modalTitulo = document.getElementById("modalTitulo");
  const modalCuerpo = document.getElementById("modalCuerpo");

  modalTitulo.textContent = modelo;
  modalCuerpo.innerHTML = `
    <div class="row g-3">
      <div class="col-md-6">
        <h6>Especificaciones</h6>
        <ul class="list-unstyled mb-0">
          <li><strong>Precio:</strong> ${detalle.precio}</li>
          <li><strong>Motor:</strong> ${detalle.motor}</li>
          <li><strong>Potencia:</strong> ${detalle.potencia}</li>
          <li><strong>Velocidad máxima:</strong> ${detalle.velocidad}</li>
          <li><strong>Aceleración:</strong> ${detalle.aceleracion}</li>
        </ul>
      </div>
      <div class="col-md-6">
        <p>Este excepcional vehículo representa la cúspide de la ingeniería automotriz.</p>
        <p class="mb-1"><strong>Disponible para:</strong></p>
        <ul class="mb-0">
          <li>Venta directa</li>
          <li>Financiamiento</li>
          <li>Leasing</li>
        </ul>
      </div>
    </div>
  `;

  modalDetalles.show();
}

// -------------------- COTIZACIÓN --------------------
function solicitarCotizacion(modelo) {
  autoSeleccionadoInput.value = modelo;
  modalCotizacion.show();
}

formCotizacion.addEventListener("submit", e => {
  e.preventDefault();
  const nombre = document.getElementById("cotizacionNombre").value.trim();
  const correo = document.getElementById("cotizacionCorreo").value.trim();

  if (nombre.length < 3) {
    mostrarToast("Por favor ingresa un nombre válido.", 4000);
    return;
  }
  if (!correo || !correo.includes("@")) {
    mostrarToast("Por favor ingresa un correo válido.", 4000);
    return;
  }

  modalCotizacion.hide();
  mostrarToast("¡Gracias por solicitar la cotización! Nuestro equipo contactará pronto.");
  formCotizacion.reset();
});

// -------------------- TOAST --------------------
function mostrarToast(mensaje, tiempo = 3000) {
  // Preferir toasts nativos de Bootstrap si el contenedor existe
  if (typeof mostrarBootstrapToast === 'function') {
    mostrarBootstrapToast(mensaje, 'info', tiempo);
    return;
  }
  toastMsg.textContent = mensaje;
  toastNotif.classList.add("visible");
  setTimeout(() => {
    toastNotif.classList.remove("visible");
  }, tiempo);
}

// Crea y muestra un Bootstrap native toast en #toastContainer
function mostrarBootstrapToast(mensaje, type = 'info', delay = 3000) {
  try {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const id = 'toast-' + Date.now();
    const toastEl = document.createElement('div');
    toastEl.className = 'toast align-items-center text-bg-dark border-0 mb-2';
    toastEl.id = id;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');
    toastEl.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${mensaje}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
      </div>
    `;
    container.appendChild(toastEl);
    const toast = new bootstrap.Toast(toastEl, { delay: delay });
    toast.show();
    // Remover del DOM después de ocultarse
    toastEl.addEventListener('hidden.bs.toast', () => { toastEl.remove(); });
  } catch (e) {
    console.warn('No se pudo mostrar toast nativo', e);
  }
}

// -------------------- SCROLL SUAVE --------------------
// Ya implementado por CSS scroll-behavior

// -------------------- NAV LINK ACTIVO --------------------
window.addEventListener("scroll", () => {
  let current = "";
  document.querySelectorAll("section").forEach(sec => {
    const top = sec.offsetTop - 130;
    if (window.pageYOffset >= top) current = sec.id;
  });
  document.querySelectorAll(".nav-link").forEach(link => {
    link.classList.remove("active");
    if (link.getAttribute("href") === "#" + current) link.classList.add("active");
  });
});

// -------------------- ANIMAR CARDS --------------------
const cards = document.querySelectorAll(".card.card-anim");
const observerOptions = { threshold: 0.15 };

const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
      observer.unobserve(entry.target);
    }
  });
}, observerOptions);

cards.forEach(card => observer.observe(card));

// -------------------- DATOS AUTOS --------------------
const detallesAutos = {
  "Koenigsegg Jesko": {
    precio: "$3,000,000",
    motor: "V8 Twin-Turbo 5.0L",
    potencia: "1,600 HP",
    velocidad: "480 km/h",
    aceleracion: "0-100 km/h en 2.5s",
  },
  "Bugatti Chiron": {
    precio: "$3,500,000",
    motor: "W16 Quad-Turbo 8.0L",
    potencia: "1,500 HP",
    velocidad: "420 km/h",
    aceleracion: "0-100 km/h en 2.4s",
  },
  "Koenigsegg Agera": {
    precio: "$2,800,000",
    motor: "V8 Twin-Turbo 5.0L",
    potencia: "1,360 HP",
    velocidad: "447 km/h",
    aceleracion: "0-100 km/h en 2.8s",
  },
  "McLaren P1": {
    precio: "$1,200,000",
    motor: "V8 Twin-Turbo Híbrido 3.8L",
    potencia: "903 HP",
    velocidad: "350+ km/h",
    aceleracion: "0-100 km/h en 2.8s",
  },
  "Ferrari LaFerrari": {
    precio: "$1,500,000",
    motor: "V12 Híbrido 6.3L",
    potencia: "963 HP",
    velocidad: "350 km/h",
    aceleracion: "0-100 km/h en 2.6s",
  },
  "Lamborghini Aventador": {
    precio: "$800,000",
    motor: "V12 6.5L",
    potencia: "770 HP",
    velocidad: "350 km/h",
    aceleracion: "0-100 km/h en 2.8s",
  },
  "Porsche 911 Turbo S": {
    precio: "$250,000",
    motor: "Bóxer 3.8L Biturbo",
    potencia: "640 HP",
    velocidad: "330 km/h",
    aceleracion: "0-100 km/h en 2.7s",
  },
  "Aston Martin DBS": {
    precio: "$350,000",
    motor: "V12 5.2L Biturbo",
    potencia: "715 HP",
    velocidad: "340 km/h",
    aceleracion: "0-100 km/h en 3.4s",
  },
  "Bugatti La Voiture Noire": {
    precio: "$18,700,000",
    motor: "W16 Quad-Turbo 8.0L",
    potencia: "1,479 HP",
    velocidad: "420 km/h",
    aceleracion: "0-100 km/h en 2.5s",
  },
  "Rimac Concept One": {
    precio: "$1,000,000",
    motor: "Eléctrico 4 Motores",
    potencia: "1,224 HP",
    velocidad: "355 km/h",
    aceleracion: "0-100 km/h en 2.6s",
  },
  "SF90": {
    precio: "$625,000",
    motor: "V8 Híbrido 4.0L",
    potencia: "986 HP",
    velocidad: "340 km/h",
    aceleracion: "0-100 km/h en 2.5s",
  }
};

document.addEventListener('DOMContentLoaded', () => {
  // Map feature removed — only initialize AI widget and other behaviors
  setupAIWidget();
  // Botón para solicitar cita desde la página de contacto
  const btnSolicitarCita = document.getElementById('btnSolicitarCita');
  if (btnSolicitarCita) {
    btnSolicitarCita.addEventListener('click', () => {
      autoSeleccionadoInput.value = '';
      modalCotizacion.show();
    });
  }
});

// ===== Widget IA (simulado / con backend opcional) =====
function setupAIWidget() {
  const aiToggle = document.getElementById('aiToggle');
  const aiPanel = document.querySelector('#aiChatWidget .ai-panel');
  const aiClose = document.getElementById('aiClose');
  const aiForm = document.getElementById('aiForm');
  const aiInput = document.getElementById('aiInput');
  const aiMessages = document.getElementById('aiMessages');

  function appendMessage(text, who='bot') {
    const div = document.createElement('div');
    div.className = 'msg ' + who;
    div.textContent = text;
    aiMessages.appendChild(div);
    aiMessages.scrollTop = aiMessages.scrollHeight;
  }

  // Añadir opciones de respuesta (chips) que el usuario puede pulsar
  function appendOptions(options = []) {
    if (!options || !options.length) return;
    const wrap = document.createElement('div');
    wrap.className = 'bot-options d-flex gap-2 p-2';
    options.forEach(opt => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'btn btn-sm btn-outline-warning';
      btn.textContent = opt;
      btn.addEventListener('click', () => {
        // si la opción es el flujo guiado, iniciarlo localmente
        if (opt.toLowerCase().includes('ayúdame') || opt.toLowerCase().includes('ayudame')) {
          startChooseFlow();
          return;
        }
        // enviar prompt como si el usuario lo hubiera escrito
        sendPromptToAi(opt);
      });
      wrap.appendChild(btn);
    });
    aiMessages.appendChild(wrap);
    aiMessages.scrollTop = aiMessages.scrollHeight;
  }
  aiToggle.addEventListener('click', () => {
    if (aiPanel.style.display === 'block') { aiPanel.style.display = 'none'; aiPanel.setAttribute('aria-hidden','true'); }
    else { aiPanel.style.display = 'block'; aiPanel.setAttribute('aria-hidden','false'); aiInput.focus(); }
  });
  aiClose.addEventListener('click', () => { aiPanel.style.display = 'none'; aiPanel.setAttribute('aria-hidden','true'); });

  // Local responder: plantillas variadas y matching por keywords
  function localResponder(q) {
    const lower = q.toLowerCase();
    const rand = (arr) => arr[Math.floor(Math.random()*arr.length)];

    const financeReplies = [
      'Ofrecemos financiamientos desde 6.9% anual. ¿Quieres que calculemos una cuota estimada?',
      'Tenemos planes a 12, 24 y 36 meses. ¿Qué plazo te interesa?',
      'Puedo pedirte una cotización sin compromiso: ¿deseas continuar?'
    ];
    const scheduleReplies = [
      'Nuestro horario es Lun-Vie 9:00 - 19:00 y Sáb 10:00 - 14:00.',
      'Atendemos de lunes a viernes de 9 a 19 hs. ¿Quieres que te muestre la dirección?'
    ];
    const visitReplies = [
      'Puedes visitarnos en nuestra sede. Abre el mapa en el pie de página para más detalles.',
      'Estaremos encantados de recibirte — ¿te gustaría agendar una visita?'
    ];
    const greetingReplies = [
      '¡Hola! ¿En qué puedo ayudarte hoy?',
      '¡Bienvenido! Puedo ayudarte con cotizaciones, horarios o detalles de modelos.'
    ];
    const defaultReplies = [
      'Lo siento, no entiendo completamente. ¿Quieres que te conecte con un asesor humano?',
      'Interesante. ¿Quieres que busque más información o que te envíe una cotización?'
    ];

    if (lower.includes('financ') || lower.includes('cuota') || lower.includes('credito')) return rand(financeReplies);
    if (lower.includes('hora') || lower.includes('horario') || lower.includes('abre')) return rand(scheduleReplies);
    if (lower.includes('visita') || lower.includes('sede') || lower.includes('donde')) return rand(visitReplies);
    if (lower.includes('hola') || lower.includes('buen') || lower.includes('hey')) return rand(greetingReplies);
    return rand(defaultReplies);
  }

  // --- Mejoras: flujo guiado para elegir un auto
  // Estado local del asistente (por sesión en la página)
  let aiState = null; // null | 'choose_budget' | 'choose_use' | 'choose_seats' | 'done'
  let aiPrefs = {};

  function resetAiFlow() { aiState = null; aiPrefs = {}; }

  function startChooseFlow() {
    aiState = 'choose_budget';
    aiPrefs = {};
    appendMessage('Perfecto — te ayudo a elegir. Primero, ¿cuál es tu presupuesto aproximado? (ej: bajo, medio, alto o un rango en USD)', 'bot');
  }

  function recommendModels(prefs) {
    // Construir tags simples a partir de detalles
    const tagsFor = (modelo, info) => {
      const tags = new Set();
      const name = modelo.toLowerCase();
      const motor = (info.motor || '').toLowerCase();
      const potencia = parseInt((info.potencia||'').replace(/[^0-9]/g,'')) || 0;
      if (motor.includes('eléctr') || name.includes('rimac') || name.includes('rimac')) tags.add('electrico');
      if (potencia >= 900 || /bugatti|koenigsegg|ferrari|lamborghini|mclaren/i.test(modelo)) tags.add('deportivo');
      if (potencia >= 600 && potencia < 900) tags.add('performance');
      if (potencia < 600) tags.add('efficient');
      if (info.precio && /\$/i.test(info.precio)) {
        const num = parseInt(info.precio.replace(/[^0-9]/g,'')) || 0;
        if (num >= 2000000) tags.add('ultra');
        else if (num >= 1000000) tags.add('high');
        else if (num >= 300000) tags.add('medium');
        else tags.add('low');
      }
      return Array.from(tags);
    };

    // Score models by matching tags
    const scored = Object.keys(detallesAutos).map(m => {
      const info = detallesAutos[m];
      const tags = tagsFor(m, info);
      let score = 0; if (prefs.budget) {
        if (prefs.budget === 'alto' && tags.includes('high')) score += 3;
        if (prefs.budget === 'medio' && tags.includes('medium')) score += 3;
        if (prefs.budget === 'bajo' && tags.includes('low')) score += 3;
      }
      if (prefs.use) {
        if (prefs.use === 'deportivo' && tags.includes('deportivo')) score += 4;
        if (prefs.use === 'electrico' && tags.includes('electrico')) score += 4;
        if (prefs.use === 'familiar' && !tags.includes('deportivo')) score += 2;
      }
      if (prefs.seats) {
        // no seat data; small bias: family gets slight boost if not hyper-sport
        if (prefs.seats >= 5 && !tags.includes('deportivo')) score += 1;
      }
      // base potency heuristic
      score += (parseInt((info.potencia||'').replace(/[^0-9]/g,''))||0) / 1000;
      return {modelo: m, score, info};
    });

    scored.sort((a,b)=>b.score-a.score);
    const picks = scored.slice(0,3);
    // generar mensajes con razones
    return picks.map(p => `${p.modelo} — ${p.info.precio}. Recomendado porque coincide con tus preferencias y ofrece ${p.info.potencia} de potencia.`);
  }


  // Submit handler improved: typing element reference + backend timeout + local fallback
  aiForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const q = aiInput.value.trim();
    if (!q) return;
    appendMessage(q, 'user');
    aiInput.value = '';

    // Crear mensaje de typing y mantener referencia para reemplazo
    const typingEl = document.createElement('div');
    typingEl.className = 'msg bot typing';
    typingEl.textContent = 'Escribiendo...';
    aiMessages.appendChild(typingEl);
    aiMessages.scrollTop = aiMessages.scrollHeight;

    // Intento al backend con timeout; si falla, usar localResponder
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 3000);

    fetch('/ai-chat', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({prompt:q}), signal: controller.signal })
      .then(r => {
        clearTimeout(timeout);
        if (!r.ok) return Promise.reject(new Error('Bad response'));
        return r.json();
      })
      .then(data => {
        const answer = data && (data.answer || data.result || data.reply) ? (data.answer || data.result || data.reply) : null;
        if (answer) {
          // pequeña latencia simulada para suavizar UX
          setTimeout(() => { typingEl.textContent = answer; aiMessages.scrollTop = aiMessages.scrollHeight; appendOptions(['Ayúdame a elegir','Ver modelos','Solicitar cotización','Horario','Ubicación']); }, 300);
        } else {
          // backend OK pero sin campo claro -> fallback
          const resp = localResponder(q);
          setTimeout(() => { typingEl.textContent = resp; aiMessages.scrollTop = aiMessages.scrollHeight; appendOptions(['Ayúdame a elegir','Ver modelos','Solicitar cotización']); }, 300);
        }
      }).catch((err) => {
        // fallback local
        const resp = localResponder(q);
        setTimeout(() => { typingEl.textContent = resp; aiMessages.scrollTop = aiMessages.scrollHeight; appendOptions(['Ayúdame a elegir','Ver modelos','Solicitar cotización']); }, 200);
        console.warn('AI backend failed or timed out, used local responder', err);
      });
  });

  // Helper to send a prompt programmatically (used by quick-action buttons)
  function sendPromptToAi(prompt) {
    aiInput.value = '';
    // reuse the same submit logic but without duplicating UI code: create typing el and call backend
    const typingEl = document.createElement('div');
    typingEl.className = 'msg bot typing';
    typingEl.textContent = 'Escribiendo...';
    aiMessages.appendChild(typingEl);
    aiMessages.scrollTop = aiMessages.scrollHeight;

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 3000);

    fetch('/ai-chat', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({prompt:prompt}), signal: controller.signal })
      .then(r => {
        clearTimeout(timeout);
        if (!r.ok) return Promise.reject(new Error('Bad response'));
        return r.json();
      })
      .then(data => {
        const answer = data && (data.answer || data.result || data.reply) ? (data.answer || data.result || data.reply) : null;
        if (answer) {
          setTimeout(() => { typingEl.textContent = answer; aiMessages.scrollTop = aiMessages.scrollHeight; appendOptions(['Ayúdame a elegir','Ver modelos','Solicitar cotización']); }, 300);
        } else {
          const resp = localResponder(prompt);
          setTimeout(() => { typingEl.textContent = resp; aiMessages.scrollTop = aiMessages.scrollHeight; appendOptions(['Ayúdame a elegir','Ver modelos']); }, 300);
        }
      }).catch((err) => {
        const resp = localResponder(prompt);
        setTimeout(() => { typingEl.textContent = resp; aiMessages.scrollTop = aiMessages.scrollHeight; appendOptions(['Ayúdame a elegir','Ver modelos']); }, 200);
        console.warn('AI backend failed or timed out, used local responder', err);
      });
  }

  // Quick action buttons
  document.querySelectorAll('.ai-quick-actions button').forEach(btn => {
    btn.addEventListener('click', () => {
      const p = btn.getAttribute('data-prompt');
      // show panel and send prompt
      aiPanel.style.display = 'block'; aiPanel.setAttribute('aria-hidden','false');
      if (p) sendPromptToAi(p);
    });
  });

  // Bot: botón especial para iniciar flujo 'Ayúdame a elegir'
  const aiHelpChooseBtn = document.getElementById('aiHelpChoose');
  if (aiHelpChooseBtn) aiHelpChooseBtn.addEventListener('click', () => { aiPanel.style.display='block'; aiPanel.setAttribute('aria-hidden','false'); startChooseFlow(); });

  // Intercept form submissions to support the guided flow
  aiForm.addEventListener('submit', (e) => {
    // note: the primary submit handler above already exists; to avoid duplicate bindings,
    // here we only handle continued flow when aiState is active.
    if (!aiState) return; // let original handler process
    // prevent the default flow when in our guided state
    e.preventDefault();
    const q = aiInput.value.trim(); if (!q) return;
    appendMessage(q, 'user'); aiInput.value = '';

    if (aiState === 'choose_budget') {
      // normalize budget
      const lowWords = ['bajo','barato','economico','pequeño','<='];
      const highWords = ['alto','caro','lujo','premium','>'];
      const medioWords = ['medio','regular','intermedio'];
      const lq = q.toLowerCase();
      if (lowWords.some(w=>lq.includes(w))) aiPrefs.budget='bajo';
      else if (highWords.some(w=>lq.includes(w))) aiPrefs.budget='alto';
      else if (medioWords.some(w=>lq.includes(w))) aiPrefs.budget='medio';
      else {
        // try to detect numeric
        const num = parseInt(q.replace(/[^0-9]/g,''));
        if (!isNaN(num)) {
          if (num >= 1000000) aiPrefs.budget='alto';
          else if (num >= 300000) aiPrefs.budget='medio';
          else aiPrefs.budget='bajo';
        } else aiPrefs.budget='medio';
      }
      aiState = 'choose_use';
      appendMessage('Perfecto. ¿Qué uso principal tendrá el auto? (deportivo, familiar, ciudad, eléctrico)', 'bot');
      return;
    }

    if (aiState === 'choose_use') {
      const lq = q.toLowerCase();
      if (lq.includes('deport') || lq.includes('rápid') || lq.includes('veloc')) aiPrefs.use='deportivo';
      else if (lq.includes('electr') || lq.includes('eléctr')) aiPrefs.use='electrico';
      else if (lq.includes('famil') || lq.includes('familiar')) aiPrefs.use='familiar';
      else aiPrefs.use='ciudad';
      aiState = 'choose_seats';
      appendMessage('¿Cuántas plazas necesitas? (ej: 2, 4, 5)', 'bot');
      return;
    }

    if (aiState === 'choose_seats') {
      const num = parseInt(q.replace(/[^0-9]/g,'')) || 4;
      aiPrefs.seats = num;
      // compute recommendations
      const recs = recommendModels(aiPrefs);
      for (const r of recs) appendMessage(r, 'bot');
      appendMessage('¿Quieres que te muestre detalles de alguno de estos modelos o que solicite una cotización?', 'bot');
      aiState = 'done';
      return;
    }
  });
}
