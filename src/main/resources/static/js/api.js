// ===== API SERVICE =====
const API_BASE = 'http://localhost:8080';

const Api = (() => {
  const getToken = () => localStorage.getItem('jwt_token');

  const headers = (extra = {}) => ({
    'Content-Type': 'application/json',
    ...(getToken() ? { 'Authorization': `Bearer ${getToken()}` } : {}),
    ...extra
  });

  const request = async (method, path, body) => {
    const opts = { method, headers: headers() };
    if (body !== undefined) opts.body = JSON.stringify(body);
    const res = await fetch(`${API_BASE}${path}`, opts);
    if (res.status === 204) return null;
    const text = await res.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }
    if (!res.ok) {
      const msg = data?.message || data?.error || (typeof data === 'string' ? data : `Error ${res.status}`);
      throw new Error(msg);
    }
    return data;
  };

  return {
    get:    (path)        => request('GET',    path),
    post:   (path, body)  => request('POST',   path, body),
    patch:  (path, body)  => request('PATCH',  path, body),
    delete: (path)        => request('DELETE', path),

    // ---- AUTH ----
    login:    (correo, contrasena) => request('POST', '/api/auth/login',    { correo, contrasena }),
    register: (body)               => request('POST', '/api/auth/register', body),

    // ---- PRODUCTOS ----
    getProductos:       ()           => request('GET',   '/api/productos?soloActivos=true'),
    getAllProductos:     ()           => request('GET',   '/api/productos'),
    createProducto:     (body)       => request('POST',  '/api/productos',          body),
    updateProducto:     (id, body)   => request('PATCH', `/api/productos/${id}`,    body),
    toggleProducto:     (id, activo) => request('PATCH', `/api/productos/${id}/estado?activo=${activo}`),
    deleteProducto:     (id)         => request('DELETE',`/api/productos/${id}`),

    // ---- PEDIDOS ----
    createPedido:        (body)   => request('POST',  '/api/pedidos',              body),
    getPedidos:          (estado) => request('GET',   `/api/pedidos${estado ? `?estado=${estado}` : ''}`),
    getMisPedidos:       (uid)    => request('GET',   `/api/pedidos/cliente/${uid}`),
    getPedido:           (id)     => request('GET',   `/api/pedidos/${id}`),
    confirmarPedidoAdmin:(id)     => request('POST',  `/api/pedidos/${id}/confirmar`),
    cambiarEstadoPedido: (id, estado) => request('PATCH', `/api/pedidos/${id}/estado`, { estado }),
    cancelarPedido:      (id)     => request('DELETE',`/api/pedidos/${id}/cancelar`),

    // ---- DOMICILIOS ----
    getDomicilios:   (uid)        => request('GET',  `/api/usuarios/${uid}/domicilios`),
    addDomicilio:    (uid, body)  => request('POST', `/api/usuarios/${uid}/domicilios`, body),

    // ---- ENVIOS ----
    getEnvios:       ()           => request('GET',  '/api/envios'),
    getEnvioPorTracking: (code)   => request('GET',  `/api/envios/tracking/${code}`),
    updateEnvioEstado: (id, estado, tracking) => {
      const params = `estado=${estado}${tracking ? `&tracking=${tracking}` : ''}`;
      return request('PATCH', `/api/envios/${id}/estado?${params}`);
    },
  };
})();
