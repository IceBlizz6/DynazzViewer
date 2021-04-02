import { createClient } from '@/graph/createClient'
import { Client } from 'graphql-typed-client'
import { QueryRequest, QueryPromiseChain, Query, MutationRequest, MutationPromiseChain, Mutation } from '@/graph/schema'

export interface GraphClient {
	query(request: QueryRequest, responseAction: (_: Query) => void): void 

	mutation(request: MutationRequest, responseAction: (_: Mutation) => void): void
	
	queryWithReturn<T>(request: QueryRequest, responseAction: (_: Query) => T): Promise<T>

	mutationWithReturn<T>(request: MutationRequest, responseAction: (_: Mutation) => T): Promise<T>
}

class GraphClientImpl implements GraphClient {
	private client: Client<QueryRequest, QueryPromiseChain, Query, MutationRequest, MutationPromiseChain, Mutation, never, never, never>

	public constructor() {
		this.client = createClient({
			fetcher: ({ query, variables }, fetch) =>
				fetch(`http://localhost:8081/graphql`, {
					credentials: 'include',
					method: 'POST',
					body: JSON.stringify({ query, variables })
				}).then(r => r.json())
		})
	}

	public query(request: QueryRequest, responseAction: (_: Query) => void): void {
		this.client.query(request)
			.then(response => {
				if (response.data == null) {
					throw new Error("No data to handle request")
				} else {
					responseAction(response.data)
				}
			})
	}

	public mutation(request: MutationRequest, responseAction: (_: Mutation) => void): void {
		this.client.mutation(request)
			.then(response => {
				if (response.data == null) {
					throw new Error("No data to handle request")
				} else {
					responseAction(response.data)
				}
			})
	}

	public queryWithReturn<T>(request: QueryRequest, responseAction: (_: Query) => T): Promise<T> {
		return this.client.query(request)
			.then(response => {
				if (response.data == null) {
					throw new Error("No data to handle request")
				} else {
					return responseAction(response.data)
				}
			})
	}

	public mutationWithReturn<T>(request: MutationRequest, responseAction: (_: Mutation) => T): Promise<T> {
		return this.client.mutation(request)
			.then(response => {
				if (response.data == null) {
					throw new Error("No data to handle request")
				} else {
					return responseAction(response.data)
				}
			})
	}
}

const graphClient: GraphClient = new GraphClientImpl()

export default graphClient
