import { api, ApiError } from './api.js';
import {
  addGuestItem,
  clearCachedCart,
  clearGuestCart,
  clearSession,
  getCachedCart,
  getCurrentCart,
  getGuestCart,
  getSession,
  isAdmin,
  removeGuestItem,
  setCachedCart,
  setSession,
  updateGuestQuantity
} from './state.js';

const main = document.querySelector('#main-content');
const nav = document.querySelector('#primary-nav');
const navToggle = document.querySelector('.nav-toggle');
const toastRegion = document.querySelector('#toast-region');
const dialog = document.querySelector('#app-dialog');
const dialogContent = document.querySelector('#dialog-content');
let renderSequence = 0;
let searchTimer;

const amount = new Intl.NumberFormat('es-PE', {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
});

function escapeHtml(value = '') {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function formatAmount(value) {
  return amount.format(Number(value || 0));
}

function formatDate(value) {
  if (!value) return 'Sin fecha';
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? value
    : new Intl.DateTimeFormat('es-PE', { dateStyle: 'medium', timeStyle: 'short' }).format(date);
}

function productInitial(name) {
  return escapeHtml((name || '?').trim().charAt(0).toUpperCase());
}

function loadingState(label = 'Cargando') {
  return `<section class="state-card" aria-busy="true"><div><div class="spinner" aria-hidden="true"></div><p>${escapeHtml(label)}…</p></div></section>`;
}

function emptyState(title, text, action = '') {
  return `<section class="state-card"><div><h2>${escapeHtml(title)}</h2><p class="muted">${escapeHtml(text)}</p>${action}</div></section>`;
}

function errorState(error, retry = true) {
  const message = error instanceof ApiError ? error.message : 'Ocurrió un error inesperado.';
  return `<section class="state-card"><div><h2>No pudimos completar la solicitud</h2><p class="muted">${escapeHtml(message)}</p>${retry ? '<button class="button" type="button" data-retry>Reintentar</button>' : ''}</div></section>`;
}

function pageHead(eyebrow, title, lede = '', action = '') {
  return `<header class="page-head"><div><p class="eyebrow">${escapeHtml(eyebrow)}</p><h1>${escapeHtml(title)}</h1>${lede ? `<p class="lede">${escapeHtml(lede)}</p>` : ''}</div>${action}</header>`;
}

function showToast(message, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `toast ${type === 'error' ? 'error' : ''}`;
  toast.setAttribute('role', type === 'error' ? 'alert' : 'status');
  toast.textContent = message;
  toastRegion.append(toast);
  window.setTimeout(() => toast.remove(), 4500);
}

function fieldErrors(form, error) {
  form.querySelectorAll('[data-field-error]').forEach((node) => { node.textContent = ''; });
  form.querySelectorAll('[aria-invalid="true"]').forEach((node) => node.removeAttribute('aria-invalid'));
  Object.entries(error?.fieldErrors || {}).forEach(([name, message]) => {
    const input = form.elements.namedItem(name);
    const output = form.querySelector(`[data-field-error="${CSS.escape(name)}"]`);
    input?.setAttribute('aria-invalid', 'true');
    if (output) output.textContent = message;
  });
}

function setBusy(form, busy) {
  form.setAttribute('aria-busy', String(busy));
  form.querySelectorAll('button, input, select').forEach((control) => { control.disabled = busy; });
}

function openDialog(html, setup) {
  dialogContent.innerHTML = `<div class="dialog-inner">${html}</div>`;
  dialogContent.querySelector('[data-close-dialog]')?.addEventListener('click', () => dialog.close());
  setup?.(dialogContent);
  dialog.showModal();
}

function closeDialog() {
  if (dialog.open) dialog.close();
}

function navigate(path, { replace = false } = {}) {
  if (replace) history.replaceState({}, '', path);
  else history.pushState({}, '', path);
  closeMobileNav();
  renderRoute();
}

function closeMobileNav() {
  nav.classList.remove('open');
  navToggle.setAttribute('aria-expanded', 'false');
}

function currentPathMatches(path) {
  return location.pathname === path || (path !== '/productos' && location.pathname.startsWith(`${path}/`));
}

function renderNav() {
  const session = getSession();
  const cart = getCurrentCart();
  const count = cart.items.reduce((sum, item) => sum + Number(item.cantidad || 0), 0);
  const link = (href, label, extra = '') => `<a href="${href}" data-link ${currentPathMatches(href) ? 'aria-current="page"' : ''} ${extra}>${label}</a>`;
  nav.innerHTML = [
    link('/productos', 'Productos'),
    link('/carrito', `Carrito <span class="cart-count" aria-label="${count} artículos">${count}</span>`, 'class="cart-link"'),
    session ? link('/ordenes', 'Mis órdenes') : '',
    session ? link('/perfil', 'Perfil') : '',
    isAdmin() ? link('/admin/productos', 'Administrar') : '',
    session
      ? '<button type="button" data-logout>Cerrar sesión</button>'
      : link('/login', 'Iniciar sesión')
  ].join('');
  nav.querySelector('[data-logout]')?.addEventListener('click', () => {
    clearSession();
    showToast('Sesión cerrada.');
    navigate('/productos');
  });
}

function adminTabs(active) {
  const tabs = [
    ['/admin/productos', 'Productos', 'productos'],
    ['/admin/categorias', 'Categorías', 'categorias'],
    ['/admin/usuarios', 'Usuarios', 'usuarios'],
    ['/admin/ordenes', 'Órdenes', 'ordenes']
  ];
  return `<nav class="admin-tabs" aria-label="Administración">${tabs.map(([href, label, key]) => `<a href="${href}" data-link ${active === key ? 'aria-current="page"' : ''}>${label}</a>`).join('')}</nav>`;
}

function requireAuth() {
  if (getSession()) return true;
  navigate(`/login?next=${encodeURIComponent(location.pathname + location.search)}`, { replace: true });
  return false;
}

function requireAdmin() {
  if (!requireAuth()) return false;
  if (isAdmin()) return true;
  main.innerHTML = `${pageHead('Acceso restringido', 'No tienes permisos para entrar aquí')} ${emptyState('Área de administración', 'Esta sección requiere el rol ADMIN.', '<a class="button" href="/productos" data-link>Volver al catálogo</a>')}`;
  return false;
}

function flattenCategories(tree, depth = 0) {
  return tree.flatMap((category) => [
    { ...category, depth },
    ...flattenCategories(category.subcategorias || [], depth + 1)
  ]);
}

async function addProduct(product, quantity = 1) {
  if (quantity < 1 || quantity > product.stock) {
    showToast('Elige una cantidad disponible.', 'error');
    return;
  }
  try {
    if (getSession()) {
      const cart = await api.agregarItemCarrito({ productoId: product.id, cantidad: quantity });
      setCachedCart(cart);
    } else {
      addGuestItem(product, quantity);
    }
    showToast(`${product.nombre} se agregó al carrito.`);
  } catch (error) {
    showToast(error.message, 'error');
  }
}

async function syncGuestCart() {
  const items = [...getGuestCart().items];
  let failures = 0;
  for (const item of items) {
    try {
      const cart = await api.agregarItemCarrito({ productoId: item.productoId, cantidad: item.cantidad });
      setCachedCart(cart);
      removeGuestItem(item.productoId);
    } catch {
      failures += 1;
    }
  }
  try {
    setCachedCart(await api.obtenerCarrito());
  } catch {
    failures += 1;
  }
  return failures;
}

async function renderCatalog(sequence) {
  main.innerHTML = `${pageHead('Catálogo', 'Encuentra lo que necesitas', 'Busca por nombre, categoría o precio y agrega productos sin perder tu carrito.')} ${loadingState('Preparando productos')}`;
  const params = new URLSearchParams(location.search);
  const filters = {
    nombre: params.get('nombre') || '',
    categoriaId: params.get('categoriaId') || '',
    precioMin: params.get('precioMin') || '',
    precioMax: params.get('precioMax') || '',
    page: Math.max(1, Number(params.get('page') || 1)),
    size: 9
  };
  try {
    const [categories, products] = await Promise.all([
      api.listarCategorias(),
      filters.nombre || filters.categoriaId || filters.precioMin || filters.precioMax
        ? api.filtrarProductos(filters)
        : api.listarProductos({ page: filters.page, size: filters.size })
    ]);
    if (sequence !== renderSequence) return;
    const flatCategories = flattenCategories(categories);
    const cards = products.content.map((product) => `
      <article class="product-card">
        <div class="product-visual" aria-hidden="true">${productInitial(product.nombre)}</div>
        <div class="product-body">
          <span class="category-chip">${escapeHtml(product.categoriaNombre)}</span>
          <h2><a href="/productos/${product.id}" data-link>${escapeHtml(product.nombre)}</a></h2>
          <div class="product-meta"><span class="price">${formatAmount(product.precio)}</span><span class="stock ${product.stock <= 0 ? 'out' : ''}">${product.stock > 0 ? `${product.stock} disponibles` : 'Agotado'}</span></div>
          <button class="button" type="button" data-add-product="${product.id}" ${product.stock <= 0 ? 'disabled' : ''}>Agregar al carrito</button>
        </div>
      </article>`).join('');

    main.innerHTML = `
      ${pageHead('Catálogo', 'Encuentra lo que necesitas', 'Busca por nombre, categoría o precio y agrega productos sin perder tu carrito.')}
      <form id="filters-form" class="panel filter-panel" novalidate>
        <div class="filter-grid">
          <div class="field"><label for="nombre">Buscar</label><input id="nombre" name="nombre" type="search" value="${escapeHtml(filters.nombre)}" placeholder="Nombre del producto"></div>
          <div class="field"><label for="categoriaId">Categoría</label><select id="categoriaId" name="categoriaId"><option value="">Todas</option>${flatCategories.map((category) => `<option value="${category.id}" ${String(category.id) === filters.categoriaId ? 'selected' : ''}>${'— '.repeat(category.depth)}${escapeHtml(category.nombre)}</option>`).join('')}</select></div>
          <div class="field"><label for="precioMin">Precio mínimo</label><input id="precioMin" name="precioMin" type="number" min="0" step="0.01" value="${escapeHtml(filters.precioMin)}"></div>
          <div class="field"><label for="precioMax">Precio máximo</label><input id="precioMax" name="precioMax" type="number" min="0" step="0.01" value="${escapeHtml(filters.precioMax)}"></div>
        </div>
        <p class="field-error" id="filter-error" role="alert"></p>
        <div class="button-row"><button class="button" type="submit">Aplicar filtros</button><a class="button secondary" href="/productos" data-link>Limpiar</a></div>
      </form>
      <div class="results-bar"><span>${products.totalElements} resultado${products.totalElements === 1 ? '' : 's'}</span><span>Página ${products.currentPage} de ${Math.max(products.totalPages, 1)}</span></div>
      ${cards ? `<section class="product-grid" aria-label="Productos">${cards}</section>` : emptyState('No encontramos productos', 'Prueba con otros filtros o limpia la búsqueda.', '<a class="button" href="/productos" data-link>Limpiar filtros</a>')}
      ${pagination(products, filters)}
    `;

    const form = main.querySelector('#filters-form');
    const applyFilters = () => {
      const data = new FormData(form);
      const min = data.get('precioMin');
      const max = data.get('precioMax');
      if (min && max && Number(min) > Number(max)) {
        main.querySelector('#filter-error').textContent = 'El precio mínimo no puede ser mayor que el máximo.';
        return;
      }
      const next = new URLSearchParams();
      ['nombre', 'categoriaId', 'precioMin', 'precioMax'].forEach((key) => {
        const value = String(data.get(key) || '').trim();
        if (value) next.set(key, value);
      });
      navigate(`/productos${next.toString() ? `?${next}` : ''}`);
    };
    form.addEventListener('submit', (event) => { event.preventDefault(); applyFilters(); });
    form.nombre.addEventListener('input', () => {
      window.clearTimeout(searchTimer);
      searchTimer = window.setTimeout(applyFilters, 450);
    });
    form.categoriaId.addEventListener('change', applyFilters);
    const productMap = new Map(products.content.map((product) => [String(product.id), product]));
    main.querySelectorAll('[data-add-product]').forEach((button) => button.addEventListener('click', () => addProduct(productMap.get(button.dataset.addProduct))));
  } catch (error) {
    if (sequence === renderSequence) main.innerHTML = `${pageHead('Catálogo', 'Encuentra lo que necesitas')} ${errorState(error)}`;
  }
}

function pagination(page, filters) {
  if (page.totalPages <= 1) return '';
  const links = [];
  const start = Math.max(1, page.currentPage - 2);
  const end = Math.min(page.totalPages, page.currentPage + 2);
  for (let current = start; current <= end; current += 1) {
    const query = new URLSearchParams();
    ['nombre', 'categoriaId', 'precioMin', 'precioMax'].forEach((key) => { if (filters[key]) query.set(key, filters[key]); });
    query.set('page', current);
    links.push(`<a class="button small secondary" href="/productos?${query}" data-link ${current === page.currentPage ? 'aria-current="page"' : ''}>${current}</a>`);
  }
  return `<nav class="pagination" aria-label="Páginas de productos">${links.join('')}</nav>`;
}

async function renderProductDetail(id, sequence) {
  main.innerHTML = loadingState('Cargando producto');
  try {
    const product = await api.obtenerProducto(id);
    if (sequence !== renderSequence) return;
    main.innerHTML = `
      <a class="button ghost" href="/productos" data-link>← Volver al catálogo</a>
      <section class="detail-layout">
        <div class="detail-visual" aria-hidden="true">${productInitial(product.nombre)}</div>
        <div class="detail-copy panel">
          <p class="eyebrow">${escapeHtml(product.categoriaNombre)}</p>
          <h1>${escapeHtml(product.nombre)}</h1>
          <p class="price">${formatAmount(product.precio)}</p>
          <p class="stock ${product.stock <= 0 ? 'out' : ''}">${product.stock > 0 ? `${product.stock} unidades disponibles` : 'Producto agotado'}</p>
          <form id="add-detail-form">
            <div class="quantity-line"><label for="cantidad">Cantidad</label><input id="cantidad" name="cantidad" type="number" min="1" max="${product.stock}" value="1" required></div>
            <button class="button" type="submit" ${product.stock <= 0 ? 'disabled' : ''}>Agregar al carrito</button>
          </form>
        </div>
      </section>`;
    main.querySelector('#add-detail-form').addEventListener('submit', async (event) => {
      event.preventDefault();
      const quantity = Number(event.currentTarget.cantidad.value);
      await addProduct(product, quantity);
    });
  } catch (error) {
    main.innerHTML = `${pageHead('Producto', error.status === 404 ? 'Producto no encontrado' : 'No pudimos cargar el producto')} ${errorState(error)}`;
  }
}

async function getCartForView() {
  if (!getSession()) return { cart: getGuestCart(), offline: false };
  try {
    const cart = await api.obtenerCarrito();
    setCachedCart(cart);
    return { cart, offline: false };
  } catch (error) {
    const cached = getCachedCart();
    if (cached.items.length) return { cart: cached, offline: true, error };
    throw error;
  }
}

async function renderCart(sequence) {
  main.innerHTML = `${pageHead('Tu selección', 'Carrito')} ${loadingState('Cargando carrito')}`;
  try {
    const { cart, offline } = await getCartForView();
    if (sequence !== renderSequence) return;
    const session = getSession();
    if (!cart.items.length) {
      main.innerHTML = `${pageHead('Tu selección', 'El carrito está vacío', 'Explora el catálogo y agrega algo que te guste.')} ${emptyState('Aún no hay productos', 'Tu selección aparecerá aquí.', '<a class="button" href="/productos" data-link>Ver productos</a>')}`;
      return;
    }
    main.innerHTML = `
      ${pageHead('Tu selección', 'Carrito', session ? 'Los cambios se guardan en tu cuenta.' : 'Este carrito está guardado en este dispositivo.')}
      ${offline ? '<div class="notice error" role="alert">Mostramos la última copia guardada. La conexión con la tienda no está disponible.</div>' : ''}
      <div class="cart-layout">
        <section class="cart-list" aria-label="Productos del carrito">
          ${cart.items.map((item) => `<article class="cart-item" data-cart-item="${item.productoId}"><div><h2>${escapeHtml(item.productoNombre)}</h2><p class="muted">Precio: ${formatAmount(item.productoPrecio)}</p></div><div class="field"><label for="quantity-${item.productoId}">Cantidad</label><input id="quantity-${item.productoId}" data-quantity="${item.productoId}" type="number" min="0" ${item.stock ? `max="${item.stock}"` : ''} value="${item.cantidad}"></div><div><strong>${formatAmount(item.subtotal)}</strong><button class="button ghost small" type="button" data-remove="${item.productoId}">Quitar</button></div></article>`).join('')}
        </section>
        <aside class="panel cart-summary"><h2>Resumen</h2><div class="total-line"><span>Total</span><strong>${formatAmount(cart.total)}</strong></div><div class="button-row"><button class="button" type="button" data-checkout>${session ? 'Confirmar compra' : 'Ingresar para comprar'}</button><button class="button secondary" type="button" data-clear-cart>Vaciar</button></div></aside>
      </div>`;
    main.querySelectorAll('[data-quantity]').forEach((input) => input.addEventListener('change', async () => {
      const productId = Number(input.dataset.quantity);
      const quantity = Math.max(0, Number(input.value));
      input.disabled = true;
      try {
        if (session) setCachedCart(await api.cambiarCantidadCarrito(productId, { cantidad: quantity }));
        else updateGuestQuantity(productId, quantity);
        await renderRoute();
      } catch (error) {
        input.disabled = false;
        showToast(error.message, 'error');
      }
    }));
    main.querySelectorAll('[data-remove]').forEach((button) => button.addEventListener('click', async () => {
      button.disabled = true;
      try {
        if (session) {
          await api.quitarItemCarrito(Number(button.dataset.remove));
          setCachedCart(await api.obtenerCarrito());
        } else removeGuestItem(Number(button.dataset.remove));
        await renderRoute();
      } catch (error) {
        button.disabled = false;
        showToast(error.message, 'error');
      }
    }));
    main.querySelector('[data-clear-cart]').addEventListener('click', async () => {
      if (!window.confirm('¿Vaciar todo el carrito?')) return;
      try {
        if (session) {
          await api.vaciarCarrito();
          setCachedCart({ id: cart.id, items: [], total: 0 });
        } else clearGuestCart();
        showToast('Carrito vaciado.');
        await renderRoute();
      } catch (error) { showToast(error.message, 'error'); }
    });
    main.querySelector('[data-checkout]').addEventListener('click', async (event) => {
      if (!session) {
        navigate('/login?next=%2Fcarrito');
        return;
      }
      event.currentTarget.disabled = true;
      try {
        const order = await api.checkout();
        clearCachedCart(session.id);
        showToast(`Orden #${order.id} creada.`);
        navigate(`/ordenes/${order.id}`);
      } catch (error) {
        event.currentTarget.disabled = false;
        showToast(error.message, 'error');
      }
    });
  } catch (error) {
    main.innerHTML = `${pageHead('Tu selección', 'Carrito')} ${errorState(error)}`;
  }
}

function renderLogin() {
  if (getSession()) {
    navigate('/productos', { replace: true });
    return;
  }
  main.innerHTML = `<section class="auth-layout"><aside class="auth-aside"><p class="eyebrow">Tu cuenta</p><h1>Continúa donde lo dejaste.</h1><p>Al entrar, sincronizaremos los productos guardados en este dispositivo.</p><p><strong>${getGuestCart().items.length}</strong> productos distintos en tu carrito local.</p></aside><form id="login-form" class="panel auth-form" novalidate><h2>Iniciar sesión</h2><div class="field"><label for="email">Correo electrónico</label><input id="email" name="email" type="email" autocomplete="email" required><p class="field-error" data-field-error="email"></p></div><div class="field"><label for="password">Contraseña</label><input id="password" name="password" type="password" autocomplete="current-password" required><p class="field-error" data-field-error="password"></p></div><p id="form-error" class="field-error" role="alert"></p><button class="button" type="submit">Entrar</button><p class="muted">¿Aún no tienes cuenta? <a href="/registro" data-link>Regístrate</a>.</p></form></section>`;
  const form = main.querySelector('#login-form');
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!form.reportValidity()) return;
    setBusy(form, true);
    main.querySelector('#form-error').textContent = '';
    try {
      const auth = await api.login({ email: form.email.value.trim(), password: form.password.value });
      setSession(auth);
      const failures = await syncGuestCart();
      showToast(failures ? 'Sesión iniciada; algunos productos no pudieron sincronizarse.' : 'Sesión iniciada.');
      const next = new URLSearchParams(location.search).get('next');
      navigate(next?.startsWith('/') && !next.startsWith('//') ? next : '/productos', { replace: true });
    } catch (error) {
      setBusy(form, false);
      fieldErrors(form, error);
      main.querySelector('#form-error').textContent = error.message;
    }
  });
}

