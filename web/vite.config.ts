import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from "path"

const apiPort = 8080
const apiEndpoint = "http://localhost:" + apiPort

export default defineConfig({
	plugins: [ vue() ],
	resolve: {
		alias: {
			'@': path.resolve(__dirname, './src')
		}
	},
	server: {
		port: 3000,
		proxy: {
			"/graphql": {
				target: apiEndpoint
			},
			"/graphiql": {
				target: apiEndpoint
			},
			"/vendor": {
				target: apiEndpoint
			},
		}
	}
})
