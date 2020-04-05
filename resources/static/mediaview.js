Vue.use(httpVueLoader);

function mediaQuery() {
	return `
		query {
			listMediaUnits {
				id
				name
				images {
					url
				}
				children {
					id
					name
					seasonNumber
					sortOrder
					children {
						id
						episodeNumber
						name
						status
						sortOrder
						aired
					}
				}
			}
		}
	`;
}

function updateEpisodeStateQuery(episodeId, status) {
	return `
		mutation {
			setEpisodeWatchState(mediaPartId: ${episodeId}, status: ${status})
		}
	`;
}

async function run() {
	let query = mediaQuery();
	let sourceData = await graphqlAsyncRequest(query);

	var app = new Vue({
		el: '#app',
		data: {
			source: sourceData.data,
			selected: null,
		},
		components: {
			'media-series': 'url:components/mediaseries.vue'
        },
        methods: {
        	selectSeries: function(item) {
        		if (this.selected == item) {
        			this.selected = null;
        		} else {
        			this.selected = item;
        		}
        	},
        	setEpisodeWatch: async function(episode, status) {
        		let query = updateEpisodeStateQuery(episode.id, status);
        		await graphqlAsyncRequest(query);
        		episode.status = status;
        	}
        },
	});
}

run();