function renderRegister() {
  main.innerHTML = `<section class="auth-layout"><aside class="auth-aside"><p class="eyebrow">Nueva cuenta</p><h1>Tu carrito puede viajar contigo.</h1><p>El registro crea una cuenta de cliente. Podrás comprar, consultar órdenes y gestionar tu perfil.</p></aside><form id="register-form" class="panel auth-form" novalidate><h2>Crear cuenta</h2>${textField('nombre', 'Nombre', 'text', 'name')}${textField('email', 'Correo electrónico', 'email', 'email')}${textField('password', 'Contraseña', 'password', 'new-password')}<p id="form-error" class="field-error" role="alert"></p><button class="button" type="submit">Crear cuenta</button><p class="muted">¿Ya tienes cuenta? <a href="/login" data-link>Inicia sesión</a>.</p></form></section>`;
  const form = main.querySelector('#register-form');
  form.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!form.reportValidity()) return;
    setBusy(form, true);
    try {
      await api.crearUsuario({ nombre: form.nombre.value.trim(), email: form.email.value.trim(), password: form.password.value });
      showToast('Cuenta creada. Ya puedes iniciar sesión.');
      navigate('/login');
    } catch (error) {
      setBusy(form, false);
      fieldErrors(form, error);
      main.querySelector('#form-error').textContent = error.message;
    }
  });
}

