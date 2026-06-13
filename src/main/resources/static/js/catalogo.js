// ===== CATALOGO MODULE =====

let allProductos = [];

async function loadCatalogo() {
  const grid = document.getElementById('productos-grid');
  grid.innerHTML = '<div class="loading">Cargando productos...</div>';
  try {
    allProductos = await Api.getProductos();
    renderProductos(allProductos);
  } catch (err) {
    grid.innerHTML = `<div class="empty-state">Error al cargar productos: ${err.message}</div>`;
  }
}

function renderProductos(productos) {
  const grid = document.getElementById('productos-grid');
  if (!productos.length) {
    grid.innerHTML = '<div class="empty-state" style="grid-column:1/-1">No se encontraron productos.</div>';
    return;
  }
  grid.innerHTML = productos.map(p => {
    const inCart = cart.find(c => c.productoID === p.productoID);
    const stockLabel = p.stock <= p.stockMin
      ? `<span class="product-stock low">⚠ Stock bajo: ${p.stock}</span>`
      : `<span class="product-stock">Stock: ${p.stock}</span>`;
    const imgHtml = p.imagen
      ? `<img src="${escapeAttr(p.imagen)}" alt="${escapeAttr(p.nombreProducto)}" onerror="this.parentElement.textContent='📦'" />`
      : '📦';
    return `
      <div class="product-card">
        <div class="product-img">${imgHtml}</div>
        <div class="product-body">
          <div class="product-name">${escape(p.nombreProducto)}</div>
          <div class="product-price">${formatMoney(p.precioProducto)}</div>
          ${stockLabel}
          <div class="product-actions">
            ${p.stock > 0
              ? inCart
                ? `<div style="display:flex;align-items:center;gap:.5rem;margin-top:.25rem;">
                     <button class="qty-btn" onclick="removeFromCart(${p.productoID})">−</button>
                     <span class="qty-value">${inCart.cantidad}</span>
                     <button class="qty-btn" onclick="addToCart(${p.productoID}, '${escapeAttr(p.nombreProducto)}', ${p.precioProducto})">+</button>
                     <button class="btn btn-sm btn-danger" onclick="deleteFromCart(${p.productoID})">✕</button>
                   </div>`
                : `<button class="btn btn-primary btn-full" onclick="addToCart(${p.productoID}, '${escapeAttr(p.nombreProducto)}', ${p.precioProducto})">
                     Agregar al carrito
                   </button>`
              : `<button class="btn btn-outline btn-full" disabled>Sin stock</button>`
            }
          </div>
        </div>
      </div>`;
  }).join('');
}

function filterProductos() {
  const q = document.getElementById('search-productos').value.toLowerCase().trim();
  if (!q) { renderProductos(allProductos); return; }
  renderProductos(allProductos.filter(p => p.nombreProducto.toLowerCase().includes(q)));
}

// ---- Helpers ----
function escape(str) {
  if (!str) return '';
  return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
}
function escapeAttr(str) {
  if (!str) return '';
  return str.replace(/"/g,'&quot;').replace(/'/g,'&#39;');
}
function formatMoney(n) {
  return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' }).format(n);
}
