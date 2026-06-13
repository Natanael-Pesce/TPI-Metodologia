// ===== ADMIN MODULE =====

let adminProductos = [];

async function loadAdminProductos() {
  const container = document.getElementById('admin-productos-list');
  container.innerHTML = '<div class="loading">Cargando productos...</div>';
  try {
    adminProductos = await Api.getAllProductos();
    renderAdminProductos(adminProductos);
  } catch (err) {
    container.innerHTML = `<div class="empty-state">Error: ${err.message}</div>`;
  }
}

function renderAdminProductos(productos) {
  const container = document.getElementById('admin-productos-list');
  if (!productos.length) {
    container.innerHTML = '<div class="empty-state">No hay productos.</div>';
    return;
  }
  container.innerHTML = `
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Imagen</th>
          <th>Nombre</th>
          <th>Precio</th>
          <th>Stock</th>
          <th>Stock Mín.</th>
          <th>Estado</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        ${productos.map(p => `
          <tr>
            <td>#${p.productoID}</td>
            <td>
              <div class="prod-img-thumb">
                ${p.imagen
                  ? `<img src="${escapeAttr(p.imagen)}" alt="${escapeAttr(p.nombreProducto)}" onerror="this.parentElement.textContent='📦'" />`
                  : '📦'}
              </div>
            </td>
            <td><strong>${escape(p.nombreProducto)}</strong></td>
            <td>${formatMoney(p.precioProducto)}</td>
            <td>${p.stock <= p.stockMin
              ? `<span style="color:var(--warning);font-weight:700">⚠ ${p.stock}</span>`
              : p.stock}</td>
            <td>${p.stockMin}</td>
            <td>
              <span class="status ${p.productoActivo ? 'status-CONFIRMADO' : 'status-CANCELADO'}">
                ${p.productoActivo ? 'Activo' : 'Inactivo'}
              </span>
            </td>
            <td>
              <div style="display:flex;gap:.375rem;flex-wrap:wrap">
                <button class="btn btn-sm btn-outline" onclick="showProductoModal(${p.productoID})">Editar</button>
                <button class="btn btn-sm ${p.productoActivo ? 'btn-danger' : 'btn-success'}"
                  onclick="toggleProductoEstado(${p.productoID}, ${!p.productoActivo})">
                  ${p.productoActivo ? 'Desactivar' : 'Activar'}
                </button>
              </div>
            </td>
          </tr>`).join('')}
      </tbody>
    </table>`;
}

function showProductoModal(id) {
  const p = id ? adminProductos.find(x => x.productoID === id) : null;
  document.getElementById('modal-producto-title').textContent = p ? 'Editar Producto' : 'Nuevo Producto';
  document.getElementById('prod-id').value     = p?.productoID || '';
  document.getElementById('prod-nombre').value = p?.nombreProducto || '';
  document.getElementById('prod-precio').value = p?.precioProducto || '';
  document.getElementById('prod-stock').value  = p?.stock ?? '';
  document.getElementById('prod-stockmin').value = p?.stockMin ?? 5;
  document.getElementById('prod-activo').value = p !== null ? String(p?.productoActivo ?? true) : 'true';
  document.getElementById('prod-imagen').value = p?.imagen || '';
  openModal('modal-producto');
}

async function saveProducto(e) {
  e.preventDefault();
  const id = document.getElementById('prod-id').value;
  const body = {
    nombreProducto: document.getElementById('prod-nombre').value.trim(),
    precioProducto: parseFloat(document.getElementById('prod-precio').value),
    stock:          parseInt(document.getElementById('prod-stock').value),
    stockMin:       parseInt(document.getElementById('prod-stockmin').value) || 0,
    productoActivo: document.getElementById('prod-activo').value === 'true',
    imagen:         document.getElementById('prod-imagen').value.trim() || null,
  };

  const btn = document.getElementById('prod-save-btn');
  btn.disabled = true;
  btn.textContent = 'Guardando...';

  try {
    if (id) {
      await Api.updateProducto(id, body);
      showToast('Producto actualizado', 'success');
    } else {
      await Api.createProducto(body);
      showToast('Producto creado', 'success');
    }
    closeModal('modal-producto');
    await loadAdminProductos();
  } catch (err) {
    showToast(err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Guardar';
  }
}

async function toggleProductoEstado(id, activo) {
  try {
    await Api.toggleProducto(id, activo);
    showToast(`Producto ${activo ? 'activado' : 'desactivado'}`, 'success');
    await loadAdminProductos();
  } catch (err) {
    showToast(err.message, 'error');
  }
}