function textField(name, label, type = 'text', autocomplete = 'off', value = '') {
  return `<div class="field"><label for="${name}">${label}</label><input id="${name}" name="${name}" type="${type}" autocomplete="${autocomplete}" value="${escapeHtml(value)}" required><p class="field-error" data-field-error="${name}"></p></div>`;
}

async function renderProfile(sequence) {
  if (!requireAuth()) return;
  main.innerHTML = loadingState('Cargando perfil');
  try {
    const profile = await api.miPerfil();
    if (sequence !== renderSequence) return;
    main.innerHTML = `${pageHead('Cuenta', 'Mi perfil', `Usuario #${profile.id} · ${profile.role}`)}<form id="profile-form" class="panel" novalidate><div class="form-grid two">${textField('nombre', 'Nombre', 'text', 'name', profile.nombre)}${textField('email', 'Correo electrónico', 'email', 'email', profile.email)}${textField('password', 'Nueva contraseña', 'password', 'new-password')}</div><p class="field-hint">La API exige una contraseña en cada actualización.</p><p id="form-error" class="field-error" role="alert"></p><div class="button-row"><button class="button" type="submit">Guardar cambios</button><button class="button danger" type="button" data-delete-account>Eliminar mi cuenta</button></div></form>`;
    const form = main.querySelector('#profile-form');
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      setBusy(form, true);
      try {
        await api.actualizarMiPerfil({ nombre: form.nombre.value.trim(), email: form.email.value.trim(), password: form.password.value });
        showToast('Perfil actualizado.');
        await renderRoute();
      } catch (error) {
        setBusy(form, false);
        fieldErrors(form, error);
        main.querySelector('#form-error').textContent = error.message;
      }
    });
    main.querySelector('[data-delete-account]').addEventListener('click', async () => {
      if (!window.confirm('¿Eliminar tu cuenta de forma permanente?')) return;
      const userId = getSession().id;
      try {
        await api.eliminarMiCuenta();
        clearCachedCart(userId);
        clearSession();
        showToast('Tu cuenta fue eliminada.');
        navigate('/productos');
      } catch (error) { showToast(error.message, 'error'); }
    });
  } catch (error) { main.innerHTML = errorState(error); }
}

