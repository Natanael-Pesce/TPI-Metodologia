// ===== CARRITO MODULE =====

let cart = [];  // [{ productoID, nombre, precio, cantidad }]

function addToCart(productoID, nombre, precio) {
  const item = cart.find(c => c.productoID === productoID);
  if (item) {
    item.cantidad++;
  } else {
    cart.push({ productoID, nombre, precio, cantidad: 1 });
  }
  updateCartBadge();
  renderCatalogo_inline();
  showToast(`"${nombre}" añadido al carrito`, 'success');
}

function removeFromCart(productoID) {
  const item = cart.find(c => c.productoID === productoID);
  if (!item) return;
  if (item.cantidad > 1) {
    item.cantidad--;
  } else {
    cart = cart.filter(c => c.productoID !== productoID);
  }
  updateCartBadge();
  renderCatalogo_inline();
  renderCarrito();
}

function deleteFromCart(productoID) {
  cart = cart.filter(c => c.productoID !== productoID);
  updateCartBadge();
  renderCatalogo_inline();
  renderCarrito();
}

function updateCartBadge() {
  const total = cart.reduce((s, c) => s + c.cantidad, 0);
  document.getElementById('cart-badge').textContent = total;
}

function renderCatalogo_inline() {
  // Refresh the catalog view if it's showing
  const view = document.getElementById('view-catalogo');
  if (!view.classList.contains('hidden')) {
    renderProductos(allProductos.filter(p => {
      const q = document.getElementById('search-productos')?.value.toLowerCase() || '';
      return !q || p.nombreProducto.toLowerCase().includes(q);
    }));
  }
}

async function renderCarrito() {
  const container = document.getElementById('cart-items');
  const subtotalEl = document.getElementById('cart-subtotal');
  const totalEl = document.getElementById('cart-total');

  if (!cart.length) {
    container.innerHTML = '<div class="empty-state">Tu carrito está vacío.<br><br><a class="btn btn-primary" onclick="showView(\'catalogo\')">Ver catálogo</a></div>';
    subtotalEl.textContent = '$0.00';
    totalEl.textContent = '$0.00';
    return;
  }

  const subtotal = cart.reduce((s, c) => s + c.precio * c.cantidad, 0);
  subtotalEl.textContent = formatMoney(subtotal);
  totalEl.textContent = formatMoney(subtotal);

  container.innerHTML = cart.map(item => `
    <div class="cart-item">
      <div class="cart-item-img">📦</div>
      <div class="cart-item-info">
        <div class="cart-item-name">${escape(item.nombre)}</div>
        <div class="cart-item-price">${formatMoney(item.precio)} c/u</div>
      </div>
      <div class="cart-item-controls">
        <button class="qty-btn" onclick="removeFromCart(${item.productoID})">−</button>
        <span class="qty-value">${item.cantidad}</span>
        <button class="qty-btn" onclick="addToCart(${item.productoID}, '${escapeAttr(item.nombre)}', ${item.precio})">+</button>
      </div>
      <div class="cart-item-total">${formatMoney(item.precio * item.cantidad)}</div>
      <button class="btn btn-sm btn-danger" onclick="deleteFromCart(${item.productoID})">✕</button>
    </div>
  `).join('');

  // Load domicilios
  await loadDomiciliosForCart();
}

async function loadDomiciliosForCart() {
  const select = document.getElementById('cart-domicilio');
  if (!currentUser) return;
  try {
    const domicilios = await Api.getDomicilios(currentUser.id);
    select.innerHTML = '<option value="">-- Seleccionar domicilio --</option>' +
      domicilios.map(d =>
        `<option value="${d.domicilioID}">${escape(d.calle)} ${escape(d.nro || '')}, ${escape(d.ciudad)}</option>`
      ).join('');
  } catch {
    select.innerHTML = '<option value="">No se pudieron cargar domicilios</option>';
  }
}

function showAddDomicilioModal() {
  ['dom-pais','dom-provincia','dom-ciudad','dom-calle','dom-nro','dom-piso','dom-depto']
    .forEach(id => document.getElementById(id).value = '');
  openModal('modal-domicilio');
}

async function saveDomicilio(e) {
  e.preventDefault();
  const body = {
    pais:             document.getElementById('dom-pais').value.trim(),
    provincia:        document.getElementById('dom-provincia').value.trim() || null,
    ciudad:           document.getElementById('dom-ciudad').value.trim(),
    calle:            document.getElementById('dom-calle').value.trim(),
    nro:              document.getElementById('dom-nro').value.trim() || null,
    piso:             document.getElementById('dom-piso').value.trim() || null,
    departamento:     document.getElementById('dom-depto').value.trim() || null,
  };
  try {
    await Api.addDomicilio(currentUser.id, body);
    showToast('Domicilio agregado', 'success');
    closeModal('modal-domicilio');
    await loadDomiciliosForCart();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function confirmarPedido() {
  if (!cart.length) { showToast('El carrito está vacío', 'error'); return; }
  if (!currentUser) { showToast('Debes iniciar sesión', 'error'); return; }

  const domicilioID = document.getElementById('cart-domicilio').value;
  const cupon = document.getElementById('cart-cupon').value.trim();

  const body = {
    UsuarioID: currentUser.id,
    detalles: cart.map(c => ({ productoID: c.productoID, cantidad: c.cantidad })),
    pago: {
      tipoPago: 'EFECTIVO',
      monto: cart.reduce((s, c) => s + c.precio * c.cantidad, 0),
    },
    domicilioEnvioID: domicilioID ? parseInt(domicilioID) : null,
    codigoCupon: cupon || null,
  };

  const btn = event.currentTarget;
  btn.disabled = true;
  btn.textContent = 'Procesando...';

  try {
    const pedido = await Api.createPedido(body);
    cart = [];
    updateCartBadge();
    showToast(`✅ Pedido #${pedido.pedidoID} confirmado. Recibirás un email de confirmación.`, 'success');
    showView('mis-pedidos');
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Confirmar pedido (Pago en efectivo)';
  }
}
