/*
  Nueva animación 'prestigio' con lógica JS dedicada.
  - Usa canvas para partículas sutiles (fábrica de luz)
  - Control de secuencia: panel on -> texto -> leds -> settle -> redirect
  - Respeta prefers-reduced-motion
*/

const canvas = document.getElementById('c');
const ctx = canvas && canvas.getContext ? canvas.getContext('2d') : null;
let W = window.innerWidth, H = window.innerHeight;
if (canvas) { canvas.width = W; canvas.height = H; }
window.addEventListener('resize', () => { W = window.innerWidth; H = window.innerHeight; if (canvas) { canvas.width = W; canvas.height = H; } });

const ui = document.getElementById('ui');
const ignite = document.getElementById('ignite');
const carContainer = document.getElementById('car-container');

const prefersReduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

/* PARTICULAS ELEGANTES EN CANVAS */
class Particle { 
  constructor() {
    this.reset();
  }
  reset() {
    this.x = Math.random() * W;
    this.y = Math.random() * H;
    this.r = 0.6 + Math.random() * 2.4;
    this.vx = (Math.random() - 0.5) * 0.2;
    this.vy = (Math.random() - 0.5) * 0.15;
    this.alpha = 0.03 + Math.random() * 0.12;
    this.color = Math.random() > 0.7 ? '#a855f7' : '#d4af37';
  }
  update() { this.x += this.vx; this.y += this.vy; if (this.x < -20 || this.x > W+20 || this.y < -40 || this.y > H+40) this.reset(); }
  draw(ctx) { ctx.globalAlpha = this.alpha; ctx.fillStyle = this.color; ctx.beginPath(); ctx.ellipse(this.x, this.y, this.r*1.6, this.r, 0, 0, Math.PI*2); ctx.fill(); ctx.globalAlpha = 1; }
}

let particles = [];
function initParticles(n=60){ particles = Array.from({length:n}, ()=> new Particle()); }

let raf = null;
function render(){ if (!ctx) return; ctx.clearRect(0,0,W,H); for (let p of particles){ p.update(); p.draw(ctx); } raf = requestAnimationFrame(render); }
function stopRender(){ if (raf) cancelAnimationFrame(raf); raf = null; if (ctx) ctx.clearRect(0,0,W,H); }

