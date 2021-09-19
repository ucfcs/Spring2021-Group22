async function renderLocationsTotalTimeColumnChart(element) {
  const data = await getData('locations_total_time_column_chart.json')

  const options = {
    title: {
      text: 'Approximate Seconds Spent in Locations by Player',
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
        text: 'Seconds',
      },
    },
  }
  new ApexCharts(element, options).render();
}
