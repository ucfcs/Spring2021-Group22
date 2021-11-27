let data = null

// TODO figure out a better way to handle this probably
let currentCharts = []

async function fetchData() {
  data = await getData('teams.json')
}

function roundFormatter(value) {
  return Math.floor(value * 100) / 100
}

function renderLocationSpreadTimeSeries(elements, data) {
  const element = elements[0]
  const options = {
    series: data.series,
    chart: {
      height: 350,
      type: 'line',
      zoom: {
        enabled: false,
      },
    },
    dataLabels: {
      enabled: false,
    },
    stroke: {
      curve: 'straight',
    },
    title: {
      text: 'Average Distance Between All Players Over Time',
      align: 'left',
    },
    grid: {
      row: {
        colors: ['#f3f3f3', 'transparent'],
        opacity: 0.5,
      },
    },
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      decimalsInFloat: 2,
    },
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderZoneTimelineChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Zone Timeline By Player',
    },
    chart: {
      type: 'rangeBar',
    },
    series: data.series,
    plotOptions: {
      bar: {
        horizontal: true,
        // barHeight: '50%',
        rangeBarGroupRows: true,
      },
    },
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderTrophyTimelineStepChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Cumulative Trophies Collected by Minute',
    },
    chart: {
      type: 'line',
    },
    stroke: {
      curve: 'stepline',
    },
    series: data.series,
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderCharts() {
  for (let chart of currentCharts) {
    chart.destroy()
  }
  currentCharts.length = 0

  const locationSpreadData = { 'series': data.map(it => ({ name: it.experiment_label, data: it.data.location_spread_time_series.series[0].data})) }
  const trophyTimelineData = { 'series': data.map(it => ({ name: it.experiment_label, data: it.data.trophy_timeline_step_chart.series[0].data})) }
  renderLocationSpreadTimeSeries(
    [document.getElementById('location_spread_overall_time_series')],
    locationSpreadData
  )
  renderTrophyTimelineStepChart(
    [document.getElementById('trophy_timeline_overall_step_chart')],
    trophyTimelineData
  )
}