async function renderOrders(sequence) {
  if (!requireAuth()) return;
  main.innerHTML = `${pageHead('Compras', 'Mis órdenes')} ${loadingState('Cargando historial')}`;
  try {
    const orders = await api.listarMisOrdenes();
    if (sequence !== renderSequence) return;
    main.innerHTML = `${pageHead('Compras', 'Mis órdenes', 'Consulta el estado y detalle de tus compras.')}${orders.length ? `<section class="order-list">${orders.map(orderCard).join('')}</section>` : emptyState('Aún no tienes órdenes', 'Cuando confirmes una compra aparecerá aquí.', '<a class="button" href="/productos" data-link>Explorar productos</a>')}`;
  } catch (error) { main.innerHTML = `${pageHead('Compras', 'Mis órdenes')} ${errorState(error)}`; }
}

function orderCard(order) {
  return `<article class="order-card"><div><span class="status-chip" data-status="${order.estado}">${order.estado}</span><h2>Orden #${order.id}</h2><p class="muted">${formatDate(order.fechaCreacion)} · ${order.items.length} ítem${order.items.length === 1 ? '' : 's'}</p></div><div><p class="price">${formatAmount(order.total)}</p><a class="button secondary small" href="/ordenes/${order.id}" data-link>Ver detalle</a></div></article>`;
}

