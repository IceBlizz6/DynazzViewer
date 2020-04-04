Vue.use(httpVueLoader);

const videoFileQuery = `
	query {
		listVideoFiles {
			root
			files {
				fileName {
					name
				}
				filePath {
					path
				}
				mediaFileId
				viewStatus
			}
		}
	}
`;

function resolvePath(rootPath, filePath) {
	if (filePath.startsWith(rootPath)) {
		let pathList = [];
		pathList.push(rootPath);
		filePath.substring(rootPath.length).split("/").forEach(el => { if (el.length > 0) {pathList.push(el); } });
		return pathList;
	} else {
		reportError(filePath + " does not start with " + rootPath);
	}
}

function lookupChildren(parent, childName) {
	for (let i = 0;i<parent.children.length;i++) {
		if (parent.children[i].name == childName) {
			return parent.children[i];
		}
	}
	let newChild = { name: childName, children: [] };
	parent.children.push(newChild);
	return newChild;
}

function pushNode(treeRoot, pathList, childObject) {
	let current = treeRoot;
	for (let i = 1;i<pathList.length - 1;i++) {
		current = lookupChildren(current, pathList[i]);
	}
	current.children.push(childObject);
}

function filePathsToTree(rootPath, filePaths) {
	var rootNode = { name: rootPath, children: [] };
	for (let i = 0;i<filePaths.length;i++) {
		let paths = resolvePath(rootPath, filePaths[i].filePath.path);
		pushNode(rootNode, paths, filePaths[i]);
	}
	return rootNode;
}

async function run() {
	let sourceData = await graphqlAsyncRequest(videoFileQuery);
	let treeRoots = sourceData.data.listVideoFiles
		.map(el => filePathsToTree(el.root, el.files));

	var app = new Vue({
		el: '#app',
		data: {
			roots: treeRoots
		},
		created: function () {
		},
		components: {
			'tree-menu': 'url:components/filetree.vue'
		}
	});
}

run();
