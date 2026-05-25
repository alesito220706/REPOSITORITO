// -------------------- ELEMENTOS --------------------
const loader = document.getElementById("loader");
const contenido = document.getElementById("contenido");
const saludoBanner = document.getElementById("saludo-banner");
const saludoNombre = document.getElementById("saludo-nombre");
const btnCerrarSaludo = document.getElementById("btn-cerrar-saludo");
const loginForm = document.getElementById("loginForm");
const loginError = document.getElementById("login-error");
const toastNotif = document.getElementById("toast-notificacion");
const toastMsg = document.getElementById("toast-mensaje");
const modalCotizacion = new bootstrap.Modal(document.getElementById('modalCotizacion'));
const modalDetalles = new bootstrap.Modal(document.getElementById("modalDetalles")); 
const formCotizacion = document.getElementById('formCotizacion');
const autoSeleccionadoInput = document.getElementById('autoSeleccionado');

// -------------------- LOADER & REVEAL --------------------
window.addEventListener("load", () => {
  if (loader) {
    loader.style.opacity = "0";
    setTimeout(() => {
      loader.style.display = "none";
    }, 1200);
  }
});

function mostrarTodo() {
  const elementos = document.querySelectorAll(".reveal");
  elementos.forEach(el => {
    el.classList.add("active");
    el.style.opacity = "1";
    el.style.transform = "translateY(0)";
  });
}

// Forzar la aparición de los elementos al cargar la ventana
window.onload = () => {
  const loader = document.getElementById('loader');
  if (loader) {
    loader.style.display = 'none';
  }
  mostrarTodo();
  console.log("AutoTec: Elementos revelados");
};

// Detector de scroll para animaciones de clases reveal
window.addEventListener("scroll", () => {
  const reveals = document.querySelectorAll(".reveal");
  for (let i = 0; i < reveals.length; i++) {
    let windowHeight = window.innerHeight;
    let elementTop = reveals[i].getBoundingClientRect().top;
    if (elementTop < windowHeight - 50) {
      reveals[i].classList.add("active");
    }
  }
});

// -------------------- RELOJ LOCAL EN TIEMPO REAL --------------------
function initLuxuryClock() {
  // Buscamos dinámicamente el contenedor del reloj en el Header
  const clockElement = document.querySelector(".luxury-header .top-bar-meta span:nth-child(2) .text-white");
  
  if (!clockElement) return;

  const updateClock = () => {
    const ahora = new Date();
    // Extraemos las horas, minutos y segundos del dispositivo local
    const horas = String(ahora.getHours()).padStart(2, "0");
    const minutos = String(ahora.getMinutes()).padStart(2, "0");
    const segundos = String(ahora.getSeconds()).padStart(2, "0");
    
    clockElement.innerHTML = `${horas}:${minutos}:${segundos} LOCAL`;
  };

  updateClock();
  setInterval(updateClock, 1000);
}

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
    toastEl.addEventListener('hidden.bs.toast', () => { toastEl.remove(); });
  } catch (e) {
    console.warn('No se pudo mostrar toast nativo', e);
  }
}

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
  "Koenigsegg Jesko": { precio: "$3,000,000", motor: "V8 Twin-Turbo 5.0L", potencia: "1,600 HP", velocidad: "480 km/h", aceleracion: "0-100 km/h en 2.5s" },
  "Bugatti Chiron": { precio: "$3,500,000", motor: "W16 Quad-Turbo 8.0L", potencia: "1,500 HP", velocidad: "420 km/h", aceleracion: "0-100 km/h en 2.4s" },
  "Koenigsegg Agera": { precio: "$2,800,000", motor: "V8 Twin-Turbo 5.0L", potencia: "1,360 HP", velocidad: "447 km/h", aceleracion: "0-100 km/h en 2.8s" },
  "McLaren P1": { precio: "$1,200,000", motor: "V8 Twin-Turbo Híbrido 3.8L", potencia: "903 HP", velocidad: "350+ km/h", aceleracion: "0-100 km/h en 2.8s" },
  "Ferrari LaFerrari": { precio: "$1,500,000", motor: "V12 Híbrido 6.3L", potencia: "963 HP", velocidad: "350 km/h", aceleracion: "0-100 km/h en 2.6s" },
  "Lamborghini Aventador": { precio: "$800,000", motor: "V12 6.5L", potencia: "770 HP", velocidad: "350 km/h", aceleracion: "0-100 km/h en 2.8s" },
  "Porsche 911 Turbo S": { precio: "$250,000", motor: "Bóxer 3.8L Biturbo", potencia: "640 HP", velocidad: "330 km/h", aceleracion: "0-100 km/h en 2.7s" },
  "Aston Martin DBS": { precio: "$350,000", motor: "V12 5.2L Biturbo", potencia: "715 HP", velocidad: "340 km/h", aceleracion: "0-100 km/h en 3.4s" },
  "Bugatti La Voiture Noire": { precio: "$18,700,000", motor: "W16 Quad-Turbo 8.0L", potencia: "1,479 HP", velocidad: "420 km/h", aceleracion: "0-100 km/h en 2.5s" },
  "Rimac Concept One": { precio: "$1,000,000", motor: "Eléctrico 4 Motores", potencia: "1,224 HP", velocidad: "355 km/h", aceleracion: "0-100 km/h en 2.6s" },
  "SF90": { precio: "$625,000", motor: "V8 Híbrido 4.0L", potencia: "986 HP", velocidad: "340 km/h", aceleracion: "0-100 km/h en 2.5s" }
};

