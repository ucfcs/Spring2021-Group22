async function renderTimeInLocationByPlayerColumnChart(element) {
  const data = await getData('time_in_location_by_player_column_chart.json')

  const options = {
    title: {
      text: 'Time in Location by Each Player',
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
