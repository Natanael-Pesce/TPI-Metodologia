// ===== APP MODULE =====

// ---- VIEW ROUTER ----
function showView(viewName) {
  document.querySelectorAll('.view').forEach(v => {
    v.classList.remove('active');
    v.classList.add('hidden');
  });
  const target = document.getElementById(`view-${viewName}`);
  if (target) {
    target.classList.remove('hidden');
    target.classList.add('active');
    onViewEnter(viewName);
  }
}

function onViewEnter(viewName) {
  switch (viewName) {
    case 'auth':
      break;
    case 'catalogo':
      loadCatalogo();
      break;
    case 'carrito':
      renderCarrito();
      break;
    case 'mis-pedidos':
      loadMisPedidos();
      break;
    case 'gestion-pedidos':
      loadPedidos();
      break;
    case 'admin-productos':
      loadAdminProductos();
      break;
  }
}

// ---- MODAL HELPERS ----
function openModal(id) {
  document.getElementById(id).classList.remove('hidden');
  document.body.style.overflow = 'hidden';
}
function closeModal(id) {
  document.getElementById(id).classList.add('hidden');
  document.body.style.overflow = '';
}

// Close modals on Escape key
document.addEventListener('keydown', e => {
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal:not(.hidden)').forEach(m => closeModal(m.id));
  }
});

// ---- TOAST SYSTEM ----
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  const icon = { success: '✅', error: '❌', info: 'ℹ️' }[type] || 'ℹ️';
  toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all .3s';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// ---- INIT ----
document.addEventListener('DOMContentLoaded', () => {
  // Start at auth by default
  showView('auth');
  // Try to restore JWT session
  tryRestoreSession();
});
