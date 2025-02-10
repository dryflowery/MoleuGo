import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

export default defineConfig({
  plugins: [svelte()],
  base: '/',
  server: {
    historyApiFallback: true,
  },
  build: {
    rollupOptions: {
      output: {
        entryFileNames: `[name]-[hash].js`,
        chunkFileNames: `[name]-[hash].js`,

        assetFileNames: assetInfo => {
          if (assetInfo.name && assetInfo.name.endsWith('.css')) {
            return `[name]-[hash][extname]`;
          }
          else if (assetInfo.name && /\.(png|jpe?g|gif|svg|webp|mp3|mp4)$/.test(assetInfo.name)) {
            return 'assets/[name]-[hash][extname]';
          }
          else {
            return 'other/[name]-[hash][extname]';
          }
        }
      }
    }
  }
});