/* SVG line-draw injection */
function injectSVG(){
  if (!carContainer) return null;
  // Clear existing injected wrapper if any
  let existing = carContainer.querySelector('.prestige-wrap');
  if (existing) existing.remove();

  const wrap = document.createElement('div'); wrap.className = 'prestige-wrap';
  const glow = document.createElement('div'); glow.className = 'prestige-glow';
  const svgNS = 'http://www.w3.org/2000/svg';
  const svg = document.createElementNS(svgNS, 'svg'); svg.setAttribute('viewBox','0 0 800 300'); svg.classList.add('prestige-svg');

  // Define gradient
  const defs = document.createElementNS(svgNS,'defs');
  const linGrad = document.createElementNS(svgNS,'linearGradient'); linGrad.id='grad'; linGrad.setAttribute('x1','0%'); linGrad.setAttribute('x2','100%');
  const stop1 = document.createElementNS(svgNS,'stop'); stop1.setAttribute('offset','0%'); stop1.setAttribute('stop-color','#d4af37');
  const stop2 = document.createElementNS(svgNS,'stop'); stop2.setAttribute('offset','100%'); stop2.setAttribute('stop-color','#a855f7');
  linGrad.appendChild(stop1); linGrad.appendChild(stop2); defs.appendChild(linGrad); svg.appendChild(defs);

  // Small emblem SVG (deportivo) — inserted above title as an emblem
  const emblem = document.createElement('div'); emblem.className = 'prestige-emblem';
  const emblemSvg = document.createElementNS(svgNS, 'svg'); emblemSvg.setAttribute('viewBox','0 0 240 60'); emblemSvg.setAttribute('aria-hidden','true'); emblemSvg.setAttribute('preserveAspectRatio','xMidYMid meet');
  // emblem gradient (self-contained so it renders correctly)
  const eDefs = document.createElementNS(svgNS,'defs');
  const eGrad = document.createElementNS(svgNS,'linearGradient'); eGrad.id = 'gradEm'; eGrad.setAttribute('x1','0%'); eGrad.setAttribute('x2','100%');
  const eS1 = document.createElementNS(svgNS,'stop'); eS1.setAttribute('offset','0%'); eS1.setAttribute('stop-color','#d4af37');
  const eS2 = document.createElementNS(svgNS,'stop'); eS2.setAttribute('offset','100%'); eS2.setAttribute('stop-color','#a855f7');
  eGrad.appendChild(eS1); eGrad.appendChild(eS2); eDefs.appendChild(eGrad); emblemSvg.appendChild(eDefs);
  // simple sporty silhouette path (minimal)
  const ePath = document.createElementNS(svgNS,'path');
  ePath.setAttribute('d','M6 42 C26 22 56 18 90 18 C122 18 150 28 184 38 C198 44 222 44 234 40');
  ePath.setAttribute('fill','none'); ePath.setAttribute('stroke','url(#gradEm)'); ePath.setAttribute('stroke-width','3'); ePath.setAttribute('stroke-linecap','round'); ePath.setAttribute('stroke-linejoin','round');
  // ensure SVG rendering area is not clipped
  ePath.style.vectorEffect = 'non-scaling-stroke';
  emblemSvg.appendChild(ePath);
  emblem.appendChild(emblemSvg);

  // Simple stylized car path (minimal, elegant)
  const path = document.createElementNS(svgNS,'path');
  path.setAttribute('d','M40 200 C90 130 180 100 300 100 C380 100 460 120 560 140 C640 155 720 170 760 170');
  path.classList.add('line'); path.setAttribute('stroke-linecap','round'); path.setAttribute('stroke-linejoin','round');
  svg.appendChild(path);

  // Title element
  const title = document.createElement('div'); title.className = 'prestige-title'; title.textContent = 'AUTOTEC';

  // Order: glow (background), emblem (small), main svg (line), then title
  wrap.appendChild(glow);
  wrap.appendChild(emblem);
  wrap.appendChild(svg);
  wrap.appendChild(title);
  carContainer.appendChild(wrap);

  // Force reflow then animate stroke
  const total = path.getTotalLength ? path.getTotalLength() : 1200;
  path.style.strokeDasharray = total;
  path.style.strokeDashoffset = total;
  // set stroke to gradient
  path.setAttribute('stroke','url(#grad)');
  path.setAttribute('stroke-width','3');

  // animate programmatically to have better control and respect reduced-motion option
  if (prefersReduced) {
    path.style.strokeDashoffset = '0'; title.classList.add('show'); emblem.classList.add('show');
  } else {
    // small timeout to sync with CSS panel appear
    setTimeout(()=>{ path.classList.add('drawn'); }, 200);
    // reveal emblem shortly after draw starts
    setTimeout(()=>{ emblem.classList.add('show'); }, 600);
    // reveal title after draw
    setTimeout(()=>{ title.classList.add('show'); }, 1000);
  }
  return {wrap, path, title};
}

function getUrlParam(name){ try{ return new URLSearchParams(window.location.search).get(name); }catch(e){return null;} }

/* Sequence controller */
function startSequence(){
  if (startSequence.running) return; startSequence.running = true;
  if (ui){ ui.style.opacity = '0'; ui.style.pointerEvents='none'; }

  if (prefersReduced){
    // reduced: show static quickly, then finish (still redirect like before)
    if (carContainer) carContainer.classList.add('visible');
    injectSVG();
    document.dispatchEvent(new Event('animationFinished'));
    // keep redirection behavior like original: redirect at ~6500ms
    setTimeout(() => { window.location.href = '/index'; }, 6500);
    return;
  }

  // normal flow
  initParticles(72);
  render();
  const svgBits = injectSVG();
  if (carContainer) carContainer.classList.add('visible');

  // after main sequence finish, dispatch event then redirect (match original timing)
  setTimeout(()=>{
    stopRender();
    document.dispatchEvent(new Event('animationFinished'));
  }, 5800);

  // redirect to index like original implementation
  setTimeout(()=>{ window.location.href = '/index'; }, 6500);
}

document.addEventListener('DOMContentLoaded', ()=>{ startSequence(); });
if (ignite) ignite.addEventListener('click', ()=> startSequence());

window.AutoTecAnim = { start: startSequence, stop: stopRender };