// -------------------- DOM CONTENT LOADED --------------------
document.addEventListener('DOMContentLoaded', () => {
  // Inicialización del Reloj Local
  initLuxuryClock();
  
  // Inicialización de la Terminal IA
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

// ==================== WIDGET IA CORREGIDO ====================
function setupAIWidget() {
  const aiToggle = document.getElementById('aiToggle');
  const aiPanel = document.querySelector('#aiChatWidget .ai-panel') || document.querySelector('#aiChatWidget .ai-cyber-panel');
  const aiClose = document.getElementById('aiClose');
  const aiForm = document.getElementById('aiForm');
  const aiInput = document.getElementById('aiInput');
  const aiMessages = document.getElementById('aiMessages');

  if (!aiToggle || !aiPanel) return;

  // Manejo de Estados Globales del Flujo Guiado
  let aiState = null; 
  let aiPrefs = {};

  function appendMessage(text, who='bot') {
    const div = document.createElement('div');
    div.className = 'msg ' + who;
    // Estilos rápidos en línea para compatibilidad con la hoja cyberpunk
    div.style.marginBottom = "12px";
    div.style.lineHeight = "1.4";
    
    if (who === 'user') {
      div.style.textAlign = "right";
      div.innerHTML = `<span style="background: rgba(255,255,255,0.04); border: 1px solid rgba(255,255,255,0.1); padding: 6px 12px; border-radius: 8px 8px 0 8px; display: inline-block; color: #fff;">${text}</span>`;
    } else {
      div.style.textAlign = "left";
      div.innerHTML = `<span class="text-neon fw-bold" style="color: #00f0ff; font-weight: bold;">AI:</span> ${text}`;
    }
    
    aiMessages.appendChild(div);
    aiMessages.scrollTop = aiMessages.scrollHeight;
  }

  function appendOptions(options = []) {
    if (!options || !options.length) return;
    const wrap = document.createElement('div');
    wrap.className = 'bot-options d-flex flex-wrap gap-2 p-2';
    options.forEach(opt => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'btn btn-sm btn-outline-warning btn-qa-cyber';
      btn.style.fontSize = '9px';
      btn.textContent = opt;
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        if (opt.toLowerCase().includes('ayúdame') || opt.toLowerCase().includes('ayudame') || opt.toLowerCase().includes('algoritmo')) {
          startChooseFlow();
          return;
        }
        sendPromptToAi(opt);
      });
      wrap.appendChild(btn);
    });
    aiMessages.appendChild(wrap);
    aiMessages.scrollTop = aiMessages.scrollHeight;
  }

  // EVENTO DE APERTURA: Corregido forzando estilos CSS inline inline directos
  aiToggle.addEventListener('click', (e) => {
    e.stopPropagation();
    if (aiPanel.style.display === 'none' || aiPanel.style.display === '') {
      aiPanel.style.display = 'block';
      aiPanel.setAttribute('aria-hidden', 'false');
      if (aiInput) aiInput.focus();
    } else {
      aiPanel.style.display = 'none';
      aiPanel.setAttribute('aria-hidden', 'true');
    }
  });

  aiClose.addEventListener('click', (e) => {
    e.stopPropagation();
    aiPanel.style.display = 'none';
    aiPanel.setAttribute('aria-hidden', 'true');
  });

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
      'Puedes visitarnos en nuestra sede. El Atelier Privado se encuentra en el Circuit de la Sarthe, Box 26.',
      'Estaremos encantados de recibirte — ¿te gustaría agendar una visita?'
    ];
    const greetingReplies = [
      'Conexión segura establecida. Soy el algoritmo AUTOTEC_AI Core. ¿En qué puedo ayudarte hoy?',
      '¡Bienvenido, Operador! Puedo ayudarte con cotizaciones, especificaciones técnicas o recomendaciones predictivas.'
    ];
    const defaultReplies = [
      'Comando recibido. Procesando solicitud de activos de lujo... ¿Quieres que te conecte con un asesor técnico?',
      'Interesante consulta. ¿Deseas que busquemos configuraciones específicas de pista o que te envíe una cotización privada?'
    ];

    if (lower.includes('financ') || lower.includes('cuota') || lower.includes('credito') || lower.includes('cotiz')) return rand(financeReplies);
    if (lower.includes('hora') || lower.includes('horario') || lower.includes('abre')) return rand(scheduleReplies);
    if (lower.includes('visita') || lower.includes('sede') || lower.includes('donde') || lower.includes('ubicacion')) return rand(visitReplies);
    if (lower.includes('hola') || lower.includes('buen') || lower.includes('hey')) return rand(greetingReplies);
    return rand(defaultReplies);
  }

  function startChooseFlow() {
    aiState = 'choose_budget';
    aiPrefs = {};
    appendMessage('Perfecto — Iniciando algoritmo predictivo de activos. Primero, ¿cuál es tu presupuesto aproximado? (ej: bajo, medio, alto o un monto en USD)', 'bot');
  }

  function recommendModels(prefs) {
    const tagsFor = (modelo, info) => {
      const tags = new Set();
      const name = modelo.toLowerCase();
      const motor = (info.motor || '').toLowerCase();
      const potencia = parseInt((info.potencia||'').replace(/[^0-9]/g,'')) || 0;
      if (motor.includes('eléctr') || name.includes('rimac')) tags.add('electrico');
      if (potencia >= 900 || /bugatti|koenigsegg|ferrari|lamborghini|mclaren/i.test(modelo)) tags.add('deportivo');
      if (potencia >= 600 && potencia < 900) tags.add('performance');
      if (info.precio && /\$/i.test(info.precio)) {
        const num = parseInt(info.precio.replace(/[^0-9]/g,'')) || 0;
        if (num >= 2000000) tags.add('ultra');
        else if (num >= 1000000) tags.add('high');
        else if (num >= 300000) tags.add('medium');
        else tags.add('low');
      }
      return Array.from(tags);
    };

    const scored = Object.keys(detallesAutos).map(m => {
      const info = detallesAutos[m];
      const tags = tagsFor(m, info);
      let score = 0; 
      if (prefs.budget) {
        if (prefs.budget === 'alto' && tags.includes('high')) score += 3;
        if (prefs.budget === 'medio' && tags.includes('medium')) score += 3;
        if (prefs.budget === 'bajo' && tags.includes('low')) score += 3;
      }
      if (prefs.use) {
        if (prefs.use === 'deportivo' && tags.includes('deportivo')) score += 4;
        if (prefs.use === 'electrico' && tags.includes('electrico')) score += 4;
        if (prefs.use === 'familiar' && !tags.includes('deportivo')) score += 2;
      }
      score += (parseInt((info.potencia||'').replace(/[^0-9]/g,''))||0) / 1000;
      return {modelo: m, score, info};
    });

    scored.sort((a,b)=>b.score-a.score);
    const picks = scored.slice(0,3);
    return picks.map(p => `${p.modelo} (${p.info.precio}). Coincide con tu perfil por su bloque de potencia de ${p.info.potencia}.`);
  }

  // GESTIÓN UNIFICADA DEL FORMULARIO DE ENVÍO
  aiForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const q = aiInput.value.trim();
    if (!q) return;

    appendMessage(q, 'user');
    aiInput.value = '';

    // Si el flujo guiado está activo, desviamos el flujo conversacional hacia la toma de decisiones
    if (aiState) {
      procesarFlujoGuiado(q);
      return;
    }

    // Flujo por defecto (Mapeo por palabras clave / simulación de servidor remoto)
    const typingEl = document.createElement('div');
    typingEl.className = 'msg bot typing';
    typingEl.style.color = "rgba(255,255,255,0.5)";
    typingEl.style.fontStyle = "italic";
    typingEl.style.marginBottom = "12px";
    typingEl.textContent = 'Cifrando respuesta cuántica...';
    aiMessages.appendChild(typingEl);
    aiMessages.scrollTop = aiMessages.scrollHeight;

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 1500);

    fetch('/ai-chat', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({prompt:q}), signal: controller.signal })
      .then(r => {
        clearTimeout(timeout);
        if (!r.ok) return Promise.reject(new Error('Local fallback integration active'));
        return r.json();
      })
      .then(data => {
        const answer = data && (data.answer || data.result || data.reply) ? (data.answer || data.result || data.reply) : null;
        if (answer) {
          typingEl.remove();
          appendMessage(answer, 'bot');
          appendOptions(['Algoritmo Predictivo', 'Solicitar cotización', 'Horario', 'Ubicación']);
        } else {
          fallbackLocal(typingEl, q);
        }
      }).catch(() => {
        fallbackLocal(typingEl, q);
      });
  });

  function fallbackLocal(elementToRemove, query) {
    if (elementToRemove && elementToRemove.parentNode) elementToRemove.remove();
    const resp = localResponder(query);
    appendMessage(resp, 'bot');
    appendOptions(['Algoritmo Predictivo', 'Solicitar cotización']);
  }

  function sendPromptToAi(prompt) {
    appendMessage(prompt, 'user');
    const resp = localResponder(prompt);
    setTimeout(() => {
      appendMessage(resp, 'bot');
    }, 400);
  }

  // Manejador secundario para procesar el árbol de decisiones del cuestionario
  function procesarFlujoGuiado(q) {
    if (aiState === 'choose_budget') {
      const lowWords = ['bajo','barato','economico','pequeño'];
      const highWords = ['alto','caro','lujo','premium'];
      const medioWords = ['medio','regular','intermedio'];
      const lq = q.toLowerCase();
      
      if (lowWords.some(w=>lq.includes(w))) aiPrefs.budget='bajo';
      else if (highWords.some(w=>lq.includes(w))) aiPrefs.budget='alto';
      else if (medioWords.some(w=>lq.includes(w))) aiPrefs.budget='medio';
      else {
        const num = parseInt(q.replace(/[^0-9]/g,''));
        if (!isNaN(num)) {
          if (num >= 1000000) aiPrefs.budget='alto';
          else if (num >= 300000) aiPrefs.budget='medio';
          else aiPrefs.budget='bajo';
        } else aiPrefs.budget='medio';
      }
      aiState = 'choose_use';
      setTimeout(() => appendMessage('Perfecto. ¿Qué uso principal tendrá el auto? (deportivo, familiar, ciudad, eléctrico)', 'bot'), 300);
      return;
    }

    if (aiState === 'choose_use') {
      const lq = q.toLowerCase();
      if (lq.includes('deport') || lq.includes('rápid') || lq.includes('veloc')) aiPrefs.use='deportivo';
      else if (lq.includes('electr') || lq.includes('eléctr')) aiPrefs.use='electrico';
      else if (lq.includes('famil') || lq.includes('familiar')) aiPrefs.use='familiar';
      else aiPrefs.use='ciudad';
      aiState = 'choose_seats';
      setTimeout(() => appendMessage('¿Cuántas plazas necesitas en cabina? (ej: 2, 4)', 'bot'), 300);
      return;
    }

    if (aiState === 'choose_seats') {
      const num = parseInt(q.replace(/[^0-9]/g,'')) || 2;
      aiPrefs.seats = num;
      const recs = recommendModels(aiPrefs);
      
      setTimeout(() => {
        appendMessage('Analizando vectores... Aquí tienes las mejores opciones para tu garaje:', 'bot');
        recs.forEach(r => appendMessage(r, 'bot'));
        appendMessage('¿Deseas que prepare una orden de cotización confidencial para alguno de ellos?', 'bot');
        aiState = null; // Reiniciar estado conversacional libre
      }, 500);
      return;
    }
  }

  // Botones rápidos del Grid Inferior de la IA
  document.querySelectorAll('.ai-quick-actions button').forEach(btn => {
    btn.addEventListener('click', () => {
      const p = btn.getAttribute('data-prompt');
      aiPanel.style.display = 'block';
      aiPanel.setAttribute('aria-hidden','false');
      if (p) sendPromptToAi(p);
    });
  });

  const aiHelpChooseBtn = document.getElementById('aiHelpChoose');
  if (aiHelpChooseBtn) {
    aiHelpChooseBtn.addEventListener('click', () => {
      aiPanel.style.display = 'block';
      aiPanel.setAttribute('aria-hidden', 'false');
      startChooseFlow();
    });
  }

  // Renderizar saludo base por defecto en el arranque limpio
  if (aiMessages && aiMessages.children.length === 0) {
    appendMessage('Conexión segura establecida. Soy el algoritmo AUTOTEC_AI Core. ¿Qué activo de nuestra matriz desea analizar hoy?', 'bot');
  }
}

// Enlace rápido para el traspaso de datos entre modales integrados
window.abrirCotizacionDirecta = function(nombreModelo) {
  if (autoSeleccionadoInput) {
    autoSeleccionadoInput.value = nombreModelo.toUpperCase();
  }
};