async function renderOrderDetail(id, sequence) {
  if (!requireAuth()) return;
  main.innerHTML = loadingState('Cargando orden');
  try {
    const order = await api.obtenerOrden(id);
    if (sequence !== renderSequence) return;
    const ownPending = order.estado === 'PENDIENTE' && order.usuarioId === getSession().id;
    main.innerHTML = `<a class="button ghost" href="${isAdmin() ? '/admin/ordenes' : '/ordenes'}" data-link>← Volver a órdenes</a>${pageHead('Detalle de compra', `Orden #${order.id}`, `${formatDate(order.fechaCreacion)} · Usuario #${order.usuarioId}`, `<span class="status-chip" data-status="${order.estado}">${order.estado}</span>`)}<section class="panel"><div class="table-wrap"><table><thead><tr><th>Producto</th><th>Cantidad</th><th>Precio</th><th>Subtotal</th></tr></thead><tbody>${order.items.map((item) => `<tr><td>${escapeHtml(item.productoNombre)}</td><td>${item.cantidad}</td><td>${formatAmount(item.precioUnitario)}</td><td>${formatAmount(item.subtotal)}</td></tr>`).join('')}</tbody></table></div><div class="total-line"><span>Total</span><strong>${formatAmount(order.total)}</strong></div>${ownPending ? '<button class="button danger" type="button" data-cancel-order>Cancelar orden</button>' : ''}</section>`;
    main.querySelector('[data-cancel-order]')?.addEventListener('click', async (event) => {
      if (!window.confirm('¿Cancelar esta orden pendiente? El stock será restaurado.')) return;
      event.currentTarget.disabled = true;
      try {
        await api.cancelarOrden(order.id);
        showToast('Orden cancelada.');
        await renderRoute();
      } catch (error) { event.currentTarget.disabled = false; showToast(error.message, 'error'); }
    });
  } catch (error) { main.innerHTML = `${pageHead('Compras', error.status === 404 ? 'Orden no encontrada' : 'No pudimos cargar la orden')} ${errorState(error)}`; }
}

