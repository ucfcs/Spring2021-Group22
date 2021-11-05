async function renderTimeInLocationByPlayerColumnChart(element) {
  const data = await getData('time_in_location_by_player_column_chart.json')

  const options = {
    title: {
      text: 'Time Spent in Location by Player',
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
  
  new ApexCharts(element, options).render();
}
