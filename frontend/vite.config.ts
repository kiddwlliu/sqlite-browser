import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
	plugins: [react()],
	server: {
		proxy: {
			'/api': 'http://localhost:8080'
		}
	},
	base: '/spa',
	// build: {
	// 	outDir: '../var/static/spa',
	// 	emptyOutDir: true, // also necessary
	// },

	define: {
		__GLOBAL_URL_BASE__: JSON.stringify('/spa'),
		// __VITE_REACT_APP_VERSION__: JSON.stringify(process.env.npm_package_version),
		__VITE_BUILD__: JSON.stringify({
			homeShortName: "GW SPA",
			homeLongName: "SPA (Single Page Application)",
			// name: process.env.npm_package_name,
			// version: process.env.npm_package_version,
			// apiBasedir: "..",
			urlBasepath: "/spa"
			// apiUsername: "auser"
		})
		// __APP_VERSION__: JSON.stringify('text')
	}
})
