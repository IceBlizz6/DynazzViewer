export class ListFunc {
    public static remove<T>(source: T[], removal: T): void {
        const index: number = source.indexOf(removal)
        if (index === -1) {
            throw new Error("Element not found")
        } else {
            source.splice(index, 1)
        }
    }
    
    public static notEmpty<TValue>(value: TValue | null | undefined): value is TValue {
        return value !== null && value !== undefined
    }
}
