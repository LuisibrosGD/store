import { getSession } from './state.js';

const API_BASE = document.querySelector('meta[name="api-base"]')?.content || '/api';

export class ApiError extends Error {
  constructor(message, status = 0, data = null) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
    this.fieldErrors = data?.errors || {};
  }
}

function queryString(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, String(value));
  });
  const text = query.toString();
  return text ? `?${text}` : '';
}

async function request(path, options = {}) {
  const { body, authenticated = false, headers, ...fetchOptions } = options;
  const requestHeaders = new Headers(headers || {});
  requestHeaders.set('Accept', 'application/json');
  if (body !== undefined) requestHeaders.set('Content-Type', 'application/json');

  if (authenticated) {
    const token = getSession()?.token;
    if (token) requestHeaders.set('Authorization', `Bearer ${token}`);
  }

  let response;
  try {
    response = await fetch(`${API_BASE}${path}`, {
      ...fetchOptions,
      headers: requestHeaders,
      body: body === undefined ? undefined : JSON.stringify(body)
    });
  } catch (error) {
    throw new ApiError('No se pudo conectar con la tienda. Revisa tu conexión e inténtalo otra vez.', 0, error);
  }

  const type = response.headers.get('content-type') || '';
  const data = response.status === 204
    ? null
    : type.includes('application/json')
      ? await response.json().catch(() => null)
      : await response.text().catch(() => '');

  if (!response.ok) {
    const message = data?.message || `La solicitud no pudo completarse (${response.status}).`;
    if (response.status === 401 && authenticated) {
      window.dispatchEvent(new CustomEvent('store:auth-expired', { detail: message }));
    }
    throw new ApiError(message, response.status, data);
  }
  return data;
}

const auth = (options = {}) => ({ ...options, authenticated: true });

export const api = {
  login: (body) => request('/auth/login', { method: 'POST', body }),
  loginCompatible: (body) => request('/usuarios/login', { method: 'POST', body }),
  refresh: () => request('/auth/refresh', auth({ method: 'POST' })),

  listarUsuarios: () => request('/usuarios', auth()),
  crearUsuario: (body, authenticated = false) => request('/usuarios', { method: 'POST', body, authenticated }),
  miPerfil: () => request('/usuarios/me', auth()),
  actualizarMiPerfil: (body) => request('/usuarios/me', auth({ method: 'PUT', body })),
  eliminarMiCuenta: () => request('/usuarios/me', auth({ method: 'DELETE' })),
  obtenerUsuario: (id) => request(`/usuarios/${id}`, auth()),
  actualizarUsuario: (id, body) => request(`/usuarios/${id}`, auth({ method: 'PUT', body })),
  eliminarUsuario: (id) => request(`/usuarios/${id}`, auth({ method: 'DELETE' })),

  listarCategorias: () => request('/categorias'),
  listarCategoriasPaginadas: (params) => request(`/categorias/paginadas${queryString(params)}`),
  obtenerCategoria: (id) => request(`/categorias/${id}`),
  crearCategoria: (body) => request('/categorias', auth({ method: 'POST', body })),
  actualizarCategoria: (id, body) => request(`/categorias/${id}`, auth({ method: 'PUT', body })),
  eliminarCategoria: (id) => request(`/categorias/${id}`, auth({ method: 'DELETE' })),

  listarProductos: (params) => request(`/productos${queryString(params)}`),
  buscarProductos: (params) => request(`/productos/buscar${queryString(params)}`),
  filtrarProductos: (params) => request(`/productos/filtrar${queryString(params)}`),
  obtenerProducto: (id) => request(`/productos/${id}`),
  crearProducto: (body) => request('/productos', auth({ method: 'POST', body })),
  actualizarProducto: (id, body) => request(`/productos/${id}`, auth({ method: 'PUT', body })),
  eliminarProducto: (id) => request(`/productos/${id}`, auth({ method: 'DELETE' })),
  actualizarStock: (id, body) => request(`/productos/${id}/stock`, auth({ method: 'PATCH', body })),

  obtenerCarrito: () => request('/carrito', auth()),
  agregarItemCarrito: (body) => request('/carrito/items', auth({ method: 'POST', body })),
  cambiarCantidadCarrito: (productoId, body) => request(`/carrito/items/${productoId}`, auth({ method: 'PUT', body })),
  quitarItemCarrito: (productoId) => request(`/carrito/items/${productoId}`, auth({ method: 'DELETE' })),
  vaciarCarrito: () => request('/carrito', auth({ method: 'DELETE' })),

  checkout: () => request('/ordenes/checkout', auth({ method: 'POST' })),
  listarMisOrdenes: () => request('/ordenes', auth()),
  obtenerOrden: (id) => request(`/ordenes/${id}`, auth()),
  listarTodasOrdenes: () => request('/ordenes/admin/todas', auth()),
  cambiarEstadoOrden: (id, body) => request(`/ordenes/${id}/estado`, auth({ method: 'PATCH', body })),
  cancelarOrden: (id) => request(`/ordenes/${id}/cancelar`, auth({ method: 'POST' }))
};
