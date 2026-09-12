const SESSION_KEY = 'store.session';
const GUEST_CART_KEY = 'store.cart.guest';
const CART_CACHE_PREFIX = 'store.cart.user.';

const parseStored = (storage, key, fallback) => {
  try {
    const value = storage.getItem(key);
    return value ? JSON.parse(value) : fallback;
  } catch {
    return fallback;
  }
};

const emptyCart = () => ({ id: null, items: [], total: 0 });

export function getSession() {
  return parseStored(sessionStorage, SESSION_KEY, null);
}

export function setSession(authResponse) {
  const session = {
    token: authResponse.token,
    id: authResponse.id,
    email: authResponse.email,
    role: authResponse.role
  };
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
  window.dispatchEvent(new CustomEvent('store:session-change', { detail: session }));
  return session;
}

export function clearSession() {
  sessionStorage.removeItem(SESSION_KEY);
  window.dispatchEvent(new CustomEvent('store:session-change', { detail: null }));
}

export function isAdmin() {
  return getSession()?.role === 'ADMIN';
}

export function getGuestCart() {
  return parseStored(localStorage, GUEST_CART_KEY, emptyCart());
}

export function setGuestCart(cart) {
  localStorage.setItem(GUEST_CART_KEY, JSON.stringify(cart));
  notifyCart(cart);
  return cart;
}

export function addGuestItem(product, quantity) {
  const cart = getGuestCart();
  const existing = cart.items.find((item) => item.productoId === product.id);
  if (existing) {
    existing.cantidad = Math.min(existing.cantidad + quantity, product.stock);
    existing.subtotal = existing.productoPrecio * existing.cantidad;
  } else {
    cart.items.push({
      id: `local-${product.id}`,
      productoId: product.id,
      productoNombre: product.nombre,
      productoPrecio: product.precio,
      cantidad: Math.min(quantity, product.stock),
      subtotal: product.precio * Math.min(quantity, product.stock),
      stock: product.stock
    });
  }
  cart.total = totalOf(cart.items);
  return setGuestCart(cart);
}

export function updateGuestQuantity(productId, quantity) {
  const cart = getGuestCart();
  const item = cart.items.find((entry) => entry.productoId === productId);
  if (!item) return cart;
  if (quantity <= 0) {
    cart.items = cart.items.filter((entry) => entry.productoId !== productId);
  } else {
    item.cantidad = Math.min(quantity, item.stock ?? quantity);
    item.subtotal = item.productoPrecio * item.cantidad;
  }
  cart.total = totalOf(cart.items);
  return setGuestCart(cart);
}

export function removeGuestItem(productId) {
  return updateGuestQuantity(productId, 0);
}

export function clearGuestCart() {
  return setGuestCart(emptyCart());
}

export function getCachedCart(userId = getSession()?.id) {
  if (!userId) return getGuestCart();
  return parseStored(localStorage, `${CART_CACHE_PREFIX}${userId}`, emptyCart());
}

export function setCachedCart(cart, userId = getSession()?.id) {
  if (!userId) return setGuestCart(cart);
  localStorage.setItem(`${CART_CACHE_PREFIX}${userId}`, JSON.stringify(cart));
  notifyCart(cart);
  return cart;
}

export function clearCachedCart(userId = getSession()?.id) {
  if (userId) localStorage.removeItem(`${CART_CACHE_PREFIX}${userId}`);
  notifyCart(emptyCart());
}

export function getCurrentCart() {
  const session = getSession();
  return session ? getCachedCart(session.id) : getGuestCart();
}

function totalOf(items) {
  return items.reduce((sum, item) => sum + Number(item.subtotal || 0), 0);
}

function notifyCart(cart) {
  window.dispatchEvent(new CustomEvent('store:cart-change', { detail: cart }));
}
