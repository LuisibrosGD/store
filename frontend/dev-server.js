'use strict';

const fs = require('node:fs');
const http = require('node:http');
const path = require('node:path');

const frontendRoot = __dirname;
const frontendPort = Number(process.env.STORE_FRONTEND_PORT || 4173);
const backendUrl = new URL(process.env.STORE_API_TARGET || 'http://localhost:8080');

const contentTypes = {
  '.css': 'text/css; charset=utf-8',
  '.html': 'text/html; charset=utf-8',
  '.ico': 'image/x-icon',
  '.jpeg': 'image/jpeg',
  '.jpg': 'image/jpeg',
  '.js': 'text/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.svg': 'image/svg+xml; charset=utf-8',
  '.webp': 'image/webp'
};

function proxyApi(clientRequest, clientResponse) {
  const target = new URL(clientRequest.url, backendUrl);
  const headers = { ...clientRequest.headers, host: backendUrl.host };

  const proxyRequest = http.request(target, {
    method: clientRequest.method,
    headers
  }, (proxyResponse) => {
    clientResponse.writeHead(proxyResponse.statusCode || 502, proxyResponse.headers);
    proxyResponse.pipe(clientResponse);
  });

  proxyRequest.on('error', () => {
    if (clientResponse.headersSent) {
      clientResponse.end();
      return;
    }
    clientResponse.writeHead(502, { 'content-type': 'application/json; charset=utf-8' });
    clientResponse.end(JSON.stringify({
      timestamp: new Date().toISOString(),
      status: 502,
      error: 'Bad Gateway',
      message: 'El backend no está disponible en http://localhost:8080'
    }));
  });

  clientRequest.pipe(proxyRequest);
}

function serveFrontend(request, response) {
  if (!['GET', 'HEAD'].includes(request.method)) {
    response.writeHead(405, { allow: 'GET, HEAD' });
    response.end();
    return;
  }

  let pathname;
  try {
    pathname = decodeURIComponent(new URL(request.url, 'http://localhost').pathname);
  } catch {
    response.writeHead(400);
    response.end('Solicitud inválida');
    return;
  }

  const requestedPath = pathname === '/'
    ? path.join(frontendRoot, 'index.html')
    : path.resolve(frontendRoot, `.${pathname}`);
  const insideFrontend = requestedPath === frontendRoot || requestedPath.startsWith(`${frontendRoot}${path.sep}`);
  const isFile = insideFrontend && fs.existsSync(requestedPath) && fs.statSync(requestedPath).isFile();
  const filePath = isFile ? requestedPath : path.join(frontendRoot, 'index.html');
  const extension = path.extname(filePath).toLowerCase();

  response.writeHead(200, {
    'cache-control': 'no-store',
    'content-type': contentTypes[extension] || 'application/octet-stream',
    'x-content-type-options': 'nosniff'
  });

  if (request.method === 'HEAD') {
    response.end();
    return;
  }

  fs.createReadStream(filePath).pipe(response);
}

const server = http.createServer((request, response) => {
  const pathname = new URL(request.url, 'http://localhost').pathname;
  if (pathname === '/api' || pathname.startsWith('/api/')) {
    proxyApi(request, response);
    return;
  }
  serveFrontend(request, response);
});

server.listen(frontendPort, '127.0.0.1', () => {
  console.log(`Frontend disponible en http://localhost:${frontendPort}`);
  console.log(`Solicitudes /api reenviadas a ${backendUrl.origin}`);
});

server.on('error', (error) => {
  console.error(`No se pudo iniciar el frontend: ${error.message}`);
  process.exitCode = 1;
});
