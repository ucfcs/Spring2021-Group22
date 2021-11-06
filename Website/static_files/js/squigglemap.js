async function squigglemap() {
	// Initial Values
	const SCALE = 2
	let width = 417 * SCALE, height = 417 * SCALE; // 417 is image size
	const CENTER = { x: width / 2, y: height / 2 };

	const inputdata = await getData('rawdata_timestamp.json')

	const _minTime = inputdata.time.min;
	const _maxTime = inputdata.time.max;

	let data = {};
	let dataMin = _minTime;
	let dataMax = _maxTime;

	generateData(_minTime, _maxTime, true);

	// iterates over every event in input data and checks if its is the correct event (playerlocationevent) and if it is within timeframe
	// if so, adds it to player object with array of coords which are turned into lines
	function generateData(lowerTime, upperTime, force = false) {
		if (!force && lowerTime == dataMin && upperTime == dataMax) {
			return;
		}

		// Reset players
		for (const player in data) {
			data[player].pos.length = 0;
		}
		for (const key of Object.keys(inputdata.timeline).sort((a,b) => parseFloat(a) - parseFloat(b))) {
			let res = inputdata.timeline[key].filter(f => f.event === "PlayerLocationEvent")
			if (res.length > 0) {
				res.forEach(r => {
					player = UUIDtoPlayer(r.player);
					if (Math.floor(r.time / 1000) >= lowerTime && Math.floor(r.time / 1000) <= upperTime) {
						if (player.name in data) {
							data[player.name].pos.push(coord(r.x, r.z))
						}
						else {
							data[player.name] = { player: player.name, color: player.color, pos: [coord(r.x, r.z)] }
						}
					}

				});
			}
		}

		dataMin = lowerTime;
		dataMax = upperTime;
	}


	var slider = createD3RangeSlider(_jsondata.time.min, _jsondata.time.max, "#slider-container", true);

	// Only redraw lines after selection is made, not on every change
	slider.onChangeEnd(function (newRange) {

		generateData(newRange.begin, newRange.end)


		drawPlayers(data)


		d3.select("#range-label").html(newRange.begin + " &mdash; " + newRange.end);
	});

	let input = d3.select("div#input")

	input.append("h3")
		.text("Toggle Visibility of player")
	input.append("p")
		.text("rises to top on reshowing")

	// Select map div and set attributes
	let map = d3.select("div#map")
		.style("width", width + "px")
		.style("height", height + "px")

	// create base svg
	let _svg = map
		.append("svg")
		.style("width", width + "px")
		.style("height", height + "px")
	let svg = _svg.append("g")

	// add image to svg
	let image = svg.append("image")
		.attr("width", width + "px")
		.attr("height", height + "px")
		.attr("xlink:href", "/img/highresmap.png")

	let heatmap = svg.append("g").attr("id", 'heatmap')

	// handle pan and zoom of svg
	let zoom = d3.zoom()
		.translateExtent([[0, 0], [width, height]])
		.scaleExtent([1, 5])
		.on('zoom', handleZoom);

	// create group <g id={id}> for each player in data
	// draw path for each player with unique color
	function drawPlayers(data) {

		console.log('draw players', data, Object.values(data))

		let path = heatmap
			.selectAll("path")
			.data(Object.values(data))

		path.enter().append("path")//.attr("id", function (d) { return d.player })
			.attr("id", function (d) { return d.player })
			.attr("stroke", function (d) { return d.color })
			.attr("fill", "none")
			.attr("visibility", "visible")
			.attr("d", function (d) {
				// console.log(d.pos, d3.line()(d.pos))
				return d3.line()(d.pos)
			})
			.each(function (d) {
				createButton(this, d)
			})

		path
			.attr("visibility", "visible")
			.attr("d", function (d) {
				return d3.line()(d.pos)
			})

		path.exit().attr("visibility", "hidden")

		return path
	}

	let players = drawPlayers(data)

	function createButton(_selection, player) {
		div = input.append("div")
		button = div.append("input")
			.attr("type", "checkbox")
			.attr("name", function () { return player.player })
			.attr("id", function () { return player.player })
			.property("checked", true)
			.on("click", function (d) {
				selection = d3.select(`path#${player.player}`);
				let button = d3.select(this);
				let state = button.property("checked");
				selection.style("display", state ? "block" : "none")
				if (state) {
					// button.raise()	// need to move label and </br> as well
					selection.raise()
				}
			})
		label = div.append("label")
			.attr("for", player.player)
			.text(player.player)
			.style("color", player.color)
	}

	function initZoom() {
		svg.call(zoom);
	}

	function coord(x, y) {
		return [Math.floor(x * SCALE + CENTER.x), Math.floor(y * SCALE + CENTER.y)]
	}

	function handleZoom(e) {
		svg.attr('transform', e.transform);
	}

	initZoom();
}