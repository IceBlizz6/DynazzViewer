import { createClient } from '@/graph/createClient'
import { Client, ClientOptions } from 'graphql-typed-client'
import { QueryRequest, QueryPromiseChain, Query, MutationRequest, MutationPromiseChain, Mutation } from '@/graph/schema'

const graphClient = createClient({
	fetcher: ({ query, variables }, fetch, qs) =>
		fetch(`http://localhost:8081/graphql`, {
			credentials: 'include',
			method: 'POST',
			body: JSON.stringify({ query, variables })
		}).then(r => r.json())
})

export default graphClient
