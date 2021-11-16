async function renderLocationConvexShellSpreadTimeSeries(element) {
  const data = await getData('location_convex_shell_spread_time_series.json')

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
      text: 'Area of Convex Shell Between Players Over Time',
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
  }

  new ApexCharts(element, options).render()
}