async function renderAdminProducts(sequence) {
  if (!requireAdmin()) return;
  main.innerHTML = `${adminTabs('productos')}${pageHead('Administración', 'Productos')} ${loadingState('Cargando inventario')}`;
  const page = Math.max(1, Number(new URLSearchParams(location.search).get('page') || 1));
  try {
    const [products, categories] = await Promise.all([api.listarProductos({ page, size: 20 }), api.listarCategorias()]);
    if (sequence !== renderSequence) return;
    const flat = flattenCategories(categories);
    main.innerHTML = `${adminTabs('productos')}${pageHead('Administración', 'Productos', 'Gestiona el catálogo y el stock.', '<button class="button" type="button" data-new-product>Nuevo producto</button>')}${products.content.length ? `<div class="table-wrap"><table><thead><tr><th>Producto</th><th>Categoría</th><th>Precio</th><th>Stock</th><th>Acciones</th></tr></thead><tbody>${products.content.map((product) => `<tr><td>${escapeHtml(product.nombre)}</td><td>${escapeHtml(product.categoriaNombre)}</td><td>${formatAmount(product.precio)}</td><td>${product.stock}</td><td><div class="inline-actions"><button class="button secondary small" data-edit-product="${product.id}">Editar</button><button class="button secondary small" data-stock-product="${product.id}">Stock</button><button class="button danger small" data-delete-product="${product.id}">Eliminar</button></div></td></tr>`).join('')}</tbody></table></div>${adminPagination(products, '/admin/productos')}` : emptyState('No hay productos', 'Crea el primer producto del catálogo.')}`;
    const byId = new Map(products.content.map((product) => [String(product.id), product]));
    main.querySelector('[data-new-product]')?.addEventListener('click', () => productDialog(null, flat));
    main.querySelectorAll('[data-edit-product]').forEach((button) => button.addEventListener('click', () => productDialog(byId.get(button.dataset.editProduct), flat)));
    main.querySelectorAll('[data-stock-product]').forEach((button) => button.addEventListener('click', () => stockDialog(byId.get(button.dataset.stockProduct))));
    main.querySelectorAll('[data-delete-product]').forEach((button) => button.addEventListener('click', async () => {
      const product = byId.get(button.dataset.deleteProduct);
      if (!window.confirm(`¿Eliminar “${product.nombre}”?`)) return;
      try { await api.eliminarProducto(product.id); showToast('Producto eliminado.'); await renderRoute(); } catch (error) { showToast(error.message, 'error'); }
    }));
  } catch (error) { main.innerHTML = `${adminTabs('productos')}${pageHead('Administración', 'Productos')} ${errorState(error)}`; }
}

function adminPagination(page, base) {
  if (page.totalPages <= 1) return '';
  return `<nav class="pagination" aria-label="Páginas"><a class="button secondary small" href="${base}?page=${Math.max(1, page.currentPage - 1)}" data-link ${page.currentPage <= 1 ? 'aria-disabled="true"' : ''}>Anterior</a><span class="button ghost small">${page.currentPage} / ${page.totalPages}</span><a class="button secondary small" href="${base}?page=${Math.min(page.totalPages, page.currentPage + 1)}" data-link ${page.currentPage >= page.totalPages ? 'aria-disabled="true"' : ''}>Siguiente</a></nav>`;
}

function dialogHeader(title) {
  return `<div class="dialog-head"><h2 id="dialog-title">${escapeHtml(title)}</h2><button class="icon-button" type="button" data-close-dialog aria-label="Cerrar">×</button></div>`;
}

function productDialog(product, categories) {
  openDialog(`${dialogHeader(product ? 'Editar producto' : 'Nuevo producto')}<form id="product-form" novalidate><div class="form-grid two">${textField('nombre', 'Nombre', 'text', 'off', product?.nombre || '')}<div class="field"><label for="precio">Precio</label><input id="precio" name="precio" type="number" min="0" step="0.01" value="${product?.precio ?? ''}" required><p class="field-error" data-field-error="precio"></p></div><div class="field"><label for="stock">Stock</label><input id="stock" name="stock" type="number" min="0" step="1" value="${product?.stock ?? 0}" required><p class="field-error" data-field-error="stock"></p></div><div class="field"><label for="categoriaId">Categoría</label><select id="categoriaId" name="categoriaId" required><option value="">Selecciona</option>${categories.map((category) => `<option value="${category.id}" ${category.id === product?.categoriaId ? 'selected' : ''}>${'— '.repeat(category.depth)}${escapeHtml(category.nombre)}</option>`).join('')}</select><p class="field-error" data-field-error="categoriaId"></p></div></div><p id="dialog-error" class="field-error" role="alert"></p><button class="button" type="submit">${product ? 'Guardar cambios' : 'Crear producto'}</button></form>`, (root) => {
    const form = root.querySelector('#product-form');
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      setBusy(form, true);
      const body = { nombre: form.nombre.value.trim(), precio: Number(form.precio.value), stock: Number(form.stock.value), categoriaId: Number(form.categoriaId.value) };
      try {
        if (product) await api.actualizarProducto(product.id, body); else await api.crearProducto(body);
        closeDialog(); showToast(product ? 'Producto actualizado.' : 'Producto creado.'); await renderRoute();
      } catch (error) { setBusy(form, false); fieldErrors(form, error); root.querySelector('#dialog-error').textContent = error.message; }
    });
  });
}

function stockDialog(product) {
  openDialog(`${dialogHeader('Actualizar stock')}<form id="stock-form" novalidate><p>${escapeHtml(product.nombre)}</p><div class="field"><label for="stock">Nuevo stock</label><input id="stock" name="stock" type="number" min="0" step="1" value="${product.stock}" required><p class="field-error" data-field-error="stock"></p></div><p id="dialog-error" class="field-error" role="alert"></p><button class="button" type="submit">Actualizar</button></form>`, (root) => {
    const form = root.querySelector('#stock-form');
    form.addEventListener('submit', async (event) => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      setBusy(form, true);
      try { await api.actualizarStock(product.id, { stock: Number(form.stock.value) }); closeDialog(); showToast('Stock actualizado.'); await renderRoute(); } catch (error) { setBusy(form, false); fieldErrors(form, error); root.querySelector('#dialog-error').textContent = error.message; }
    });
  });
}

