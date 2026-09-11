// ============================================================
// common.js — 全局工具：认证态 + fetch 封装 + 侧边栏注入
// 严格按参考截图的视觉结构
// ============================================================

(function () {
  'use strict';

  const TOKEN_KEY = 'token';
  const USER_ID_KEY = 'userId';
  const NICKNAME_KEY = 'nickname';
  const ROLE_KEY = 'role';
  const AVATAR_KEY = 'avatar';

  const store = {
    get token()   { return localStorage.getItem(TOKEN_KEY); },
    set token(v)  { v ? localStorage.setItem(TOKEN_KEY, v) : localStorage.removeItem(TOKEN_KEY); },
    get userId()  { return localStorage.getItem(USER_ID_KEY); },
    get nickname(){ return localStorage.getItem(NICKNAME_KEY) || '未命名'; },
    get role()    { return parseInt(localStorage.getItem(ROLE_KEY) || '0', 10); },
    get avatar()  { return localStorage.getItem(AVATAR_KEY); },
    get isLogin() { return !!this.token; },
    get isAdmin() { return this.role === 1; },
    clear() {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_ID_KEY);
      localStorage.removeItem(NICKNAME_KEY);
      localStorage.removeItem(ROLE_KEY);
      localStorage.removeItem(AVATAR_KEY);
    },
    setUser(data) {
      if (data.token)    localStorage.setItem(TOKEN_KEY, data.token);
      if (data.userId)   localStorage.setItem(USER_ID_KEY, String(data.userId));
      if (data.nickname) localStorage.setItem(NICKNAME_KEY, data.nickname);
      if (data.role)     localStorage.setItem(ROLE_KEY, String(data.role));
      if (data.avatar)   localStorage.setItem(AVATAR_KEY, data.avatar);
      if (data.username) localStorage.setItem('username', data.username);
      if (data.email)    localStorage.setItem('email', data.email);
    }
  };

  // ---------- 纯 SVG stroke 图标（currentColor） ----------
  const I = {
    home:     '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 12l9-9 9 9"/><path d="M5 10v10a1 1 0 0 0 1 1h4v-6h4v6h4a1 1 0 0 0 1-1V10"/></svg>',
    doc:      '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/><path d="M8 13h8M8 17h8"/></svg>',
    box:      '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/><path d="M3.27 6.96L12 12.01l8.73-5.05"/><path d="M12 22.08V12"/></svg>',
    folder:   '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>',
    info:     '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4M12 8h.01"/></svg>',
    pen:      '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4z"/></svg>',
    mydocs:   '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M14 2v6h6"/></svg>',
    wrench:   '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"/></svg>',
    cog:      '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>',
    logout:   '<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><path d="M16 17l5-5-5-5"/><path d="M21 12H9"/></svg>',
    search:   '<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>',
    moon:     '<svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>',
    arrowR:   '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M12 5l7 7-7 7"/></svg>',
  };

  // ---------- fetch 封装 ----------
  async function request(url, options = {}) {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    if (store.token) {
      headers['Authorization'] = 'Bearer ' + store.token;
      headers['authHeader']    = 'Bearer ' + store.token;
    }
    const res = await fetch(url, Object.assign({}, options, { headers }));
    const json = await res.json().catch(() => ({ code: 500, msg: '网络异常' }));
    if (json.code === 401) {
      store.clear();
      if (!url.startsWith('/api/banner/list') && !url.startsWith('/api/article/list') && !url.startsWith('/api/article/detail')) {
        setTimeout(() => { window.location.href = '/login.html'; }, 300);
      }
      return Promise.reject(json);
    }
    if (json.code === 403) { alert(json.msg || '无权限'); return Promise.reject(json); }
    if (json.code !== 200) { alert(json.msg || '请求失败'); return Promise.reject(json); }
    return json;
  }

  const api = {
    login(body)    { return request('/api/user/login',    { method: 'POST', body: JSON.stringify(body) }); },
    register(body) { return request('/api/user/register', { method: 'POST', body: JSON.stringify(body) }); },

    articleList(params = {}) {
      const qs = new URLSearchParams(Object.assign({ pageNum: 1, pageSize: 10 }, params)).toString();
      return request('/api/article/list?' + qs);
    },
    articleDetail(id)       { return request('/api/article/detail/' + id); },
    myArticles(params = {}) {
      const qs = new URLSearchParams(Object.assign({ pageNum: 1, pageSize: 10 }, params)).toString();
      return request('/api/article/my?' + qs);
    },
    publish(body)  { return request('/api/article/publish', { method: 'POST', body: JSON.stringify(body) }); },
    update(body)   { return request('/api/article/update',  { method: 'PUT',  body: JSON.stringify(body) }); },
    deleteArt(id)  { return request('/api/article/' + id,   { method: 'DELETE' }); },

    commentList(userId) { return request('/api/comment/list?userId=' + userId); },
    commentAdd(params)  {
      return request('/api/comment/add', {
        method: 'POST',
        body: JSON.stringify({ artileId: params.articleId, content: params.content })
      });
    },
    commentDel(id) { return request('/api/comment/' + id, { method: 'DELETE' }); },

    bannerList()    { return request('/api/banner/list'); },
    bannerAll()     { return request('/api/banner/all'); },
    bannerAdd(body) { return request('/api/banner/add',   { method: 'POST', body: JSON.stringify(body) }); },
    bannerDel(id)   { return request('/api/banner/' + id, { method: 'DELETE' }); },

    upload(file) {
      const fd = new FormData();
      fd.append('file', file);
      return fetch('/api/upload', {
        method: 'POST',
        headers: store.token ? {
          'Authorization': 'Bearer ' + store.token,
          'authHeader':    'Bearer ' + store.token,
        } : {},
        body: fd
      }).then(r => r.json());
    },

    userInfo()  { return request('/api/user/info'); },
    userUpdate(body) { return request('/api/user/update', { method: 'PUT', body: JSON.stringify(body) }); },

    adminUsers()           { return request('/api/admin/users'); },
    adminUserStatus(body)  { return request('/api/admin/users/status', { method: 'PUT', body: JSON.stringify(body) }); },
    adminArticles(params = {}) {
      const qs = new URLSearchParams(Object.assign({ pageNum: 1, pageSize: 10 }, params)).toString();
      return request('/api/admin/articles?' + qs);
    },
    adminArticleDetail(id)  { return request('/api/admin/articles/' + id); },
    adminArticleUpdate(body){ return request('/api/admin/articles',   { method: 'PUT',  body: JSON.stringify(body) }); },
    adminArticleStatus(body){ return request('/api/admin/articles/status', { method: 'PUT', body: JSON.stringify(body) }); },
    adminArticleDel(id)     { return request('/api/admin/articles/' + id, { method: 'DELETE' }); },
    adminComments(params = {}) {
      const qs = new URLSearchParams(Object.assign({ pageNum: 1, pageSize: 10 }, params)).toString();
      return request('/api/admin/comments?' + qs);
    },
    adminCommentStatus(body){ return request('/api/admin/comments/status', { method: 'PUT', body: JSON.stringify(body) }); },
    adminCommentDel(id)     { return request('/api/admin/comments/' + id, { method: 'DELETE' }); },
  };

  // ---------- 侧边栏注入（严格按参考截图） ----------
  function injectSidebar(activeKey) {
    const body = document.body;
    if (!body.classList.contains('with-sidebar')) return;
    if (document.getElementById('sidebar-left')) return;

    const isLog = store.isLogin;
    const isAdm = store.isAdmin;
    const nick  = store.nickname;
    const initial = (nick || '?').charAt(0).toUpperCase();

    // 顶部品牌区：大头像 + 名字 + "个人博客" 副标题
    const topBlock = `
      <div class="sb-top">
        <div class="sb-avatar-lg" style="${store.avatar ? `background-image:url('${store.avatar}');background-size:cover` : ''}">${store.avatar ? '' : initial}</div>
        <div class="sb-top-info">
          <div class="sb-top-name">${escapeHtml(nick)}</div>
          <div class="sb-top-sub">个人博客</div>
        </div>
      </div>`;

    // 导航菜单
    const staticNav = [
      { key: 'home',   label: '首页',     href: '/index.html',              icon: I.home },
      { key: 'all',    label: '全部文章', href: '/index.html?view=all',     icon: I.doc },
    ];

    const loggedNav = isLog ? [
      { key: 'write',  label: '写文章',   href: '/write.html',        icon: I.pen },
      { key: 'my',     label: '我的文章', href: '/my.html',           icon: I.mydocs },
    ] : [];

    const adminNav = (isLog && isAdm) ? [
      { key: 'admin',  label: '管理后台', href: '/admin.html',        icon: I.wrench },
    ] : [];

    const allNav = [...staticNav, ...loggedNav, ...adminNav];

    const navHtml = allNav.map(l => {
      const active = (l.key === activeKey) ? ' sb-item-active' : '';
      return `<a class="sb-item${active}" href="${l.href}"><span class="sb-item-icon">${l.icon}</span>${l.label}</a>`;
    }).join('');

    // 底部：分割线 + 小头像区 + 设置/退出
    const bottomBlock = isLog ? `
      <div class="sb-divider"></div>
      <div class="sb-user-mini">
        <div class="sb-avatar-sm" style="${store.avatar ? `background-image:url('${store.avatar}');background-size:cover` : ''}">${store.avatar ? '' : initial}</div>
        <div class="sb-user-mini-info">
          <div class="sb-user-mini-name">${escapeHtml(nick)}</div>
          <div class="sb-user-mini-sub">记录代码，也记录生活</div>
        </div>
      </div>
      <a class="sb-item sb-item-bottom" href="/profile.html" data-active="profile"><span class="sb-item-icon">${I.info}</span>关于我</a>
      <a class="sb-item sb-item-bottom" href="javascript:void(0)" onclick="logout()"><span class="sb-item-icon">${I.logout}</span>退出登录</a>`
    : `
      <div class="sb-divider"></div>
      <div class="sb-user-mini">
        <a href="/login.html" class="sb-login-btn">登录</a>
        <a href="/register.html" class="sb-login-btn-secondary">注册</a>
      </div>`;

    const sidebar = document.createElement('aside');
    sidebar.id = 'sidebar-left';
    sidebar.innerHTML = `
      ${topBlock}
      <nav class="sb-nav">${navHtml}</nav>
      <div class="sb-bottom">${bottomBlock}</div>
    `;
    body.insertBefore(sidebar, body.firstChild);

    window.logout = function () {
      if (!confirm('确定退出登录？')) return;
      store.clear();
      window.location.href = '/index.html';
    };
  }

  // ---------- 工具 ----------
  function escapeHtml(str) {
    if (!str) return '';
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(str));
    return d.innerHTML;
  }

  function formatDate(dt) {
    if (!dt) return '';
    const s = String(dt).replace('T', ' ').substring(0, 10);
    return s;
  }

  // ---------- 启动 ----------
  document.addEventListener('DOMContentLoaded', function () {
    const activeKey = document.body.getAttribute('data-active') || '';
    injectSidebar(activeKey);
    // 登录态时异步从后端拉最新用户信息（头像、昵称等），确保侧边栏不显示旧数据
    if (store.isLogin) {
      api.userInfo().then(res => {
        const u = res.data;
        if (!u) return;
        store.setUser({
          userId: u.id, username: u.username, nickname: u.nickname,
          avatar: u.avatar, role: u.role, email: u.email
        });
        // 更新侧边栏头像和昵称
        const sb = document.getElementById('sidebar-left');
        if (!sb) return;
        const initial = (u.nickname || u.username || '?').charAt(0).toUpperCase();
        const avatarStyle = u.avatar ? `background-image:url('${u.avatar}');background-size:cover` : '';
        sb.querySelectorAll('.sb-avatar-lg, .sb-avatar-sm').forEach(el => {
          if (u.avatar) { el.setAttribute('style', avatarStyle); el.textContent = ''; }
          else { el.removeAttribute('style'); el.textContent = initial; }
        });
        sb.querySelectorAll('.sb-top-name, .sb-user-mini-name').forEach(el => {
          el.textContent = u.nickname || u.username || '';
        });
      }).catch(() => {});
    }
  });

  window.App = { store, api, formatDate, escapeHtml, injectSidebar, I };
})();
