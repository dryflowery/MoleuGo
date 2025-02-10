import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

export default defineConfig({
  plugins: [svelte()],
  base: '/', // 기본 경로 설정
  server: {
    historyApiFallback: true, // history API fallback 추가
  }
});