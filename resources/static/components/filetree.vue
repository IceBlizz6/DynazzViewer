<template>
	<li>
		<div @click="toggleChildren()" class="tree-item-header directory-node">
			<img class="tree-icon" src="/assets/FolderContentAvailable.png">
			<span>{{ label }}</span>
		</div>
		<ul class="tree-children-list">
			<tree-menu
				v-if="showChildren"
				v-for="node in childrenDirectories"
				:nodes="node.children"
				:label="node.name"
				:key="node.name">
			</tree-menu>
			<li v-for="node in childrenFiles" v-if="showChildren" :key="node.fileName.name">
				<div class="tree-item-header file-node">
					<img v-if="node.viewStatus == 'None'" class="tree-icon" src="/assets/VideoFileNeutral.png">
					<img v-if="node.viewStatus == 'Viewed'" class="tree-icon" src="/assets/VideoFileViewed.png">
					<img v-if="node.viewStatus == 'Skipped'" class="tree-icon" src="/assets/VideoFileSkipped.png">
					<span>{{ node.fileName.name }}</span>
				</div>
			</li>
		</ul>
	</li>
</template>
<script>
module.exports = {
	name: 'tree-menu',
	props: [ 'label', 'nodes' ],
	data: function() {
		return {
			showChildren: false,
			childrenDirectories: this.nodes.filter(el => el.children != undefined),
			childrenFiles: this.nodes.filter(el => el.children == undefined),
		}
	},
	methods: {
		toggleChildren() {
			this.showChildren = !this.showChildren;
		}
	}
}
</script>
