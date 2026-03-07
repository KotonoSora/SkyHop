# SkyHop Web - Cloudflare Pages Starter

Minimal static website starter for Cloudflare Pages using Wrangler CLI.

## Prerequisites

- Node.js 18+
- Cloudflare account

## Setup

```bash
npm install
npm run cf:login
npm run cf:whoami
```

## Local Development

```bash
npm run dev
```

Wrangler starts a local Pages server and serves `public/`.

## Deploy

```bash
npm run deploy
```

This deploys the `public/` directory to Cloudflare Pages project `skyhop`.

## Create project once (if needed)

If deployment fails because the project does not exist yet:

```bash
npm run cf:project:create
```

Then run `npm run deploy` again.

## Custom Domain: skyhop.kotonosora.com

Wrangler can deploy Pages, but custom-domain binding is managed in Cloudflare Pages + DNS.

1. Deploy once so the project is live:

```bash
npm run deploy
```

2. In Cloudflare Dashboard:
- Pages -> project `skyhop` -> `Custom domains` -> `Set up a custom domain`
- Enter: `skyhop.kotonosora.com`

3. Ensure DNS in zone `kotonosora.com`:
- Type: `CNAME`
- Name: `skyhop`
- Target: `skyhop.pages.dev`
- Proxy status: `Proxied` (orange cloud)

4. Wait for SSL issuance and status to become `Active`.

5. Verify:

```bash
curl -I https://skyhop.kotonosora.com
```

## Structure

- `public/index.html` - main page
- `public/styles.css` - page styling
- `public/main.js` - client script
- `wrangler.toml` - Cloudflare config
