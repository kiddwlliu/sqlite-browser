import { defineConfig } from 'vite';
import laravel from 'laravel-vite-plugin';
import tailwindcss from '@tailwindcss/vite';

export default defineConfig({
	plugins: [
		laravel({
			input: ['resources/css/app.css', 'resources/js/app.js'],
			refresh: true,
		}),
		tailwindcss(),
	],
	base: '/spa',
	// build: {
	// 	outDir: '../var/static/spa',
	// 	emptyOutDir: true, // also necessary
	// },

	define: {
		__VITE_REACT_APP_VERSION__: JSON.stringify(process.env.npm_package_version),
		__VITE_BUILD__: JSON.stringify({
			// homeShortName: "GW SPA",
			// homeLongName: "SPA (Single Page Application)",
			name: process.env.npm_package_name,
			version: process.env.npm_package_version,
			// apiBasedir: "..",
			urlBasepath: "/spa"
			// apiUsername: "auser"
		})
		// __APP_VERSION__: JSON.stringify('text')
	}
});
