// ---- 交互粒子连线背景（共享，参照桌面 index.html 的 field 动画） ----
(function () {
  var canvas = document.getElementById('field');
  if (!canvas) return;
  var ctx = canvas.getContext('2d');
  var w, h, dpr, particles = [], mouse = { x: -9999, y: -9999 };
  var COLORS = ['109,74,255', '11,158,150', '240,67,127'];
  var reduced = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  function resize() {
    dpr = Math.min(window.devicePixelRatio || 1, 2);
    w = canvas.width = window.innerWidth * dpr;
    h = canvas.height = window.innerHeight * dpr;
    canvas.style.width = window.innerWidth + 'px';
    canvas.style.height = window.innerHeight + 'px';
    var count = Math.min(90, Math.floor(window.innerWidth * window.innerHeight / 16000));
    particles = [];
    for (var i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * w,
        y: Math.random() * h,
        vx: (Math.random() - .5) * .3 * dpr,
        vy: (Math.random() - .5) * .3 * dpr,
        r: (Math.random() * 1.6 + .6) * dpr,
        c: COLORS[i % COLORS.length]
      });
    }
  }
  resize();
  window.addEventListener('resize', resize);
  if (reduced) return;

  window.addEventListener('mousemove', function (e) {
    mouse.x = e.clientX * dpr;
    mouse.y = e.clientY * dpr;
  });
  window.addEventListener('mouseleave', function () { mouse.x = -9999; mouse.y = -9999; });

  var LINK = 140;

  function frame() {
    ctx.clearRect(0, 0, w, h);
    var g = ctx.createRadialGradient(w * 0.7, h * 0.2, 0, w * 0.7, h * 0.2, w * 0.5);
    g.addColorStop(0, 'rgba(109,74,255,0.06)');
    g.addColorStop(1, 'rgba(109,74,255,0)');
    ctx.fillStyle = g;
    ctx.fillRect(0, 0, w, h);
    var g2 = ctx.createRadialGradient(w * 0.15, h * 0.8, 0, w * 0.15, h * 0.8, w * 0.45);
    g2.addColorStop(0, 'rgba(11,158,150,0.05)');
    g2.addColorStop(1, 'rgba(11,158,150,0)');
    ctx.fillStyle = g2;
    ctx.fillRect(0, 0, w, h);

    var link = LINK * dpr;
    for (var i = 0; i < particles.length; i++) {
      var p = particles[i];
      p.x += p.vx;
      p.y += p.vy;
      if (p.x < 0 || p.x > w) p.vx *= -1;
      if (p.y < 0 || p.y > h) p.vy *= -1;
      var dx = p.x - mouse.x, dy = p.y - mouse.y, md = Math.hypot(dx, dy);
      if (md < 150 * dpr && md > 0) {
        var f = (150 * dpr - md) / (150 * dpr) * 0.6;
        p.x += dx / md * f;
        p.y += dy / md * f;
      }
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
      ctx.fillStyle = 'rgba(' + p.c + ',0.9)';
      ctx.fill();
      for (var j = i + 1; j < particles.length; j++) {
        var q = particles[j];
        var d = Math.hypot(p.x - q.x, p.y - q.y);
        if (d < link) {
          ctx.strokeStyle = 'rgba(' + p.c + ',' + (0.14 * (1 - d / link)).toFixed(3) + ')';
          ctx.lineWidth = dpr * 0.6;
          ctx.beginPath();
          ctx.moveTo(p.x, p.y);
          ctx.lineTo(q.x, q.y);
          ctx.stroke();
        }
      }
    }
    requestAnimationFrame(frame);
  }
  frame();
})();
