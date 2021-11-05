
async function renderTimeOfPlayerByLocationColumnChart(element) {
  const data = await getData('time_of_player_by_location_column_chart.json')

  const options = {
    title: {
      text: 'Time of Player by Location',
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
        text: 'Seconds',
      },
    },
  }
  
  new ApexCharts(element, options).render();
}
