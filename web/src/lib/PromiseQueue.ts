export class PromiseQueue {
	private tasks: (() => Promise<boolean>)[] = []
	private started: boolean = false

	public add(task: () => Promise<boolean>): void {
		if (this.started) {
			throw new Error("Already started")
		} else {
			this.tasks.push(task)
		}
	}

	public run(): Promise<boolean> {
		this.started = true
		if (this.tasks.length > 0) {
			return this.runTask(0)
		} else {
			return Promise.resolve(true)
		}
	}

	private runTask(index: number): Promise<boolean> {
		return this.tasks[index]().then(success => {
			if (success) {
				if (index+1 < this.tasks.length) {
					return this.runTask(index + 1)
				} else {
					return true
				}
			} else {
				return false
			}
		})
	}
}
