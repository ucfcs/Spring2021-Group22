let data = null

// TODO figure out a better way to handle this probably
let currentCharts = []

async function fetchData() {
  data = await getData('teams.json')
}

function roundFormatter(value) {
  return Math.floor(value * 100) / 100
}

function renderDropdown() {
  const element = $('#team-select')
  for (teamData of data) {
    element.append(
      $(
        '<button onclick="renderCharts(\'' +
          teamData.experiment_label +
          '\')">' +
          teamData.experiment_label +
          '</button>'
      )
    )
  }
}

function renderChatContentColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Total Chat Content By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    colors: data.colors,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Content Length',
      },
    },
    plotOptions: {
      bar: {
          distributed: true
      }
    }
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderChatCountColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Total Chat Messages Sent By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    colors: data.colors,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Message Count',
      },
    },
    plotOptions: {
      bar: {
          distributed: true
      }
    }
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderDistanceFromCenterByPlayerTimeSeries(elements, data) {
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
      text: 'Average Distance From Center By Player Over Time',
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

function renderDiversityOfLaborByEventPieCharts(elements, data) {
  for (let i = 0; i < data.charts.length; i++) {
    const element = elements[i]
    const chart = data.charts[i]
    const options = {
      title: {
        text: chart.title,
      },
      chart: {
        type: 'pie',
      },
      series: chart.series,
      labels: chart.labels,
      colors: chart.colors,
      dataLabels: {
        minAngleToShowLabel: 10,
      },
      tooltip: {
        y: {
          formatter: roundFormatter
        }
      }
    }

    const achart = new ApexCharts(element, options)
    achart.render()
    currentCharts.push(achart)
  }
}

function renderDiversityOfLaborByPlayerColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Percentage of Labor Events By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Percentage',
      },
      min: 0,
      max: 100,
      decimalsInFloat: 2,
    },
    dataLabels: {
      enabled: false,
    },
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderEntityDamageInteractionsColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Damage Dealt And Taken From Entities By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Total',
      },
      decimalsInFloat: 2,
    },
    dataLabels: {
      formatter: roundFormatter,
    },
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderLocationSpreadByPlayerTimeSeries(elements, data) {
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
      text: 'Average Distance Between Players By Player Over Time',
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

function renderPercentDuplicateLocationColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Percent of New Locations That Were Already Visited',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    colors: data.colors,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Percent',
      },
      min: 0,
      decimalsInFloat: 2,
    },
    dataLabels: {
      formatter: roundFormatter,
    },
    plotOptions: {
      bar: {
          distributed: true
      }
    }
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderSharingColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Items Shared By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Count',
      },
    },
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderSpecialItemsOverTimeUnweightedTimeSeries(elements, data) {
  const element = elements[0]
  const options = {
    series: data.series,
    chart: {
      height: 350,
      type: 'line',
      zoom: {
        type: 'x',
        enabled: true,
        autoScaleYaxis: true,
      },
    },
    dataLabels: {
      enabled: false,
    },
    title: {
      text: 'Unweighted Special Item Usage Over Time By Player',
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

function renderSpecialItemsOverTimeWeightedTimeSeries(elements, data) {
  const element = elements[0]
  const options = {
    series: data.series,
    chart: {
      height: 350,
      type: 'line',
      zoom: {
        type: 'x',
        enabled: true,
        autoScaleYaxis: true,
      },
    },
    dataLabels: {
      enabled: false,
    },
    title: {
      text: 'Weighted Special Item Usage By Player Over Time',
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

function renderTimeOfPlayerByLocationColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Time Spent For Player By Location',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Time Spent (s)',
      },
    },
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderTimeOfPlayerByLocationPieCharts(elements, data) {
  for (let i = 0; i < data.charts.length; i++) {
    const element = elements[i]
    const chart = data.charts[i]
    const options = {
      title: {
        text: chart.title,
      },
      chart: {
        type: 'pie',
      },
      colors: chart.colors,
      series: chart.series,
      labels: chart.labels,
      dataLabels: {
        minAngleToShowLabel: 10,
      },
      tooltip: {
        y: {
          formatter: roundFormatter
        }
      }
    }

    const achart = new ApexCharts(element, options)
    achart.render()
    currentCharts.push(achart)
  }
}

function renderTotalDistanceColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Total Distance Travelled By Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    colors: data.colors,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Total Distance (m)',
      },
      decimalsInFloat: 2,
    },
    dataLabels: {
      formatter: roundFormatter,
    },
    plotOptions: {
      bar: {
          distributed: true
      }
    }
  }

  const achart = new ApexCharts(element, options)
  achart.render()
  currentCharts.push(achart)
}

function renderTrophyCountColumnChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Trophies Collected by Player',
    },
    chart: {
      type: 'bar',
      height: 350,
    },
    series: data.series,
    colors: data.colors,
    xaxis: {
      categories: data.categories,
    },
    yaxis: {
      title: {
        text: 'Trophies',
      },
    },
    plotOptions: {
      bar: {
          distributed: true
      }
    }
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

function renderDeathTimelineStepChart(elements, data) {
  const element = elements[0]
  const options = {
    title: {
      text: 'Cumulative Deaths by Minute',
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

function renderCharts(experimentLabel) {
  $('#selected-team').html(experimentLabel)
  for (let chart of currentCharts) {
    chart.destroy()
  }
  currentCharts.length = 0

  const chartSource = data.find(
    (it) => it.experiment_label === experimentLabel
  ).data
  renderChatContentColumnChart(
    [document.getElementById('chat_content_column_chart')],
    chartSource.chat_content_column_chart
  )
  renderChatCountColumnChart(
    [document.getElementById('chat_count_column_chart')],
    chartSource.chat_count_column_chart
  )
  renderDistanceFromCenterByPlayerTimeSeries(
    [document.getElementById('distance_from_center_by_player_time_series')],
    chartSource.distance_from_center_by_player_time_series
  )
  renderDiversityOfLaborByEventPieCharts(
    [
      document.getElementById('diversity_of_labor_by_event_pie_charts_1'),
      document.getElementById('diversity_of_labor_by_event_pie_charts_2'),
      document.getElementById('diversity_of_labor_by_event_pie_charts_3'),
      document.getElementById('diversity_of_labor_by_event_pie_charts_4'),
      document.getElementById('diversity_of_labor_by_event_pie_charts_5'),
      document.getElementById('diversity_of_labor_by_event_pie_charts_6'),
      document.getElementById('diversity_of_labor_by_event_pie_charts_7'),
    ],
    chartSource.diversity_of_labor_by_event_pie_charts
  )
  renderDiversityOfLaborByPlayerColumnChart(
    [document.getElementById('diversity_of_labor_by_player_column_chart')],
    chartSource.diversity_of_labor_by_player_column_chart
  )
  renderEntityDamageInteractionsColumnChart(
    [document.getElementById('entity_damage_interactions_column_chart')],
    chartSource.entity_damage_interactions_column_chart
  )
  renderLocationSpreadByPlayerTimeSeries(
    [document.getElementById('location_spread_by_player_time_series')],
    chartSource.location_spread_by_player_time_series
  )
  renderLocationSpreadTimeSeries(
    [document.getElementById('location_spread_time_series')],
    chartSource.location_spread_time_series
  )
  renderPercentDuplicateLocationColumnChart(
    [document.getElementById('percent_duplicate_location_column_chart')],
    chartSource.percent_duplicate_location_column_chart
  )
  renderSharingColumnChart(
    [document.getElementById('sharing_column_chart')],
    chartSource.sharing_column_chart
  )
  renderSpecialItemsOverTimeUnweightedTimeSeries(
    [document.getElementById('special_items_over_time_unweighted_time_series')],
    chartSource.special_items_over_time_unweighted_time_series
  )
  renderSpecialItemsOverTimeWeightedTimeSeries(
    [document.getElementById('special_items_over_time_weighted_time_series')],
    chartSource.special_items_over_time_weighted_time_series
  )
  renderTimeOfPlayerByLocationColumnChart(
    [document.getElementById('time_of_player_by_location_column_chart')],
    chartSource.time_of_player_by_location_column_chart
  )
  renderTimeOfPlayerByLocationPieCharts(
    [
      document.getElementById('time_of_player_by_location_pie_charts_1'),
      document.getElementById('time_of_player_by_location_pie_charts_2'),
      document.getElementById('time_of_player_by_location_pie_charts_3'),
      document.getElementById('time_of_player_by_location_pie_charts_4'),
    ],
    chartSource.time_of_player_by_location_pie_charts
  )
  renderTotalDistanceColumnChart(
    [document.getElementById('total_distance_column_chart')],
    chartSource.total_distance_column_chart
  )
  renderTrophyCountColumnChart(
    [document.getElementById('trophy_count_column_chart')],
    chartSource.trophy_count_column_chart
  )
  renderZoneTimelineChart(
    [document.getElementById('zone_timeline_chart')],
    chartSource.zone_timeline_chart
  )
  renderDeathTimelineStepChart(
    [document.getElementById('death_timeline_step_chart')],
    chartSource.death_timeline_step_chart
  )
  renderTrophyTimelineStepChart(
    [document.getElementById('trophy_timeline_step_chart')],
    chartSource.trophy_timeline_step_chart
  )
}
