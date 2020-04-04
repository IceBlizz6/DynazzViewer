async function graphqlAsyncRequest(query) {
	return await fetch('/graphql', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			'Accept': 'application/json',
		},
		body: JSON.stringify({query: query})
	}).then(r => r.json());
}

function reportError(msg) {
	console.error(msg);
}