async function renderAdminCategories(sequence) {
  if (!requireAdmin()) return;
  main.innerHTML = `${adminTabs('categorias')}${pageHead('Administración', 'Categorías')} ${loadingState('Cargando categorías')}`;
  try {
    const tree = await api.listarCategorias();
    if (sequence !== renderSequence) return;
    const flat = flattenCategories(tree);
    main.innerHTML = `${adminTabs('categorias')}${pageHead('Administración', 'Categorías', 'La estructura admite hasta dos niveles.', '<button class="button" type="button" data-new-category>Nueva categoría</button>')}${flat.length ? `<section class="category-tree">${flat.map((category) => `<article class="category-row" style="margin-left:${category.depth * 1.5}rem"><div><span class="category-chip">${category.depth ? 'Subcategoría' : 'Raíz'}</span><h2>${escapeHtml(category.nombre)}</h2></div><div class="inline-actions"><button class="button secondary small" data-edit-category="${category.id}">Renombrar</button><button class="button danger small" data-delete-category="${category.id}">Eliminar</button></div></article>`).join('')}</section>` : emptyState('No hay categorías', 'Crea una categoría raíz para comenzar.')}`;
    const byId = new Map(flat.map((category) => [String(category.id), category]));
    main.querySelector('[data-new-category]')?.addEventListener('click', () => categoryDialog(null, tree));
    main.querySelectorAll('[data-edit-category]').forEach((button) => button.addEventListener('click', () => categoryDialog(byId.get(button.dataset.editCategory), tree)));
    main.querySelectorAll('[data-delete-category]').forEach((button) => button.addEventListener('click', async () => {
      const category = byId.get(button.dataset.deleteCategory);
      if (!window.confirm(`¿Eliminar “${category.nombre}”?`)) return;
      try { await api.eliminarCategoria(category.id); showToast('Categoría eliminada.'); await renderRoute(); } catch (error) { showToast(error.message, 'error'); }
    }));
  } catch (error) { main.innerHTML = `${adminTabs('categorias')}${pageHead('Administración', 'Categorías')} ${errorState(error)}`; }
}

function categoryDialog(category, roots) {
  openDialog(`${dialogHeader(category ? 'Renombrar categoría' : 'Nueva categoría')}<form id="category-form" novalidate>${textField('nombre', 'Nombre', 'text', 'off', category?.nombre || '')}${category ? '' : `<div class="field"><label for="padreId">Categoría padre</label><select id="padreId" name="padreId"><option value="">Ninguna (categoría raíz)</option>${roots.map((root) => `<option value="${root.id}">${escapeHtml(root.nombre)}</option>`).join('')}</select><p class="field-hint">Solo las categorías raíz pueden ser padre.</p></div>`}<p id="dialog-error" class="field-error" role="alert"></p><button class="button" type="submit">${category ? 'Guardar nombre' : 'Crear categoría'}</button></form>`, (root) => {
    const form = root.querySelector('#category-form');
    form.addEventListener('submit', async (event) => {
      event.preventDefault(); if (!form.reportValidity()) return; setBusy(form, true);
      const body = { nombre: form.nombre.value.trim() };
      if (!category && form.padreId.value) body.padreId = Number(form.padreId.value);
      try { if (category) await api.actualizarCategoria(category.id, body); else await api.crearCategoria(body); closeDialog(); showToast(category ? 'Categoría actualizada.' : 'Categoría creada.'); await renderRoute(); } catch (error) { setBusy(form, false); fieldErrors(form, error); root.querySelector('#dialog-error').textContent = error.message; }
    });
  });
}

async function renderAdminUsers(sequence) {
  if (!requireAdmin()) return;
  main.innerHTML = `${adminTabs('usuarios')}${pageHead('Administración', 'Usuarios')} ${loadingState('Cargando usuarios')}`;
  try {
    const users = await api.listarUsuarios();
    if (sequence !== renderSequence) return;
    main.innerHTML = `${adminTabs('usuarios')}${pageHead('Administración', 'Usuarios', 'Crea clientes o administradores y mantén sus datos.', '<button class="button" type="button" data-new-user>Nuevo usuario</button>')}${users.length ? `<div class="table-wrap"><table><thead><tr><th>ID</th><th>Nombre</th><th>Email</th><th>Rol</th><th>Acciones</th></tr></thead><tbody>${users.map((user) => `<tr><td>${user.id}</td><td>${escapeHtml(user.nombre)}</td><td>${escapeHtml(user.email)}</td><td><span class="category-chip">${user.role}</span></td><td><div class="inline-actions">${user.id === getSession().id ? '<a class="button secondary small" href="/perfil" data-link>Mi perfil</a>' : `<button class="button secondary small" data-edit-user="${user.id}">Editar</button><button class="button danger small" data-delete-user="${user.id}">Eliminar</button>`}</div></td></tr>`).join('')}</tbody></table></div>` : emptyState('No hay usuarios', 'Crea el primer usuario.')}`;
    const byId = new Map(users.map((user) => [String(user.id), user]));
    main.querySelector('[data-new-user]')?.addEventListener('click', () => userDialog());
    main.querySelectorAll('[data-edit-user]').forEach((button) => button.addEventListener('click', () => userDialog(byId.get(button.dataset.editUser))));
    main.querySelectorAll('[data-delete-user]').forEach((button) => button.addEventListener('click', async () => {
      const user = byId.get(button.dataset.deleteUser);
      if (!window.confirm(`¿Eliminar a “${user.nombre}”?`)) return;
      try { await api.eliminarUsuario(user.id); showToast('Usuario eliminado.'); await renderRoute(); } catch (error) { showToast(error.message, 'error'); }
    }));
  } catch (error) { main.innerHTML = `${adminTabs('usuarios')}${pageHead('Administración', 'Usuarios')} ${errorState(error)}`; }
}

