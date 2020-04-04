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
					<img v-if="node.viewStatus == 'None'" class="tree-icon" src="/assets/videofiles/Neutral.png">
					<img v-if="node.viewStatus == 'Viewed'" class="tree-icon" src="/assets/videofiles/Viewed.png">
					<img v-if="node.viewStatus == 'Skipped'" class="tree-icon" src="/assets/videofiles/Skipped.png">
					<span>{{ node.fileName.name }}</span>
					<span class="toolbar-action" @click="$root.setViewed(node, 'None')">
						<img class="tree-icon" src="/assets/videofiles/Neutral.png">
						Undo
					</span>
					<span class="toolbar-action" @click="$root.setViewed(node, 'Viewed')">
						<img class="tree-icon" src="/assets/videofiles/Viewed.png">
						Viewed
					</span>
					<span class="toolbar-action" @click="$root.setViewed(node, 'Skipped')">
						<img class="tree-icon" src="/assets/videofiles/Skipped.png">
						Skipped
					</span>
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
			showChildren: true,
		}
	},
	computed: {
		childrenDirectories: function() {
			return this.nodes.filter(el => el.children != undefined).sort((a, b) => (a.name > b.name)? 1 : -1);
		},
		childrenFiles: function() {
			return this.nodes.filter(el => el.children == undefined).sort((a, b) => (a.fileName.name > b.fileName.name)? 1 : -1);
		}
	},
	methods: {
		toggleChildren() {
			this.showChildren = !this.showChildren;
		}
	}
}
</script>
