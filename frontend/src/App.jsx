import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function App() {
	const [input, setInput] = useState("")
	const [response, setResponse] = useState("")

	const sendToBackend = async () => {
		const res = await fetch('/api/log', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ message: input })
		})
		const data = await res.json()
		setResponse(data.response)
	}

	return (
		<div>
			<h1>Database/Sqlite browser</h1>
			<input value={input} onChange={e => setInput(e.target.value)} placeholder="Enter message" />
			<button onClick={sendToBackend}>Send</button>
			<p>Response: {response}</p>
		</div>
	)
}

export default App