function userDialog(user = null) {
  openDialog(`${dialogHeader(user ? 'Editar usuario' : 'Nuevo usuario')}<form id="user-form" novalidate><div class="form-grid two">${textField('nombre', 'Nombre', 'text', 'name', user?.nombre || '')}${textField('email', 'Correo electrónico', 'email', 'email', user?.email || '')}${textField('password', user ? 'Nueva contraseña' : 'Contraseña', 'password', 'new-password')}<div class="field"><label for="role">Rol</label><select id="role" name="role" required><option value="CLIENTE" ${user?.role !== 'ADMIN' ? 'selected' : ''}>CLIENTE</option><option value="ADMIN" ${user?.role === 'ADMIN' ? 'selected' : ''}>ADMIN</option></select><p class="field-error" data-field-error="role"></p></div></div><p id="dialog-error" class="field-error" role="alert"></p><button class="button" type="submit">${user ? 'Guardar cambios' : 'Crear usuario'}</button></form>`, (root) => {
    const form = root.querySelector('#user-form');
    form.addEventListener('submit', async (event) => {
      event.preventDefault(); if (!form.reportValidity()) return; setBusy(form, true);
      const body = { nombre: form.nombre.value.trim(), email: form.email.value.trim(), password: form.password.value, role: form.role.value };
      try { if (user) await api.actualizarUsuario(user.id, body); else await api.crearUsuario(body, true); closeDialog(); showToast(user ? 'Usuario actualizado.' : 'Usuario creado.'); await renderRoute(); } catch (error) { setBusy(form, false); fieldErrors(form, error); root.querySelector('#dialog-error').textContent = error.message; }
    });
  });
}

async function renderAdminOrders(sequence) {
  if (!requireAdmin()) return;
  main.innerHTML = `${adminTabs('ordenes')}${pageHead('Administración', 'Órdenes')} ${loadingState('Cargando órdenes')}`;
  try {
    const orders = await api.listarTodasOrdenes();
    if (sequence !== renderSequence) return;
    const nextState = { PENDIENTE: 'PAGADA', PAGADA: 'ENVIADA', ENVIADA: 'ENTREGADA' };
    main.innerHTML = `${adminTabs('ordenes')}${pageHead('Administración', 'Órdenes', 'Revisa todas las compras y avanza su estado.')}${orders.length ? `<div class="table-wrap"><table><thead><tr><th>Orden</th><th>Usuario</th><th>Fecha</th><th>Estado</th><th>Total</th><th>Acciones</th></tr></thead><tbody>${orders.map((order) => `<tr><td>#${order.id}</td><td>#${order.usuarioId}</td><td>${formatDate(order.fechaCreacion)}</td><td><span class="status-chip" data-status="${order.estado}">${order.estado}</span></td><td>${formatAmount(order.total)}</td><td><div class="inline-actions"><a class="button secondary small" href="/ordenes/${order.id}" data-link>Detalle</a>${nextState[order.estado] ? `<button class="button small" data-advance-order="${order.id}" data-next-state="${nextState[order.estado]}">Marcar ${nextState[order.estado]}</button>` : ''}</div></td></tr>`).join('')}</tbody></table></div>` : emptyState('No hay órdenes', 'Las compras confirmadas aparecerán aquí.')}`;
    main.querySelectorAll('[data-advance-order]').forEach((button) => button.addEventListener('click', async () => {
      button.disabled = true;
      try { await api.cambiarEstadoOrden(Number(button.dataset.advanceOrder), { estado: button.dataset.nextState }); showToast('Estado actualizado.'); await renderRoute(); } catch (error) { button.disabled = false; showToast(error.message, 'error'); }
    }));
  } catch (error) { main.innerHTML = `${adminTabs('ordenes')}${pageHead('Administración', 'Órdenes')} ${errorState(error)}`; }
}

function renderNotFound() {
  main.innerHTML = `${pageHead('Error 404', 'Página no encontrada')} ${emptyState('Esta ruta no existe', 'Vuelve al catálogo para seguir navegando.', '<a class="button" href="/productos" data-link>Ir al catálogo</a>')}`;
}

async function renderRoute() {
  const sequence = ++renderSequence;
  renderNav();
  window.scrollTo({ top: 0, behavior: 'instant' });
  const path = location.pathname.replace(/\/$/, '') || '/';
  if (path === '/') return navigate('/productos', { replace: true });
  if (path === '/productos') await renderCatalog(sequence);
  else if (/^\/productos\/\d+$/.test(path)) await renderProductDetail(path.split('/').pop(), sequence);
  else if (path === '/carrito') await renderCart(sequence);
  else if (path === '/login') renderLogin();
  else if (path === '/registro') renderRegister();
  else if (path === '/perfil') await renderProfile(sequence);
  else if (path === '/ordenes') await renderOrders(sequence);
  else if (/^\/ordenes\/\d+$/.test(path)) await renderOrderDetail(path.split('/').pop(), sequence);
  else if (path === '/admin/productos') await renderAdminProducts(sequence);
  else if (path === '/admin/categorias') await renderAdminCategories(sequence);
  else if (path === '/admin/usuarios') await renderAdminUsers(sequence);
  else if (path === '/admin/ordenes') await renderAdminOrders(sequence);
  else renderNotFound();
  renderNav();
  main.focus({ preventScroll: true });
}

document.addEventListener('click', (event) => {
  const anchor = event.target.closest('a[data-link]');
  if (!anchor || event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || anchor.target) return;
  if (anchor.getAttribute('aria-disabled') === 'true') { event.preventDefault(); return; }
  const url = new URL(anchor.href, location.href);
  if (url.origin !== location.origin) return;
  event.preventDefault();
  navigate(url.pathname + url.search + url.hash);
});

document.addEventListener('click', (event) => {
  if (event.target.closest('[data-retry]')) renderRoute();
});

navToggle.addEventListener('click', () => {
  const open = navToggle.getAttribute('aria-expanded') === 'true';
  navToggle.setAttribute('aria-expanded', String(!open));
  nav.classList.toggle('open', !open);
});

dialog.addEventListener('click', (event) => {
  if (event.target === dialog) dialog.close();
});

window.addEventListener('popstate', renderRoute);
window.addEventListener('store:cart-change', renderNav);
window.addEventListener('store:session-change', renderNav);
window.addEventListener('store:auth-expired', (event) => {
  if (!getSession()) return;
  clearSession();
  showToast(event.detail || 'Tu sesión terminó. Vuelve a iniciar sesión.', 'error');
  navigate(`/login?next=${encodeURIComponent(location.pathname + location.search)}`, { replace: true });
});

renderRoute();
