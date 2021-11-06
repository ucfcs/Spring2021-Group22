async function heatmap() {
	const SCALE = 2
	let width = 417 * SCALE, height = 417 * SCALE; // 417 is image size
	const CENTER = { x: width / 2, y: height / 2 };

	function coord(x, y) {
		return [Math.floor(x * SCALE + CENTER.x), Math.floor(y * SCALE + CENTER.y)]
	}

	// https://css-tricks.com/converting-color-spaces-in-javascript/
	function rgb2hex(rgb) {
		r = rgb[0].toString(16);
		g = rgb[1].toString(16);
		b = rgb[2].toString(16);

		if (r.length == 1)
			r = "0" + r;
		if (g.length == 1)
			g = "0" + g;
		if (b.length == 1)
			b = "0" + b;

		return "#" + r + g + b;
	}

	function scale(number, inMin, inMax, outMin, outMax) {
		return (number - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
	}


	// https://stackoverflow.com/questions/17242144/javascript-convert-hsb-hsv-color-to-rgb-accurately
	function hsv2rgb(h, s, v) {
		var r, g, b, i, f, p, q, t;
		i = Math.floor(h * 6);
		f = h * 6 - i;
		p = v * (1 - s);
		q = v * (1 - f * s);
		t = v * (1 - (1 - f) * s);
		switch (i % 6) {
			case 0: r = v, g = t, b = p; break;
			case 1: r = q, g = v, b = p; break;
			case 2: r = p, g = v, b = t; break;
			case 3: r = p, g = q, b = v; break;
			case 4: r = t, g = p, b = v; break;
			case 5: r = v, g = p, b = q; break;
		}
		return [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)]
	}



	// Select map div and set attributes
	let map = d3.select("div#my_dataviz")
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
		.attr("xlink:href", "highresmap.png")

	// Create square that is able to be placed under selected cell as outline
	// weird issues trying to use css outline on elements that are only a few pixels in size
	let selected = svg.append("rect")
		.attr("x", '0')
		.attr("y", '0')
		.attr("width", `${SCALE * 2}px`)
		.attr("height", `${SCALE * 2}px`)
		.style("opacity", 0)


	//Read the data
	// d3.csv("https://raw.githubusercontent.com/holtzy/D3-graph-gallery/master/DATA/heatmap_data.csv").then(function (data) {
	d3.csv('https://raw.githubusercontent.com/ucfcs/Spring2021-Group22/78fe6d609d3de713462bd984d3cdcfb7ae9999b5/Heatmap/heatmap.csv').then(function (data) {

		let max = Math.max(...data.map(d => parseInt(d.count)))

		// create a tooltip
		const tooltip = d3.select("#tooltip")
			.append("div")
			.style("opacity", 0)
			.attr("class", "tooltip")
			.style("background-color", "white")
			.style("border", "solid")
			.style("border-width", "2px")
			.style("border-radius", "5px")
			.style("padding", "5px")

		// Three function that change the tooltip when user hover / move / leave a cell
		const mouseover = function (event, d) {
			selected
				.attr("x", function () { return coord(d.x, 0)[0] - 0.5 - (SCALE / 2) })
				.attr("y", function () { return coord(0, d.y)[1] - 0.5 - (SCALE / 2) })
				.style("opacity", 1)
				.raise()
			tooltip
				.style("opacity", 1)
			d3.select(this)
				.style("opacity", 1)
				.raise()
		}
		const mousemove = function (event, d) {
			tooltip
				.html(`The exact value of<br>this cell is: ${d.count} at ${d.x}, ${d.y}`)
				.style("left", (event.x) / 2 + "px")
				.style("top", (event.y) / 2 + "px")
		}
		const mouseleave = function (event, d) {
			tooltip
				.style("opacity", 0)
			d3.select(this)
				.style("opacity", 0.8)
			selected.style("opacity", 0)

		}

		// add the squares
		svg.selectAll()
			.data(data, function (d) { return d.x + ':' + d.y; })
			.join("rect")
			.attr("x", function (d) { return coord(d.x, 0)[0] - 0.5 })
			.attr("y", function (d) { return coord(0, d.y)[1] - 0.5 })
			.attr("width", `${SCALE}px`)
			.attr("height", `${SCALE}px`)
			.style("fill", function (d) { return rgb2hex(hsv2rgb(scale(d.count, 0, max, 0.2, 0.8), 0.8, 0.8)) })
			.style("opacity", 0.8)
			.on("mouseover", mouseover)
			.on("mousemove", mousemove)
			.on("mouseleave", mouseleave)
	})

	let zoom = d3.zoom()
		.translateExtent([[0, 0], [width, height]])
		.scaleExtent([1, 5])
		.on('zoom', handleZoom);

	function handleZoom(e) {
		svg.attr('transform', e.transform);
	}

	function initZoom() {
		svg.call(zoom)
	}

	initZoom();
}