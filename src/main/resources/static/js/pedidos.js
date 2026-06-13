// ===== PEDIDOS MODULE =====

// ---- MIS PEDIDOS (CLIENTE) ----
async function loadMisPedidos() {
  const container = document.getElementById('mis-pedidos-list');
  container.innerHTML = '<div class="loading">Cargando pedidos...</div>';
  try {
    const pedidos = await Api.getMisPedidos(currentUser.id);
    if (!pedidos.length) {
      container.innerHTML = '<div class="empty-state">Aún no tenés pedidos.<br><br><a class="btn btn-primary" onclick="showView(\'catalogo\')">Ir al catálogo</a></div>';
      return;
    }
    container.innerHTML = pedidos.map(p => renderPedidoCard(p, 'cliente')).join('');
  } catch (err) {
    container.innerHTML = `<div class="empty-state">Error: ${err.message}</div>`;
  }
}

// ---- GESTIÓN PEDIDOS (VENDEDOR / ADMIN) ----
async function loadPedidos() {
  const container = document.getElementById('pedidos-admin-list');
  const estado = document.getElementById('filter-estado-pedido')?.value || '';
  container.innerHTML = '<div class="loading">Cargando pedidos...</div>';
  try {
    const pedidos = await Api.getPedidos(estado);
    if (!pedidos.length) {
      container.innerHTML = '<div class="empty-state">No hay pedidos con ese estado.</div>';
      return;
    }
    container.innerHTML = pedidos.map(p => renderPedidoCard(p, 'admin')).join('');
  } catch (err) {
    container.innerHTML = `<div class="empty-state">Error: ${err.message}</div>`;
  }
}

function renderPedidoCard(p, mode) {
  const fecha = p.fechaPedido ? new Date(p.fechaPedido).toLocaleDateString('es-AR') : '-';
  const trackingChip = p.envio?.tracking
    ? `<span class="chip">📦 ${escape(p.envio.tracking)}</span>`
    : '';
  const adminActions = mode === 'admin' ? `
    <div class="pedido-actions">
      ${p.estado === 'PENDIENTE' || p.estado === 'EN_PREPARACION'
        ? `<button class="btn btn-sm btn-success" onclick="confirmarPedidoAdmin(${p.pedidoID})">✓ Confirmar</button>`
        : ''}
      ${p.estado === 'CONFIRMADO'
        ? `<button class="btn btn-sm btn-primary" onclick="cambiarEstado(${p.pedidoID}, 'ENVIO')">📦 Enviar</button>`
        : ''}
      ${p.estado === 'ENVIO'
        ? `<button class="btn btn-sm btn-success" onclick="cambiarEstado(${p.pedidoID}, 'ENTREGADO')">✅ Entregado</button>`
        : ''}
      ${p.estado !== 'CANCELADO' && p.estado !== 'ENTREGADO'
        ? `<button class="btn btn-sm btn-danger" onclick="cancelarPedidoAdmin(${p.pedidoID})">Cancelar</button>`
        : ''}
      <button class="btn btn-sm btn-outline" onclick="verDetallePedido(${p.pedidoID})">Ver detalle</button>
    </div>` : `
    <div class="pedido-actions">
      <button class="btn btn-sm btn-outline" onclick="verDetallePedido(${p.pedidoID})">Ver detalle</button>
      ${p.estado === 'PENDIENTE'
        ? `<button class="btn btn-sm btn-danger" onclick="cancelarPedidoCliente(${p.pedidoID})">Cancelar</button>`
        : ''}
    </div>`;

  return `
    <div class="pedido-card" id="pedido-card-${p.pedidoID}">
      <div class="pedido-id">#${p.pedidoID}</div>
      <div class="pedido-info">
        <strong>${escape(p.usuarioNombre || '')} ${escape(p.usuarioApellido || '')}</strong>
        <p>${fecha} · ${p.detalles?.length || 0} producto(s)</p>
        ${trackingChip}
      </div>
      <span class="status status-${p.estado}">${estadoLabel(p.estado)}</span>
      <div class="pedido-total">${formatMoney(p.total)}</div>
      ${adminActions}
    </div>`;
}

