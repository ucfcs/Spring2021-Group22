async function squigglemap(dataPath) {
	// Jquery empty node and reset before adding data
	// d3 code just appends what it needs, could rework to reuse nodes
	$("div#squigglemap").empty()
	$("div#squigglemap").append("\
	<div id=\"squigglemap\">\
		<div class=\"row align-items-center\">\
			<div>\
				<p id=\"value-range\"></p>\
				<div id=\"slider-button\"><div>\
			</div>\
			<div>\
				<div id=\"slider-range\"></div>\
			</div>\
		</div>\
		<div id=\"map\"></div>\
		<div id=\"input\"></div>\
	</div>")

	// Initial Values
	const SCALE = 2
	let width = 417 * SCALE, height = 417 * SCALE; // 417 is image size
	const CENTER = { x: width / 2, y: height / 2 };

	const inputdata = await getData(dataPath)

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
		for (const key of Object.keys(inputdata.timeline).sort((a, b) => parseFloat(a) - parseFloat(b))) {
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

	// Show/update slider
	var sliderRange = d3
		.sliderBottom()
		.min(_minTime)
		.max(_maxTime)
		.width(400)
		.tickFormat(x => parseInt(x))
		// .ticks(10)
		.default([_minTime, _maxTime])
		.fill('#2196f3')
		.step(1)
		.on('onchange', val => {
			// d3.select('p#value-range').text(val.map(d3.format('.2%')).join('-'));
			d3.select('p#value-range').text(val.join('-'));
			// console.log(val)
		})
		.on('end', val => {
			console.log(val)
			generateData(val[0], val[1])
			drawPlayers(data)
		})

	var gRange = d3
		.select('#slider-range')
		.append('svg')
		.attr('width', 500)
		.attr('height', 100)
		.append('g')
		.attr('transform', 'translate(30,30)');

	gRange.call(sliderRange);

	d3.select('p#value-range').text(
		sliderRange
			.value()
			.join('-')
	);
	playbutton = d3.select('div#slider-button')
	.append("button")
	.text('Play')
	.on('click', async () => {
		playbutton.text('Playing')
		await onPlay()
		playbutton.text('Play')
	})

	// Create simple play animation
	const timer = ms => new Promise(res => setTimeout(res, ms))
	const onPlay = async () => {
		let space = Math.floor((_maxTime - _minTime) / 255)
		for (i = 0; i < 250; i++) {
			sliderRange.value([space * i, space * (i + 5)])
			generateData(space * i, space * (i + 5))
			drawPlayers(data)
			await timer(100)
		}
	}

	let input = d3.select("div#input")

	input.append("h5")
		.text("Toggle Visibility of player")
	// input.append("p")
	// 	.text("rises to top on reshowing")

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
		.attr("xlink:href", "/img/map.png")

	let heatmap = svg.append("g").attr("id", 'heatmap')

	// handle pan and zoom of svg
	let zoom = d3.zoom()
		.translateExtent([[0, 0], [width, height]])
		.scaleExtent([1, 5])
		.on('zoom', handleZoom);

	// create group <g id={id}> for each player in data
	// draw path for each player with unique color
	function drawPlayers(data) {

		// console.log('draw players', data, Object.values(data))

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
			.style("background-color", player.color == 'yellow' ? 'gray' : '')
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