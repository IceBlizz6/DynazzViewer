export class BatchRequester<T> {
    private nextAvailable = true
    private fetchedCount = 0
    private isFetching = false
    
    public constructor(
        private batchSize: number,
        private requestBatch: (skip: number, take: number) => Promise<T[]>,
        private process: (list: T[]) => void
    ) {

    }

    public async fetchNext(): Promise<void> {
        if (this.isNextAvailable && !this.isFetching) {
            this.isFetching = true
            const fetched = await this.requestBatch(this.fetchedCount, this.batchSize)
            this.fetchedCount += fetched.length
            this.nextAvailable = (fetched.length == this.batchSize)
            this.process(fetched)
            this.isFetching = false
        }
    }

    public get isNextAvailable() {
        return this.nextAvailable
    }
}
