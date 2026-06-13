// ===== AUTH MODULE =====

let currentUser = null;

function switchAuthTab(tab) {
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  event.currentTarget.classList.add('active');
  document.getElementById('login-form').classList.toggle('hidden', tab !== 'login');
  document.getElementById('register-form').classList.toggle('hidden', tab !== 'register');
}

async function handleLogin(e) {
  e.preventDefault();
  const btn = document.getElementById('login-btn');
  const correo     = document.getElementById('login-correo').value.trim();
  const contrasena = document.getElementById('login-contrasena').value;

  btn.disabled = true;
  btn.textContent = 'Iniciando sesión...';
  try {
    // API returns: { token, usuarioID, nombre, apellido, correo, rol }
    const res = await Api.login(correo, contrasena);
    localStorage.setItem('jwt_token', res.token);
    currentUser = {
      id:       res.usuarioID,
      nombre:   res.nombre,
      apellido: res.apellido,
      correo:   res.correo,
      rol:      res.rol,   // e.g. "ROLE_CLIENTE"
    };
    onAuthSuccess();
  } catch (err) {
    showToast(err.message || 'Credenciales incorrectas', 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Iniciar sesión';
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const btn = document.getElementById('register-btn');
  const body = {
    nombre:     document.getElementById('reg-nombre').value.trim(),
    apellido:   document.getElementById('reg-apellido').value.trim(),
    correo:     document.getElementById('reg-correo').value.trim(),
    contrasena: document.getElementById('reg-contrasena').value,
    cuit:       document.getElementById('reg-cuit').value.trim() || null,
  };
  btn.disabled = true;
  btn.textContent = 'Creando cuenta...';
  try {
    const res = await Api.register(body);
    localStorage.setItem('jwt_token', res.token);
    currentUser = {
      id:       res.usuarioID,
      nombre:   res.nombre,
      apellido: res.apellido,
      correo:   res.correo,
      rol:      res.rol,
    };
    showToast(`¡Bienvenido/a, ${currentUser.nombre}!`, 'success');
    onAuthSuccess();
  } catch (err) {
    showToast(err.message || 'No se pudo registrar', 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Crear cuenta';
  }
}

function onAuthSuccess() {
  applyRoleVisibility();
  const label = rolLabel(currentUser.rol);
  document.getElementById('nav-username').textContent = `${currentUser.nombre} (${label})`;
  document.getElementById('navbar').classList.remove('hidden');
  // Route to the right home view per role
  const rol = currentUser.rol;
  if (rol === 'ROLE_ADMINISTRADOR') {
    showView('admin-productos');
  } else if (rol === 'ROLE_VENDEDOR') {
    showView('gestion-pedidos');
  } else {
    showView('catalogo');
  }
}

function applyRoleVisibility() {
  const rol = currentUser?.rol;
  document.querySelectorAll('.role-cliente').forEach(el =>
    el.classList.toggle('hidden', rol !== 'ROLE_CLIENTE'));
  document.querySelectorAll('.role-vendedor').forEach(el =>
    el.classList.toggle('hidden', rol !== 'ROLE_VENDEDOR' && rol !== 'ROLE_ADMINISTRADOR'));
  document.querySelectorAll('.role-admin').forEach(el =>
    el.classList.toggle('hidden', rol !== 'ROLE_ADMINISTRADOR'));
}

function rolLabel(rol) {
  return { ROLE_CLIENTE: 'Cliente', ROLE_VENDEDOR: 'Vendedor', ROLE_ADMINISTRADOR: 'Admin' }[rol] || rol;
}

function logout() {
  localStorage.removeItem('jwt_token');
  currentUser = null;
  cart = [];
  updateCartBadge();
  document.getElementById('navbar').classList.add('hidden');
  showView('auth');
}

function tryRestoreSession() {
  const token = localStorage.getItem('jwt_token');
  if (!token) return;
  try {
    const parts = token.split('.');
    if (parts.length !== 3) throw new Error('bad token');
    // Decode base64url payload
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
    // Check expiry
    if (payload.exp && payload.exp * 1000 < Date.now()) {
      localStorage.removeItem('jwt_token');
      return;
    }
    // JwtService adds: sub (correo), rol (authority string)
    currentUser = {
      id:       payload.id || null,
      nombre:   payload.nombre || payload.sub,
      apellido: payload.apellido || '',
      correo:   payload.sub,
      rol:      payload.rol || 'ROLE_CLIENTE',
    };
    onAuthSuccess();
  } catch {
    localStorage.removeItem('jwt_token');
  }
}