async function verDetallePedido(id) {
  const body = document.getElementById('pedido-detalle-body');
  body.innerHTML = '<div class="loading">Cargando...</div>';
  openModal('modal-pedido-detalle');
  try {
    const p = await Api.getPedido(id);
    const fecha = p.fechaPedido ? new Date(p.fechaPedido).toLocaleDateString('es-AR') : '-';
    body.innerHTML = `
      <div class="pedido-detail-section">
        <h4>Información del pedido</h4>
        <div class="detail-grid">
          <span class="detail-label">ID</span>   <span class="detail-value">#${p.pedidoID}</span>
          <span class="detail-label">Fecha</span> <span class="detail-value">${fecha}</span>
          <span class="detail-label">Estado</span><span class="detail-value"><span class="status status-${p.estado}">${estadoLabel(p.estado)}</span></span>
          <span class="detail-label">Cliente</span><span class="detail-value">${escape(p.usuarioNombre)} ${escape(p.usuarioApellido)}</span>
        </div>
      </div>

      <div class="pedido-detail-section">
        <h4>Productos</h4>
        <div class="detail-items">
          ${(p.detalles || []).map(d => `
            <div class="detail-item">
              <span>${escape(d.nombreProducto || `Producto #${d.productoID}`)} × ${d.cantidad}</span>
              <span>${formatMoney(d.subtotal || (d.precioUnitario * d.cantidad))}</span>
            </div>`).join('')}
          <div class="detail-item" style="font-weight:700">
            <span>Total</span><span>${formatMoney(p.total)}</span>
          </div>
        </div>
      </div>

      ${p.pago ? `
      <div class="pedido-detail-section">
        <h4>Pago</h4>
        <div class="detail-grid">
          <span class="detail-label">Método</span><span class="detail-value">${escape(p.pago.tipoPago || '-')}</span>
          <span class="detail-label">Monto</span><span class="detail-value">${formatMoney(p.pago.monto || 0)}</span>
          <span class="detail-label">Estado</span><span class="detail-value">${escape(p.pago.estado || '-')}</span>
        </div>
      </div>` : ''}

      ${p.envio ? `
      <div class="pedido-detail-section">
        <h4>Envío</h4>
        ${p.envio.tracking
          ? `<div class="tracking-box">🚚 ${escape(p.envio.tracking)}</div>`
          : '<p style="color:var(--gray-400);font-size:.875rem">Número de tracking aún no disponible</p>'}
        <div class="detail-grid" style="margin-top:.75rem">
          <span class="detail-label">Estado envío</span>
          <span class="detail-value"><span class="status status-${p.envio.estadoEnvio}">${estadoEnvioLabel(p.envio.estadoEnvio)}</span></span>
          ${p.envio.domicilio ? `
          <span class="detail-label">Dirección</span>
          <span class="detail-value">${escape(p.envio.domicilio.calle)} ${escape(p.envio.domicilio.nro||'')}, ${escape(p.envio.domicilio.ciudad)}</span>` : ''}
        </div>
      </div>` : ''}
    `;
  } catch (err) {
    body.innerHTML = `<div class="empty-state">Error: ${err.message}</div>`;
  }
}

async function confirmarPedidoAdmin(id) {
  try {
    await Api.confirmarPedidoAdmin(id);
    showToast(`Pedido #${id} confirmado`, 'success');
    loadPedidos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function cambiarEstado(id, estado) {
  try {
    await Api.cambiarEstadoPedido(id, estado);
    showToast(`Estado actualizado a ${estadoLabel(estado)}`, 'success');
    loadPedidos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function cancelarPedidoAdmin(id) {
  if (!confirm(`¿Cancelar pedido #${id}?`)) return;
  try {
    await Api.cancelarPedido(id);
    showToast(`Pedido #${id} cancelado`, 'success');
    loadPedidos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function cancelarPedidoCliente(id) {
  if (!confirm(`¿Cancelar pedido #${id}?`)) return;
  try {
    await Api.cancelarPedido(id);
    showToast(`Pedido #${id} cancelado`, 'success');
    loadMisPedidos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ---- Label helpers ----
function estadoLabel(estado) {
  const map = {
    PENDIENTE: 'Pendiente', EN_PREPARACION: 'En preparación',
    CONFIRMADO: 'Confirmado', ENVIO: 'En envío',
    ENTREGADO: 'Entregado', CANCELADO: 'Cancelado',
  };
  return map[estado] || estado;
}
function estadoEnvioLabel(estado) {
  const map = {
    PENDIENTE: 'Pendiente', PREPARANDO: 'Preparando',
    DESPACHADO: 'Despachado', EN_CAMINO: 'En camino',
    ENTREGADO: 'Entregado', DEVUELTO: 'Devuelto',
  };
  return map[estado] || estado;
}
