import { Chain } from '@/zeus'

export class GraphClient {
	private graphClient = Chain("http://localhost:8080/graphql")

	public query = this.graphClient(
		"query", 
		{
			scalars: {
				LocalDate: {
					encode: (e: unknown) => e as string,
					decode: (e: unknown) => e as string
				}
			}
		}
	)

	public mutation = this.graphClient(
		"mutation", 
		{
			scalars: {
				LocalDate: {
					encode: (e: unknown) => e as string,
					decode: (e: unknown) => e as string
				}
			}
		}
	)
}

const graphClient = new GraphClient()
export { graphClient }
