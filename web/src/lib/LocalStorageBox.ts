export class LocalStorageBox<T> {
    public constructor(
        private key: string
    ) {}

    public get(): T | null {
        const rawJson = localStorage.getItem(this.key)
        if (rawJson === null) {
            return null
        } else {
            return JSON.parse(rawJson)
        }
    }

    public getOrDefault(createDefault: () => T): T {
        const value = this.get()
        if (value === null) {
            return createDefault()
        } else {
            return value
        }
    }

    public set(value: T) {
        localStorage.setItem(this.key, JSON.stringify(value))
    }

    public clear() {
        localStorage.removeItem(this.key)
    }
}
