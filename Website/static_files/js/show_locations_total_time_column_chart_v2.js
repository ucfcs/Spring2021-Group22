
async function renderLocationsTotalTimeColumnChartV2(element) {
  const data = await getData('locations_total_time_column_chart_v2.json')

  const options = {
    title: {
      text: 'Approximate Seconds Spent in Locations by Player V2',
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
